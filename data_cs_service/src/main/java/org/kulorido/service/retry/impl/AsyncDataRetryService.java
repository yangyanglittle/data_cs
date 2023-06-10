package com.baidu.personalcode.crmdatads.service.retry.impl;

import com.baidu.personalcode.crmdatads.builder.DataBaseBuilder;
import com.baidu.personalcode.crmdatads.mapper.SynchronizationBaseMapper;
import com.baidu.personalcode.crmdatads.pojo.TableDbInfo;
import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;
import com.baidu.personalcode.crmdatads.pojo.datasync.MybatisDataSynchronizationPo;
import com.baidu.personalcode.crmdatads.pojo.datasync.thread.JdbcThreadErrorPo;
import com.baidu.personalcode.crmdatads.pojo.datasync.thread.MybatisThreadPo;
import com.baidu.personalcode.crmdatads.service.datasynchronization.databaseoperations.MybatisDataSynchronization;
import com.baidu.personalcode.crmdatads.service.retry.ExceptionRetryStrategyService;
import com.baidu.personalcode.crmdatads.util.AbstractJudge;
import com.baidu.personalcode.crmdatads.util.JsonUtil;
import com.baidu.personalcode.crmdatads.util.ThreadUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.baidu.personalcode.crmdatads.common.constants.DataSourceConstants.MASTER_SOURCE;
import static com.baidu.personalcode.crmdatads.common.constants.DataSynchronizationConstants.ASYNC_DATA_RETRY_FLAG;
import static com.baidu.personalcode.crmdatads.common.constants.ResponseConstants.RES_MSG_DATA_NULL_BASIC;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 17:12
 * @Version 1.0
 */
@Service("asyncDataRetryService")
@Slf4j
public class AsyncDataRetryService implements ExceptionRetryStrategyService {

    @Autowired
    private SynchronizationBaseMapper synchronizationBaseMapper;

    @Autowired
    private MybatisDataSynchronization mybatisDataSynchronization;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    /**
     * 重试分为两种
     * jdbc：
     *      存储的失败数据为readSql，把SQL拿出来直接从新读一遍，然后挨个从新执行一遍，调用mybatis的insert即可，
     *      这个时候不需要全跑了
     * mybatis:
     *      存储的失败数据为insertSql，把SQL拿出来直接从新插入，插不进去就稍后再试 or 打 error 日志
     * @param retryParam
     */
    @Override
    public void retry(String retryParam) {
        retryParam = retryParam.substring(1, retryParam.length() - 1).replace("\\", "");
        // 包含了insert，说明是mybatis跑的，这里直接插入，但是数据源要切换以下，切换到target数据源
        if (retryParam.toLowerCase().contains(ASYNC_DATA_RETRY_FLAG.toLowerCase())){

            // 解析的时候需要判断json里面是否嵌套了json数据，不然解析的时候有问题
            retryParam = this.couversionRetryParam(retryParam);
            MybatisThreadPo mybatisThreadPo = JsonUtil.deserialize(retryParam, MybatisThreadPo.class);
            DataBaseBuilder.refreshThreadDataSource(mybatisThreadPo.getTargetDataSourceName(),
                    dynamicRoutingDataSource);
            // 如果insert语句中有特殊字符，这里将无法插入，暂时需要自行手动处理下
            synchronizationBaseMapper.insert(mybatisThreadPo.getParam());
        } else {

            // jdbc异常重试
            JdbcThreadErrorPo jdbcThreadErrorPo = JsonUtil.deserialize(
                    retryParam,
                    JdbcThreadErrorPo.class);

            String originDataSource = DataBaseBuilder.getDataSourceName(
                    new TableDbInfo(jdbcThreadErrorPo.getConfigId()), true);

            // 是select，先调用select，使用origin的数据源
            DataBaseBuilder.refreshThreadDataSource(originDataSource, dynamicRoutingDataSource);
            List<Map<String, Object>> dataResultMaps;
            try {
                dataResultMaps = synchronizationBaseMapper.getOriginDataBySql(
                        jdbcThreadErrorPo.getReadSql());
            } catch (Exception e) {
                log.error(" AsyncDataRetryService getOriginDataBySql error", e);
                // todo 有些关键字段，如果没加 ` 会报错，需要转换一下，所以这里直接给全部字段加上  `
                String conversionReadSql = this.conversionReadSql(jdbcThreadErrorPo.getReadSql());
                log.info("conversion read sql :{}", conversionReadSql);
                dataResultMaps = synchronizationBaseMapper.getOriginDataBySql(
                        conversionReadSql);
            }
            AbstractJudge.isNull(dataResultMaps, RES_MSG_DATA_NULL_BASIC);

            DataBaseBuilder.clearThreadDataSource();

            ExecutorService executorService = ThreadUtil.getExecutorService(5, 20,
                    Integer.MAX_VALUE, "mybatis-batch-insert-into-table");

            // 使用target的数据源
            String targetDataSource = DataBaseBuilder.getDataSourceName(
                    new TableDbInfo(jdbcThreadErrorPo.getConfigId()), false);
            DataBaseBuilder.refreshThreadDataSource(targetDataSource, dynamicRoutingDataSource);

            List<CompletableFuture<Void>> insertTableFutures = new ArrayList<>();
            DataSynchronizationPoBase dataSynchronizationPoBase = new DataSynchronizationPoBase();
            MybatisDataSynchronizationPo mybatisDataSynchronizationPo = new MybatisDataSynchronizationPo();
            mybatisDataSynchronizationPo.setDataResultMaps(dataResultMaps);
            mybatisDataSynchronizationPo.setInsertTableFutures(insertTableFutures);
            mybatisDataSynchronizationPo.setTargetDataSourceName(targetDataSource);
            mybatisDataSynchronizationPo.setTableName(jdbcThreadErrorPo.getTableName());
            dataSynchronizationPoBase.setMybatisDataSynchronizationPo(mybatisDataSynchronizationPo);

            // 使用target数据源，调用mybatis进行数据的写入
            mybatisDataSynchronization.doMybatisDataSynchronization(dataSynchronizationPoBase, executorService);
            insertTableFutures.forEach(item -> CompletableFuture.allOf(item).join());

            DataBaseBuilder.refreshThreadDataSource(MASTER_SOURCE, dynamicRoutingDataSource);
        }
    }

