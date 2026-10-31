document.addEventListener("DOMContentLoaded", () => {
    const menu = document.getElementById("menuAdmin");
    const botao = document.getElementById("alternarMenu");
    const layout = document.querySelector(".layout-admin");

    if (!menu || !botao || !layout) {
        return;
    }

    const mobile = window.matchMedia("(max-width: 768px)");

    const botaoAbrir = document.createElement("button");
    botaoAbrir.type = "button";
    botaoAbrir.className = "botao-abrir-menu";
    botaoAbrir.textContent = "☰";
    botaoAbrir.setAttribute("aria-label", "Abrir menu");
    botaoAbrir.setAttribute("aria-controls", "menuAdmin");
    botaoAbrir.setAttribute("aria-expanded", "false");

    document.body.appendChild(botaoAbrir);

    function atualizarEstadoMobile(aberto) {
        menu.classList.toggle("mobile-aberto", aberto);
        menu.classList.toggle("mobile-fechado", !aberto);
        layout.classList.toggle("menu-mobile-aberto", aberto);

        botaoAbrir.hidden = aberto;
        botaoAbrir.setAttribute("aria-expanded", String(aberto));

        botao.setAttribute("aria-label", aberto
            ? "Fechar menu"
            : "Abrir menu");

        botao.setAttribute("aria-expanded", String(aberto));
    }

    function configurarTela() {
        if (mobile.matches) {
            menu.classList.remove("recolhido");
            atualizarEstadoMobile(false);
        } else {
            menu.classList.remove("mobile-aberto", "mobile-fechado");
            layout.classList.remove("menu-mobile-aberto");
            botaoAbrir.hidden = true;

            const recolhido = menu.classList.contains("recolhido");

            botao.setAttribute("aria-label", recolhido
                ? "Expandir menu"
                : "Recolher menu");

            botao.setAttribute("aria-expanded", String(!recolhido));
        }
    }

    botao.addEventListener("click", () => {
        if (mobile.matches) {
            atualizarEstadoMobile(menu.classList.contains("mobile-fechado"));
        } else {
            const recolhido = menu.classList.toggle("recolhido");

            botao.setAttribute("aria-label", recolhido
                ? "Expandir menu"
                : "Recolher menu");

            botao.setAttribute("aria-expanded", String(!recolhido));
        }
    });

    botaoAbrir.addEventListener("click", () => {
        atualizarEstadoMobile(true);
    });

    layout.addEventListener("click", (evento) => {
        if (
            mobile.matches &&
            menu.classList.contains("mobile-aberto") &&
            !menu.contains(evento.target)
        ) {
            atualizarEstadoMobile(false);
        }
    });

    menu.querySelectorAll("a").forEach((link) => {
        link.addEventListener("click", () => {
            if (mobile.matches) {
                atualizarEstadoMobile(false);
            }
        });
    });

    document.addEventListener("keydown", (evento) => {
        if (evento.key === "Escape" && mobile.matches) {
            atualizarEstadoMobile(false);
        }
    });

    mobile.addEventListener("change", configurarTela);

    configurarTela();
});