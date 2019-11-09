/**
 *    Copyright 2009-2016 the original author or authors.
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
package org.apache.ibatis.session;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.NullAppender;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for specify the behavior when detects an unknown column (or unknown property type) of automatic mapping target.
 *
 * @since 3.4.0
 * @author Kazuki Shimizu
 */
public class AutoMappingUnknownColumnBehaviorTest {

    interface Mapper {
        @Select({
                "SELECT ",
                "  ID,",
                "  USERNAME as USERNAMEEEE,", // unknown column
                "  PASSWORD,",
                "  EMAIL,",
                "  BIO",
                "FROM AUTHOR WHERE ID = #{id}"})
        Author selectAuthor(int id);

        @Select({
                "SELECT ",
                "  ID,", // unknown property type
                "  USERNAME",
                "FROM AUTHOR WHERE ID = #{id}"})
        SimpleAuthor selectSimpleAuthor(int id);
    }

    static class SimpleAuthor {
        private AtomicInteger id; // unknown property type
        private String username;

        public AtomicInteger getId() {
            return id;
        }

        public void setId(AtomicInteger id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class LastEventSavedAppender extends NullAppender {
        private static LoggingEvent event;

        public void doAppend(LoggingEvent event) {
            LastEventSavedAppender.event = event;
        }
    }

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public static void setup() throws Exception {
        /**
         * 获取数据源对象
         */
        DataSource dataSource = BaseDataTest.createBlogDataSource();

        /**
         * 获取构建事务工厂对象
         */
        TransactionFactory transactionFactory = new JdbcTransactionFactory();

        /**
         * 获取环境对象
         */
        Environment environment = new Environment("Production", transactionFactory, dataSource);

        /**
         * 获取配置对象
         */
        Configuration configuration = new Configuration(environment);

        /**
         * 获取映射对象
         */
        configuration.addMapper(Mapper.class);

        /**
         * 获取 sqlSession 工厂对象
         */
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Test
    public void none() {
        /**
         * 设置配置信息
         */
        sqlSessionFactory.getConfiguration().setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.NONE);

        /**
         * 获取 sqlSession 对象
         */
        SqlSession session = sqlSessionFactory.openSession();
        try {
            /**
             * 获取映射对象
             */
            Mapper mapper = session.getMapper(Mapper.class);

            /**
             * 执行查询功能
             */
            Author author = mapper.selectAuthor(101);

            /**
             * 业务逻辑处理，这里的代码是具有个性化的，所以，框架做了公公共的抽象的部分，个性化的地方就需要根据业务状况来编写了
             *
             * now is : 2019-11-9 14:07:32 , author is : Author{id=101, username='null', password='********', email='jim@ibatis.apache.org', bio='', favouriteSection=null}
             */
            System.out.println("\n\n\n now is : " + Calendar.getInstance().getTime().toLocaleString() + " , author is : " + author +"\n\n\n");

            assertThat(author.getId()).isEqualTo(101);
            assertThat(author.getUsername()).isNull();
        } finally {
            /**
             * 关闭 sqlSession
             */
            session.close();
        }

    }

    @Test
    public void warningCauseByUnknownPropertyType() {
        sqlSessionFactory.getConfiguration().setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.WARNING);

        SqlSession session = sqlSessionFactory.openSession();

        try {
            Mapper mapper = session.getMapper(Mapper.class);
            SimpleAuthor author = mapper.selectSimpleAuthor(101);
            assertThat(author.getId()).isNull();
            assertThat(author.getUsername()).isEqualTo("jim");
            assertThat(LastEventSavedAppender.event.getMessage().toString()).isEqualTo("Unknown column is detected on 'org.apache.ibatis.session.AutoMappingUnknownColumnBehaviorTest$Mapper.selectSimpleAuthor' auto-mapping. Mapping parameters are [columnName=ID,propertyName=id,propertyType=java.util.concurrent.atomic.AtomicInteger]");

        } finally {
            session.close();
        }

    }

    @Test
    public void failingCauseByUnknownColumn() {
        sqlSessionFactory.getConfiguration().setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.FAILING);

        SqlSession session = sqlSessionFactory.openSession();

        try {
            Mapper mapper = session.getMapper(Mapper.class);
            mapper.selectAuthor(101);
        } catch (PersistenceException e) {
            assertThat(e.getCause()).isInstanceOf(SqlSessionException.class);
            assertThat(e.getCause().getMessage()).isEqualTo("Unknown column is detected on 'org.apache.ibatis.session.AutoMappingUnknownColumnBehaviorTest$Mapper.selectAuthor' auto-mapping. Mapping parameters are [columnName=USERNAMEEEE,propertyName=USERNAMEEEE,propertyType=null]");
        } finally {
            session.close();
        }

    }

}
