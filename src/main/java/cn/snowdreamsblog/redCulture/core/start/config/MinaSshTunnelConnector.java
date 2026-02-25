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

import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
@Configuration
public class MinaSshTunnelConnector {
    private final SshProperties sshProperties;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private SshClient sshClient;
    private ClientSession session;

    public MinaSshTunnelConnector(SshProperties properties) {
        this.sshProperties = properties;
    }

    @PostConstruct
    public void start() {
        try {
            sshClient = SshClient.setUpDefaultClient();
            sshClient.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE); // 忽略服务器指纹检查

            FileKeyPairProvider fileKeyPairProvider = new FileKeyPairProvider(Paths.get(sshProperties.getPrivateKey()));
            sshClient.setKeyIdentityProvider(fileKeyPairProvider);

            sshClient.start();

            // 建立会话
            session = sshClient.connect(
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
                if (sshClient != null && sshClient.isStarted()) {
                    log.warn("检测到JVM退出信号，正在清理ssh连接。");
                    this.shutdown();
                }
            }));
        } catch (Exception e) {
            log.error("SSH 隧道初始化失败，准备回滚资源", e);
            this.shutdown(); // 启动失败时主动清理可能已开启的线程池，且无论其抛出什么异常，都执行这一步。
            throw new RuntimeException("SSH 隧道初始化失败", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        // 验证并发，确保逻辑具有幂等性
        if (!isClosed.compareAndSet(false, true)) {
            return;
        }
        log.info("开始安全退出SSH...");

        Thread cleanupThread = new Thread(() -> {
            try {
                if (session != null && session.isOpen()) {
                    // 立即发送关闭请求，不等待缓冲区刷新完成。
                    session.close(true).addListener(future -> {
                        log.info("会话已断开。");
                    });
                }
                if (sshClient != null && sshClient.isStarted()) {
                    sshClient.stop();
                    log.info("SSH 客户端已停止，端口映射已解除。");
                }
            } catch (Exception e) {
                log.error("安全退出时发生异常，尝试强制回收资源。", e);
            }
        });

        cleanupThread.start();

        try {
            // 设定 2 秒超时，防止网络死锁导致 JVM 无法退出
            cleanupThread.join(2000);
            if (cleanupThread.isAlive()) {
                log.warn("SSH 资源清理任务超时，放弃等待，交由系统强制回收。");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
