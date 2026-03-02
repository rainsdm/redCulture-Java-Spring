package cn.snowdreamsblog.redCulture.utils.jwt;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于Jose4j的定制封装，已从静态类修正为动态类，需要实例化后使用。
 */
@Component
public class JwtUtilJose4j {
    private final JwtGenerator jwtGenerator;

    // 推荐做法：通过构造器注入配置，确保实例化时配置已就绪
    @Autowired
    public JwtUtilJose4j(JwtConfig jwtConfig) {
        this.jwtGenerator = new JwtGenerator(jwtConfig.getSecret());
    }

    public String createToken(String userId) {
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("redCulture-Website");
        claims.setSubject(userId);
        claims.setClaim("userId", userId);

        NumericDate now = NumericDate.now();
        claims.setIssuedAt(now);
        now.addSeconds(60 * 60); // 1小时有效期
        claims.setExpirationTime(now);

        return jwtGenerator.generateToken(claims);
    }
}
