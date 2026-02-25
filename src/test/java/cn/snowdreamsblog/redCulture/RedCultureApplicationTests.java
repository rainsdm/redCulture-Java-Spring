package cn.snowdreamsblog.redCulture;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "ssh-tunnel.enabled=false")
class RedCultureApplicationTests {

	@Test
	void contextLoads() {
	}

}
