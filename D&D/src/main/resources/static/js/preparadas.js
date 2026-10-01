function estaPreparada(id) {
    var lista = localStorage.getItem("preparadas");
    lista = lista ? JSON.parse(lista) : [];
    return lista.includes(String(id));
}

function alternar(id) {
    var lista = localStorage.getItem("preparadas");
    lista = lista ? JSON.parse(lista) : [];
    id = String(id);

    if (lista.includes(id)) {
        lista = lista.filter(function (item) {
            return item !== id;
        });
    } else {
        lista.push(id);
    }

    localStorage.setItem("preparadas", JSON.stringify(lista));
    atualizarTela();
}

function atualizarTela() {
    var botoes = document.querySelectorAll(".favorito");
    botoes.forEach(function (botao) {
        var id = botao.getAttribute("data-id");
        botao.textContent = estaPreparada(id) ? "★" : "☆";
    });

    var cartas = document.querySelectorAll(".carta-preparada");
    cartas.forEach(function (carta) {
        var id = carta.getAttribute("data-id");
        carta.style.display = estaPreparada(id) ? "block" : "none";
    });
}

document.addEventListener("DOMContentLoaded", atualizarTela);
