package co.optimak.modelo;

public class RegistroPulsacion {

    private long idPulsacion;
    private String timestampPulsacion; // lo asigna la BD con DEFAULT NOW
    private String numeroCedula;
    private int operacionId;
    private String loteOm;

    public RegistroPulsacion() {}

    public RegistroPulsacion(long idPulsacion, String timestampPulsacion,
                              String numeroCedula, int operacionId, String loteOm) {
        this.idPulsacion       = idPulsacion;
        this.timestampPulsacion = timestampPulsacion;
        this.numeroCedula      = numeroCedula;
        this.operacionId       = operacionId;
        this.loteOm            = loteOm;
    }

    public long getIdPulsacion() { return idPulsacion; }
    public void setIdPulsacion(long idPulsacion) { this.idPulsacion = idPulsacion; }

    public String getTimestampPulsacion() { return timestampPulsacion; }
    public void setTimestampPulsacion(String timestampPulsacion) { this.timestampPulsacion = timestampPulsacion; }

    public String getNumeroCedula() { return numeroCedula; }
    public void setNumeroCedula(String numeroCedula) { this.numeroCedula = numeroCedula; }

    public int getOperacionId() { return operacionId; }
    public void setOperacionId(int operacionId) { this.operacionId = operacionId; }

    public String getLoteOm() { return loteOm; }
    public void setLoteOm(String loteOm) { this.loteOm = loteOm; }
}
