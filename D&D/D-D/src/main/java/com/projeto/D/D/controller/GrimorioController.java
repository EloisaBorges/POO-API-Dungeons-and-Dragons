package com.projeto.D.D.controller;

import com.projeto.D.D.model.ClasseConjuradora;
import com.projeto.D.D.model.Componente;
import com.projeto.D.D.model.Escola;
import com.projeto.D.D.model.Magia;
import com.projeto.D.D.repository.MagiaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CRUD do grimorio (banco de dados local) e a aba de magias preparadas.
 *
 * Cadastrar -> GET /grimorio/nova + POST /grimorio/salvar
 * Listar -> GET /grimorio
 * Editar -> GET /grimorio/{id}/editar + POST /grimorio/salvar
 * Excluir -> POST /grimorio/{id}/excluir
 */
@Controller
@RequestMapping("/grimorio")
public class GrimorioController {

    private final MagiaRepository magiaRepository;

    public GrimorioController(MagiaRepository magiaRepository) {
        this.magiaRepository = magiaRepository;
    }

    // ------------------------------------------------------------------
    // Listar
    // ------------------------------------------------------------------

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer nivel,
            @RequestParam(required = false) String classe,
            @RequestParam(required = false) String componente,
            Model model) {

        String termo = (nome == null || nome.isBlank()) ? null : nome.trim();
        String indiceClasse = (classe == null || classe.isBlank()) ? null : classe.trim();
        String siglaComponente = (componente == null || componente.isBlank()) ? null : componente.trim();

        List<Magia> magias = magiaRepository.buscarComFiltros(termo, nivel, indiceClasse, siglaComponente);

        model.addAttribute("magias", magias);
        model.addAttribute("total", magiaRepository.count());
        model.addAttribute("totalDeTruques", magiaRepository.countByNivel(0));
        model.addAttribute("classes", ClasseConjuradora.values());
        model.addAttribute("componentesDisponiveis", Componente.values());
        model.addAttribute("filtroNome", termo);
        model.addAttribute("filtroNivel", nivel);
        model.addAttribute("filtroClasse", indiceClasse);
        model.addAttribute("filtroComponente", siglaComponente);
        return "grimorio/lista";
    }

    @GetMapping("/{id}")
    public String detalhar(@PathVariable Long id, Model model, RedirectAttributes atributos) {
        Optional<Magia> encontrada = magiaRepository.findById(id);
        if (encontrada.isEmpty()) {
            atributos.addFlashAttribute("erro", "Magia não encontrada.");
            return "redirect:/grimorio";
        }
        model.addAttribute("magia", encontrada.get());
        return "grimorio/detalhe";
    }

    // ------------------------------------------------------------------
    // Cadastrar / Editar
    // ------------------------------------------------------------------

    @GetMapping("/nova")
    public String formularioDeCadastro(Model model) {
        model.addAttribute("magia", new Magia());
        prepararFormulario(model);
        return "grimorio/form";
    }

    @GetMapping("/{id}/editar")
    public String formularioDeEdicao(@PathVariable Long id, Model model, RedirectAttributes atributos) {
        Optional<Magia> encontrada = magiaRepository.findById(id);
        if (encontrada.isEmpty()) {
            atributos.addFlashAttribute("erro", "Magia não encontrada.");
            return "redirect:/grimorio";
        }
        model.addAttribute("magia", encontrada.get());
        prepararFormulario(model);
        return "grimorio/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("magia") Magia formulario,
            @RequestParam(name = "componentesSelecionados", required = false) List<String> componentes,
            @RequestParam(name = "classesSelecionadas", required = false) List<String> classes,
            Model model,
            RedirectAttributes atributos) {

        if (formulario.getNome() == null || formulario.getNome().isBlank()) {
            model.addAttribute("erro", "O nome da magia é obrigatório.");
            prepararFormulario(model);
            return "grimorio/form";
        }
        if (formulario.getNivel() < 0 || formulario.getNivel() > 9) {
            model.addAttribute("erro", "O nível precisa estar entre 0 (truque) e 9.");
            prepararFormulario(model);
            return "grimorio/form";
        }

        Magia magia;
        if (formulario.getId() == null) {
            magia = new Magia();
        } else {
            magia = magiaRepository.findById(formulario.getId()).orElseGet(Magia::new);
        }

        magia.setNome(formulario.getNome().trim());
        magia.setNivel(formulario.getNivel());
        magia.setEscola(formulario.getEscola());
        magia.setTempoConjuracao(formulario.getTempoConjuracao());
        magia.setAlcance(formulario.getAlcance());
        magia.setDuracao(formulario.getDuracao());
        magia.setMateriais(formulario.getMateriais());
        magia.setRitual(formulario.isRitual());
        magia.setConcentracao(formulario.isConcentracao());
        magia.setDescricao(formulario.getDescricao());
        magia.setNiveisSuperiores(formulario.getNiveisSuperiores());
        magia.setAnotacoes(formulario.getAnotacoes());
        magia.setListaComponentes(componentes);
        magia.setListaClasses(classes);

        boolean eraNova = magia.isNovo();
        Magia salva = magiaRepository.save(magia);

        atributos.addFlashAttribute("sucesso", eraNova
                ? "\"" + salva.getNome() + "\" foi adicionada ao grimório."
                : "\"" + salva.getNome() + "\" foi atualizada.");
        return "redirect:/grimorio/" + salva.getId();
    }

    // ------------------------------------------------------------------
    // Excluir
    // ------------------------------------------------------------------

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes atributos) {
        Optional<Magia> encontrada = magiaRepository.findById(id);
        if (encontrada.isEmpty()) {
            atributos.addFlashAttribute("erro", "Magia não encontrada.");
            return "redirect:/grimorio";
        }
        String nome = encontrada.get().getNome();
        magiaRepository.deleteById(id);
        atributos.addFlashAttribute("sucesso", "\"" + nome + "\" foi removida do grimório.");
        return "redirect:/grimorio";
    }

    // ------------------------------------------------------------------

    private void prepararFormulario(Model model) {
        model.addAttribute("escolas", Escola.values());
        model.addAttribute("classes", ClasseConjuradora.values());
        model.addAttribute("componentesDisponiveis", Componente.values());
    }
}
