# Grimório de Magias

Trabalho de POO - 3º Bimestre - IFPR Cascavel.

## Integrantes

- Eloísa Borges
- Kamile Kortz

## Tema

Aplicação para jogadores de D&D guardarem suas magias. Dá para cadastrar
magias na mão ou buscar numa API e salvar no banco de dados.

## Descrição do sistema

- `/magias` - lista as magias salvas, com filtro por nome, classe, nível e componente.
  Cada carta mostra os dados da magia e a descrição (botão "Ver descrição").
  Dá para cadastrar, editar e excluir (CRUD).
- `/buscar` - busca magias na API por nome (em inglês), classe e/ou nível e mostra o resultado na tela.

## API usada

- D&D 5e SRD API: https://www.dnd5eapi.co/api
- Documentação: https://5e-bits.github.io/docs/api

Rotas usadas:
- `GET /api/spells?name={nome}` - procura magias pelo nome
- `GET /api/classes/{classe}/spells` - lista as magias de uma classe
- `GET /api/spells?level={nivel}` - lista as magias de um nível
- `GET /api/spells/{indice}` - detalhes de uma magia

## Funcionalidade feita com a API

O usuário digita um nome e/ou escolhe uma classe e/ou um nível na tela de busca. A aplicação
consulta a API, recebe o JSON e mostra para cada magia o nome, nível, escola,
tempo de conjuração, alcance, duração, componentes e a descrição completa. Em cada resultado tem o
botão **Adicionar ao grimório**, que pega os dados da API, monta um objeto
`Magia` e salva no banco de dados. Se a magia já foi adicionada antes, a
aplicação avisa e não salva de novo.

## Tecnologias

- Java 17 + Spring Boot
- Spring Web + Spring Data JPA
- Banco H2 (arquivo, fica salvo na pasta `dados/`)
- Thymeleaf + HTML

## Estrutura (MVC)

- `model/Magia.java` - entidade
- `repository/MagiaRepository.java` - JpaRepository
- `controller/` - controllers (`MagiaController` faz o CRUD, `BuscaController` usa a API)
- `controller/DndApiClient.java` - classe que faz as chamadas HTTP para a API
- `resources/templates/` - páginas Thymeleaf

## Como rodar

Entrar na pasta do projeto:

```
cd "D&D"
```

Windows (PowerShell):

```
.\mvnw.cmd spring-boot:run
```

Windows (Prompt de Comando / cmd):

```
mvnw.cmd spring-boot:run
```

Linux / Mac:

```
sh mvnw spring-boot:run
```

Também dá para abrir a pasta `D&D` na IDE e rodar a classe `Application`.

Depois é só abrir `http://localhost:8080` no navegador.
(Precisa de internet para a busca na API funcionar.)
