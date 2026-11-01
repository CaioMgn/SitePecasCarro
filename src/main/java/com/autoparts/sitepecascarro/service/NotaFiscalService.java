package com.autoparts.sitepecascarro.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import com.autoparts.sitepecascarro.entity.Pedido;

import jakarta.mail.internet.MimeMessage;

// Monta a nota fiscal fictícia de um pedido e manda por e-mail para o cliente.
@Service
public class NotaFiscalService {

    private static final Logger log = LoggerFactory.getLogger(NotaFiscalService.class);

    private final JavaMailSender mailSender;
    private final ITemplateEngine templateEngine; // o mesmo Thymeleaf das páginas
    private final String remetente;

    public NotaFiscalService(JavaMailSender mailSender,
                             ITemplateEngine templateEngine,
                             @Value("${spring.mail.username:}") String remetente) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.remetente = remetente;
    }

    // Se o envio falhar, a compra NÃO pode ser desfeita: o erro só vai para o log.
    // Por isso o Controller chama este método depois que o pedido já foi gravado.
    // Devolve true se o e-mail saiu, para a tela avisar o cliente.
    public boolean enviar(Pedido pedido) {
        try {
            Context contexto = new Context(Locale.forLanguageTag("pt-BR"));
            contexto.setVariable("pedido", pedido);
            String html = templateEngine.process("email/nota-fiscal", contexto);

            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
            if (!remetente.isBlank()) {
                helper.setFrom(remetente, "AutoParts");
            }
            helper.setTo(pedido.getCliente().getEmail());
            helper.setSubject("Nota fiscal do pedido " + pedido.getNumero() + " - AutoParts");
            helper.setText(html, true); // true = o texto é HTML

            mailSender.send(mensagem);
            log.info("Nota fiscal do pedido {} enviada para {}",
                    pedido.getNumero(), pedido.getCliente().getEmail());
            return true;
        } catch (Exception e) {
            log.warn("Falha ao enviar a nota do pedido {}: {}", pedido.getId(), e.getMessage());
            return false;
        }
    }
}
