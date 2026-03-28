package com.tt1.trabajo;

import interfaces.InterfazEnviarEmails;
import modelo.Destinatario;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
public class ServicioEmails implements InterfazEnviarEmails {

    private final Logger logger;

    public ServicioEmails(Logger logger){
        this.logger=logger;
    }

    @Override
    public boolean enviarEmail(Destinatario dest, String email) {
        if(email==null || email.trim().isEmpty() || dest==null){
            return false;
        }else {
            logger.info("Simulacion email");
            logger.info("Para: " + dest);
            logger.info("Cuerpo: " + email);
            return true;
        }
    }
}
