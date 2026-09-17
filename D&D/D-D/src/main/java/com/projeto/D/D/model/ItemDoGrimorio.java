package com.projeto.D.D.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Superclasse abstrata de tudo que pode ser guardado no grimorio.
 * Concentra o identificador e a data de criacao, evitando repeticao
 * de codigo nas entidades filhas (heranca + encapsulamento).
 */
@MappedSuperclass
public abstract class ItemDoGrimorio implements Descritivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    /** Verdadeiro enquanto o item ainda nao foi persistido. */
    public boolean isNovo() {
        return id == null;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof ItemDoGrimorio item)) {
            return false;
        }
        return id != null && Objects.equals(id, item.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getTitulo();
    }
}
