package cn.snowdreamsblog.redCulture.core.auth.dto.response.login;

import java.util.regex.Pattern;

public record Security(
        String id,
        int roleLevel,
        String validUntil
) {
    // 预编译正则表达式以提升性能
    private static final Pattern ID_PATTERN = Pattern.compile("^(G(?!000)\\d{3}|A(?!00)\\d{2})$");

    public Security {
        // 1. 检查 ID 非空及正则格式
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id 不能为空");
        }
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException("id 格式不符合规范，必须为 GXXX 或 AXX");
        }

        // 2. 检查角色等级，利用最值符号，使其只能是0或1
        if (roleLevel < 0 || roleLevel > 1) {
            throw new IllegalArgumentException("roleLevel 必须为 0 (管理员) 或 1 (普通用户)");
        }

        // 3. 检查有效期非空
        if (validUntil == null || validUntil.isBlank()) {
            throw new IllegalArgumentException("validUntil 不能为空");
        }
    }
}