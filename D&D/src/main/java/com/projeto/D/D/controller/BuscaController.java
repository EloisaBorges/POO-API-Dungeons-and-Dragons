package com.projeto.D.D.controller;

import com.projeto.D.D.model.Magia;
import com.projeto.D.D.repository.MagiaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BuscaController {

    private final DndApiClient api;
    private final MagiaRepository repository;

    public BuscaController(DndApiClient api, MagiaRepository repository) {
        this.api = api;
        this.repository = repository;
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String classe,
                         @RequestParam(required = false) Integer nivel,
                         Model model) {

        List<Map> encontradas = new ArrayList<>();

        try {
            if (classe != null && !classe.isBlank()) {
                List<Map> referencias = api.listarPorClasse(classe);
                for (Map referencia : referencias) {
                    Map detalhe = api.detalhar((String) referencia.get("index"));
                    if (detalhe != null && (nivel == null || nivel.equals(detalhe.get("level")))) {
                        encontradas.add(detalhe);
                    }
                }
            } else if (nivel != null) {
                List<Map> referencias = api.listarPorNivel(nivel);
                for (Map referencia : referencias) {
                    Map detalhe = api.detalhar((String) referencia.get("index"));
                    if (detalhe != null) {
                        encontradas.add(detalhe);
                    }
                }
            }
        } catch (Exception e) {
            model.addAttribute("erro", "Não foi possível consultar a API. Verifique sua internet.");
        }

        model.addAttribute("magias", encontradas);
        model.addAttribute("classe", classe);
        model.addAttribute("nivel", nivel);
        return "busca";
    }

    @PostMapping("/buscar/importar")
    public String importar(@RequestParam String indice, RedirectAttributes attrs) {
        Map detalhe = api.detalhar(indice);
        if (detalhe == null) {
            attrs.addFlashAttribute("erro", "Não foi possível importar essa magia.");
            return "redirect:/buscar";
        }

        Magia magia = new Magia();
        magia.setIndiceApi(indice);
        magia.setNome((String) detalhe.get("name"));
        magia.setNivel((Integer) detalhe.get("level"));

        Map escola = (Map) detalhe.get("school");
        magia.setEscola(escola != null ? (String) escola.get("name") : "");

        magia.setTempoConjuracao((String) detalhe.get("casting_time"));
        magia.setAlcance((String) detalhe.get("range"));
        magia.setDuracao((String) detalhe.get("duration"));

        List<String> componentes = (List<String>) detalhe.get("components");
        magia.setComponentes(componentes != null ? String.join(", ", componentes) : "");

        List<Map> classes = (List<Map>) detalhe.get("classes");
        List<String> nomesClasses = new ArrayList<>();
        if (classes != null) {
            for (Map c : classes) {
                nomesClasses.add((String) c.get("name"));
            }
        }
        magia.setClasses(String.join(", ", nomesClasses));

        List<String> desc = (List<String>) detalhe.get("desc");
        magia.setDescricao(desc != null ? String.join("\n\n", desc) : "");

        repository.save(magia);
        attrs.addFlashAttribute("sucesso", "Magia adicionada ao grimório!");
        return "redirect:/magias";
    }
}
