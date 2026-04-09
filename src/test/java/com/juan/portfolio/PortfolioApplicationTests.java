package com.juan.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "githubclient.base-url=http://localhost",
        "githubclient.token=fake-token",
        "githubclient.user-agent=test-agent",
        "githubclient.username=testuser"
})
class PortfolioApplicationTests {

	@Test
	void contextLoads() {

	}

}
