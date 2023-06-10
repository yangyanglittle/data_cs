package org.kulorido.service.datasynchronization.thread;

import org.kulorido.pojo.datasync.thread.DataThreadPo;

/**
 * @Author kulorido
 * @Date 2099/12/31 10:58
 * @Version 1.0
 */
public interface DataThreadService {

    void dataThreadInvoke(DataThreadPo dataThreadPo) throws Exception;
}
