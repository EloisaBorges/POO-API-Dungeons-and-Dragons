package com.projeto.D.D.controller;

import com.projeto.D.D.model.Magia;
import com.projeto.D.D.repository.MagiaRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/magias")
public class MagiaController {

    private final MagiaRepository repository;

    public MagiaController(MagiaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String classe,
                         @RequestParam(required = false) Integer nivel,
                         @RequestParam(required = false) String componente,
                         Model model) {

        List<Magia> todas = repository.findAll();
        List<Magia> filtradas = new ArrayList<>();

        for (Magia magia : todas) {
            boolean ok = true;

            if (classe != null && !classe.isBlank()) {
                if (magia.getClasses() == null || !magia.getClasses().toLowerCase().contains(classe.toLowerCase())) {
                    ok = false;
                }
            }
            if (nivel != null && magia.getNivel() != nivel) {
                ok = false;
            }
            if (componente != null && !componente.isBlank()) {
                if (magia.getComponentes() == null || !magia.getComponentes().toUpperCase().contains(componente.toUpperCase())) {
                    ok = false;
                }
            }
            if (ok) {
                filtradas.add(magia);
            }
        }

        model.addAttribute("magias", filtradas);
        model.addAttribute("classe", classe);
        model.addAttribute("nivel", nivel);
        model.addAttribute("componente", componente);
        return "lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("magia", new Magia());
        return "form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Magia magia = repository.findById(id).orElse(new Magia());
        model.addAttribute("magia", magia);
        return "form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Magia magia) {
        repository.save(magia);
        return "redirect:/magias";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/magias";
    }
}
