package com.projeto.D.D.controller;

import com.projeto.D.D.api.DndApiClient;
import com.projeto.D.D.api.RespostaApi.MagiaApi;
import com.projeto.D.D.api.RespostaApi.Referencia;
import com.projeto.D.D.model.ClasseConjuradora;
import com.projeto.D.D.model.Componente;
import com.projeto.D.D.model.Magia;
import com.projeto.D.D.repository.MagiaRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Tela de consulta a API externa do D&D 5e.
 *
 * Fluxo: o jogador escolhe classe / nivel / componentes -> a aplicacao consulta
 * /api/classes/{classe}/spells e/ou /api/spells -> busca o detalhe de cada
 * magia
 * -> converte o JSON em objetos Magia -> exibe os cartoes na interface, ja
 * indicando quais delas o jogador possui no grimorio.
 */
@Controller
public class BuscaController {

    private final DndApiClient api;
    private final MagiaRepository magiaRepository;

    public BuscaController(DndApiClient api, MagiaRepository magiaRepository) {
        this.api = api;
        this.magiaRepository = magiaRepository;
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String classe,
            @RequestParam(required = false) Integer nivel,
            @RequestParam(required = false) List<String> componentes,
            @RequestParam(required = false) String nome,
            Model model) {

        ClasseConjuradora classeEscolhida = ClasseConjuradora.peloIndice(classe);
        List<Componente> componentesExigidos = Componente.pelasSiglas(componentes);
        String termo = (nome == null || nome.isBlank()) ? null : nome.trim();

        model.addAttribute("classes", ClasseConjuradora.values());
        model.addAttribute("componentesDisponiveis", Componente.values());
        model.addAttribute("filtroClasse", classe);
        model.addAttribute("filtroNivel", nivel);
        model.addAttribute("filtroComponentes", componentes == null ? List.of() : componentes);
        model.addAttribute("filtroNome", termo);
        model.addAttribute("pesquisou", classeEscolhida != null || nivel != null || termo != null
                || !componentesExigidos.isEmpty());

        if (classeEscolhida == null && nivel == null && termo == null && componentesExigidos.isEmpty()) {
            // Primeira visita a tela: nao dispara nenhuma requisicao a API.
            model.addAttribute("resultados", List.of());
            return "busca";
        }

        try {
            List<Referencia> candidatas = reunirCandidatas(classeEscolhida, nivel);

            if (termo != null) {
                String alvo = termo.toLowerCase();
                candidatas = candidatas.stream()
                        .filter(ref -> ref.name() != null && ref.name().toLowerCase().contains(alvo))
                        .collect(Collectors.toList());
            }

            model.addAttribute("limiteAtingido", api.atingiuOLimite(candidatas.size()));
            model.addAttribute("limite", api.getLimiteDeDetalhes());

            List<MagiaApi> detalhes = api.detalhar(candidatas);

            List<Magia> resultados = new ArrayList<>();
            for (MagiaApi detalhe : detalhes) {
                Magia magia = api.converterParaEntidade(detalhe);
                if (possuiTodosOsComponentes(magia, componentesExigidos)) {
                    resultados.add(magia);
                }
            }

            Set<String> jaNoGrimorio = magiaRepository.findAll().stream()
                    .map(Magia::getIndiceApi)
                    .filter(indice -> indice != null)
                    .collect(Collectors.toCollection(HashSet::new));

            model.addAttribute("resultados", resultados);
            model.addAttribute("jaNoGrimorio", jaNoGrimorio);

        } catch (DndApiClient.ApiIndisponivelException e) {
            model.addAttribute("resultados", List.of());
            model.addAttribute("erro", e.getMessage() + " Verifique sua conexão com a internet e tente novamente.");
        }

        return "busca";
    }

    /** Importa uma magia da API para o banco de dados da aplicacao. */
    @PostMapping("/buscar/importar")
    public String importar(@RequestParam String indice, RedirectAttributes atributos) {
        if (magiaRepository.existsByIndiceApi(indice)) {
            atributos.addFlashAttribute("aviso", "Essa magia já estava no seu grimório.");
            return "redirect:/grimorio";
        }

        MagiaApi detalhe = api.buscarMagia(indice);
        if (detalhe == null) {
            atributos.addFlashAttribute("erro", "Não foi possível importar a magia da API externa.");
            return "redirect:/buscar";
        }

        Magia salva = magiaRepository.save(api.converterParaEntidade(detalhe));
        atributos.addFlashAttribute("sucesso", "\"" + salva.getNome() + "\" foi adicionada ao grimório.");
        return "redirect:/grimorio";
    }

    // ------------------------------------------------------------------

    /**
     * Monta a lista de candidatas combinando as duas rotas da API.
     * Quando classe e nivel sao informados juntos, faz a intersecao das listas.
     */
    private List<Referencia> reunirCandidatas(ClasseConjuradora classe, Integer nivel) {
        if (classe != null && nivel != null) {
            Set<String> doNivel = api.listarMagias(nivel).stream()
                    .map(Referencia::index)
                    .collect(Collectors.toSet());
            return api.listarMagiasDaClasse(classe.getIndice()).stream()
                    .filter(ref -> doNivel.contains(ref.index()))
                    .collect(Collectors.toList());
        }
        if (classe != null) {
            return new ArrayList<>(api.listarMagiasDaClasse(classe.getIndice()));
        }
        return new ArrayList<>(api.listarMagias(nivel));
    }

    private boolean possuiTodosOsComponentes(Magia magia, List<Componente> exigidos) {
        for (Componente componente : exigidos) {
            if (!magia.temComponente(componente)) {
                return false;
            }
        }
        return true;
    }
}
