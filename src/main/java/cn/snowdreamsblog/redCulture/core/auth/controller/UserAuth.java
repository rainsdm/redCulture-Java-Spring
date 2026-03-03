package cn.snowdreamsblog.redCulture.core.auth.controller;

import cn.snowdreamsblog.redCulture.core.auth.dto.request.UserAuthRequest;
import cn.snowdreamsblog.redCulture.core.auth.dto.response.*;
import cn.snowdreamsblog.redCulture.core.auth.dto.response.login.*;
import cn.snowdreamsblog.redCulture.core.auth.service.*;
import cn.snowdreamsblog.redCulture.utils.jwt.JwtUtilJose4j;
import org.jspecify.annotations.NonNull;
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
    private final Register registerService;
    private final JwtUtilJose4j jwtUtilJose4j;

    @Autowired
    public UserAuth(Login loginService, Register registerService, JwtUtilJose4j jwtUtilJose4j) {
        this.loginService = loginService;
        this.jwtUtilJose4j = jwtUtilJose4j;
        this.registerService = registerService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserAuthRequest request) {
        BaseUserProfileResponse profile = loginService.login(request.username(), request.password());

        return getResponse(profile, profile.getMessages());
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserAuthRequest request) {


        BaseUserProfileResponse checkRegister = registerService.register(request);

        return getResponse(checkRegister, checkRegister.getMessages());
    }

    /**
     * 处理登录与注册成功后的响应结果。由于我设计的是注册成功后，自动登录，并跳转到首页，所以这个功能被我提取出来，供两方面共同使用。
     *
     * @param profile 从数据库中返回的基本用户信息。
     * @return 提供给前端的用户信息。
     */
    private @NonNull ResponseEntity<?> getResponse(BaseUserProfileResponse profile, String successMsg) {
        if ("登录成功".equals(successMsg) || "注册成功".equals(successMsg)) {
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
            LoginResponse response = new LoginResponse(200, successMsg, loginData);

            return ResponseEntity.ok(response);
        }

        if ("用户名已存在".equals(successMsg)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // 兜底处理：所有不明确的状态统一归为 401 或 400
        // 使用 Optional 或简单的三元表达式防止 Map.of 崩溃
        String errorMsg = (profile.getMessages() != null) ? profile.getMessages() : "服务器内部错误：未获取到状态信息";

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("code", 401, "msg", errorMsg));
    }
}
