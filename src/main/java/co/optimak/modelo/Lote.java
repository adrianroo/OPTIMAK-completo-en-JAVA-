package co.optimak.modelo;

/**
 * Clase modelo que representa un Lote 
 */
public class Lote {

    // Clave primaria
    private String om;

    // Datos generales del lote
    private String numeroRemisionEntrada;
    private String oc;
    private String color;
    private String fechaIngresoPlanita;
    private String referenciaIdRef;
    private String moduloOperativoId;
    private String moduloOperativoEmpresaNit;

    // Cantidades por talla
    private int cantXXL;
    private int cantXL;
    private int cantL;
    private int cantM;
    private int cantS;
    private int cantXS;

    // Constructor vacio par crear objetos sin datos iniciales
    public Lote() {
    }

    // constructor completo 
    public Lote(String om, String numeroRemisionEntrada, String oc, String color,
                String fechaIngresoPlanita, String referenciaIdRef,
                String moduloOperativoId, String moduloOperativoEmpresaNit,
                int cantXXL, int cantXL, int cantL, int cantM, int cantS, int cantXS) {
        this.om = om;
        this.numeroRemisionEntrada = numeroRemisionEntrada;
        this.oc = oc;
        this.color = color;
        this.fechaIngresoPlanita = fechaIngresoPlanita;
        this.referenciaIdRef = referenciaIdRef;
        this.moduloOperativoId = moduloOperativoId;
        this.moduloOperativoEmpresaNit = moduloOperativoEmpresaNit;
        this.cantXXL = cantXXL;
        this.cantXL = cantXL;
        this.cantL = cantL;
        this.cantM = cantM;
        this.cantS = cantS;
        this.cantXS = cantXS;
    }

    public String getOm() {
        return om;
    }

    public void setOm(String om) {
        this.om = om;
    }

    public String getNumeroRemisionEntrada() {
        return numeroRemisionEntrada;
    }

    public void setNumeroRemisionEntrada(String numeroRemisionEntrada) {
        this.numeroRemisionEntrada = numeroRemisionEntrada;
    }

    public String getOc() {
        return oc;
    }

    public void setOc(String oc) {
        this.oc = oc;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFechaIngresoPlanita() {
        return fechaIngresoPlanita;
    }

    public void setFechaIngresoPlanita(String fechaIngresoPlanita) {
        this.fechaIngresoPlanita = fechaIngresoPlanita;
    }

    public String getReferenciaIdRef() {
        return referenciaIdRef;
    }

    public void setReferenciaIdRef(String referenciaIdRef) {
        this.referenciaIdRef = referenciaIdRef;
    }

    public String getModuloOperativoId() {
        return moduloOperativoId;
    }

    public void setModuloOperativoId(String moduloOperativoId) {
        this.moduloOperativoId = moduloOperativoId;
    }

    public String getModuloOperativoEmpresaNit() {
        return moduloOperativoEmpresaNit;
    }

    public void setModuloOperativoEmpresaNit(String moduloOperativoEmpresaNit) {
        this.moduloOperativoEmpresaNit = moduloOperativoEmpresaNit;
    }

    public int getCantXXL() {
        return cantXXL;
    }

    public void setCantXXL(int cantXXL) {
        this.cantXXL = cantXXL;
    }

    public int getCantXL() {
        return cantXL;
    }

    public void setCantXL(int cantXL) {
        this.cantXL = cantXL;
    }

    public int getCantL() {
        return cantL;
    }

    public void setCantL(int cantL) {
        this.cantL = cantL;
    }

    public int getCantM() {
        return cantM;
    }

    public void setCantM(int cantM) {
        this.cantM = cantM;
    }

    public int getCantS() {
        return cantS;
    }

    public void setCantS(int cantS) {
        this.cantS = cantS;
    }

    public int getCantXS() {
        return cantXS;
    }

    public void setCantXS(int cantXS) {
        this.cantXS = cantXS;
    }

    /**
     *esta es para calcular el total de unidades del lote sumando todas las tallas
     * @return da como resultado total
     */
    public int getTotalUnidades() {
        return cantXXL + cantXL + cantL + cantM + cantS + cantXS;
    }
}
