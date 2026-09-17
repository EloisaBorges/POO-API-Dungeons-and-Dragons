package com.projeto.D.D.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Componentes necessarios para conjurar uma magia.
 * A API retorna as siglas em ingles ("V", "S", "M"), que sao as mesmas
 * usadas na traducao brasileira do jogo.
 */
public enum Componente {

    V("V", "Verbal", "Exige palavras magicas pronunciadas em voz alta."),
    S("S", "Somatica", "Exige gestos precisos com as maos."),
    M("M", "Material", "Exige um componente material especifico.");

    private final String sigla;
    private final String nome;
    private final String explicacao;

    Componente(String sigla, String nome, String explicacao) {
        this.sigla = sigla;
        this.nome = nome;
        this.explicacao = explicacao;
    }

    public String getSigla() {
        return sigla;
    }

    public String getNome() {
        return nome;
    }

    public String getExplicacao() {
        return explicacao;
    }

    /** Converte a sigla vinda da API (ou do formulario) no enum correspondente. */
    public static Componente pelaSigla(String sigla) {
        if (sigla == null) {
            return null;
        }
        for (Componente componente : values()) {
            if (componente.sigla.equalsIgnoreCase(sigla.trim())) {
                return componente;
            }
        }
        return null;
    }

    /** Converte uma lista de siglas ("V", "S") na lista de enums. */
    public static List<Componente> pelasSiglas(List<String> siglas) {
        List<Componente> lista = new ArrayList<>();
        if (siglas == null) {
            return lista;
        }
        for (String sigla : siglas) {
            Componente componente = pelaSigla(sigla);
            if (componente != null && !lista.contains(componente)) {
                lista.add(componente);
            }
        }
        return lista;
    }
}
