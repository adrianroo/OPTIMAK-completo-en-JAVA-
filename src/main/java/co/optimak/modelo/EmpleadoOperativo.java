package co.optimak.modelo;

public class EmpleadoOperativo {

    private String numeroCedula;
    private String nombre;
    private String telefono;
    private int edad;
    private String sexo;
    private String cargoRol;
    private String correo;
    private String hashContrasena;
    private int activo;
    private String moduloOperativoId;
    private String moduloOperativoEmpresaNit;

    public EmpleadoOperativo() {
    }

    public EmpleadoOperativo(String numeroCedula, String nombre, String telefono, int edad,
                              String sexo, String cargoRol, String correo,
                              String hashContrasena, int activo,
                              String moduloOperativoId, String moduloOperativoEmpresaNit) {
        this.numeroCedula = numeroCedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.edad = edad;
        this.sexo = sexo;
        this.cargoRol = cargoRol;
        this.correo = correo;
        this.hashContrasena = hashContrasena;
        this.activo = activo;
        this.moduloOperativoId = moduloOperativoId;
        this.moduloOperativoEmpresaNit = moduloOperativoEmpresaNit;
    }

    public String getNumeroCedula() { return numeroCedula; }
    public void setNumeroCedula(String numeroCedula) { this.numeroCedula = numeroCedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getCargoRol() { return cargoRol; }
    public void setCargoRol(String cargoRol) { this.cargoRol = cargoRol; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getHashContrasena() { return hashContrasena; }
    public void setHashContrasena(String hashContrasena) { this.hashContrasena = hashContrasena; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    public String getModuloOperativoId() { return moduloOperativoId; }
    public void setModuloOperativoId(String moduloOperativoId) { this.moduloOperativoId = moduloOperativoId; }

    public String getModuloOperativoEmpresaNit() { return moduloOperativoEmpresaNit; }
    public void setModuloOperativoEmpresaNit(String moduloOperativoEmpresaNit) { this.moduloOperativoEmpresaNit = moduloOperativoEmpresaNit; }
}
