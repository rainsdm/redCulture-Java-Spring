package cn.snowdreamsblog.redCulture.core.auth.dto.request;

/**
 * 由于登录、注册均只有用户名和密码两个要素，因此暂时合并为认证请求。
 * @param username
 * @param password
 */
public record UserAuthRequest(String username, String password) {
}
