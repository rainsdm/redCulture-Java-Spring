package cn.snowdreamsblog.redCulture.core.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于返回基本的用户信息。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserProfileResponse {
    private String userId;
    private String username;
    private long role;
    private long points;
    private String messages;

    public BaseUserProfileResponse(String messages) {
        this.messages = messages;
    }
}
