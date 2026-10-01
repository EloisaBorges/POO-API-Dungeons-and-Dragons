package com.projeto.D.D.controller;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DndApiClient {

    private final RestTemplate rest = new RestTemplate();
    private final String base = "https://www.dnd5eapi.co/api";

    public List<Map> listarPorClasse(String classe) {
        Map resposta = rest.getForObject(base + "/classes/" + classe + "/spells", Map.class);
        return (List<Map>) resposta.get("results");
    }

    public List<Map> listarPorNivel(int nivel) {
        Map resposta = rest.getForObject(base + "/spells?level=" + nivel, Map.class);
        return (List<Map>) resposta.get("results");
    }

    public Map detalhar(String indice) {
        try {
            return rest.getForObject(base + "/spells/" + indice, Map.class);
        } catch (Exception e) {
            return null;
        }
    }
}
