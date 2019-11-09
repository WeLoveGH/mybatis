MyBatis SQL Mapper Framework for Java
=====================================

[![Build Status](https://travis-ci.org/mybatis/mybatis-3.svg?branch=master)](https://travis-ci.org/mybatis/mybatis-3)
[![Coverage Status](https://coveralls.io/repos/mybatis/mybatis-3/badge.svg?branch=master&service=github)](https://coveralls.io/github/mybatis/mybatis-3?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/56199c04a193340f320005d3/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56199c04a193340f320005d3)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/org.mybatis/mybatis/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.mybatis/mybatis)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Stack Overflow](http://img.shields.io/:stack%20overflow-mybatis-brightgreen.svg)](http://stackoverflow.com/questions/tagged/mybatis)
[![Project Stats](https://www.openhub.net/p/mybatis/widgets/project_thin_badge.gif)](https://www.openhub.net/p/mybatis)

![mybatis](http://mybatis.github.io/images/mybatis-logo.png)

The MyBatis SQL mapper framework makes it easier to use a relational database with object-oriented applications.
MyBatis couples objects with stored procedures or SQL statements using a XML descriptor or annotations.
Simplicity is the biggest advantage of the MyBatis data mapper over object relational mapping tools.

Essentials
----------

* [See the docs](http://mybatis.github.io/mybatis-3)
* [Download Latest](https://github.com/mybatis/mybatis-3/releases)
* [Download Snapshot](https://oss.sonatype.org/content/repositories/snapshots/org/mybatis/mybatis/)


add by godtrue

* [中文文档](https://mybatis.org/mybatis-3/zh/index.html)

* [测试调试的过程中会报 Error:java: Compilation failed: internal java compiler error](https://hello.blog.csdn.net/article/details/82119860)

通过调用相关的代码，发下如下事实：

1：MyBatis 仅是一个 ORM 持久化框架而已，底层操作数据库的实现还是通过JDK的JDBC

2：MyBatis 实现功能的逻辑

   2-1：获取数据库的配置信息，数据库驱动、数据库地址、数据库用户名、数据库密码等 —— Configuration
   
   2-2：获取SqlSessionFactoryBuilder —— SQL会话工厂的构造器
   
   2-3：获取SqlSessionFactory —— SQL会话工厂
   
   2-4：获取SqlSession —— SQL会话，
        注意，是一个动态代理的 sqlSession 功能增强的方法调用处理器，用于针对没有指定SQL会话的实例进行提交和回滚的增强
        这是 mybatis 的核心接口
        
   2-4：获取Mapper —— 对象和表的映射关系，用于获取对应的SQL语句
   
   2-5：开启事务控制，通过配置获取事务工厂，然后选择合适的事务
   
   2-6：通过SQLSession进行各种SQL操作
   
   2-7：然后解析结果，且返回
   
   2-8：源码也并不是那么难读，当然，需要一定的基础，特别是：数据结构与算法、线程编程、网络编程、设计模式等知识的掌握
   
   
