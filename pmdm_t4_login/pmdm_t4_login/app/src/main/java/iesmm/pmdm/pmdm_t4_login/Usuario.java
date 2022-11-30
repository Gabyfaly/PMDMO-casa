package iesmm.pmdm.pmdm_t4_login;

public class Usuario {
    private String nombre;
    private String contrasenia;
    private String email;
    private String telefono;

    public Usuario(String nombre, String contrasenia, String email, String telefono) {
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.email = email;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}
