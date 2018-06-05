# db-data-move
Fast and convenient use of annotations for data transfer
项目背景

随着业务的增长，或者一个业务的扩展兼容，往往会涉及的数据的迁移。

原来的数据迁移方式：1.查询数据 2.数据处理 3.数据插入

大部分的流程都是上面的流程，数据插入可能是直接调用dao去做插入操作，或者调用服务插入数据。

存在的一些的问题：1.不能重启，重启可能导入数据重复插入，通常需要做一个业务逻辑判断去避免。2.如果迁移的数据多，需要写很多业务代码，繁琐。每次都要经常大量测试，包括一些数据的转化，贼麻烦。
项目介绍

通过描述几个pojo对象，完成对象和表、sql之间的映射关系 ，比较简单高效的完成繁琐的迁移工作。
项目使用

1.mavan jar
<dependency>
 <groupId>com.potlid</groupId>
 <artifactId>db-data-move-task</artifactId>
 <version>1.0.0-SNAPSHOT</version>
</dependency>

2.配置lister 或者配置一个bean

<listener>
 <listener-class>com.raycloud.crm.transfer.task.listener.DataTransferListener</listener-class>
</listener>


3.配置数据源

4.描述一个bean

@Transfer(beforeDataSource = "datasource1" ,afterDataSource = "datasource2")
public class Test {
 @Field
 private Integer id;

 @Field
 private String userName;
}


这个bean 将会吧 spring容器里名字为datasource1的test表的 id 和user_name 字段 迁移到 datasource2的test表的 id 和user_name

注解说明

@Transfer 类注解

public @interface Transfer {

    String before() default ""; //迁移之前的表名字

    String after() default "";//迁移之后的表明

    String beforeDataSource();//需要迁移的数据库名

    String afterDataSource();//需要迁移到的数据库名

    TransferMode transferMode() default TransferMode.INSERT; //迁移模式 insert 迁移后不删除数据  delete 迁移后删除数据

    int order() default 0;  //迁移优先级
}

@Fileld 字段注解

public @interface Field {

    String before() default ""; //迁移之前的字段名

    String after() default "";//迁移之后的字段名

    boolean insert() default true; //是否需要插入

    boolean select() default true; //是否需要查询

    String insertValue() default ""; //默认的插入值

    boolean unique() default false; //是否是唯一的 用来防止重复迁移
}

@Conditon 字段注解 表示where 后面的那些条件

public @interface Condition {

    String operator() default "="; //操作符号

    String before() default ""; // 迁移之前的字段名

    String after() default ""; //迁移之后的字段名

    boolean insert() default true; //是否需要插入

    boolean select() default true; //是否需要查询

    boolean unique() default false; //唯一字段

}




