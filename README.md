# Grimório de Magias

Trabalho de POO - 3º Bimestre - IFPR Cascavel.

## Integrantes

- Eloísa Borges
- (nome da dupla)

## Tema

App para jogadores de D&D guardarem suas magias favoritas. Dá pra buscar
magias numa API, salvar no banco e marcar quais estão preparadas.

## Telas

- `/magias` - lista as magias salvas, com filtro por classe, nível e componente. Dá pra cadastrar, editar e excluir.
- `/buscar` - busca magias direto na API, por classe e nível. Dá pra importar pro grimório.
- `/preparadas` - mostra só as magias marcadas com estrela. Essa marcação fica salva no navegador (localStorage).

## API usada

- D&D 5e SRD API: https://www.dnd5eapi.co/api
- Documentação: https://5e-bits.github.io/docs/api

Rotas usadas:
- `GET /api/classes/{classe}/spells`
- `GET /api/spells?level={nivel}`
- `GET /api/spells/{indice}`

## Tecnologias

- Java + Spring Boot
- Spring Web + Spring Data JPA
- Banco H2 (arquivo, fica salvo em `dados/`)
- Thymeleaf + HTML

## Como rodar

Entrar na pasta do projeto:

```
cd "D&D/D-D"
```

Rodar:

```
./mvnw spring-boot:run
```

No Windows:

```
mvnw.cmd spring-boot:run
```

Depois é só abrir `http://localhost:8080` no navegador.
