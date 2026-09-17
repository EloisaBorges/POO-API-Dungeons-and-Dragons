package com.projeto.D.D.model;

/**
 * Escolas de magia do SRD 5e, com o "index" usado pela API externa.
 */
public enum Escola {

    ABJURACAO("abjuration", "Abjuracao"),
    ADIVINHACAO("divination", "Adivinhacao"),
    CONJURACAO("conjuration", "Conjuracao"),
    ENCANTAMENTO("enchantment", "Encantamento"),
    EVOCACAO("evocation", "Evocacao"),
    ILUSAO("illusion", "Ilusao"),
    NECROMANCIA("necromancy", "Necromancia"),
    TRANSMUTACAO("transmutation", "Transmutacao");

    private final String indice;
    private final String nome;

    Escola(String indice, String nome) {
        this.indice = indice;
        this.nome = nome;
    }

    public String getIndice() {
        return indice;
    }

    public String getNome() {
        return nome;
    }

    public static Escola peloIndice(String indice) {
        if (indice == null) {
            return null;
        }
        for (Escola escola : values()) {
            if (escola.indice.equalsIgnoreCase(indice.trim())) {
                return escola;
            }
        }
        return null;
    }
}
