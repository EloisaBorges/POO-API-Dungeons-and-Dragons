package com.projeto.D.D.controller;

import com.projeto.D.D.model.Magia;
import com.projeto.D.D.repository.MagiaRepository;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Aba "Favoritas / Preparadas".
 *
 * A selecao do jogador fica guardada no proprio navegador (localStorage), entao
 * o servidor apenas envia todas as magias do grimorio e o JavaScript da pagina
 * mostra somente as que estao marcadas naquele navegador. Isso permite que cada
 * jogador tenha sua propria lista do dia sem precisar de login.
 */
@Controller
public class PreparadasController {

    private final MagiaRepository magiaRepository;

    public PreparadasController(MagiaRepository magiaRepository) {
        this.magiaRepository = magiaRepository;
    }

    @GetMapping("/preparadas")
    public String preparadas(Model model) {
        List<Magia> magias = magiaRepository.findAllByOrderByNivelAscNomeAsc();
        model.addAttribute("magias", magias);
        return "preparadas";
    }
}
