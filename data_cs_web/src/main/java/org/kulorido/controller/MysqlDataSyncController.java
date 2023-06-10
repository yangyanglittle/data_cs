package org.kulorido.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.kulorido.common.constants.ResponseConstants;
import org.kulorido.service.TableDataSyncService;
import org.kulorido.service.TableSyncService;
import org.kulorido.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/mysql")
@Api(tags = {"Mysql表结构同步"})
public class MysqlController {

    @Autowired
    private TableSyncService tableSyncService;

    @Autowired
    private TableDataSyncService tableDataSyncService;


    @RequestMapping(value = "/syncTabData", method = RequestMethod.POST)
    @ApiOperation(value = "数据同步")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "configId",value = "配置ID"),
            @ApiImplicitParam(name = "tableName",value = "数据库表名(单个/多个逗号隔开)"),
            @ApiImplicitParam(name = "size",value = "每次读写条数")
    })
    public String syncTabData(String configId, String tableName, Integer size) {
        if (StringUtils.isBlank(configId) || StringUtils.isBlank(tableName)){
            return ResponseConstants.RES_MSG_PROCESS_ERROR;
        }
        tableDataSyncService.syncTableData(configId, tableName, size);
        return ResponseConstants.RES_MSG_PROCESS_SUCCESS;
    }
}
