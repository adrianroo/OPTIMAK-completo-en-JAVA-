package co.optimak.modelo;

public class Referencia {

    private String idRef;
    private String tipoPrenda;
    private String cliente;
    private String coleccion;
    private double precioUnitario;

    // estos campos no son columnas de BD, se calculan con un JOIN en listarTodas()
    private int cantOperaciones;
    private double totalSam;

    public Referencia() {}

    public Referencia(String idRef, String tipoPrenda, String cliente, String coleccion, double precioUnitario) {
        this.idRef = idRef;
        this.tipoPrenda = tipoPrenda;
        this.cliente = cliente;
        this.coleccion = coleccion;
        this.precioUnitario = precioUnitario;
    }

    public String getIdRef() { return idRef; }
    public void setIdRef(String idRef) { this.idRef = idRef; }

    public String getTipoPrenda() { return tipoPrenda; }
    public void setTipoPrenda(String tipoPrenda) { this.tipoPrenda = tipoPrenda; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getColeccion() { return coleccion; }
    public void setColeccion(String coleccion) { this.coleccion = coleccion; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public int getCantOperaciones() { return cantOperaciones; }
    public void setCantOperaciones(int cantOperaciones) { this.cantOperaciones = cantOperaciones; }

    public double getTotalSam() { return totalSam; }
    public void setTotalSam(double totalSam) { this.totalSam = totalSam; }
}
