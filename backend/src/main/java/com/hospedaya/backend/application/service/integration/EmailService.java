package com.hospedaya.backend.application.service.integration;

import com.hospedaya.backend.domain.entity.Usuario;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreoRegistro(Usuario usuario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // No es necesario setFrom con Gmail, usa automáticamente el MAIL_USERNAME
            helper.setTo(usuario.getEmail());
            helper.setSubject("¡Bienvenido a HospedaYa!");
            
            String htmlContent = construirHtmlBienvenida(usuario);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Correo de bienvenida enviado a: {}", usuario.getEmail());
            
        } catch (MessagingException e) {
            log.error("Error al enviar correo de bienvenida a {}: {}", usuario.getEmail(), e.getMessage());
            // No lanzamos excepción para que el registro continúe aunque falle el correo
        }
    }

    private String construirHtmlBienvenida(Usuario usuario) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Poppins', Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 20px; text-align: center; color: white; }
                    .header h1 { margin: 0; font-size: 32px; font-weight: 700; }
                    .content { padding: 40px 30px; }
                    .content h2 { color: #333; margin-top: 0; font-size: 24px; }
                    .content p { color: #666; line-height: 1.6; font-size: 16px; }
                    .info-box { background-color: #f8f9fa; border-left: 4px solid #667eea; padding: 20px; margin: 20px 0; border-radius: 5px; }
                    .info-box strong { color: #333; display: block; margin-bottom: 5px; }
                    .info-box span { color: #667eea; font-weight: 600; }
                    .button { display: inline-block; padding: 15px 30px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white !important; text-decoration: none; border-radius: 5px; font-weight: 600; margin-top: 20px; }
                    .footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #999; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class=\"container\">
                    <div class=\"header\">
                        <h1>🏠 HospedaYa</h1>
                    </div>
                    <div class=\"content\">
                        <h2>¡Bienvenido, %s!</h2>
                        <p>Nos complace darte la bienvenida a <strong>HospedaYa</strong>, tu plataforma de confianza para encontrar y reservar alojamientos únicos.</p>
                        
                        <div class=\"info-box\">
                            <strong>Tus datos de registro:</strong>
                            <p style=\"margin: 10px 0 5px 0;\">
                                <strong>Nombre:</strong> <span>%s</span><br>
                                <strong>Email:</strong> <span>%s</span><br>
                                <strong>Teléfono:</strong> <span>%s</span><br>
                                <strong>Rol:</strong> <span>%s</span><br>
                                <strong>Fecha de registro:</strong> <span>%s</span>
                            </p>
                        </div>
                        
                        <p>Ya puedes comenzar a explorar miles de alojamientos o, si eres anfitrión, publicar tu propiedad.</p>
                        
                        <center>
                            <a href=\"http://localhost:4200/login\" class=\"button\">Iniciar Sesión</a>
                        </center>
                    </div>
                    <div class=\"footer\">
                        <p>© 2025 HospedaYa. Todos los derechos reservados.</p>
                        <p>Si no creaste esta cuenta, ignora este correo.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                usuario.getNombre(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono() != null ? usuario.getTelefono() : "No proporcionado",
                usuario.getRol() != null ? usuario.getRol().name() : "HUESPED",
                usuario.getFechaRegistro() != null ? usuario.getFechaRegistro().toString() : "Hoy"
            );
    }

    public void enviarCorreoRecuperacion(Usuario usuario, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(usuario.getEmail());
            helper.setSubject("Recupera tu contraseña - HospedaYa");

            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Poppins', Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 40px auto; background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px 20px; text-align: center; color: #fff; }
                        .content { padding: 30px 25px; color: #333; }
                        .button { display: inline-block; padding: 12px 22px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: #fff !important; text-decoration: none; border-radius: 6px; font-weight: 600; }
                        .muted { color: #777; font-size: 14px; }
                    </style>
                </head>
                <body>
                    <div class=\"container\">
                        <div class=\"header\"><h2>Recupera tu contraseña</h2></div>
                        <div class=\"content\">
                            <p>Hola %s, recibimos una solicitud para restablecer tu contraseña.</p>
                            <p>Puedes crear una nueva contraseña haciendo clic en el siguiente botón:</p>
                            <p><a class=\"button\" href=\"%s\">Restablecer contraseña</a></p>
                            <p class=\"muted\">Si no solicitaste este cambio, ignora este mensaje.</p>
                        </div>
                    </div>
                </body>
                </html>
            """.formatted(usuario.getNombre() != null ? usuario.getNombre() : usuario.getEmail(), resetLink);

            helper.setText(html, true);
            mailSender.send(message);
            log.info("Correo de recuperación enviado a: {}", usuario.getEmail());
        } catch (MessagingException e) {
            log.error("Error al enviar correo de recuperación a {}: {}", usuario.getEmail(), e.getMessage());
        }
    }
}
