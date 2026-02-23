package cn.snowdreamsblog.redCulture.core.start.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ssh-tunnel")
public class SshProperties {
    private boolean enabled;
    private String host;
    private int port;
    private String username;
    private String password;
    private String remoteHost;
    private int remotePort;
    private int localPort;
}
