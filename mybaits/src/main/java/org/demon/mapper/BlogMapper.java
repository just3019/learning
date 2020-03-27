package org.demon.mapper;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.demon.pojo.Blog;

/**
 * @author demon
 * @version 1.0.0
 */
public interface BlogMapper {

    @Results(id = "test", value = {
            @Result(property = "id", column = "id", id = true)
    })
    @Select("select * from Blog where id = #{id}")
    Blog selectBlog(int id);
}
