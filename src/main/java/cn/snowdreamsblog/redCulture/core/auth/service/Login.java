package cn.snowdreamsblog.redCulture.core.auth.service;

import cn.snowdreamsblog.redCulture.core.auth.dto.response.BaseUserProfileResponse;
import cn.snowdreamsblog.redCulture.domain.user.repository.mapper.UserMapper;
import cn.snowdreamsblog.redCulture.domain.user.repository.po.UserPo;
import org.springframework.stereotype.Service;

@Service
public class Login {
    private final UserMapper userMapper;

    public Login(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public BaseUserProfileResponse login(String username, String password) {
        UserPo userPo = userMapper.selectUserByUsername(username);
        if (userPo == null) {
            return new BaseUserProfileResponse("用户不存在");
        }
        if (!userPo.getPassword().equals(password)) {
            return new BaseUserProfileResponse("密码错误");
        }

        return new BaseUserProfileResponse(
                userPo.getUserId(), userPo.getUsername(),
                userPo.getRole(), userPo.getPoints(), "登录成功"
        );
    }
}
