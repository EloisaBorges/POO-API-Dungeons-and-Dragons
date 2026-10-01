package com.projeto.D.D.repository;

import com.projeto.D.D.model.Magia;
import org.springframework.data.jpa.repository.JpaRepository;

// Essa interface é o "acesso ao banco de dados" da entidade Magia.
// Repare que não escrevemos nenhum método aqui dentro - isso porque
// ao estender JpaRepository<Magia, Long>, o Spring já cria sozinho,
// na hora de rodar o programa, métodos prontos como:
//
//   repository.findAll()          -> traz todas as magias do banco
//   repository.findById(id)       -> busca uma magia pelo id
//   repository.save(magia)        -> salva (ou atualiza) uma magia
//   repository.deleteById(id)     -> apaga uma magia pelo id
//
// O "Magia" entre os < > diz qual entidade esse repositório controla,
// e o "Long" diz qual é o tipo do id dela.
public interface MagiaRepository extends JpaRepository<Magia, Long> {
}
