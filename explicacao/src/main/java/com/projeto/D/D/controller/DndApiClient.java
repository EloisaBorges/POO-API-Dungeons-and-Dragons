package com.projeto.D.D.controller;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// @Component diz ao Spring: "crie um objeto dessa classe sozinho e deixe
// disponível para outras classes usarem" (isso se chama injeção de dependência).
// É por isso que nos outros controllers a gente só escreve "DndApiClient api"
// no construtor e o Spring entrega o objeto pronto, sem a gente precisar
// escrever "new DndApiClient()".
@Component
public class DndApiClient {

    // RestTemplate é a ferramenta do Spring para fazer requisições HTTP,
    // tipo um "navegador" que a nossa aplicação usa para conversar com
    // outros sites/serviços pela internet.
    private final RestTemplate rest = new RestTemplate();

    // Endereço base da API de D&D que estamos usando.
    private final String base = "https://www.dnd5eapi.co/api";

    // Busca a lista de magias de uma classe específica.
    // Exemplo de chamada: GET https://www.dnd5eapi.co/api/classes/wizard/spells
    // A API responde um JSON parecido com:
    //   { "count": 5, "results": [ {"index": "fireball", "name": "Fireball", ...}, ... ] }
    // getForObject já transforma esse JSON direto em um Map do Java.
    public List<Map> listarPorClasse(String classe) {
        Map resposta = rest.getForObject(base + "/classes/" + classe + "/spells", Map.class);
        // "results" é a chave do JSON que tem a lista de magias.
        return (List<Map>) resposta.get("results");
    }

    // Busca a lista de magias de um nível específico (0 a 9).
    // Exemplo: GET https://www.dnd5eapi.co/api/spells?level=3
    public List<Map> listarPorNivel(int nivel) {
        Map resposta = rest.getForObject(base + "/spells?level=" + nivel, Map.class);
        return (List<Map>) resposta.get("results");
    }

    // Busca os detalhes completos de UMA magia, usando o "index" dela
    // (ex: "fireball"). É aqui que vem a descrição, componentes, alcance etc.
    // Exemplo: GET https://www.dnd5eapi.co/api/spells/fireball
    public Map detalhar(String indice) {
        try {
            return rest.getForObject(base + "/spells/" + indice, Map.class);
        } catch (Exception e) {
            // Se der qualquer erro (internet caiu, a API está fora do ar,
            // o índice não existe...), devolvemos null em vez de quebrar
            // a aplicação inteira.
            return null;
        }
    }
}
