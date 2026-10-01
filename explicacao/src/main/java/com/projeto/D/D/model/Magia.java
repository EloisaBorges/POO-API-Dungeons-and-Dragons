package com.projeto.D.D.model;

// Essas importações são classes do Spring/JPA que usamos para transformar
// esta classe em uma "tabela" do banco de dados.
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

// @Entity avisa o Spring: "essa classe representa uma tabela no banco".
// O nome da tabela vai ser "magia" (o nome da classe em minúsculo).
@Entity
public class Magia {

    // @Id diz que este campo é a chave primária (o identificador único de
    // cada magia no banco, tipo o "número de registro").
    // @GeneratedValue faz o próprio banco gerar esse número sozinho (1, 2, 3...).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome da magia, ex: "Bola de Fogo".
    private String nome;

    // Nível da magia. 0 significa "truque" (magia que não gasta espaço de
    // magia), e vai até 9.
    private int nivel;

    // Escola de magia, ex: "Evocação", "Ilusão".
    private String escola;

    // Quanto tempo leva para conjurar, ex: "1 ação".
    private String tempoConjuracao;

    // Até onde a magia alcança, ex: "18 metros".
    private String alcance;

    // Quanto tempo o efeito dura, ex: "Instantânea", "1 minuto".
    private String duracao;

    // Quais componentes a magia precisa: V (verbal), S (somática), M (material).
    // Aqui guardamos como um texto só, tipo "V, S, M".
    private String componentes;

    // Quais classes podem usar essa magia, ex: "Mago, Feiticeiro".
    private String classes;

    // Texto explicando o que a magia faz.
    // @Column(length = 5000) aumenta o tamanho máximo do texto no banco,
    // porque a descrição pode ser bem grande.
    @Column(length = 5000)
    private String descricao;

    // Guarda o "código" da magia na API externa (ex: "fireball"), caso ela
    // tenha vindo de lá. Se o jogador criou a magia na mão, isso fica vazio.
    private String indiceApi;

    // ---------------------------------------------------------------
    // Daqui pra baixo só tem "getters" e "setters": métodos que servem
    // para LER (get) e ALTERAR (set) cada um dos campos acima.
    // O Spring e o Thymeleaf usam esses métodos por trás dos panos sempre
    // que a gente escreve, por exemplo, magia.nome no HTML.
    // ---------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEscola() {
        return escola;
    }

    public void setEscola(String escola) {
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

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIndiceApi() {
        return indiceApi;
    }

    public void setIndiceApi(String indiceApi) {
        this.indiceApi = indiceApi;
    }
}
