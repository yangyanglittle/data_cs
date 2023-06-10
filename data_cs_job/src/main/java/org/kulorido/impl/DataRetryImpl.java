package org.kulorido.impl;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.base.JobAbstractService;
import org.kulorido.enums.DataRetryEnum;
import org.kulorido.mapper.DataRetryMapper;
import org.kulorido.model.DataRetryModel;
import org.kulorido.pojo.work.JobPo;
import org.kulorido.service.DataRetryAbstractService;
import org.kulorido.service.retry.DataRetryInterface;
import org.kulorido.util.DataEmptyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(value = "dataRetryJob")
@Slf4j
public class DataRetryImpl extends DataRetryAbstractService {

    @Autowired
    private DataRetryMapper dataRetryMapper;

    @Override
    public void worker(JobPo jobPo) {
        Integer autoId = 0;
        do {
            List<DataRetryModel> exceptionRetries = super.getDataRetryModel(200, autoId);
            if (DataEmptyUtil.isEmpty(exceptionRetries)) {
                break;
            }
            autoId = exceptionRetries.get(exceptionRetries.size() - 1).getAutoId();
            for (DataRetryModel item : exceptionRetries) {
                try {
                    super.retryException(item);
                } catch (Exception e) {
                    log.error("retryException error", e);
                }
            }
        } while(true);
    }
}
