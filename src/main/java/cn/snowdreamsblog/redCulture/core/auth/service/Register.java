package cn.snowdreamsblog.redCulture.core.auth.service;

import cn.snowdreamsblog.redCulture.core.auth.dto.request.UserAuthRequest;
import cn.snowdreamsblog.redCulture.core.auth.dto.response.BaseUserProfileResponse;
import cn.snowdreamsblog.redCulture.domain.user.repository.mapper.UserMapper;
import cn.snowdreamsblog.redCulture.domain.user.repository.po.UserPo;
import org.springframework.stereotype.Service;

@Service
public class Register {
    private final UserMapper userMapper;

    public Register(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public BaseUserProfileResponse register(UserAuthRequest request) {
        if (userExists(request.username())) {
            return new BaseUserProfileResponse("用户名已存在");
        }
        int registerNum = userMapper.insertGeneralUser(request.username(), request.password());
        if  (registerNum <= 0) {
            return new BaseUserProfileResponse("注册失败");
        }
        UserPo userPo = userMapper.selectUserByUsername(request.username());
        userMapper.setLoginTime(userPo.getUserId());
        return new BaseUserProfileResponse(
                userPo.getUserId(), userPo.getUsername(),
                userPo.getRole(), userPo.getPoints(), "注册成功"
        );
    }

    /**
     * 判断用户名是否存在。
     * @param username 用于判断的用户名。
     * @return 存在返回true, 不存在返回false。
     */
    private boolean userExists(String username) {
        return userMapper.selectUserByUsername(username) != null;
    }
}
