package org.kulorido.init;

/**
 * @Author kulorido
 * @Date 2099/12/31 18:37
 * @Version 1.0
 */


import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.common.cache.TableCacheData;
import org.kulorido.mapper.TableConfigMapper;
import org.kulorido.mapper.TableDbInfoMapper;
import org.kulorido.model.TableConfig;
import org.kulorido.model.TableDbInfo;
import org.kulorido.service.TableSyncService;
import org.kulorido.vo.TableMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InitTableCacheData implements ApplicationListener {

    /**
     * 幂等
     */
    private static boolean isStart = false;

    @Autowired
    private TableSyncService tableSyncService;

    @Autowired
    private TableConfigMapper tableConfigMapper;

    @Autowired
    private TableDbInfoMapper tableDbInfoMapper;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        try {
            if (!InitTableCacheData.isStart) {
                InitTableCacheData.isStart = true;

                List<TableConfig> tableConfigs = tableConfigMapper.queryAllConfig("2");

                List<TableDbInfo> tableDbInfos = tableDbInfoMapper.queryAll();

                Map<String, List<TableDbInfo>> tableDbInfoMaps = tableDbInfos.stream()
                        .collect(Collectors.groupingBy(TableDbInfo::getConfigId));

                Map<String, List<TableMenuVo>> tableColumsMaps = new HashMap<>(16);

                tableConfigs.forEach(tableConfig -> {
                    List<TableMenuVo> menuVos = new ArrayList<>();
                    List<String> tableColumns = tableSyncService.getMasterTabList(tableConfig.getId());
                    int id = -1;
                    for (String item : tableColumns) {
                        id++;
                        TableMenuVo vo = new TableMenuVo();
                        vo.setTitle(item);
                        vo.setId(""+id);
                        menuVos.add(vo);
                    }
                    tableColumsMaps.put(tableConfig.getId(), menuVos);
                });

                TableCacheData.setTableDbConfigMap(tableDbInfoMaps);

                TableCacheData.setMenuvoMap(tableColumsMaps);
            }
        } catch (Exception e) {
            log.error("onApplicationEvent error", e);
        }
    }
}
