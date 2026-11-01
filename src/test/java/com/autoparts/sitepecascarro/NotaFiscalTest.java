package com.autoparts.sitepecascarro;

import java.util.Locale;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import com.autoparts.sitepecascarro.entity.ItemPedido;
import com.autoparts.sitepecascarro.entity.Papel;
import com.autoparts.sitepecascarro.entity.Peca;
import com.autoparts.sitepecascarro.entity.Pedido;
import com.autoparts.sitepecascarro.entity.Usuario;
import com.autoparts.sitepecascarro.service.NotaFiscalService;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class NotaFiscalTest {

    // Servidor de e-mail falso: nenhum e-mail de verdade sai durante os testes.
    @MockitoBean
    private JavaMailSender mailSender;

    @Autowired
    private NotaFiscalService notaFiscalService;

    @Autowired
    private ITemplateEngine templateEngine;

    private Pedido pedido;

    @BeforeEach
    void prepararPedidoFalso() {
        when(mailSender.createMimeMessage())
                .thenReturn(new MimeMessage(Session.getInstance(new Properties())));

        Usuario cliente = new Usuario("Cliente Teste", "cliente.teste@autoparts.com", null, Papel.USER);

        Peca peca = new Peca("Pastilha de freio", "Freios", 120.0, "Peça de teste");

        // Pedido só em memória; o id é preciso para o número (PED-1999).
        pedido = new Pedido(cliente);
        pedido.setId(999L);
        pedido.adicionarItem(new ItemPedido(peca, 2));
    }

    @Test
    void falhaNoEnvioDoEmailNaoLancaExcecao() {
        doThrow(new MailSendException("Servidor de e-mail fora do ar"))
                .when(mailSender).send(any(MimeMessage.class));

        boolean enviada = assertDoesNotThrow(() -> notaFiscalService.enviar(pedido));

        assertFalse(enviada);
    }

    @Test
    void enviaNotaParaOEmailDoCliente() throws Exception {
        assertTrue(notaFiscalService.enviar(pedido));

        var captor = org.mockito.ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage enviada = captor.getValue();

        assertEquals("cliente.teste@autoparts.com", enviada.getAllRecipients()[0].toString());
        assertTrue(enviada.getSubject().contains("PED-1999"));
    }

    @Test
    void notaTemAvisoNumeroItensETotal() {
        Context contexto = new Context(Locale.forLanguageTag("pt-BR"));
        contexto.setVariable("pedido", pedido);

        String html = templateEngine.process("email/nota-fiscal", contexto);

        assertTrue(html.contains("SEM VALOR FISCAL"));
        assertTrue(html.contains("PED-1999"));
        assertTrue(html.contains("Pastilha de freio"));
        assertTrue(html.contains("R$ 240,00"));
    }
}
