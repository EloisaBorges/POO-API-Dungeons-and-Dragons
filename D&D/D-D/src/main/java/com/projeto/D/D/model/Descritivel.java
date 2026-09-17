package com.projeto.D.D.model;

/**
 * Contrato que define o que todo item exibivel do grimorio precisa saber
 * responder sobre si mesmo. Usado para demonstrar abstracao/polimorfismo.
 */
public interface Descritivel {

    /** Texto curto usado nas listagens. */
    String getResumo();

    /** Titulo principal exibido nas telas. */
    String getTitulo();
}
