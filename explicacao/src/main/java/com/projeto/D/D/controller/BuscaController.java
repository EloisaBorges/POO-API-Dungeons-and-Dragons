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

// Esse controller cuida da tela de BUSCA, que é onde a gente conversa
// com a API externa (a internet) em vez de olhar só o nosso banco.
@Controller
public class BuscaController {

    // api  -> quem sabe conversar com a API do D&D
    // repository -> quem sabe salvar magias no NOSSO banco
    private final DndApiClient api;
    private final MagiaRepository repository;

    public BuscaController(DndApiClient api, MagiaRepository repository) {
        this.api = api;
        this.repository = repository;
    }

    // TELA DE BUSCA --------------------------------------------------
    // GET /buscar?classe=wizard&nivel=3
    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String classe,
                         @RequestParam(required = false) Integer nivel,
                         Model model) {

        // Lista onde vamos guardar o "detalhe completo" de cada magia
        // encontrada (nome, descrição, componentes etc).
        List<Map> encontradas = new ArrayList<>();

        try {
            // Se o usuário escolheu uma classe...
            if (classe != null && !classe.isBlank()) {
                // 1) busca a lista de magias daquela classe (só nome e índice)
                List<Map> referencias = api.listarPorClasse(classe);

                // 2) para cada magia da lista, busca o detalhe completo
                for (Map referencia : referencias) {
                    Map detalhe = api.detalhar((String) referencia.get("index"));

                    // Se também escolheu um nível, só guarda a magia se
                    // o nível bater. Senão, guarda todas.
                    if (detalhe != null && (nivel == null || nivel.equals(detalhe.get("level")))) {
                        encontradas.add(detalhe);
                    }
                }

            // Se não escolheu classe, mas escolheu nível...
            } else if (nivel != null) {
                List<Map> referencias = api.listarPorNivel(nivel);
                for (Map referencia : referencias) {
                    Map detalhe = api.detalhar((String) referencia.get("index"));
                    if (detalhe != null) {
                        encontradas.add(detalhe);
                    }
                }
            }
            // Se não escolheu nada, "encontradas" fica vazia mesmo -
            // a tela vai pedir pro usuário escolher um filtro.

        } catch (Exception e) {
            // Se a internet cair ou a API estiver fora do ar, mostramos
            // um aviso em vez de travar a aplicação.
            model.addAttribute("erro", "Não foi possível consultar a API. Verifique sua internet.");
        }

        model.addAttribute("magias", encontradas);
        model.addAttribute("classe", classe);
        model.addAttribute("nivel", nivel);
        return "busca";
    }

    // IMPORTAR PARA O GRIMÓRIO ----------------------------------------
    // POST /buscar/importar  (chamado quando clica em "Adicionar ao grimório")
    @PostMapping("/buscar/importar")
    public String importar(@RequestParam String indice, RedirectAttributes attrs) {

        // Busca de novo o detalhe completo da magia na API, usando o índice
        // (ex: "fireball") que veio escondido no formulário.
        Map detalhe = api.detalhar(indice);
        if (detalhe == null) {
            // addFlashAttribute guarda uma mensagem que sobrevive a UM
            // redirecionamento - é assim que a mensagem de erro aparece
            // na próxima tela.
            attrs.addFlashAttribute("erro", "Não foi possível importar essa magia.");
            return "redirect:/buscar";
        }

        // Agora vamos "traduzir" o JSON da API (que é um Map) para o
        // nosso objeto Magia, que é o que o nosso banco entende.
        Magia magia = new Magia();
        magia.setIndiceApi(indice);
        magia.setNome((String) detalhe.get("name"));
        magia.setNivel((Integer) detalhe.get("level"));

        // "school" vem como um objeto dentro do JSON, por isso pegamos
        // ele como outro Map e depois lemos o campo "name" de dentro dele.
        Map escola = (Map) detalhe.get("school");
        magia.setEscola(escola != null ? (String) escola.get("name") : "");

        magia.setTempoConjuracao((String) detalhe.get("casting_time"));
        magia.setAlcance((String) detalhe.get("range"));
        magia.setDuracao((String) detalhe.get("duration"));

        // "components" vem como uma lista, tipo ["V", "S", "M"].
        // Transformamos em um texto só: "V, S, M".
        List<String> componentes = (List<String>) detalhe.get("components");
        magia.setComponentes(componentes != null ? String.join(", ", componentes) : "");

        // "classes" vem como uma lista de objetos. Pegamos o nome de cada
        // um e juntamos tudo em um texto só, separado por vírgula.
        List<Map> classes = (List<Map>) detalhe.get("classes");
        List<String> nomesClasses = new ArrayList<>();
        if (classes != null) {
            for (Map c : classes) {
                nomesClasses.add((String) c.get("name"));
            }
        }
        magia.setClasses(String.join(", ", nomesClasses));

        // "desc" vem como uma lista de parágrafos. Juntamos todos em um
        // texto só, com uma linha em branco entre cada parágrafo.
        List<String> desc = (List<String>) detalhe.get("desc");
        magia.setDescricao(desc != null ? String.join("\n\n", desc) : "");

        // Agora sim: salva essa magia no NOSSO banco de dados.
        repository.save(magia);

        attrs.addFlashAttribute("sucesso", "Magia adicionada ao grimório!");
        return "redirect:/magias";
    }
}
