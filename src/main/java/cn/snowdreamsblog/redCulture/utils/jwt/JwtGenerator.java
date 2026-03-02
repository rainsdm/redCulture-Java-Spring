package cn.snowdreamsblog.redCulture.utils.jwt;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import java.nio.charset.StandardCharsets;

/**
 * 用于操作令牌生成、验证与解析的类。当前仅支持HMAC_SHA256摘要算法。
 * 为了安全起见，这里只允许
 */
public class JwtGenerator {
    private final byte[] keyByte;

    /**
     * 令牌操作的初始化函数。
     *
     * @param secret 用于签名的密钥。
     */
    public JwtGenerator(String secret) {
        this.keyByte = secret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 生成令牌。
     *
     * @param claims 令牌的配置信息。详情可以参考我提供的JWT使用简介。
     * @return 字符串形式的令牌文本。
     */
    public String generateToken(JwtClaims claims) {
        // 必须作为局部变量，保证每次请求独享实例，做到线程安全
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setKey(new HmacKey(this.keyByte));

        jws.setHeader("typ", "JWT");

        String jwt;

        try {
            jwt = jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new RuntimeException(e);
        }

        return jwt;
    }
}
