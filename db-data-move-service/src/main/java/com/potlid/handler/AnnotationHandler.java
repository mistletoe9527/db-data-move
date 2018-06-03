package com.potlid.handler;


import com.potlid.common.define.job.TransferDescribe;

/**
 * Created by styb on 2018/4/3.
 * 扩展接口
 */

public interface AnnotationHandler {

    public TransferDescribe handleAnnotation(Object bean, TransferDescribe transferJob) throws Exception;
}
