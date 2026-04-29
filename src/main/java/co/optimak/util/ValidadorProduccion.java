package co.optimak.util;

import co.optimak.dao.RegistroPulsacionDAO;
import co.optimak.modelo.Lote;

/**
 * Verifica que las pulsaciones no superen el total de unidades del lote.
 * Se ejecuta ANTES de confirmar cada pulsación.
 * Regla: sum(pulsaciones de una operación en el lote) < totalUnidadesLote
 */
public class ValidadorProduccion {

    private final RegistroPulsacionDAO pulsacionDAO;

    public ValidadorProduccion(RegistroPulsacionDAO pulsacionDAO) {
        this.pulsacionDAO = pulsacionDAO;
    }

    /**
     * Retorna true si aún se puede pulsar (no se alcanzó el límite).
     * Retorna false si el lote ya está completo para esa operación.
     */
    public boolean esPulsacionValida(int operacionId, Lote lote) {
        int pulsacionesActuales = pulsacionDAO.contarPorOperacionYLote(operacionId, lote.getOm());
        return pulsacionesActuales < lote.getTotalUnidades();
    }
}
