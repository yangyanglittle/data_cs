package org.kulorido.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.kulorido.common.constants.ResponseConstants;
import org.kulorido.request.MysqlConfigRequest;
import org.kulorido.request.MysqlDataSourceRequest;
import org.kulorido.request.SyncMysqlDataRequest;
import org.kulorido.service.MysqlTableConfigService;
import org.kulorido.service.MysqlTableDataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @package org.kulorido.controller
 * @Author kulorido
 * @Data 2023/6/9 13:32
 */
@RestController
@RequestMapping("/data/config")
@Api(tags = {"表配置"})
public class MysqlDataConfigController {

    @Autowired
    private MysqlTableConfigService mysqlTableConfigService;

    @PostMapping("/create/config")
    @ApiOperation(value = "新建表模型配置")
    public String createConfig(@RequestBody MysqlConfigRequest mysqlConfigRequest) {
        return mysqlTableConfigService.createConfig(mysqlConfigRequest);
    }

    @PostMapping("/create/dataSource")
    @ApiOperation(value = "新增数据库配置")
    public String createDataSource(@RequestBody MysqlDataSourceRequest mysqlDataSourceRequest) {
        mysqlTableConfigService.createDataSource(mysqlDataSourceRequest);
        return ResponseConstants.RES_MSG_PROCESS_SUCCESS;
    }
}
