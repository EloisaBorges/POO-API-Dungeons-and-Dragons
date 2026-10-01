package com.projeto.D.D.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller diz ao Spring que essa classe vai responder a páginas
// (diferente de @RestController, que responde dados tipo JSON).
@Controller
public class HomeController {

    // @GetMapping("/") significa: quando alguém acessa a página inicial
    // do site (http://localhost:8080/), esse método é chamado.
    @GetMapping("/")
    public String inicio() {
        // "redirect:/magias" não mostra nenhuma tela: ele manda o
        // navegador ir automaticamente para a página /magias.
        // Ou seja, a página inicial do site é o grimório.
        return "redirect:/magias";
    }
}
