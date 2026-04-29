package co.optimak.modelo;

public class HistorialModuloEmpleado {

    private long idHistorial;
    private String fechaInicio;
    private String fechaFin; // null = asignación activa actual
    private String numeroCedula;
    private String moduloOperativoId;
    private String moduloOperativoEmpresaNit;

    public HistorialModuloEmpleado() {}

    public HistorialModuloEmpleado(long idHistorial, String fechaInicio, String fechaFin,
                                   String numeroCedula, String moduloOperativoId,
                                   String moduloOperativoEmpresaNit) {
        this.idHistorial = idHistorial;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numeroCedula = numeroCedula;
        this.moduloOperativoId = moduloOperativoId;
        this.moduloOperativoEmpresaNit = moduloOperativoEmpresaNit;
    }

    public long getIdHistorial() { return idHistorial; }
    public void setIdHistorial(long idHistorial) { this.idHistorial = idHistorial; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getNumeroCedula() { return numeroCedula; }
    public void setNumeroCedula(String numeroCedula) { this.numeroCedula = numeroCedula; }

    public String getModuloOperativoId() { return moduloOperativoId; }
    public void setModuloOperativoId(String moduloOperativoId) { this.moduloOperativoId = moduloOperativoId; }

    public String getModuloOperativoEmpresaNit() { return moduloOperativoEmpresaNit; }
    public void setModuloOperativoEmpresaNit(String moduloOperativoEmpresaNit) {
        this.moduloOperativoEmpresaNit = moduloOperativoEmpresaNit;
    }
}
