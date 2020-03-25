package org.demon;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.demon.mapper.BlogMapper;
import org.demon.pojo.Blog;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author demon
 * @version 1.0.0
 */
@Slf4j
public class MybatisTest {

    @Test
    public void test() throws IOException {

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        try (SqlSession session = sqlSessionFactory.openSession()) {
            //1
            //Blog blog = session.selectOne("org.demon.mapper.BlogMapper.selectBlog", 1);
            //2
            BlogMapper blogMapper = session.getMapper(BlogMapper.class);
            Blog blog = blogMapper.selectBlog(1);
            if (blog != null){
                log.info(blog.toString());
            }
        }


    }

}
