package ru.netology.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {
	// Контейнер для DEV образа
	@Container
	private static GenericContainer<?> devContainer = new GenericContainer<>("devapp:latest")
			.withExposedPorts(8080);

	// Контейнер для PROD образа
	@Container
	private static GenericContainer<?> prodContainer = new GenericContainer<>("prodapp:latest")
			.withExposedPorts(8081);

	@Autowired
	private static TestRestTemplate restTemplate;

	@BeforeAll
	public static void setUp() {
		devContainer.start();
		prodContainer.start();

		restTemplate = new TestRestTemplate();

		System.out.println("=== Контейнеры запущены ===");
		System.out.println("DEV контейнер на порту: " + devContainer.getMappedPort(8080));
		System.out.println("PROD контейнер на порту: " + prodContainer.getMappedPort(8081));
	}

	@Test
	void contextLoads() {
		Integer devPort = devContainer.getMappedPort(8080);
		String urlDev = "http://localhost:" + devPort + "/profile";

		System.out.println("Testing DEV profile at: " + urlDev);

		ResponseEntity<String> responseDev = restTemplate.getForEntity(urlDev, String.class);

		assertEquals(200, responseDev.getStatusCodeValue());
		assertNotNull(responseDev.getBody());
		assertEquals("Current profile is dev", responseDev.getBody());

		System.out.println("DEV response: " + responseDev.getBody());

		Integer prodPort = prodContainer.getMappedPort(8081);
		String urlProd = "http://localhost:" + prodPort + "/profile";

		System.out.println("Testing PROD profile at: " + urlProd);

		ResponseEntity<String> responseProd = restTemplate.getForEntity(urlProd, String.class);

		assertEquals(200, responseProd.getStatusCodeValue());
		assertNotNull(responseProd.getBody());
		assertEquals("Current profile is production", responseProd.getBody());

		System.out.println("PROD response: " + responseProd.getBody());

	}


}