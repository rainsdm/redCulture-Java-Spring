package cn.snowdreamsblog.redCulture.domain.user.repository.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于返回基本的用户信息。建议作为core.auth的DTO-response返回。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserProfilePo {
    private String userId;
    private String username;
    private long role;
    private long points;
}
