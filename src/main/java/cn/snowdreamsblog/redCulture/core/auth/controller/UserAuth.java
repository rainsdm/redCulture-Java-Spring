package cn.snowdreamsblog.redCulture.core.auth.controller;

import cn.snowdreamsblog.redCulture.core.auth.dto.request.UserLoginRequest;
import cn.snowdreamsblog.redCulture.core.auth.dto.response.BaseUserProfileResponse;
import cn.snowdreamsblog.redCulture.core.auth.service.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserAuth {
    private final Login loginService;
    @Autowired
    public UserAuth(Login loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        BaseUserProfileResponse profile = loginService.login(request.username(), request.password());

        if ("登录成功".equals(profile.getMessages())) {
            return ResponseEntity.ok(Map.of("message", "登录成功"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", profile.getMessages()));
        }
    }
}
