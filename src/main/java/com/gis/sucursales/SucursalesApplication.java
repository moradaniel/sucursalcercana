package com.gis.sucursales;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SucursalesApplication implements CommandLineRunner {


	public static void main(String[] args) {
		SpringApplication.run(SucursalesApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
