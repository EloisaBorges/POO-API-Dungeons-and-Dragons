package com.projeto.D.D;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Essa é a classe que liga tudo. Quando ela roda, o Spring Boot "acorda"
// e vai procurando sozinho, dentro do pacote com.projeto.D.D e suas
// subpastas, todas as classes marcadas com @Controller, @Entity,
// @Component etc, e organiza tudo para funcionar junto.
@SpringBootApplication
public class Application {

	// main() é o método por onde o programa Java começa a rodar, igual
	// em qualquer programa Java comum. Aqui ele só entrega o controle
	// para o Spring Boot continuar o resto sozinho (abrir o servidor,
	// conectar no banco, carregar as páginas...).
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
