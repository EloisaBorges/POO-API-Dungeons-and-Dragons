package com.projeto.D.D.repository;

import com.projeto.D.D.model.Magia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Camada de persistencia do grimorio. Herda de JpaRepository, entao ja ganha
 * save/findAll/findById/deleteById sem precisar escrever SQL.
 */
public interface MagiaRepository extends JpaRepository<Magia, Long> {

  /** Listagem padrao: truques primeiro, depois por nome. */
  List<Magia> findAllByOrderByNivelAscNomeAsc();

  Optional<Magia> findByIndiceApi(String indiceApi);

  boolean existsByIndiceApi(String indiceApi);

  long countByNivel(int nivel);

  List<Magia> findByNomeContainingIgnoreCaseOrderByNivelAscNomeAsc(String nome);

  /**
   * Busca com todos os filtros da tela do grimorio. Cada parametro nulo
   * simplesmente nao filtra nada.
   */
  @Query("""
      select m from Magia m
      where (:nome is null or lower(m.nome) like lower(concat('%', :nome, '%')))
        and (:nivel is null or m.nivel = :nivel)
        and (:classe is null or lower(m.classes) like lower(concat('%', :classe, '%')))
        and (:componente is null or upper(m.componentes) like upper(concat('%', :componente, '%')))
      order by m.nivel asc, m.nome asc
      """)
  List<Magia> buscarComFiltros(@Param("nome") String nome,
      @Param("nivel") Integer nivel,
      @Param("classe") String classe,
      @Param("componente") String componente);
}
