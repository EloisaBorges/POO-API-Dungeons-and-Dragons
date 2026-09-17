/*
 * Controle das "Magias Preparadas".
 *
 * A seleção é guardada no localStorage do próprio navegador, em uma lista de
 * ids das magias que estão no banco. Assim cada jogador monta sua lista do dia
 * sem precisar de login e sem alterar o grimório salvo no servidor.
 */
(function () {
    "use strict";

    var CHAVE = "grimorio.preparadas";

    function ler() {
        try {
            var bruto = window.localStorage.getItem(CHAVE);
            var lista = bruto ? JSON.parse(bruto) : [];
            return Array.isArray(lista) ? lista.map(String) : [];
        } catch (e) {
            return [];
        }
    }

    function gravar(lista) {
        try {
            window.localStorage.setItem(CHAVE, JSON.stringify(lista));
        } catch (e) {
            console.warn("Não foi possível salvar as magias preparadas neste navegador.", e);
        }
    }

    function estaPreparada(id) {
        return ler().indexOf(String(id)) !== -1;
    }

    function alternar(id) {
        var lista = ler();
        var posicao = lista.indexOf(String(id));
        if (posicao === -1) {
            lista.push(String(id));
        } else {
            lista.splice(posicao, 1);
        }
        gravar(lista);
        return posicao === -1;
    }

    function atualizarEstrelas() {
        var botoes = document.querySelectorAll("[data-preparar]");
        for (var i = 0; i < botoes.length; i++) {
            var botao = botoes[i];
            var marcada = estaPreparada(botao.getAttribute("data-preparar"));
            botao.textContent = marcada ? "★" : "☆";
            botao.classList.toggle("marcada", marcada);
            botao.setAttribute("aria-pressed", marcada ? "true" : "false");
            botao.title = marcada
                ? "Remover das magias preparadas"
                : "Marcar como preparada neste navegador";
        }
    }

    function atualizarContador() {
        var quantidade = ler().length;
        var contadores = document.querySelectorAll("[data-contador-preparadas]");
        for (var i = 0; i < contadores.length; i++) {
            contadores[i].textContent = quantidade;
        }
    }

    /* Na aba "Preparadas", esconde tudo que não estiver marcado. */
    function filtrarAbaPreparadas() {
        var lista = document.querySelector("[data-lista-preparadas]");
        if (!lista) {
            return;
        }
        var cartas = lista.querySelectorAll("[data-magia-id]");
        var visiveis = 0;
        for (var i = 0; i < cartas.length; i++) {
            var carta = cartas[i];
            var mostrar = estaPreparada(carta.getAttribute("data-magia-id"));
            carta.hidden = !mostrar;
            if (mostrar) {
                visiveis++;
            }
        }
        var vazio = document.querySelector("[data-vazio-preparadas]");
        if (vazio) {
            vazio.hidden = visiveis !== 0 || cartas.length === 0;
        }
    }

    function sincronizar() {
        atualizarEstrelas();
        atualizarContador();
        filtrarAbaPreparadas();
    }

    document.addEventListener("click", function (evento) {
        var botao = evento.target.closest ? evento.target.closest("[data-preparar]") : null;
        if (botao) {
            alternar(botao.getAttribute("data-preparar"));
            sincronizar();
            return;
        }

        var limpar = evento.target.closest ? evento.target.closest("[data-limpar-preparadas]") : null;
        if (limpar) {
            if (window.confirm("Limpar todas as magias preparadas deste navegador?")) {
                gravar([]);
                sincronizar();
            }
        }
    });

    /* Mantém as abas abertas em sincronia quando há mais de uma janela. */
    window.addEventListener("storage", function (evento) {
        if (evento.key === CHAVE) {
            sincronizar();
        }
    });

    document.addEventListener("DOMContentLoaded", sincronizar);
})();
