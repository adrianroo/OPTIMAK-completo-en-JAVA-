package co.optimak.util;

import co.optimak.dao.RegistroPulsacionDAO;

import java.util.Map;

/**
 * Detecta condiciones de alerta — NO persiste nada en BD.
 * Alerta individual: eficiencia < 65%.
 * Cuello de botella: una operación con 0 pulsaciones hoy
 *   mientras otras del mismo lote ya tienen >= 5.
 */
public class SistemaAlertas {

    private final RegistroPulsacionDAO pulsacionDAO;

    public SistemaAlertas(RegistroPulsacionDAO pulsacionDAO) {
        this.pulsacionDAO = pulsacionDAO;
    }

    /** true si la eficiencia está por debajo del mínimo aceptable (65%). */
    public boolean esEficienciaBaja(double eficiencia) {
        return eficiencia > 0 && eficiencia < 65.0;
    }

    /**
     * Detecta cuello de botella: si hay alguna operación de la referencia del lote
     * con 0 pulsaciones hoy mientras otras ya tienen >= 5.
     * Devuelve el nombre de la operación atascada, o null si no hay cuello.
     */
    public String detectarCuelloBotella(String referenciaIdRef, String loteOm) {
        // Map<idOperacion, [count, nombreCorto]>
        Map<Integer, Object[]> conteos = pulsacionDAO.contarPorOperacionHoyEnLote(loteOm, referenciaIdRef);
        if (conteos == null || conteos.size() < 2) return null;

        int maxPulsaciones = 0;
        for (Object[] dato : conteos.values()) {
            int count = (int) dato[0];
            if (count > maxPulsaciones) maxPulsaciones = count;
        }

        // solo alertar si hay suficiente actividad como para que sea significativo
        if (maxPulsaciones < 5) return null;

        for (Map.Entry<Integer, Object[]> entry : conteos.entrySet()) {
            int count = (int) entry.getValue()[0];
            String nombre = (String) entry.getValue()[1];
            if (count == 0) {
                return nombre;
            }
        }
        return null;
    }
}
