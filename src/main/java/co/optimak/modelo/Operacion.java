package co.optimak.modelo;

public class Operacion {

    private int idOperacion;
    private String nombreCorto;
    private String detalle;
    private String maquina;
    private double samOperacion;
    private String referenciaIdRef;

    public Operacion() {}

    public Operacion(int idOperacion, String nombreCorto, String detalle,
                     String maquina, double samOperacion, String referenciaIdRef) {
        this.idOperacion = idOperacion;
        this.nombreCorto = nombreCorto;
        this.detalle = detalle;
        this.maquina = maquina;
        this.samOperacion = samOperacion;
        this.referenciaIdRef = referenciaIdRef;
    }

    public int getIdOperacion() { return idOperacion; }
    public void setIdOperacion(int idOperacion) { this.idOperacion = idOperacion; }

    public String getNombreCorto() { return nombreCorto; }
    public void setNombreCorto(String nombreCorto) { this.nombreCorto = nombreCorto; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getMaquina() { return maquina; }
    public void setMaquina(String maquina) { this.maquina = maquina; }

    public double getSamOperacion() { return samOperacion; }
    public void setSamOperacion(double samOperacion) { this.samOperacion = samOperacion; }

    public String getReferenciaIdRef() { return referenciaIdRef; }
    public void setReferenciaIdRef(String referenciaIdRef) { this.referenciaIdRef = referenciaIdRef; }
}
