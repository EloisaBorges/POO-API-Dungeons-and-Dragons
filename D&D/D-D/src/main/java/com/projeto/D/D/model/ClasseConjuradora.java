package com.projeto.D.D.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classes capazes de conjurar magias no SRD 5e.
 * Cada constante guarda o "index" usado pela API externa
 * (ex.: /api/classes/wizard/spells) e o nome em portugues.
 */
public enum ClasseConjuradora {

    BARDO("bard", "Bardo"),
    BRUXO("warlock", "Bruxo"),
    CLERIGO("cleric", "Clerigo"),
    DRUIDA("druid", "Druida"),
    FEITICEIRO("sorcerer", "Feiticeiro"),
    MAGO("wizard", "Mago"),
    PALADINO("paladin", "Paladino"),
    PATRULHEIRO("ranger", "Patrulheiro");

    private final String indice;
    private final String nome;

    ClasseConjuradora(String indice, String nome) {
        this.indice = indice;
        this.nome = nome;
    }

    public String getIndice() {
        return indice;
    }

    public String getNome() {
        return nome;
    }

    /** Recupera a classe a partir do "index" devolvido pela API. */
    public static ClasseConjuradora peloIndice(String indice) {
        if (indice == null) {
            return null;
        }
        for (ClasseConjuradora classe : values()) {
            if (classe.indice.equalsIgnoreCase(indice.trim())) {
                return classe;
            }
        }
        return null;
    }

    /** Traduz uma lista de indices da API, ignorando classes nao conjuradoras. */
    public static List<ClasseConjuradora> pelosIndices(List<String> indices) {
        List<ClasseConjuradora> lista = new ArrayList<>();
        if (indices == null) {
            return lista;
        }
        for (String indice : indices) {
            ClasseConjuradora classe = peloIndice(indice);
            if (classe != null && !lista.contains(classe)) {
                lista.add(classe);
            }
        }
        return lista;
    }
}
