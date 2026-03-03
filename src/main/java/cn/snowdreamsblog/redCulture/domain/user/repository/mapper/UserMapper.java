package cn.snowdreamsblog.redCulture.domain.user.repository.mapper;

import cn.snowdreamsblog.redCulture.domain.user.repository.po.UserPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查找用户数据。
     * @param username 待查找的用户名。
     * @return 打包好的用户数据对象。
     */
    @Select("SELECT " +
            "user_id, username, password, role, " +
            "points, create_time " +
            "FROM users WHERE username = #{username}")
    UserPo selectUserByUsername(String username);

    @Update("update users set last_accessed_time = current_timestamp where user_id = #{userID}")
    int setLoginTime(String userId);

    /**
     * 插入普通用户数据。由于注册后自动登录，上次访问时间的第一次出现会等于注册时间。
     * @param username 新用户的用户名。
     * @param password 新用户的密码。
     * @return 成功插入的列数。返回0表示插入失败，没有插入任何行。
     */
    @Insert("insert into users" +
            "(username, password, role, last_accessed_time)" +
            "values (#{username}, #{password}, 1, current_timestamp)"
    )
    int insertGeneralUser(String username, String password);
}