    /**
     * 对json嵌套的json数据做特殊处理
     * @param retryParam
     * @return
     */
    private String couversionRetryParam(String retryParam){
        Map<String, List<Integer>> firstJsonFlag = getKeyIndex(retryParam, "{");
        Map<String, List<Integer>> lastJsonFlag = getKeyIndex(retryParam, "}");
        if (firstJsonFlag.get("{").size() > 1){
            // 第一个lastJsonFlag的 { 出现的索引和最后一个lastJsonFlag的 } 是固定的，直接丢了就好
            firstJsonFlag.get("{").remove(0);
            lastJsonFlag.get("}").remove(lastJsonFlag.get("}").size() - 1);
            if (firstJsonFlag.size() != lastJsonFlag.size()){
                throw new RuntimeException("两边json数据不一致，请开发人员核实");
            }
            for (int i = 0; i < firstJsonFlag.get("{").size(); i++){
                int firstIndex = firstJsonFlag.get("{").get(i);
                int lastIndex = lastJsonFlag.get("}").get(i);
                String retrySubStr = retryParam.substring(firstIndex - 1, lastIndex);
                if (retrySubStr.contains("\"")){
                    String retrySubReplaceStr = retrySubStr.replace("\"", "\\\"");
                    retryParam = retryParam.replace(retrySubStr, retrySubReplaceStr);
                }
            }
        }
        return retryParam;
    }

    /**
     * 对读SQL进行字段的转换
     * @param readSql
     * @return
     */
    private String conversionReadSql(String readSql){
        String lastStr = readSql.substring(readSql.indexOf("FROM"));
        String[] splitStr = readSql.substring(6, readSql.indexOf("FROM")).split(",");
        StringBuilder stringBuilder = new StringBuilder("SELECT ");
        for (int i = 0; i < splitStr.length; i++){
            if (i == splitStr.length - 1){
                stringBuilder.append("`").append(splitStr[i].trim()).append("`");
            } else {
                stringBuilder.append("`").append(splitStr[i].trim()).append("`").append(",");
            }
        }
        stringBuilder.append(" ").append(lastStr);
        return stringBuilder.toString();
    }

    private Map<String, List<Integer>> getKeyIndex(String str, String key) {
        //定义变量。记录每一次找到的key的位置。
        int index = 0;
        //定义变量，记录出现的次数。
        int count = 0;
        Map<String, List<Integer>> keyMap = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        //定义循环，只要索引到的位置不是-1，就继续查找
        while((index = str.indexOf(key, index))!=-1){
            //每循环一次，就要明确下一次起始的位置
            index = index + key.length();
            list.add(index);
            //每查找一次，count自增一次
            count++;
        }
        keyMap.put(key, list);
        return keyMap;
    }

}
