package co.optimak.modelo;

public class Empresa {

    private String nit;
    private String nombre;
    private String direccion;
    private String ciudad;

    public Empresa() {
    }

    public Empresa(String nit, String nombre, String direccion, String ciudad) {
        this.nit = nit;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
}
