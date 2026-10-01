package com.projeto.D.D.controller;

import com.projeto.D.D.repository.MagiaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PreparadasController {

    private final MagiaRepository repository;

    public PreparadasController(MagiaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/preparadas")
    public String preparadas(Model model) {
        model.addAttribute("magias", repository.findAll());
        return "preparadas";
    }
}
