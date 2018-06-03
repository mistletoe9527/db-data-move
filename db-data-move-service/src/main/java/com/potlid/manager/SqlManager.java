package com.potlid.manager;

import com.potlid.common.define.job.ExecuteSqlJob;
import com.potlid.common.define.job.TransferDescribe;
import com.potlid.common.define.job.TransferExchangeJob;
import com.potlid.common.util.StringConvertUtil;
import com.potlid.handler.ConditionDataHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by styb on 2018/4/4.
 */
public class SqlManager {

    /**
     * 生成selectsql
     * @param transferJob
     * @param tableName
     * @param conditionDataMap
     * @param executeSqlJob
     * @throws Exception
     */
    public static void generateSelectSql(TransferDescribe transferJob,String tableName,Map<String,Object> conditionDataMap,ExecuteSqlJob executeSqlJob) throws Exception{
        StringBuilder selectSql=new StringBuilder();
        selectSql.append("select ");
        for(String selectField:transferJob.getSelectFields()) {
            selectSql.append(","+selectField);
        }
        selectSql=new StringBuilder(selectSql.toString().replaceFirst(",",""));
        selectSql.append(" from "+ tableName);
        StringBuilder selectMinIdSql=new StringBuilder("select id from "+tableName);
        StringBuilder selectMaxIdSql=new StringBuilder("select id from "+tableName);
        StringBuilder checkCountSql=new StringBuilder("select count(1) from "+tableName);
        if(!CollectionUtils.isEmpty(transferJob.getConditionFields())) {
            selectSql.append(" where ");
            selectMinIdSql.append(" where ");
            selectMaxIdSql.append(" where ");
            checkCountSql.append(" where ");
            boolean andFlag = false;
            for (String conditionField : transferJob.getConditionFields()) {
                if (andFlag) {
                    selectSql.append(" and ");
                    selectMaxIdSql.append(" and ");
                    selectMinIdSql.append(" and ");
                    checkCountSql.append(" and ");
                } else {
                    andFlag = true;
                }
                selectSql.append(conditionField + " " + transferJob.getFieldOpratorMap().get(conditionField));
                selectMaxIdSql.append(conditionField + " " + transferJob.getFieldOpratorMap().get(conditionField));
                selectMinIdSql.append(conditionField + " " + transferJob.getFieldOpratorMap().get(conditionField));
                checkCountSql.append(conditionField + " " + transferJob.getFieldOpratorMap().get(conditionField));
                Object value = conditionDataMap.get(conditionField);
                if(value == null){
                    selectSql.append(" " + value);
                    selectMaxIdSql.append(" " + value);
                    selectMinIdSql.append(" " + value);
                    checkCountSql.append(" " + value);
                }else{
                    if (value instanceof String) {
                        selectSql.append("'" + value + "'");
                        selectMaxIdSql.append("'" + value + "'");
                        selectMinIdSql.append("'" + value + "'");
                        checkCountSql.append("'" + value + "'");
                    } else if (value instanceof Date) {
                        selectSql.append("'" + DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss") + "'");
                        selectMaxIdSql.append("'" + DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss") + "'");
                        selectMinIdSql.append("'" + DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss") + "'");
                        checkCountSql.append("'" + DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss") + "'");
                    } else if (value instanceof Collection && "in".equals(transferJob.getFieldOpratorMap().get(conditionField).toLowerCase())) {
                        selectSql.append("(");
                        selectMaxIdSql.append("(");
                        selectMinIdSql.append("(");
                        checkCountSql.append("(");
                        boolean dh = false;
                            for (Object v :(List)value) {
                            if (!dh) {
                                selectSql.append("'" + v + "'");
                                selectMaxIdSql.append("'" + v + "'");
                                selectMinIdSql.append("'" + v + "'");
                                checkCountSql.append("'" + v + "'");
                                dh = true;
                            } else {
                                selectSql.append(",'" + v + "'");
                                selectMaxIdSql.append(",'" + v + "'");
                                selectMinIdSql.append(",'" + v + "'");
                                checkCountSql.append(",'" + v + "'");
                            }

                        }
                        selectSql.append(")");
                        selectMaxIdSql.append(")");
                        selectMinIdSql.append(")");
                        checkCountSql.append(")");
                    } else {
                        selectSql.append("'" + value + "'");
                        selectMaxIdSql.append("'" + value + "'");
                        selectMinIdSql.append("'" + value + "'");
                        checkCountSql.append("'" + value + "'");
                    }
                }


            }

        }
        selectMaxIdSql.append(" order by id desc limit 1;");
        selectMinIdSql.append(" order by id limit 1;");
//        for(;;){
//            System.out.println(selectMaxIdSql.toString());
//            System.out.println(selectMinIdSql.toString());
//        }
        List<List<Object>> v1=executeSelectSql(transferJob.getBeforeDataSource(),selectMaxIdSql.toString(),1);
        if(!CollectionUtils.isEmpty(v1)){
            if(v1.get(0).get(0) instanceof Long){
                long id=(Long)v1.get(0).get(0);
                executeSqlJob.setMaxId(id);
            }else{
                Integer id=(Integer)v1.get(0).get(0);
                executeSqlJob.setMaxId(id);
            }

        }
        List<List<Object>> v2=executeSelectSql(transferJob.getBeforeDataSource(),selectMinIdSql.toString(),1);
        if(!CollectionUtils.isEmpty(v2)){
            if(v2.get(0).get(0) instanceof Long){
                long id=(Long)v2.get(0).get(0);
                executeSqlJob.setMinId(id);
            }else{
                Integer id=(Integer)v2.get(0).get(0);
                executeSqlJob.setMinId(id);
            }

        }
        executeSqlJob.setMaxIdSql(selectMaxIdSql.toString());
        executeSqlJob.setSelectSql(selectSql.toString());
    }

    /**
     * 生成insert sql
     * @param transferExchangeJob
     * @param executeSqlJob
     * @throws Exception
     */
    public static void generateInsertSql(TransferExchangeJob transferExchangeJob,ExecuteSqlJob executeSqlJob) throws Exception{
        StringBuilder insertSql=new StringBuilder();
        insertSql.append("insert into "+executeSqlJob.getAfterTable()+" (");
        for(String selectField:transferExchangeJob.getInsertFieldMap().keySet()) {
            insertSql.append(","+selectField);
        }
        insertSql.append(")");
        insertSql=new StringBuilder(insertSql.toString().replaceFirst(",",""));
        insertSql.append(" values ");
        if(!CollectionUtils.isEmpty(executeSqlJob.getSelectDataList())){

            if(!CollectionUtils.isEmpty(transferExchangeJob.getUniqueFields()) && !CollectionUtils.isEmpty(executeSqlJob.getSelectDataList())){
                StringBuilder checkSql=new StringBuilder();
                checkSql.append("select id from "+executeSqlJob.getAfterTable());
                Map<String,Object> data=executeSqlJob.getSelectDataList().get(0);
                checkSql.append(" where ");
                boolean andFlag=true;
                for(String uinqueField:transferExchangeJob.getUniqueFields()){
                    if(andFlag){
                        Object value=data.get(uinqueField);
                        if(value instanceof Date){
                            value=DateFormatUtils.format((Date) value,"yyyy-MM-dd HH:mm:ss");
                        }
                        if(value==null){
                            checkSql.append(uinqueField+" is null ");
                        }else{
                            checkSql.append(uinqueField+"="+"'"+ StringConvertUtil.escapeExprSpecialWord(String.valueOf(value))+"'");
                        }

                        andFlag=false;
                    }else{
                        Object value=data.get(uinqueField);
                        if(value instanceof Date){
                            value=DateFormatUtils.format((Date) value,"yyyy-MM-dd HH:mm:ss");
                        }
                        if(value==null){
                            checkSql.append(" and "+uinqueField+" is null ");
                        }else{
                            checkSql.append(" and "+uinqueField+"="+"'"+StringConvertUtil.escapeExprSpecialWord(String.valueOf(value))+"'");
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(executeSelectSql(transferExchangeJob.getAfterDataSource(), checkSql.toString(), 1))){
                    executeSqlJob.setInsertSql(null);
                    return;
                }

            }

            boolean insertFlag=false;
            for(Map<String,Object> data:executeSqlJob.getSelectDataList()){
                if(insertFlag){
                    insertSql.append(",");
                }else{
                    insertFlag=true;
                }
                insertSql.append("(");
                boolean kh=false;
                for(String selectField:transferExchangeJob.getInsertFieldMap().keySet()){
                    if(kh){
                        insertSql.append(",");
                    }else{
                        kh=true;
                    }
                    Object value=data.get(selectField);
                    if(value == null){
                        value=transferExchangeJob.getInsertValue().get(selectField);
                    }
                    if(value == null){
                        insertSql.append("null");
                    }else{
                        if(value instanceof Date){
                            insertSql.append("'"+DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss")+"'");
                        }else{
                            insertSql.append("'"+StringConvertUtil.escapeExprSpecialWord(String.valueOf(value))+"'");
                        }
                    }


                }
                insertSql.append(")");

            }
            insertSql.append(";");
            executeSqlJob.setInsertSql(insertSql.toString());
        }else{
            executeSqlJob.setInsertSql(null);
        }

    }

    public static void main(String[] args) throws Exception{
//        TransferJob transferJob=new TransferJob();
//        transferJob.setBeforeTableName("u");
//        List<String> s=new ArrayList<String>();
//        s.add("user_name");
//        List<String> ss=new ArrayList<String>();
//        ss.add("user_name");
//        ss.add("haha");
//        Map<String,String> op=new HashMap<String, String>();
//        op.put("user_name","=");
//        op.put("haha","=");
//        Map<String,ConditionData> map=new HashMap<String, ConditionData>();
//        List<String> l=new ArrayList<String>();
//        l.add("df");
//        l.add("dfdf");
//        ConditionData conditionData=new ConditionData();
//        conditionData.setValue(124);
//        map.put("user_name",conditionData);
//        ConditionData conditionData2=new ConditionData();
//        conditionData2.setValue(1242);
//        map.put("haha",conditionData2);
//        transferJob.setFieldOpratorMap(op);
//        transferJob.setConditionFields(ss);
//        transferJob.setConditionDataMap(map);
//        transferJob.setSelectFields(s);
        ExecuteSqlJob executeSqlJob=new ExecuteSqlJob();
        List<String> insertFields=new ArrayList<String>();
        insertFields.add("user_name");
        insertFields.add("created");
//        executeSqlJob.setInsertFields(insertFields);
        List<Map<String,Object>> data=new ArrayList<Map<String, Object>>();
        Map<String,Object> m=new HashMap<String, Object>();
        m.put("user_name","hahhaha");
        m.put("created",new Date());
        Map<String,Object> m2=new HashMap<String, Object>();
        m2.put("user_name","hahhaha");
        m2.put("created",new Date());
        data.add(m);
        data.add(m2);
        executeSqlJob.setSelectDataList(data);
//        System.out.println(generateInsertSql(insertFields,executeSqlJob,"u"));
    }

    /**
     *
     * @param transferExchangeJob
     * @param executeSqlJob
     * @throws Exception
     */
    public static void executeSelectSql(TransferExchangeJob transferExchangeJob,ExecuteSqlJob executeSqlJob) throws Exception{

        PreparedStatement ps = null;
        ResultSet rs=null;
        Connection connection=DataSourceManager.getConnection(transferExchangeJob.getBeforeDataSource());
        try {
            ps = connection
                    .prepareStatement(executeSqlJob.getSelectSql());
            rs=ps.executeQuery();
            List<Map<String,Object>> selectDataList=new ArrayList<Map<String, Object>>();
            while(rs.next()){
                Map<String,Object> map=new HashMap<String, Object>();
                for(String key:transferExchangeJob.getInsertFieldMap().keySet()){
                    if(transferExchangeJob.getInsertFieldMap().get(key).getSelectField() !=null){
                        Object value=rs.getObject(transferExchangeJob.getInsertFieldMap().get(key).getSelectField());
                        if(transferExchangeJob.getBean() instanceof ConditionDataHandler)
                            map.put(key,((ConditionDataHandler)transferExchangeJob.getBean()).convert(transferExchangeJob.getInsertFieldMap().get(key).getField(),value));
                        else
                            map.put(key,value);
                    }else{
                        Object value=transferExchangeJob.getInsertValue().get(key);
                        if(transferExchangeJob.getBean() instanceof ConditionDataHandler)
                            map.put(key,((ConditionDataHandler)transferExchangeJob.getBean()).convert(transferExchangeJob.getInsertFieldMap().get(key).getField(),value));
                        else
                            map.put(key,value);
                    }

                }
                selectDataList.add(map);
            }
            executeSqlJob.setSelectDataList(selectDataList);
        } catch (Exception e) {
            e.printStackTrace();
           throw e;
        } finally {
            try {
                if(rs!=null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    /**
     * 执行select sql
     * @param dataSourceName
     * @param sql
     * @param filedSize
     * @return
     * @throws Exception
     */
    public static List<List<Object>> executeSelectSql(String dataSourceName,String sql,int filedSize) throws Exception{

        PreparedStatement ps = null;
        ResultSet rs=null;
        Connection connection=DataSourceManager.getConnection(dataSourceName);
        try {

            ps = connection
                    .prepareStatement(sql);
            rs=ps.executeQuery();
            List<List<Object>> selectDataList=new ArrayList<List<Object>>();
            while(rs.next()){
                List<Object> l=new ArrayList<Object>();
                for(int i=0;i<filedSize;i++){
                    Object value=rs.getObject(i+1);
                    l.add(value);
                }
                selectDataList.add(l);
            }
            return selectDataList;
        } catch (Exception e) {
            System.out.println("出错的sql==="+sql);
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if(rs!=null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                throw e;
            }
        }
    }


    /**
     * 执行insert sql
     * @param transferExchangeJob
     * @param executeSqlJob
     * @throws Exception
     */
    public static void executeInsertSql(TransferExchangeJob transferExchangeJob,ExecuteSqlJob executeSqlJob) throws Exception{
        if(StringUtils.isNotEmpty(executeSqlJob.getInsertSql())){
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = DataSourceManager.getConnection(transferExchangeJob.getAfterDataSource());
                conn.setAutoCommit(false);
                ps = conn
                        .prepareStatement(executeSqlJob.getInsertSql());
                ps.executeUpdate();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                System.out.println("出错的sql==="+executeSqlJob.getInsertSql());
                e.printStackTrace();
                throw e;
            } finally {
                try {
                    if (ps != null)
                        ps.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {
                    throw e;
                }
            }
        }
    }

    /**
     * 执行删除数据sql
     * @param transferExchangeJob
     * @param executeSqlJob
     * @throws Exception
     */
    public static void executeDeleteSql(TransferExchangeJob transferExchangeJob,ExecuteSqlJob executeSqlJob) throws Exception{
        if(StringUtils.isNotEmpty(executeSqlJob.getSelectSql()) && StringUtils.isNotEmpty(executeSqlJob.getInsertSql())){
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                String selectSql=executeSqlJob.getSelectSql();
                if(StringUtils.isNotEmpty(selectSql)){
                    conn = DataSourceManager.getConnection(transferExchangeJob.getBeforeDataSource());
                    ps = conn
                            .prepareStatement(selectSql.replace(selectSql.substring(0, selectSql.indexOf("from")), "delete "));
                    ps.executeUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                try {
                    if (ps != null)
                        ps.close();
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {
                    throw e;
                }
            }

        }

    }

    /**
     * 转化id 根据id操作
     * @param executeSqlJob
     * @param batchCount
     * @param replaceStr
     * @return
     * @throws Exception
     */
    public static String exchangeSelectSql(ExecuteSqlJob executeSqlJob,int batchCount,String replaceStr) throws Exception{
        if(StringUtils.isNotEmpty(replaceStr)){
            executeSqlJob.setSelectSql(executeSqlJob.getSelectSql().replaceFirst(replaceStr, ""));
        }
        if(executeSqlJob.getSelectSql().contains("where")){
            replaceStr=" and id<" + String.valueOf(executeSqlJob.getMinId() + batchCount)+" and id>="+executeSqlJob.getMinId();
            executeSqlJob.setSelectSql(executeSqlJob.getSelectSql() + replaceStr);
        }else{
            replaceStr= " where id<" + String.valueOf(executeSqlJob.getMinId() + batchCount) + " and id>=" + executeSqlJob.getMinId();
            executeSqlJob.setSelectSql(executeSqlJob.getSelectSql() + replaceStr);
        }
        executeSqlJob.setMinId(executeSqlJob.getMinId() + batchCount);

        return replaceStr;
    }

    /**
     * 任务需要执行的得到最大id
     * @param executeSqlJob
     * @param transferExchangeJob
     * @throws Exception
     */
    public static void resetMaxId(ExecuteSqlJob executeSqlJob,TransferExchangeJob transferExchangeJob) throws Exception{

        List<List<Object>> v1=executeSelectSql(transferExchangeJob.getBeforeDataSource(), executeSqlJob.getMaxIdSql().toString(), 1);
        if(!CollectionUtils.isEmpty(v1)){
            if(v1.get(0).get(0) instanceof Long){
                long id=(Long)v1.get(0).get(0);
                executeSqlJob.setMaxId(id);
            }else{
                Integer id=(Integer)v1.get(0).get(0);
                executeSqlJob.setMaxId(id);
            }

        }
    }




}
