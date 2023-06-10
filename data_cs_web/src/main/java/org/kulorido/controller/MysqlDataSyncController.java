package org.kulorido.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.kulorido.common.constants.ResponseConstants;
import org.kulorido.request.SyncMysqlDataRequest;
import org.kulorido.service.MysqlTableDataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据同步
 */
@RestController
@RequestMapping("/data/cs")
@Api(tags = {"数据同步"})
public class MysqlDataSyncController {

    @Autowired
    private MysqlTableDataSyncService mysqlTableDataSyncService;


    @PostMapping("/sync/mysql/data")
    @ApiOperation(value = "数据同步")
    public String syncTabData(@RequestBody SyncMysqlDataRequest syncMysqlDataRequest) {
        mysqlTableDataSyncService.syncTableData(syncMysqlDataRequest);
        return ResponseConstants.RES_MSG_PROCESS_SUCCESS;
    }
}
