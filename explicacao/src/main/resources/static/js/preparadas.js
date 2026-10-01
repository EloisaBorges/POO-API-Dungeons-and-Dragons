// Esse arquivo controla a "estrela" das magias, usando o localStorage,
// que é uma memória que o PRÓPRIO NAVEGADOR guarda (ela não vai pro
// servidor, nem é vista por outras pessoas usando o site).

// Verifica se uma magia (pelo id) está marcada como preparada.
function estaPreparada(id) {
    // Pega o texto salvo na chave "preparadas". Se nunca salvamos nada
    // ainda, localStorage.getItem devolve null.
    var lista = localStorage.getItem("preparadas");

    // O que está salvo é um TEXTO (JSON), então precisamos transformar
    // de volta em uma lista de verdade com JSON.parse. Se não existir
    // nada salvo ainda, começamos com uma lista vazia.
    lista = lista ? JSON.parse(lista) : [];

    // .includes verifica se o id está dentro da lista. Convertemos para
    // String porque o que vem do HTML (data-id) é sempre texto.
    return lista.includes(String(id));
}

// Marca ou desmarca uma magia como preparada (alterna entre os dois).
function alternar(id) {
    var lista = localStorage.getItem("preparadas");
    lista = lista ? JSON.parse(lista) : [];
    id = String(id);

    if (lista.includes(id)) {
        // Já estava marcada -> tira da lista (desmarca)
        lista = lista.filter(function (item) {
            return item !== id;
        });
    } else {
        // Não estava marcada -> adiciona na lista (marca)
        lista.push(id);
    }

    // Salva a lista atualizada de volta no navegador, transformando
    // em texto (JSON) de novo, porque o localStorage só guarda texto.
    localStorage.setItem("preparadas", JSON.stringify(lista));

    // Atualiza a tela para refletir a mudança na hora.
    atualizarTela();
}

// Essa função roda toda vez que a página carrega, ou depois que o
// usuário clica em alguma estrela. Ela faz duas coisas:
function atualizarTela() {

    // 1) Troca o desenho (☆ ou ★) de cada botão de estrela que existir
    //    na página, de acordo com o que está salvo no localStorage.
    var botoes = document.querySelectorAll(".favorito");
    botoes.forEach(function (botao) {
        var id = botao.getAttribute("data-id");
        botao.textContent = estaPreparada(id) ? "★" : "☆";
    });

    // 2) Na tela de "Preparadas", esconde (display: none) as magias que
    //    NÃO estão marcadas, e mostra (display: block) as que estão.
    //    Nas outras telas simplesmente não existe nenhum elemento com
    //    a classe "carta-preparada", então esse trecho não faz nada.
    var cartas = document.querySelectorAll(".carta-preparada");
    cartas.forEach(function (carta) {
        var id = carta.getAttribute("data-id");
        carta.style.display = estaPreparada(id) ? "block" : "none";
    });
}

// Quando a página terminar de carregar, já deixa tudo certinho (estrelas
// e cartas) sem precisar clicar em nada antes.
document.addEventListener("DOMContentLoaded", atualizarTela);
