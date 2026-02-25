package cn.snowdreamsblog.redCulture.domain.user.repository.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPo {
    private String userId;
    private String username;
    private String password;
    private long role;
    private long points;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp lastAccessedTime;
}
