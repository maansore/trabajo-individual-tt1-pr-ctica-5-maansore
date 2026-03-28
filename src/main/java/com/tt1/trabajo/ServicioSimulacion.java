package com.tt1.trabajo;

import interfaces.InterfazContactoSim;
import modelo.DatosSimulation;
import modelo.DatosSolicitud;
import modelo.Entidad;
import modelo.Punto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServicioSimulacion implements InterfazContactoSim {

    private final Map<Integer, DatosSolicitud> solicitudesProvisionales = new HashMap<>();
    private final Random random = new Random();

    @Override
    public int solicitarSimulation(DatosSolicitud sol) {
        int token = random.nextInt(1000000);
        solicitudesProvisionales.put(token, sol);
        return token;
    }

    @Override
    public DatosSimulation descargarDatos(int ticket) {
        if(solicitudesProvisionales.containsKey(ticket)){
            DatosSimulation ds = new DatosSimulation();
            ds.setAnchoTablero(1);
            ds.setMaxSegundos(1);
            ds.setPuntos(new HashMap<Integer, List<Punto>>());
            return ds;
        }
        return null;
    }

    @Override
    public List<Entidad> getEntities() {
        Entidad e = new Entidad();
        e.setId(1);
        e.setName("Entidad");
        e.setDescripcion("Descripcion entidad");
        List<Entidad> entidades = new ArrayList<>();
        entidades.add(e);
        return entidades;
    }

    @Override
    public boolean isValidEntityId(int id) {
        // No entiendo muy bien, cada entidad tiene un entero asociado diferente del ticket?
        return id > 0;
    }
}
