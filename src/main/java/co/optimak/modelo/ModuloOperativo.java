package co.optimak.modelo;

public class ModuloOperativo {

    private String idModulo;
    private String empresaNit;
    private int cantOperarios; // calculado via JOIN, no es columna en BD

    public ModuloOperativo() {}

    public ModuloOperativo(String idModulo, String empresaNit) {
        this.idModulo = idModulo;
        this.empresaNit = empresaNit;
    }

    public String getIdModulo() { return idModulo; }
    public void setIdModulo(String idModulo) { this.idModulo = idModulo; }

    public String getEmpresaNit() { return empresaNit; }
    public void setEmpresaNit(String empresaNit) { this.empresaNit = empresaNit; }

    public int getCantOperarios() { return cantOperarios; }
    public void setCantOperarios(int cantOperarios) { this.cantOperarios = cantOperarios; }
}
