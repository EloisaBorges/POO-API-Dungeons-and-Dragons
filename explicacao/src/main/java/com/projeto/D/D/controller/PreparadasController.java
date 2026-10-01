package com.projeto.D.D.controller;

import com.projeto.D.D.repository.MagiaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Essa tela mostra as magias "preparadas". Só que aqui no Java a gente
// nem sabe quais estão marcadas! O servidor manda TODAS as magias do
// banco para a página, e quem decide quais aparecem é o JavaScript,
// olhando o que está salvo no navegador (localStorage). Veja o arquivo
// preparadas.js para entender essa parte.
@Controller
public class PreparadasController {

    private final MagiaRepository repository;

    public PreparadasController(MagiaRepository repository) {
        this.repository = repository;
    }

    // GET /preparadas
    @GetMapping("/preparadas")
    public String preparadas(Model model) {
        model.addAttribute("magias", repository.findAll());
        return "preparadas";
    }
}
