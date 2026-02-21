package cn.snowdreamsblog.redCulture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RedCultureApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedCultureApplication.class, args);
	}

}
