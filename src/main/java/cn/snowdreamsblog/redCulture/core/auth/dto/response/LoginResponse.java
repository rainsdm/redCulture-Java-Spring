package cn.snowdreamsblog.redCulture.core.auth.dto.response;

import cn.snowdreamsblog.redCulture.core.auth.dto.response.login.LoginData;

import java.util.Objects;

public record LoginResponse(
        int code,
        String msg,
        LoginData data
) {
    public LoginResponse {
        Objects.requireNonNull(msg, "msg 不能为 null");
        Objects.requireNonNull(data, "data 不能为 null");
    }
}



