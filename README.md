# MYSQL数据库同步工具

目前仅针对Mysql数据库

1.表结构同步 (支持1对多数据库配置：新表/单表/多表/全表 (注:多配置全表同步速度会慢一点))

2.视图同步 (支持1对多数据库配置)

3.函数同步 (支持1对多数据库配置)

4.数据全量同步 (1对1数据库配置, 支持多表)

5.本地启动 http://localhost:8765/

6.默认管理账户admin/123456

```
A.附加说明：
	程序采用SpringBoot + Shiro + Mysql + thymeleaf架构，做了基础的用户权限控制(有其他需求可自行扩展)。
	前端页面使用了LayUi（本人做后端，前端不太熟，不喜勿喷）
B.配置方式1：
用数据库配置（脚本自行导入tabsync.sql,参考图示）,并且配置可以根据不同用户设置为私有（公开配置，所有用户都可见，私有配置只有自己可见。）

C.配置方式2：
配置JSON文件（master.json/targetList.json; 使用测试类直接执行,该方式未写数据同步，可自行改代码）
{"host": "127.0.0.1:3306",
  "username": "root",
  "password": "root",
  "database": "test",
  "mysqlType": "5",
  "charSet": "utf8"
}

D.可以配置自动执行代码-程序启动完成自动执行一次（ExecuteTask.java）

E.备注：表结构比对借鉴于https://gitee.com/alchemystar/Lancer，并按需求做了部分改动。

F.本地执行，先执行createTable.sql
```



![image-20210507115959992](IMG/image-20210507115959992.png)



![image-20210507130624221](IMG/image-20210507130624221.png)



![image-20210507130545532](IMG/image-20210507130545532.png)

![image-20210507130826280](IMG/image-20210507130826280.png)



![image-20210507130726752](IMG/image-20210507130726752.png)

![image-20210507140413009](IMG/image-20210507140413009.jpg)

#### 





