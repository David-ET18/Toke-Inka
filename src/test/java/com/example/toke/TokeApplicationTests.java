package com.example.toke;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase; // <--- IMPORTANTE
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
// ESTA LÍNEA ES LA QUE ARREGLA EL ERROR:
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
class TokeApplicationTests {

	@Test
	void contextLoads() {
	}

}