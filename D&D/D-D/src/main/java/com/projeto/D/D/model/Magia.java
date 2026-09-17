package com.projeto.D.D.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entidade principal da aplicacao: uma magia guardada no grimorio do jogador.
 *
 * Os componentes e as classes sao guardados como texto separado por virgula
 * ("V,S,M" / "wizard,cleric") para simplificar as consultas, mas o acesso
 * externo acontece sempre por metodos que devolvem os enums correspondentes,
 * mantendo os atributos encapsulados.
 */
@Entity
@Table(name = "magias")
public class Magia extends ItemDoGrimorio {

    /**
     * Identificador da magia na API externa (ex.: "fireball"). Nulo se foi criada a
     * mao.
     */
    @Column(name = "indice_api", unique = true, length = 120)
    private String indiceApi;

    @Column(nullable = false, length = 150)
    private String nome;

    /** 0 = truque (cantrip), 1 a 9 = circulos de magia. */
    @Column(nullable = false)
    private int nivel;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Escola escola;

    @Column(name = "tempo_conjuracao", length = 150)
    private String tempoConjuracao;

    @Column(length = 150)
    private String alcance;

    @Column(length = 150)
    private String duracao;

    /** Siglas separadas por virgula: "V,S,M". */
    @Column(length = 20)
    private String componentes = "";

    @Column(length = 600)
    private String materiais;

    @Column(nullable = false)
    private boolean ritual;

    @Column(nullable = false)
    private boolean concentracao;

    @Column(length = 10000)
    private String descricao;

    @Column(name = "niveis_superiores", length = 4000)
    private String niveisSuperiores;

    /** Indices das classes separados por virgula: "wizard,sorcerer". */
    @Column(length = 250)
    private String classes = "";

    /** Marca se a magia veio da API externa ou foi cadastrada manualmente. */
    @Column(name = "origem_api", nullable = false)
    private boolean origemApi;

    /** Anotacoes livres do jogador sobre como usa a magia na mesa. */
    @Column(length = 2000)
    private String anotacoes;

    public Magia() {
    }

    public Magia(String nome, int nivel, Escola escola) {
        this.nome = nome;
        this.nivel = nivel;
        this.escola = escola;
    }

    // ------------------------------------------------------------------
    // Regras de negocio da entidade
    // ------------------------------------------------------------------

    /** Truques podem ser conjurados a vontade, sem gastar espaco de magia. */
    public boolean isTruque() {
        return nivel == 0;
    }

    /** Ex.: "Truque", "3o circulo". */
    public String getNivelFormatado() {
        return isTruque() ? "Truque" : nivel + "o circulo";
    }

    public List<Componente> getListaComponentes() {
        return separar(componentes).stream()
                .map(Componente::pelaSigla)
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    public void setListaComponentes(List<String> siglas) {
        this.componentes = Componente.pelasSiglas(siglas).stream()
                .map(Componente::getSigla)
                .collect(Collectors.joining(","));
    }

    public boolean temComponente(Componente componente) {
        return getListaComponentes().contains(componente);
    }

    /** Ex.: "V, S, M". */
    public String getComponentesFormatados() {
        List<Componente> lista = getListaComponentes();
        return lista.isEmpty() ? "-"
                : lista.stream()
                        .map(Componente::getSigla)
                        .collect(Collectors.joining(", "));
    }

    public List<ClasseConjuradora> getListaClasses() {
        return ClasseConjuradora.pelosIndices(separar(classes));
    }

    public void setListaClasses(List<String> indices) {
        this.classes = ClasseConjuradora.pelosIndices(indices).stream()
                .map(ClasseConjuradora::getIndice)
                .collect(Collectors.joining(","));
    }

    public boolean ehConjuradaPor(ClasseConjuradora classe) {
        return getListaClasses().contains(classe);
    }

    public String getClassesFormatadas() {
        List<ClasseConjuradora> lista = getListaClasses();
        return lista.isEmpty() ? "-"
                : lista.stream()
                        .map(ClasseConjuradora::getNome)
                        .collect(Collectors.joining(", "));
    }

    /** Selo curto exibido nos cartoes: "Evocacao - 3o circulo". */
    @Override
    public String getResumo() {
        String nomeEscola = (escola == null) ? "Escola desconhecida" : escola.getNome();
        return nomeEscola + " - " + getNivelFormatado();
    }

    @Override
    public String getTitulo() {
        return nome;
    }

    private List<String> separar(String texto) {
        List<String> partes = new ArrayList<>();
        if (texto == null || texto.isBlank()) {
            return partes;
        }
        for (String parte : texto.split(",")) {
            if (!parte.isBlank()) {
                partes.add(parte.trim());
            }
        }
        return partes;
    }

    // ------------------------------------------------------------------
    // Getters e setters
    // ------------------------------------------------------------------

    public String getIndiceApi() {
        return indiceApi;
    }

    public void setIndiceApi(String indiceApi) {
        this.indiceApi = indiceApi;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public Escola getEscola() {
        return escola;
    }

    public void setEscola(Escola escola) {
        this.escola = escola;
    }

    public String getTempoConjuracao() {
        return tempoConjuracao;
    }

    public void setTempoConjuracao(String tempoConjuracao) {
        this.tempoConjuracao = tempoConjuracao;
    }

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getComponentes() {
        return componentes;
    }

    public void setComponentes(String componentes) {
        this.componentes = componentes;
    }

    public String getMateriais() {
        return materiais;
    }

    public void setMateriais(String materiais) {
        this.materiais = materiais;
    }

    public boolean isRitual() {
        return ritual;
    }

    public void setRitual(boolean ritual) {
        this.ritual = ritual;
    }

    public boolean isConcentracao() {
        return concentracao;
    }

    public void setConcentracao(boolean concentracao) {
        this.concentracao = concentracao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNiveisSuperiores() {
        return niveisSuperiores;
    }

    public void setNiveisSuperiores(String niveisSuperiores) {
        this.niveisSuperiores = niveisSuperiores;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public boolean isOrigemApi() {
        return origemApi;
    }

    public void setOrigemApi(boolean origemApi) {
        this.origemApi = origemApi;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }
}
