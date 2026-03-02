package cn.snowdreamsblog.redCulture.core.auth.controller;

import cn.snowdreamsblog.redCulture.core.auth.dto.request.UserLoginRequest;
import cn.snowdreamsblog.redCulture.core.auth.dto.response.BaseUserProfileResponse;
import cn.snowdreamsblog.redCulture.core.auth.dto.response.LoginResponse;
import cn.snowdreamsblog.redCulture.core.auth.dto.response.login.LoginData;
import cn.snowdreamsblog.redCulture.core.auth.dto.response.login.Security;
import cn.snowdreamsblog.redCulture.core.auth.dto.response.login.User;
import cn.snowdreamsblog.redCulture.core.auth.service.Login;
import cn.snowdreamsblog.redCulture.utils.jwt.JwtUtilJose4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
public class UserAuth {
    private final Login loginService;
    private final JwtUtilJose4j jwtUtilJose4j;

    @Autowired
    public UserAuth(Login loginService, JwtUtilJose4j jwtUtilJose4j) {
        this.loginService = loginService;
        this.jwtUtilJose4j = jwtUtilJose4j;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        BaseUserProfileResponse profile = loginService.login(request.username(), request.password());

        if ("登录成功".equals(profile.getMessages())) {
            String accessToken = jwtUtilJose4j.createToken(profile.getUserId());

            // 有效期设置
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String validUntil = LocalDateTime.now().plusHours(1).format(formatter);

            User user = new User(
                    profile.getUsername(),
                    (int) profile.getPoints()
            );

            Security security = new Security(
                    profile.getUserId(),
                    (int) profile.getRole(),
                    validUntil
            );

            LoginData loginData = new LoginData(accessToken, user, security);
            LoginResponse response = new LoginResponse(200, "登录成功", loginData);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "code", 401, "msg", profile.getMessages()
                            )
                    );
        }
    }
}
