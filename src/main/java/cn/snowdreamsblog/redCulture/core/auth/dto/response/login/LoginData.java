package cn.snowdreamsblog.redCulture.core.auth.dto.response.login;

import java.util.Objects;

public record LoginData(
        String accessToken,
        User user,
        Security security
) {
    public LoginData {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken 不能为空");
        }
        Objects.requireNonNull(user, "user 不能为 null");
        Objects.requireNonNull(security, "security 不能为 null");
    }
}
