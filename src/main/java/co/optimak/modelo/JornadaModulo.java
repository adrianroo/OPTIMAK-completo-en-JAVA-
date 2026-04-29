package co.optimak.modelo;

import java.util.List;

public class JornadaModulo {

    private long idJornada;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private int esDiaLaborable;
    private String moduloOperativoId;
    private String moduloOperativoEmpresaNit;

    // lista de pausas asociadas — se carga aparte cuando se necesita
    private List<PausaModulo> pausas;

    public JornadaModulo() {}

    public JornadaModulo(long idJornada, String fecha, String horaInicio, String horaFin,
                         int esDiaLaborable, String moduloOperativoId, String moduloOperativoEmpresaNit) {
        this.idJornada = idJornada;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.esDiaLaborable = esDiaLaborable;
        this.moduloOperativoId = moduloOperativoId;
        this.moduloOperativoEmpresaNit = moduloOperativoEmpresaNit;
    }

    public long getIdJornada() { return idJornada; }
    public void setIdJornada(long idJornada) { this.idJornada = idJornada; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public int getEsDiaLaborable() { return esDiaLaborable; }
    public void setEsDiaLaborable(int esDiaLaborable) { this.esDiaLaborable = esDiaLaborable; }

    public String getModuloOperativoId() { return moduloOperativoId; }
    public void setModuloOperativoId(String moduloOperativoId) { this.moduloOperativoId = moduloOperativoId; }

    public String getModuloOperativoEmpresaNit() { return moduloOperativoEmpresaNit; }
    public void setModuloOperativoEmpresaNit(String moduloOperativoEmpresaNit) {
        this.moduloOperativoEmpresaNit = moduloOperativoEmpresaNit;
    }

    public List<PausaModulo> getPausas() { return pausas; }
    public void setPausas(List<PausaModulo> pausas) { this.pausas = pausas; }
}
