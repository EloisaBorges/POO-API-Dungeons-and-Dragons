package com.projeto.D.D.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Estruturas que espelham o JSON devolvido pela D&D 5e SRD API.
 * Ficam agrupadas aqui para deixar claro que sao apenas o "formato de chegada"
 * dos dados externos, e nao entidades do banco.
 */
public final class RespostaApi {

    private RespostaApi() {
    }

    /** Referencia curta usada nas listagens ({ index, name, url }). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Referencia(String index, String name, String url) {
    }

    /** Envelope de qualquer listagem: { count, results: [...] }. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Listagem(Integer count, List<Referencia> results) {

        public List<Referencia> resultadosSeguros() {
            return results == null ? List.of() : results;
        }
    }

    /** Detalhe completo de uma magia (GET /api/spells/{index}). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MagiaApi(
            String index,
            String name,
            List<String> desc,
            List<String> higher_level,
            String range,
            List<String> components,
            String material,
            Boolean ritual,
            String duration,
            Boolean concentration,
            String casting_time,
            Integer level,
            Referencia school,
            List<Referencia> classes) {

        public int nivelSeguro() {
            return level == null ? 0 : level;
        }

        public boolean ritualSeguro() {
            return Boolean.TRUE.equals(ritual);
        }

        public boolean concentracaoSegura() {
            return Boolean.TRUE.equals(concentration);
        }

        public List<String> componentesSeguros() {
            return components == null ? List.of() : components;
        }

        public List<String> indicesDasClasses() {
            return classes == null ? List.of() : classes.stream().map(Referencia::index).toList();
        }

        public String descricaoUnificada() {
            return desc == null ? "" : String.join("\n\n", desc);
        }

        public String niveisSuperioresUnificados() {
            return higher_level == null ? null : String.join("\n\n", higher_level);
        }
    }
}
