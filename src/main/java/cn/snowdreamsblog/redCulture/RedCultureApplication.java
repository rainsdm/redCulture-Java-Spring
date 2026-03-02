package cn.snowdreamsblog.redCulture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
@MapperScan("cn.snowdreamsblog.redCulture.domain.user.repository.mapper")
public class RedCultureApplication {

    static void main(String[] args) {
        SpringApplication.run(RedCultureApplication.class, args);
    }

}
