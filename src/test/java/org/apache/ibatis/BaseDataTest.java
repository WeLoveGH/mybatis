/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public abstract class BaseDataTest {

  /**
   * 博客数据库——数据库配置信息，数据库驱动、数据库地址、用户名、密码
   */
  public static final String BLOG_PROPERTIES = "org/apache/ibatis/databases/blog/blog-derby.properties";

  /**
   * 博客数据库——数据库DDL信息，删表、建表、建存储过程
   */
  public static final String BLOG_DDL = "org/apache/ibatis/databases/blog/blog-derby-schema.sql";

  /**
   * 数据库数据信息，插入数据
   */
  public static final String BLOG_DATA = "org/apache/ibatis/databases/blog/blog-derby-dataload.sql";

  /**
   * 博客数据库——数据库配置信息，数据库驱动、数据库地址、用户名、密码
   */
  public static final String JPETSTORE_PROPERTIES = "org/apache/ibatis/databases/jpetstore/jpetstore-hsqldb.properties";

  /**
   * 博客数据库——数据库DDL信息，删表、建表、建存储过程
   */
  public static final String JPETSTORE_DDL = "org/apache/ibatis/databases/jpetstore/jpetstore-hsqldb-schema.sql";

  /**
   * 数据库数据信息，插入数据
   */
  public static final String JPETSTORE_DATA = "org/apache/ibatis/databases/jpetstore/jpetstore-hsqldb-dataload.sql";

  /**
   * 创建非吃化的数据源对象
   * @param resource
   * @return
   * @throws IOException
   */
  public static UnpooledDataSource createUnpooledDataSource(String resource) throws IOException {
    /**
     * 获取数据源的配置信息，配置文件以键值对的形式编辑
     */
    Properties props = Resources.getResourceAsProperties(resource);

    /**
     * 构建非池化的数据源，并且通过数据源的配置填充数据源的相关信息，数据库驱动、数据库地址、数据库用户名、数据库密码
     */
    UnpooledDataSource ds = new UnpooledDataSource();
    ds.setDriver(props.getProperty("driver"));
    ds.setUrl(props.getProperty("url"));
    ds.setUsername(props.getProperty("username"));
    ds.setPassword(props.getProperty("password"));
    return ds;
  }

  /**
   * 创建池化的数据源
   * @param resource
   * @return
   * @throws IOException
   */
  public static PooledDataSource createPooledDataSource(String resource) throws IOException {
    Properties props = Resources.getResourceAsProperties(resource);
    PooledDataSource ds = new PooledDataSource();
    ds.setDriver(props.getProperty("driver"));
    ds.setUrl(props.getProperty("url"));
    ds.setUsername(props.getProperty("username"));
    ds.setPassword(props.getProperty("password"));
    return ds;
  }

  /**
   * 执行脚本命令 —— 上层壳子
   * @param ds
   * @param resource
   * @throws IOException
   * @throws SQLException
   */
  public static void runScript(DataSource ds, String resource) throws IOException, SQLException {

    /**
     * 获取数据库连接
     */
    Connection connection = ds.getConnection();
    try {
      /**
       * 构建脚本执行器，并设置对应的参数
       */
      ScriptRunner runner = new ScriptRunner(connection);

      /**
       * 自动提交
       */
      runner.setAutoCommit(true);

      /**
       * 发生错误时是否停止
       */
      runner.setStopOnError(false);

      /**
       * 日志输出器，如果不想打印日志，就输入 null
       */
      runner.setLogWriter(new PrintWriter(System.out));
      //runner.setLogWriter(null);

      /**
       * 发生错误时的日志输出器，如果不想打印日志，就输入 null
       */
      runner.setErrorLogWriter(new PrintWriter(System.out));
      //runner.setErrorLogWriter(null);

      /**
       * 执行脚本
       */
      runScript(runner, resource);
    } finally {
      /**
       * 关闭连接
       */
      connection.close();
    }
  }

  /**
   * 执行脚本命令 —— 底层逻辑
   * @param runner
   * @param resource
   * @throws IOException
   * @throws SQLException
   */
  public static void runScript(ScriptRunner runner, String resource) throws IOException, SQLException {
    /**
     * 获取脚本阅读器
     */
    Reader reader = Resources.getResourceAsReader(resource);
    try {
      /**
       * 执行脚本
       */
      runner.runScript(reader);
    } finally {
      /**
       * 关闭脚本阅读器
       */
      reader.close();
    }
  }

  public static DataSource createBlogDataSource() throws IOException, SQLException {
    /**
     * 创建数据源对象，通过数据源的配置文件来配置数据源的相关信息
     */
    DataSource ds = createUnpooledDataSource(BLOG_PROPERTIES);

    /**
     * 执行数据库建表的脚本
     */
    runScript(ds, BLOG_DDL);

    /**
     * 执行数据库插入数据的脚本
     */
    runScript(ds, BLOG_DATA);
    return ds;
  }

  /**
   * 创建数据源
   * @return
   * @throws IOException
   * @throws SQLException
   */
  public static DataSource createJPetstoreDataSource() throws IOException, SQLException {
    DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
    runScript(ds, JPETSTORE_DDL);
    runScript(ds, JPETSTORE_DATA);
    return ds;
  }
}
