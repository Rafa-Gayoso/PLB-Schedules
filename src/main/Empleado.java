package main;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Empleado {
    private SimpleIntegerProperty cod_empleado;
    private SimpleStringProperty nombre;
    private SimpleStringProperty primer_apellido;
    private SimpleStringProperty segundo_apellido;
    private SimpleStringProperty nif;
    private SimpleStringProperty numero_afiliacion;
    private SimpleIntegerProperty cod_empresa;
    private SimpleStringProperty nombre_empresa;
    private SimpleIntegerProperty horas_laborables;

    public Empleado(String nombre, String primer_apellido, String segundo_apellido, String nif, String numero_afiliacion, int horas_laborables,int cod_empresa) {
        this.nombre = new SimpleStringProperty(nombre);
        this.primer_apellido = new SimpleStringProperty(primer_apellido);
        this.segundo_apellido = new SimpleStringProperty(segundo_apellido);
        this.nif = new SimpleStringProperty(nif);
        this.numero_afiliacion = new SimpleStringProperty(numero_afiliacion);
        this.horas_laborables = new SimpleIntegerProperty(horas_laborables);
        this.cod_empresa = new SimpleIntegerProperty(cod_empresa);
        this.nombre_empresa = new SimpleStringProperty(ServicesLocator.getEmpresa().getEmpresaNombreByCod(cod_empresa));
    }

    public Empleado(int cod_empleado, String nombre, String primer_apellido, String segundo_apellido, String nif, String numero_afiliacion, int horas_laborables,int cod_empresa) {
        this.cod_empleado = new SimpleIntegerProperty(cod_empleado);
        this.nombre = new SimpleStringProperty(nombre);
        this.primer_apellido = new SimpleStringProperty(primer_apellido);
        this.segundo_apellido = new SimpleStringProperty(segundo_apellido);
        this.nif = new SimpleStringProperty(nif);
        this.numero_afiliacion = new SimpleStringProperty(numero_afiliacion);
        this.horas_laborables = new SimpleIntegerProperty(horas_laborables);
        this.cod_empresa = new SimpleIntegerProperty(cod_empresa);
        this.nombre_empresa = new SimpleStringProperty(ServicesLocator.getEmpresa().getEmpresaNombreByCod(cod_empresa));
    }

    public Empleado() {
        this.cod_empleado = new SimpleIntegerProperty();
        this.nombre = new SimpleStringProperty();
        this.primer_apellido = new SimpleStringProperty();
        this.segundo_apellido = new SimpleStringProperty();
        this.nif = new SimpleStringProperty();
        this.numero_afiliacion = new SimpleStringProperty();
        this.cod_empresa = new SimpleIntegerProperty();
        this.nombre_empresa = new SimpleStringProperty();
        this.horas_laborables = new SimpleIntegerProperty();
    }




    public int getCod_empleado() {
        return cod_empleado.get();
    }

    public SimpleIntegerProperty cod_empleadoProperty() {
        return cod_empleado;
    }

    public void setCod_empleado(int cod_empleado) {
        this.cod_empleado.set(cod_empleado);
    }

    public String getNombre() {
        return nombre.get();
    }

    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getPrimer_apellido() {
        return primer_apellido.get();
    }

    public SimpleStringProperty primer_apellidoProperty() {
        return primer_apellido;
    }

    public void setPrimer_apellido(String primer_apellido) {
        this.primer_apellido.set(primer_apellido);
    }

    public String getSegundo_apellido() {
        return segundo_apellido.get();
    }

    public SimpleStringProperty segundo_apellidoProperty() {
        return segundo_apellido;
    }

    public void setSegundo_apellido(String segundo_apellido) {
        this.segundo_apellido.set(segundo_apellido);
    }

    public String getNif() {
        return nif.get();
    }

    public SimpleStringProperty nifProperty() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif.set(nif);
    }

    public String getNumero_afiliacion() {
        return numero_afiliacion.get();
    }

    public SimpleStringProperty numero_afiliacionProperty() {
        return numero_afiliacion;
    }

    public void setNumero_afiliacion(String numero_afiliacion) {
        this.numero_afiliacion.set(numero_afiliacion);
    }

    public int getCod_empresa() {
        return cod_empresa.get();
    }

    public SimpleIntegerProperty cod_empresaProperty() {
        return cod_empresa;
    }

    public void setCod_empresa(int cod_empresa) {
        this.cod_empresa.set(cod_empresa);
    }

    public String getNombre_empresa() {
        return nombre_empresa.get();
    }

    public SimpleStringProperty nombre_empresaProperty() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa.set(nombre_empresa);
    }

    public int getHoras_laborables() {
        return horas_laborables.get();
    }

    public SimpleIntegerProperty horas_laborablesProperty() {
        return horas_laborables;
    }

    public void setHoras_laborables(int horas_laborables) {
        this.horas_laborables.set(horas_laborables);
    }

    public String toStringCode() {
        return "Empleado{" +
                "cod_empleado=" + cod_empleado +
                ", nombre='" + nombre + '\'' +
                ", primer_apellido='" + primer_apellido + '\'' +
                ", segundo_apellido='" + segundo_apellido + '\'' +
                ", nif='" + nif + '\'' +
                ", numero_afiliacion='" + numero_afiliacion + '\'' +
                ", nombre_empresa=" + nombre_empresa +
                '}';
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "nombre='" + nombre + '\'' +
                ", primer_apellido='" + primer_apellido + '\'' +
                ", segundo_apellido='" + segundo_apellido + '\'' +
                ", nif='" + nif + '\'' +
                ", numero_afiliacion='" + numero_afiliacion + '\'' +
                ", nombre_empresa=" + nombre_empresa +
                '}';
    }
}
