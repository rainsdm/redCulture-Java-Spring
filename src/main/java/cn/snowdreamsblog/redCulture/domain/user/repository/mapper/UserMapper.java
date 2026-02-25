package cn.snowdreamsblog.redCulture.domain.user.repository.mapper;

import cn.snowdreamsblog.redCulture.domain.user.repository.po.UserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT " +
            "user_id, username, password, role, " +
            "points, create_time, last_accessed_time " +
            "FROM users")
    UserPo selectUserByUsername(String username);
}
