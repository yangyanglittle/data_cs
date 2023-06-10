package org.kulorido.service.datasynchronization.thread;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.builder.DataBaseBuilder;
import org.kulorido.common.constants.DataSourceConstants;
import org.kulorido.pojo.datasync.thread.DataThreadPo;
import org.kulorido.pojo.datasync.thread.JdbcThreadPo;
import org.kulorido.pojo.datasync.thread.MybatisThreadPo;
import org.kulorido.util.DataEmptyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

import static org.kulorido.builder.DataBaseBuilder.clearThreadDataSource;
import static org.kulorido.common.constants.QuantitativeRestrictionsConstants.THREAD_NUM_SYNC_FLAG;

/**
 * @Author kulorido
 * @Date 2099/12/31 10:47
 * @Version 1.0
 */
@Slf4j
public abstract class DataAbstractThreadService implements DataThreadService {

    @Autowired
    DynamicRoutingDataSource dynamicRoutingDataSource;

    @Override
    public void dataThreadInvoke(DataThreadPo dataThreadPo) throws Exception {
        if (DataEmptyUtil.isNotEmpty(dataThreadPo.getMybatisThreadPo())){
            mybatisAsynchronousExecution(dataThreadPo.getMybatisThreadPo());
        } else {
            jdbcAsynchronousExecution(dataThreadPo.getJdbcThreadPo());
        }
    }

    public void jdbcAsynchronousExecution(JdbcThreadPo jdbcThreadPo) throws SQLException{
        if (jdbcThreadPo.getTotalCount() > THREAD_NUM_SYNC_FLAG &&
                !jdbcThreadPo.isQueue()){

            // 并发批量刷数据
            asynchronousBatchExecution(jdbcThreadPo);
        } else {
            // 开并发写数据 && 跑之前跑之后不操作队列表
            asynchronousExecution(jdbcThreadPo);
        }
    }

    abstract void asynchronousBatchExecution(JdbcThreadPo jdbcThreadPo) throws SQLException;

    abstract void asynchronousExecution(JdbcThreadPo jdbcThreadPo) throws SQLException;

    /**
     * 并发的情况下，dynamicRoutingDataSource切换数据源的时候有时候会出现问题
     * 加了volatile 可以保证CPU不会重排序执行和可见性，但是并不能保证原子性，也不能保证线程的安全
     * 升级版本不使用3.1的也不能保证线程安全，这个文章是描述升级版本的
     * https://zhuanlan.zhihu.com/p/540651080
     *
     * 并发的时候，ThreadLocal由于是线程唯一的，会得到为空的情况，
     * 然后获取数据源执行的时候，很有可能获取的是内存中共享的dataSource
     * 例如我执行了target数据源，然后切到master执行，这个时候会出现还在target的情况
     * 因为DynamicDataSourceContextHolder中的LOOKUP_KEY_HOLDER是ThreadLocal，线程唯一的
     * 多线程的时候又开辟了一个线程，里面啥也没，注定为空
     * 详情见：https://www.jianshu.com/p/a7fb23a847a4
     *
     * 解决办法：
     *      升级版本+切换数据源的时候，要自定义DynamicDataSourceContextHolder中的LOOKUP_KEY_HOLDER
     *
     * @param mybatisThreadPo
     */
    public void mybatisAsynchronousExecution(MybatisThreadPo mybatisThreadPo){
        try{
            mybatisThreadPo.setRetryFlag("mybatis");
            refreshThreadDataSource(mybatisThreadPo.getTargetDataSourceName());
            mybatisAsynchronousExecutionInvoke(mybatisThreadPo);
        }catch (Exception e){
            log.error("synchronizationBaseMapper insert error", e);
            try {
                refreshThreadDataSource(DataSourceConstants.MASTER_SOURCE);
                mybatisAsynchronousExecutionError(e, mybatisThreadPo);
            } finally {
                clearThreadDataSource();
            }
        } finally {
            clearThreadDataSource();
        }
    }

    private void refreshThreadDataSource(String dataSourceName) {
        DataBaseBuilder.refreshThreadDataSource(dataSourceName, dynamicRoutingDataSource);
    }

    abstract void mybatisAsynchronousExecutionInvoke(MybatisThreadPo mybatisThreadPo);

    abstract void mybatisAsynchronousExecutionError(Exception e, MybatisThreadPo mybatisThreadPo);
}
