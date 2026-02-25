package cn.snowdreamsblog.redCulture.core.start.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Log4j2
@Component
public class SshTunnelDependencyProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // SSH 隧道 Bean 的默认名称
        String sshBeanName = "minaSshTunnelConnector";

        // 安全追加依赖
        safeAddDependency(beanFactory, "dataSource", sshBeanName);
        safeAddDependency(beanFactory, "flywayInitializer", sshBeanName);
        safeAddDependency(beanFactory, "sqlSessionFactory", sshBeanName);
    }

    /**
     * 安全地为目标 Bean 追加前置依赖，防止覆盖原有的依赖关系
     *
     * @param beanFactory    Bean 工厂
     * @param targetBeanName 目标 Bean 名称 (例如 dataSource)
     * @param dependBeanName 需要前置依赖的 Bean 名称 (例如 minaSshTunnelConnector)
     */
    private void safeAddDependency(ConfigurableListableBeanFactory beanFactory, String targetBeanName, String dependBeanName) {
        if (beanFactory.containsBeanDefinition(targetBeanName)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(targetBeanName);
            String[] existingDependsOn = beanDefinition.getDependsOn();

            if (existingDependsOn == null) {
                // 如果原本没有依赖，直接设置
                beanDefinition.setDependsOn(dependBeanName);
                log.info("已强制 {} 依赖 {}", targetBeanName, dependBeanName);
            } else {
                // 如果原本已有依赖，检查是否已经包含我们的目标 Bean
                boolean alreadyDepends = Arrays.asList(existingDependsOn).contains(dependBeanName);
                if (!alreadyDepends) {
                    // 创建一个新数组并追加依赖
                    String[] newDependsOn = Arrays.copyOf(existingDependsOn, existingDependsOn.length + 1);
                    newDependsOn[existingDependsOn.length] = dependBeanName;
                    beanDefinition.setDependsOn(newDependsOn);
                    log.info("已在 {} 的现有依赖基础上，追加强制依赖 {}", targetBeanName, dependBeanName);
                }
            }
        }
    }
}