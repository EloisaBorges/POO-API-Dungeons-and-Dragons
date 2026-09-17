package com.projeto.D.D.api;

import com.projeto.D.D.api.RespostaApi.Listagem;
import com.projeto.D.D.api.RespostaApi.MagiaApi;
import com.projeto.D.D.api.RespostaApi.Referencia;
import com.projeto.D.D.model.Escola;
import com.projeto.D.D.model.Magia;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Responsavel por conversar com a API externa do D&D 5e
 * (https://www.dnd5eapi.co/api).
 *
 * Rotas utilizadas:
 * GET /api/spells -> lista de magias (aceita ?level= e ?school=)
 * GET /api/classes/{classe}/spells -> magias de uma classe conjuradora
 * GET /api/spells/{indice} -> detalhe completo de uma magia
 *
 * Como o endpoint de listagem devolve apenas { index, name, url }, o detalhe de
 * cada magia precisa ser buscado a parte. Para nao repetir requisicoes, os
 * detalhes ja carregados ficam guardados em um cache na memoria.
 */
@Component
public class DndApiClient {

    private static final Logger log = LoggerFactory.getLogger(DndApiClient.class);
    private static final String URL_BASE = "https://www.dnd5eapi.co/api";

    /** Teto de magias detalhadas por busca, para a tela nao demorar demais. */
    private static final int LIMITE_DE_DETALHES = 120;

    private final RestClient rest;
    private final Map<String, MagiaApi> cacheDeDetalhes = new ConcurrentHashMap<>();

    public DndApiClient(RestClient.Builder builder) {
        this.rest = builder.baseUrl(URL_BASE).build();
    }

    // ------------------------------------------------------------------
    // Chamadas a API
    // ------------------------------------------------------------------

    /** GET /api/spells (opcionalmente filtrando por nivel direto na API). */
    public List<Referencia> listarMagias(Integer nivel) {
        try {
            Listagem listagem = rest.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/spells");
                        if (nivel != null) {
                            uriBuilder.queryParam("level", nivel);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(Listagem.class);
            return listagem == null ? List.of() : listagem.resultadosSeguros();
        } catch (Exception e) {
            log.warn("Falha ao listar magias na API externa: {}", e.getMessage());
            throw new ApiIndisponivelException("Não foi possível consultar a lista de magias.", e);
        }
    }

    /** GET /api/classes/{classe}/spells */
    public List<Referencia> listarMagiasDaClasse(String indiceDaClasse) {
        try {
            Listagem listagem = rest.get()
                    .uri("/classes/{classe}/spells", indiceDaClasse)
                    .retrieve()
                    .body(Listagem.class);
            return listagem == null ? List.of() : listagem.resultadosSeguros();
        } catch (Exception e) {
            log.warn("Falha ao listar magias da classe {}: {}", indiceDaClasse, e.getMessage());
            throw new ApiIndisponivelException("Não foi possível consultar as magias da classe.", e);
        }
    }

    /** GET /api/spells/{indice}, com cache em memoria. */
    public MagiaApi buscarMagia(String indice) {
        MagiaApi emCache = cacheDeDetalhes.get(indice);
        if (emCache != null) {
            return emCache;
        }
        try {
            MagiaApi magia = rest.get()
                    .uri("/spells/{indice}", indice)
                    .retrieve()
                    .body(MagiaApi.class);
            if (magia != null) {
                cacheDeDetalhes.put(indice, magia);
            }
            return magia;
        } catch (Exception e) {
            log.warn("Falha ao buscar a magia {}: {}", indice, e.getMessage());
            return null;
        }
    }

    /**
     * Carrega o detalhe de varias magias de uma vez. As requisicoes sao feitas em
     * paralelo porque cada magia e um GET independente.
     */
    public List<MagiaApi> detalhar(List<Referencia> referencias) {
        List<Referencia> recortadas = referencias.size() > LIMITE_DE_DETALHES
                ? referencias.subList(0, LIMITE_DE_DETALHES)
                : referencias;

        List<MagiaApi> detalhes = new ArrayList<>(recortadas.parallelStream()
                .map(referencia -> buscarMagia(referencia.index()))
                .filter(magia -> magia != null)
                .toList());

        detalhes.sort(Comparator.comparingInt(MagiaApi::nivelSeguro)
                .thenComparing(MagiaApi::name, String.CASE_INSENSITIVE_ORDER));
        return detalhes;
    }

    public boolean atingiuOLimite(int quantidadeDeCandidatas) {
        return quantidadeDeCandidatas > LIMITE_DE_DETALHES;
    }

    public int getLimiteDeDetalhes() {
        return LIMITE_DE_DETALHES;
    }

    // ------------------------------------------------------------------
    // Conversao API -> entidade
    // ------------------------------------------------------------------

    /** Transforma o JSON recebido em uma entidade pronta para ser persistida. */
    public Magia converterParaEntidade(MagiaApi origem) {
        Magia magia = new Magia();
        magia.setIndiceApi(origem.index());
        magia.setNome(origem.name());
        magia.setNivel(origem.nivelSeguro());
        magia.setEscola(origem.school() == null ? null : Escola.peloIndice(origem.school().index()));
        magia.setTempoConjuracao(origem.casting_time());
        magia.setAlcance(origem.range());
        magia.setDuracao(origem.duration());
        magia.setListaComponentes(origem.componentesSeguros());
        magia.setMateriais(origem.material());
        magia.setRitual(origem.ritualSeguro());
        magia.setConcentracao(origem.concentracaoSegura());
        magia.setDescricao(recortar(origem.descricaoUnificada(), 9900));
        magia.setNiveisSuperiores(recortar(origem.niveisSuperioresUnificados(), 3900));
        magia.setListaClasses(origem.indicesDasClasses());
        magia.setOrigemApi(true);
        return magia;
    }

    private String recortar(String texto, int limite) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= limite ? texto : texto.substring(0, limite);
    }

    /** Erro de comunicacao com a API externa, tratado pelos controllers. */
    public static class ApiIndisponivelException extends RuntimeException {
        public ApiIndisponivelException(String mensagem, Throwable causa) {
            super(mensagem, causa);
        }
    }
}
