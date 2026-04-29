package co.optimak.modelo;

public class Administrador {

    private int idAdministrador;
    private String nombre;
    private String departamento;
    private String hashContrasena;
    private String empresaNit;

    public Administrador() {
    }

    public Administrador(int idAdministrador, String nombre, String departamento,
                         String hashContrasena, String empresaNit) {
        this.idAdministrador = idAdministrador;
        this.nombre = nombre;
        this.departamento = departamento;
        this.hashContrasena = hashContrasena;
        this.empresaNit = empresaNit;
    }

    public int getIdAdministrador() { return idAdministrador; }
    public void setIdAdministrador(int idAdministrador) { this.idAdministrador = idAdministrador; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getHashContrasena() { return hashContrasena; }
    public void setHashContrasena(String hashContrasena) { this.hashContrasena = hashContrasena; }

    public String getEmpresaNit() { return empresaNit; }
    public void setEmpresaNit(String empresaNit) { this.empresaNit = empresaNit; }
}
