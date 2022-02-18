package model;

public class Usuario {

    private int usarioId;
    private String username;
    private String password;
    private int rol;

    public Usuario(String username, String password, int rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public Usuario(int usarioId, String username, String password, int rol) {
        this.usarioId = usarioId;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public int getUsarioId() {
        return usarioId;
    }

    public void setUsarioId(int uusarioId) {
        this.usarioId = uusarioId;
    }
}
