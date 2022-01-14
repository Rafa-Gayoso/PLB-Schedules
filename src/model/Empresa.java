package model;

public class Empresa {
    private int cod_empresa;
    private String nombre;
    private String nif;
    private String centro_de_trabajo;
    private String c_c_c;

    public int getCod_empresa() {
        return cod_empresa;
    }

    public void setCod_empresa(int cod_empresa) {
        this.cod_empresa = cod_empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getCentro_de_trabajo() {
        return centro_de_trabajo;
    }

    public void setCentro_de_trabajo(String centro_de_trabajo) {
        this.centro_de_trabajo = centro_de_trabajo;
    }

    public String getC_c_c() {
        return c_c_c;
    }

    public void setC_c_c(String c_c_c) {
        this.c_c_c = c_c_c;
    }

    public Empresa(int cod_empresa, String nombre, String nif, String centro_de_trabajo, String c_c_c) {
        this.cod_empresa = cod_empresa;
        this.nombre = nombre;
        this.nif = nif;
        this.centro_de_trabajo = centro_de_trabajo;
        this.c_c_c = c_c_c;
    }
}
