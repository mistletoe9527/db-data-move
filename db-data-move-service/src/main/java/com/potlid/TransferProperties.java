package com.potlid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by styb on 2018/4/17.
 */
public class TransferProperties {

    private static TransferProperties transferProperties=new TransferProperties();

    private Map<String,Object> properties=new HashMap<String, Object>();

    public static final String THREAD_COUNT="threadCount";
    public static final String EXECUTE_COUNT="executeCount";
    private final Lock lock=new ReentrantLock();
    private TransferProperties(){}
    public static TransferProperties getInstance(){
        return transferProperties;
    }
    public void load(){
        if(CollectionUtils.isEmpty(properties)){
            try{
                lock.lock();
                if(CollectionUtils.isEmpty(properties)) {
                    ResourceBundle bundle = PropertyResourceBundle.getBundle("transfer");
                    if(StringUtils.isNotBlank(bundle.getString("threadCount"))){
                        properties.put(THREAD_COUNT,Integer.parseInt(bundle.getString("threadCount")));
                    }
                    if(StringUtils.isNotBlank(bundle.getString("executeCount"))){
                        properties.put(EXECUTE_COUNT,Integer.parseInt(bundle.getString("executeCount")));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }

    }

    public Map<String, Object> getProperties() {
        load();
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
