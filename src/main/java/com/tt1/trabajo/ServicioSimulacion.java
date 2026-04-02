package com.tt1.trabajo;

import com.tt1.trabajo.utils.generated.api.ResultadosApi;
import com.tt1.trabajo.utils.generated.api.SolicitudApi;
import interfaces.InterfazContactoSim;
import modelo.DatosSimulation;
import modelo.DatosSolicitud;
import modelo.Entidad;
import modelo.Punto;
import org.openapitools.client.model.ResultsResponse;
import org.openapitools.client.model.Solicitud;
import org.openapitools.client.model.SolicitudResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServicioSimulacion implements InterfazContactoSim {

    private final SolicitudApi solicitudApi;
    private final ResultadosApi resultadosApi;

    // Cadena constante pedida por la Práctica 5
    private static final String USUARIO_CONSTANTE = "mi_usuario";

    public ServicioSimulacion() {
        this.solicitudApi = new SolicitudApi();
        this.solicitudApi.getApiClient().setBasePath("http://servicio-consumible:8080");

        this.resultadosApi = new ResultadosApi();
        this.resultadosApi.getApiClient().setBasePath("http://servicio-consumible:8080");
    }

    @Override
    public int solicitarSimulation(DatosSolicitud sol) {
        Solicitud peticionApi = new Solicitud();
        List<String> nombres = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();

        // Convertimos tus datos a las listas que espera la API
        for (Map.Entry<Integer, Integer> entry : sol.getNums().entrySet()) {
            nombres.add("Entidad_" + entry.getKey());
            cantidades.add(entry.getValue());
        }
        peticionApi.setNombreEntidades(nombres);
        peticionApi.setCantidadesIniciales(cantidades);

        try {
            // Enviamos la petición y recogemos el Token real de la Máquina Virtual
            SolicitudResponse response = solicitudApi.solicitudSolicitarPost(USUARIO_CONSTANTE, peticionApi).block();
            if (response != null && response.getTokenSolicitud() != null) {
                return response.getTokenSolicitud();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // En caso de fallo devolvemos -1
    }

    @Override
    public DatosSimulation descargarDatos(int ticket) {
        try {
            // Le pedimos los datos a la máquina virtual usando el Token
            ResultsResponse response = resultadosApi.resultadosPost(USUARIO_CONSTANTE, ticket).block();

            if (response != null && response.getData() != null) {
                // Parseamos el texto que llega ("0,7,5, red...")
                return parsearDatosGrid(response.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método auxiliar para traducir el texto de la VM a tus objetos Java
    private DatosSimulation parsearDatosGrid(String data) {
        DatosSimulation ds = new DatosSimulation();
        Map<Integer, List<Punto>> puntosMap = new HashMap<>();
        int maxSegundos = 0;

        String[] lineas = data.split("\\r?\\n");
        if (lineas.length > 0) {
            // La primera línea es el tamaño del ancho del tablero
            int ancho = Integer.parseInt(lineas[0].trim());
            ds.setAnchoTablero(ancho);

            for (int i = 1; i < lineas.length; i++) {
                String linea = lineas[i].trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(",");
                if (partes.length >= 4) {
                    int tiempo = Integer.parseInt(partes[0].trim());
                    int y = Integer.parseInt(partes[1].trim());
                    int x = Integer.parseInt(partes[2].trim());
                    String color = partes[3].trim();

                    Punto p = new Punto();
                    p.setX(x);
                    p.setY(y);
                    p.setColor(color);

                    // Añadir el punto al mapa de ese segundo
                    puntosMap.computeIfAbsent(tiempo, k -> new ArrayList<>()).add(p);

                    if (tiempo > maxSegundos) {
                        maxSegundos = tiempo;
                    }
                }
            }
        }

        // Sumamos 1 porque si el tiempo llega a 2, hay tiempos 0, 1 y 2 (total 3 segundos)
        ds.setMaxSegundos(maxSegundos + 1);
        ds.setPuntos(puntosMap);
        return ds;
    }

    @Override
    public List<Entidad> getEntities() {
        Entidad e1 = new Entidad(); e1.setId(1); e1.setName(" Cherbourg"); e1.setDescripcion("...");
        Entidad e2 = new Entidad(); e2.setId(2); e2.setName(" Rochefort"); e2.setDescripcion("...");
        return Arrays.asList(e1, e2);
    }

    @Override
    public boolean isValidEntityId(int id) {
        return id == 1 || id == 2;
    }
}