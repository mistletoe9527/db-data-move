package com.potlid.manager;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by styb on 2018/4/2.
 */
public class DataSourceManager {

    private static Map<String,DataSource> dataSourceMap=new HashMap<String, DataSource>();

    public static void loadDataSource(ApplicationContext applicationContext){
        if(CollectionUtils.isEmpty(dataSourceMap)){
            String[] dataSourceNames=applicationContext.getBeanNamesForType(DataSource.class);
            if(ArrayUtils.isNotEmpty(dataSourceNames)) {
                for(String beanName:dataSourceNames){
                    dataSourceMap.put(beanName, (DataSource) applicationContext.getBean(beanName));
                }
            }
        }
    }

    public static Connection getConnection(String name) throws Exception{
        return dataSourceMap.get(name).getConnection();
    }

    public static Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }

    public static void setDataSourceMap(Map<String, DataSource> dataSourceMap) {
        DataSourceManager.dataSourceMap = dataSourceMap;
    }
}
