package dev.codewithsam.payments;

import org.springframework.boot.SpringApplication;

public class TestSpringPaymentsApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringPaymentsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
