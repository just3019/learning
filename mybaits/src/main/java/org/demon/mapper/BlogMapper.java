package org.demon.mapper;

import org.apache.ibatis.annotations.Select;
import org.demon.pojo.Blog;

/**
 * @author demon
 * @version 1.0.0
 */
public interface BlogMapper {

    @Select("select * from Blog where id = #{id}")
    Blog selectBlog(int id);
}
