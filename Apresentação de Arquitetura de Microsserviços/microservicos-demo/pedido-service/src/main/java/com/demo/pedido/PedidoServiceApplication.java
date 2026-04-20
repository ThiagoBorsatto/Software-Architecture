package com.demo.pedido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PedidoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedidoServiceApplication.class, args);
    }

    // RestTemplate é o cliente HTTP que usamos para chamar outros microserviços
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
