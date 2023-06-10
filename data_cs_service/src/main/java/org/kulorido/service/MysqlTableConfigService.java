package org.kulorido.service;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.exception.DataSynchronizationDataSourceException;
import org.kulorido.mapper.TableConfigMapper;
import org.kulorido.mapper.TableDbInfoMapper;
import org.kulorido.model.TableConfig;
import org.kulorido.model.TableDbInfo;
import org.kulorido.request.BaseOperatorRequest;
import org.kulorido.request.MysqlConfigRequest;
import org.kulorido.request.MysqlDataSourceRequest;
import org.kulorido.util.DataSynchronizationJudge;
import org.kulorido.util.DataSynchronizationReflectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * DB相关配置
 */
@Slf4j
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MysqlTableConfigService {

    @Autowired
    private TableDbInfoMapper tableDbInfoMapper;

    @Autowired
    private TableConfigMapper tableConfigMapper;

    public String createConfig(MysqlConfigRequest mysqlConfigRequest){
        DataSynchronizationJudge.isAnyParamEmpty(mysqlConfigRequest, mysqlConfigRequest.getConfigType(),
                mysqlConfigRequest.getName(), mysqlConfigRequest.getRemark());
        TableConfig tableConfig = new TableConfig();
        BeanUtils.copyProperties(mysqlConfigRequest, tableConfig);
        tableConfig.setId(UUID.randomUUID().toString());
        DataSynchronizationReflectUtils.initCreateAndUpdate(tableConfig, new BaseOperatorRequest(),
                true);
        tableConfigMapper.insert(tableConfig);
        return tableConfig.getId();
    }

    public void createDataSource(MysqlDataSourceRequest mysqlDataSourceRequest){
        String [] dbHostArray = mysqlDataSourceRequest.getDbHost().split(":");
        if (dbHostArray.length < 2){
            throw new DataSynchronizationDataSourceException("host输入格式有误，请确认并调整");
        }
        DataSynchronizationJudge.isNull(mysqlDataSourceRequest.getConfigId(),"配置表外键ID不可为空");
        DataSynchronizationJudge.isNull(tableConfigMapper.queryConfigById(mysqlDataSourceRequest.getConfigId()),
                "配置表外键ID输入有误，请确认并调整");
        TableDbInfo tableDbInfo = new TableDbInfo();
        BeanUtils.copyProperties(mysqlDataSourceRequest, tableDbInfo);
        tableDbInfo.setId(UUID.randomUUID().toString());
        tableDbInfoMapper.insert(tableDbInfo);
    }

    /** 根据配置ID查询数据库列表 */
    public List<TableDbInfo> queryByConfigId(String configId){
        return tableDbInfoMapper.queryByConfigId(configId, null);
    }
}
