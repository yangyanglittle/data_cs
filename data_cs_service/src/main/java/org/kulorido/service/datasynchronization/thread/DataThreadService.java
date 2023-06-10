package com.baidu.personalcode.crmdatads.service.datasynchronization.thread;

import com.baidu.personalcode.crmdatads.pojo.datasync.thread.DataThreadPo;

/**
 * @Author v_xueweidong
 * @Date 2022/9/27 10:58
 * @Version 1.0
 */
public interface DataThreadService {

    void dataThreadInvoke(DataThreadPo dataThreadPo) throws Exception;
}
