package co.optimak.modelo;

public class RegistroEficiencia {

    private long idRegistro;
    private String periodoInicio; // formato "yyyy-MM-dd HH:mm:ss"
    private String periodoFin;
    private int cantidadProducida;
    private double eficienciaPorcentaje;
    private String observaciones; // solo el admin escribe aquí
    private String numeroCedula;
    private String om;

    public RegistroEficiencia() {}

    public RegistroEficiencia(long idRegistro, String periodoInicio, String periodoFin,
                               int cantidadProducida, double eficienciaPorcentaje,
                               String observaciones, String numeroCedula, String om) {
        this.idRegistro          = idRegistro;
        this.periodoInicio       = periodoInicio;
        this.periodoFin          = periodoFin;
        this.cantidadProducida   = cantidadProducida;
        this.eficienciaPorcentaje = eficienciaPorcentaje;
        this.observaciones       = observaciones;
        this.numeroCedula        = numeroCedula;
        this.om                  = om;
    }

    public long getIdRegistro() { return idRegistro; }
    public void setIdRegistro(long idRegistro) { this.idRegistro = idRegistro; }

    public String getPeriodoInicio() { return periodoInicio; }
    public void setPeriodoInicio(String periodoInicio) { this.periodoInicio = periodoInicio; }

    public String getPeriodoFin() { return periodoFin; }
    public void setPeriodoFin(String periodoFin) { this.periodoFin = periodoFin; }

    public int getCantidadProducida() { return cantidadProducida; }
    public void setCantidadProducida(int cantidadProducida) { this.cantidadProducida = cantidadProducida; }

    public double getEficienciaPorcentaje() { return eficienciaPorcentaje; }
    public void setEficienciaPorcentaje(double eficienciaPorcentaje) { this.eficienciaPorcentaje = eficienciaPorcentaje; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getNumeroCedula() { return numeroCedula; }
    public void setNumeroCedula(String numeroCedula) { this.numeroCedula = numeroCedula; }

    public String getOm() { return om; }
    public void setOm(String om) { this.om = om; }
}
