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

// Esse é o controller do CRUD: Cadastrar, Listar, Editar e Excluir
// as magias que já estão salvas no NOSSO banco de dados.
// @RequestMapping("/magias") significa que todo endereço aqui dentro
// começa com /magias (ex: /magias, /magias/nova, /magias/5/editar...).
@Controller
@RequestMapping("/magias")
public class MagiaController {

    // O repository é quem realmente conversa com o banco de dados.
    // O Spring injeta (entrega pronto) esse objeto no construtor.
    private final MagiaRepository repository;

    public MagiaController(MagiaRepository repository) {
        this.repository = repository;
    }

    // LISTAR -------------------------------------------------------
    // GET /magias  (com os filtros opcionais: ?classe=...&nivel=...&componente=...)
    @GetMapping
    public String listar(@RequestParam(required = false) String classe,
                         @RequestParam(required = false) Integer nivel,
                         @RequestParam(required = false) String componente,
                         Model model) {

        // Pega TODAS as magias do banco.
        List<Magia> todas = repository.findAll();

        // Vamos guardar aqui só as que passarem nos filtros.
        List<Magia> filtradas = new ArrayList<>();

        // Percorre cada magia uma por uma e verifica os filtros.
        for (Magia magia : todas) {
            boolean ok = true;

            // Se o usuário escolheu uma classe, verifica se o campo
            // "classes" da magia contém esse texto.
            if (classe != null && !classe.isBlank()) {
                if (magia.getClasses() == null || !magia.getClasses().toLowerCase().contains(classe.toLowerCase())) {
                    ok = false;
                }
            }
            // Se escolheu um nível, verifica se é exatamente igual.
            if (nivel != null && magia.getNivel() != nivel) {
                ok = false;
            }
            // Se escolheu um componente (V, S ou M), verifica se está
            // no campo "componentes" da magia.
            if (componente != null && !componente.isBlank()) {
                if (magia.getComponentes() == null || !magia.getComponentes().toUpperCase().contains(componente.toUpperCase())) {
                    ok = false;
                }
            }
            // Só adiciona na lista final se passou em todos os filtros.
            if (ok) {
                filtradas.add(magia);
            }
        }

        // "Model" é a "mochila" de informações que mandamos para o HTML.
        // Tudo que colocamos aqui vira uma variável que o Thymeleaf
        // consegue usar na página (ex: ${magias}).
        model.addAttribute("magias", filtradas);
        model.addAttribute("classe", classe);
        model.addAttribute("nivel", nivel);
        model.addAttribute("componente", componente);

        // O método devolve o NOME do arquivo HTML que deve ser exibido
        // (sem o .html). Aqui ele mostra templates/lista.html.
        return "lista";
    }

    // FORMULÁRIO DE CADASTRO ----------------------------------------
    // GET /magias/nova
    @GetMapping("/nova")
    public String nova(Model model) {
        // Manda um objeto Magia "vazio" para o formulário preencher.
        model.addAttribute("magia", new Magia());
        return "form";
    }

    // FORMULÁRIO DE EDIÇÃO --------------------------------------------
    // GET /magias/5/editar
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        // Busca a magia pelo id. Se não encontrar, usa uma magia vazia
        // em vez de quebrar a aplicação.
        Magia magia = repository.findById(id).orElse(new Magia());
        model.addAttribute("magia", magia);
        return "form";
    }

    // SALVAR (serve tanto para criar quanto para editar) ---------------
    // POST /magias/salvar
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Magia magia) {
        // @ModelAttribute pega todos os campos que vieram do formulário
        // HTML e já monta um objeto Magia sozinho.
        //
        // Se "magia.getId()" vier vazio, o JPA entende que é uma magia
        // NOVA e faz um INSERT. Se vier com um id (porque estávamos
        // editando), o JPA entende que é para ATUALIZAR e faz um UPDATE.
        repository.save(magia);

        // Depois de salvar, volta para a lista de magias.
        return "redirect:/magias";
    }

    // EXCLUIR ------------------------------------------------------
    // POST /magias/5/excluir
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/magias";
    }
}
