package com.tt1.trabajo;

import com.tt1.trabajo.utils.generated.api.EmailApi;
import interfaces.InterfazEnviarEmails;
import modelo.Destinatario;
import org.openapitools.client.model.EmailResponse;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
public class ServicioEmails implements InterfazEnviarEmails {

    private final Logger logger;
    private final EmailApi emailApi;

    public ServicioEmails(Logger logger){
        this.logger = logger;
        this.emailApi = new EmailApi();
        this.emailApi.getApiClient().setBasePath("http://servicio-consumible:8080");
    }

    @Override
    public boolean enviarEmail(Destinatario dest, String email) {
        if (email == null || email.trim().isEmpty() || dest == null) {
            return false;
        } else {
            try {
                EmailResponse response = emailApi.emailPost("usuario@prueba.com", email).block();

                logger.info("Email enviado a la máquina virtual. Éxito: " + response.getDone());
                return response.getDone() != null && response.getDone();

            } catch (Exception e) {
                logger.error("Error al intentar comunicarse con el servicio de emails de la VM", e);
                return false;
            }
        }
    }
}