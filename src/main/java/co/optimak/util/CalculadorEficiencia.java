package co.optimak.util;

import co.optimak.modelo.JornadaModulo;
import co.optimak.modelo.PausaModulo;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

/**
 * Lógica pura de cálculo de eficiencia — sin acceso a BD.
 * Recibe los datos como parámetros y devuelve resultados.
 */
public class CalculadorEficiencia {

    /**
     * Cuántos minutos productivos han pasado desde el inicio de la jornada hasta horaHasta.
     * Descuenta las pausas que caen dentro de ese intervalo.
     */
    public static int calcularMinutosProductivosHasta(JornadaModulo jornada,
                                                       List<PausaModulo> pausas,
                                                       LocalTime horaHasta) {
        if (jornada == null || jornada.getEsDiaLaborable() == 0) return 0;

        LocalTime inicio = LocalTime.parse(jornada.getHoraInicio());
        LocalTime fin    = LocalTime.parse(jornada.getHoraFin());
        LocalTime limite = horaHasta.isAfter(fin) ? fin : horaHasta;

        if (!limite.isAfter(inicio)) return 0;

        int total = (int) Duration.between(inicio, limite).toMinutes();
        total -= minutasDePausasEnIntervalo(pausas, inicio, limite);
        return Math.max(0, total);
    }

    /**
     * Cuántos minutos productivos hay entre dos horas específicas.
     * Se usa para medir si ya pasaron 30 min productivos desde el último registro.
     */
    public static int calcularMinutosProductivosEntrePeriodo(JornadaModulo jornada,
                                                               List<PausaModulo> pausas,
                                                               LocalTime desde,
                                                               LocalTime hasta) {
        if (jornada == null || jornada.getEsDiaLaborable() == 0) return 0;
        if (!hasta.isAfter(desde)) return 0;

        int total = (int) Duration.between(desde, hasta).toMinutes();
        total -= minutasDePausasEnIntervalo(pausas, desde, hasta);
        return Math.max(0, total);
    }

    /**
     * Verifica si ya pasaron >= 20 minutos productivos desde desdeHora hasta ahora.
     */
    public static boolean hanPasadoMinutosProductivos(JornadaModulo jornada,
                                                       List<PausaModulo> pausas,
                                                       LocalTime desdeHora) {
        int minutos = calcularMinutosProductivosEntrePeriodo(jornada, pausas, desdeHora, LocalTime.now());
        return minutos >= 20;
    }

    /**
     * Eficiencia = (totalSamProducido / minutosProductivos) × 100
     * totalSamProducido = suma de SAM × pulsaciones del período.
     */
    public static double calcularEficiencia(double totalSamProducido, int minutosProductivos) {
        if (minutosProductivos <= 0) return 0;
        return (totalSamProducido / minutosProductivos) * 100.0;
    }

    /**
     * Eficiencia modular = promedio de las eficiencias individuales de quienes trabajaron hoy.
     * Los que no tienen registros no entran al promedio.
     */
    public static double calcularEficienciaModular(List<Double> eficienciasIndividuales) {
        if (eficienciasIndividuales == null || eficienciasIndividuales.isEmpty()) return 0;
        double suma = 0;
        for (double e : eficienciasIndividuales) suma += e;
        return suma / eficienciasIndividuales.size();
    }

    // --- privado ---

    private static int minutasDePausasEnIntervalo(List<PausaModulo> pausas,
                                                   LocalTime desde, LocalTime hasta) {
        if (pausas == null) return 0;
        int descuento = 0;
        for (PausaModulo pausa : pausas) {
            LocalTime inicioPausa = LocalTime.parse(pausa.getHoraInicioPausa());
            LocalTime finPausa    = inicioPausa.plusMinutes(pausa.getDuracionMinutos());

            // intersección de [desde, hasta] con [inicioPausa, finPausa]
            LocalTime intInicio = inicioPausa.isAfter(desde) ? inicioPausa : desde;
            LocalTime intFin    = finPausa.isBefore(hasta)   ? finPausa    : hasta;

            if (intFin.isAfter(intInicio)) {
                descuento += (int) Duration.between(intInicio, intFin).toMinutes();
            }
        }
        return descuento;
    }
}
