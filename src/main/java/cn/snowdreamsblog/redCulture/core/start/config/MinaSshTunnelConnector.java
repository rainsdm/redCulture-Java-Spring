package cn.snowdreamsblog.redCulture.core.start.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;

@Log4j2
@Configuration
public class MinaSshTunnelConnector {
    private final SshProperties sshProperties;
    private SshClient SshClient;
    private ClientSession session;

    public MinaSshTunnelConnector(SshProperties properties) {
        this.sshProperties = properties;
    }

    @PostConstruct
    public void start() {
        try {
            SshClient = new SshClient();
            SshClient.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE); // 忽略服务器指纹检查

            FileKeyPairProvider fileKeyPairProvider = new FileKeyPairProvider(Paths.get(sshProperties.getPrivateKey()));
            SshClient.setKeyIdentityProvider(fileKeyPairProvider);

            SshClient.start();

            // 建立会话
            session = SshClient.connect(
                    sshProperties.getUsername(),
                    sshProperties.getHost(),
                    sshProperties.getPort()
                    )
                    .verify(Duration.ofSeconds(10))
                    .getSession();

            session.auth().verify(Duration.ofSeconds(10));
            log.info("使用私钥连接到 {}:{}", sshProperties.getHost(), sshProperties.getPort());

            SshdSocketAddress localAddress = new SshdSocketAddress(
                    "127.0.0.1", sshProperties.getLocalPort()
            );
            SshdSocketAddress remoteAddress = new SshdSocketAddress(
                    sshProperties.getRemoteHost(), sshProperties.getRemotePort()
            );

            session.startLocalPortForwarding(localAddress, remoteAddress);

            log.info("Apache MINA SSH 隧道已启动: 本地 {} -> 远程 {}:{}",
                    sshProperties.getLocalPort(),
                    sshProperties.getRemoteHost(),
                    sshProperties.getRemotePort()
            );

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (SshClient != null && SshClient.isStarted()) {
                    log.warn("检测到JVM退出信号，正在清理ssh连接。");
                    this.shutdown();
                }
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("开始安全退出SSH...");
        try {
            if (session != null &&  session.isOpen()) {
                // 立即发送关闭请求，不等待缓冲区刷新完成。
                session.close(true).addListener(future -> {
                    log.info("会话已端开。");
                });
            }
            if (SshClient != null && SshClient.isStarted()) {
                SshClient.stop();
                log.info("SSH 客户端已停止，端口映射已解除。");
            }
        } catch (Exception e) {
            log.error("安全退出时发生异常，尝试强制回收资源。", e);
        }
    }
}
