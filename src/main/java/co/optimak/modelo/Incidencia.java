package co.optimak.modelo;

public class Incidencia {

    private long idIncidencia;
    private String fechaHora; // DEFAULT NOW en BD
    private String descripcion;
    private String numeroCedula;
    private long idRegistroEficiencia; // FK — CASCADE si se borra el registro de eficiencia

    public Incidencia() {}

    public Incidencia(long idIncidencia, String fechaHora, String descripcion,
                      String numeroCedula, long idRegistroEficiencia) {
        this.idIncidencia         = idIncidencia;
        this.fechaHora            = fechaHora;
        this.descripcion          = descripcion;
        this.numeroCedula         = numeroCedula;
        this.idRegistroEficiencia = idRegistroEficiencia;
    }

    public long getIdIncidencia() { return idIncidencia; }
    public void setIdIncidencia(long idIncidencia) { this.idIncidencia = idIncidencia; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNumeroCedula() { return numeroCedula; }
    public void setNumeroCedula(String numeroCedula) { this.numeroCedula = numeroCedula; }

    public long getIdRegistroEficiencia() { return idRegistroEficiencia; }
    public void setIdRegistroEficiencia(long idRegistroEficiencia) { this.idRegistroEficiencia = idRegistroEficiencia; }
}
