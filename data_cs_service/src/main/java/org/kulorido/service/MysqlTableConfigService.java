package org.kulorido.service;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.mapper.TableDbInfoMapper;
import org.kulorido.model.TableDbInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据库信息
 */
@Slf4j
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TableDbInfoService {

    @Autowired
    private TableDbInfoMapper tableDbInfoDao;


    /** 根据配置ID查询数据库列表 */
    public List<TableDbInfo> queryByConfigId(String configId){
        return tableDbInfoDao.queryByConfigId(configId, null);
    }
}
