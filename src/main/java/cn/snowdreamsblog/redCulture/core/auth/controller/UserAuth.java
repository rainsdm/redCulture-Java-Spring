package cn.snowdreamsblog.redCulture.core.auth.controller;

import cn.snowdreamsblog.redCulture.core.auth.dto.request.UserLoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserAuth {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        String username = userLoginRequest.username();
        String password = userLoginRequest.password();

        // 模拟业务逻辑验证
        if ("123456".equals(username) && "123456".equals(password)) {
//            return "登录成功";
            return ResponseEntity.ok(Map.of("message", "欢迎光临"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "用户名或密码错误。"));
        }
    }
}
