package com.restaurant.catalogapi.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public Mono<Void> sendPasswordResetEmail(String toEmail, String resetLink) {
        return Mono.fromCallable(() -> {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(senderEmail);
                    message.setTo(toEmail);
                    message.setSubject("RestaurantApp - Recuperacion de contrasena");
                    message.setText(
                            "Hola,\n\n" +
                            "Recibimos una solicitud para restablecer la contrasena de tu cuenta en RestaurantApp.\n\n" +
                            "Haz clic en el siguiente enlace para crear una nueva contrasena:\n\n" +
                            resetLink + "\n\n" +
                            "Este enlace expirara en 30 minutos.\n\n" +
                            "Si no solicitaste este cambio, puedes ignorar este correo con tranquilidad.\n\n" +
                            "Saludos,\n" +
                            "Equipo RestaurantApp"
                    );
                    mailSender.send(message);
                    log.info("Password reset email sent to {}", toEmail);
                    return true;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
