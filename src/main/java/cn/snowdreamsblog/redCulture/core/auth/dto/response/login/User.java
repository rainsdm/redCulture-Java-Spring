package cn.snowdreamsblog.redCulture.core.auth.dto.response.login;

public record User(
        String username,
        int points
) {
    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username 不能为空");
        }
        // 限制数值范围: minimum: 0, maximum: 100
        if (points < 0 || points > 100) {
            throw new IllegalArgumentException("学习积分 points 必须在 0 到 100 之间");
        }
    }
}