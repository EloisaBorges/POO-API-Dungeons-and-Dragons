# Grimório de Magias — Digital Spellbook

Trabalho do **3º bimestre** da disciplina de Programação Orientada a Objetos
— IFPR, Campus Cascavel, 3º ano de Informática.

## Integrantes

- Eloísa Borges
- *(preencher o nome do(a) colega de dupla)*

## Tema

**Grimório de Magias (Digital Spellbook)** — um aplicativo web para jogadores de
classes conjuradoras de D&D 5e (Mago, Clérigo, Bardo e as demais) buscarem
magias, montarem o próprio grimório e marcarem quais magias estão preparadas
durante a partida.

## Descrição do sistema

A aplicação tem três telas principais:

| Tela | Rota | O que faz |
|---|---|---|
| **Buscar Magias** | `/buscar` | Consulta a API externa do D&D 5e com filtros de classe, nível e componentes. Cada resultado pode ser importado para o banco com um clique. |
| **Meu Grimório** | `/grimorio` | CRUD completo das magias salvas no banco de dados (cadastrar, listar, editar e excluir), com os mesmos filtros aplicados sobre os dados locais. |
| **Favoritas / Preparadas** | `/preparadas` | Mostra apenas as magias que o jogador marcou com a estrela ★. Essa seleção é salva no **localStorage do próprio navegador**. |

### Filtros disponíveis

- **Classe conjuradora**: Bardo, Bruxo, Clérigo, Druida, Feiticeiro, Mago, Paladino e Patrulheiro.
- **Nível da magia**: truque (nível 0) até 9º círculo.
- **Componentes**: **V** (Verbal), **S** (Somática) e **M** (Material).
- **Nome** da magia.

### Magias preparadas (localStorage)

O botão de estrela presente nas cartas e na tela de detalhe grava o `id` da
magia em `localStorage["grimorio.preparadas"]`. O contador no menu e a aba
"Preparadas" leem essa mesma lista. O comportamento é intencional: cada jogador,
no seu próprio navegador, monta a lista de magias preparadas do dia sem precisar
de login e sem alterar o grimório compartilhado que está no banco.

## API externa utilizada

- **Nome**: D&D 5e SRD API
- **URL base**: <https://www.dnd5eapi.co/api>
- **Documentação**: <https://5e-bits.github.io/docs/introduction> · <https://5e-bits.github.io/docs/api>
- **Autenticação**: nenhuma (API pública, somente `GET`)

### Rotas consumidas

| Rota | Uso na aplicação |
|---|---|
| `GET /api/spells` | Lista geral de magias; aceita `?level=` para filtrar por nível direto na API. |
| `GET /api/classes/{classe}/spells` | Lista as magias de uma classe conjuradora (ex.: `/api/classes/wizard/spells`). |
| `GET /api/spells/{indice}` | Detalhe completo de uma magia (descrição, escola, componentes, alcance, duração, classes). |

### Funcionalidade implementada com a API

1. O usuário escolhe **classe**, **nível** e/ou **componentes** na tela *Buscar Magias*.
2. A aplicação monta a consulta: com classe e nível juntos, ela busca as duas
   listas (`/api/classes/{classe}/spells` e `/api/spells?level=N`) e faz a
   **interseção** dos resultados, já que a API não combina esses dois filtros.
3. Como a listagem devolve apenas `{ index, name, url }`, a aplicação busca o
   **detalhe de cada magia** em `/api/spells/{indice}` (requisições em paralelo,
   com cache em memória para não repetir chamadas).
4. O JSON é convertido em objetos `Magia` e exibido em cartas formatadas —
   nível, escola, componentes, tempo de conjuração, alcance, duração, se é
   ritual, se exige concentração e quais classes conjuram. **Em nenhum momento o
   JSON bruto é exibido.**
5. O filtro por componentes é aplicado sobre os dados já convertidos, porque a
   API não oferece esse filtro.
6. Ao clicar em *Adicionar ao grimório*, a magia é buscada novamente pelo índice
   e **persistida no banco** via JPA, passando a participar do CRUD.

## Requisitos técnicos atendidos

- **Java 17 + Spring Boot 4.1.1**
- **Spring Web** (MVC + `RestClient` para consumir a API externa)
- **Spring Data JPA** com `JpaRepository`
- **Banco de dados H2** em arquivo (`./dados/grimorio.mv.db`), os dados
  permanecem entre execuções
- **Thymeleaf + HTML** em todas as telas, com fragmentos reutilizáveis

### Arquitetura MVC

```
src/main/java/com/projeto/D/D/
├── Application.java
├── model/
│   ├── Descritivel.java        (interface — abstração)
│   ├── ItemDoGrimorio.java     (classe abstrata @MappedSuperclass — herança)
│   ├── Magia.java              (@Entity — entidade principal)
│   ├── ClasseConjuradora.java  (enum: índice da API + nome em português)
│   ├── Componente.java         (enum: V, S, M)
│   └── Escola.java             (enum das escolas de magia)
├── repository/
│   └── MagiaRepository.java    (JpaRepository + consultas derivadas + @Query)
├── api/
│   ├── DndApiClient.java       (cliente HTTP da API externa + conversão)
│   └── RespostaApi.java        (records que espelham o JSON recebido)
└── controller/
    ├── HomeController.java
    ├── BuscaController.java    (consulta e importação da API)
    ├── GrimorioController.java (CRUD)
    └── PreparadasController.java

src/main/resources/
├── application.properties
├── templates/
│   ├── fragments/layout.html   (cabeçalho, menu e rodapé reutilizáveis)
│   ├── busca.html
│   ├── preparadas.html
│   └── grimorio/{lista,form,detalhe}.html
└── static/
    ├── css/estilo.css
    └── js/preparadas.js
```

### Conceitos de POO aplicados

- **Encapsulamento**: todos os atributos de `Magia` são privados e acessados por
  métodos; os componentes e as classes são guardados como texto no banco, mas o
  acesso externo acontece sempre por métodos que devolvem enums.
- **Herança**: `Magia` estende a classe abstrata `ItemDoGrimorio`, que concentra
  `id`, data de criação, `equals`/`hashCode` e `toString`.
- **Abstração e polimorfismo**: a interface `Descritivel` define `getResumo()` e
  `getTitulo()`, implementados por `Magia` e usados diretamente pelos templates.
- **Enumerações com comportamento**: `Componente`, `ClasseConjuradora` e `Escola`
  guardam o índice usado pela API e sabem se converter a partir dele.
- **Regras na própria entidade**: `isTruque()`, `getNivelFormatado()`,
  `temComponente()`, `ehConjuradaPor()`, `getComponentesFormatados()`.

## Como executar

```bash
cd "D&D/D-D"

# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Depois acesse <http://localhost:8080>.

O console do banco fica em <http://localhost:8080/h2-console>
(JDBC URL `jdbc:h2:file:./dados/grimorio`, usuário `sa`, senha em branco).

> A tela *Buscar Magias* precisa de conexão com a internet. Se a API estiver
> fora do ar, a aplicação exibe um aviso e continua funcionando normalmente com
> as magias já salvas no banco.

## Créditos

Dados das magias fornecidos pela [D&D 5e SRD API](https://www.dnd5eapi.co/api),
mantida pelo projeto [5e-bits](https://github.com/5e-bits), sob a licença do
System Reference Document 5.1 (Wizards of the Coast, OGL).
