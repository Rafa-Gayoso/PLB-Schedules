package model;

import dao.implementation.EmpresaDaoImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import services.ServicesLocator;

import java.io.File;
import java.util.Objects;

public class Empleado {
    private SimpleIntegerProperty cod_empleado;
    private final SimpleStringProperty nombre;
    private final SimpleStringProperty primer_apellido;
    private final SimpleStringProperty segundo_apellido;
    private final SimpleStringProperty nif;
    private final SimpleStringProperty numero_afiliacion;
    private final SimpleIntegerProperty cod_empresa;
    private final SimpleStringProperty nombre_empresa;
    private final SimpleIntegerProperty horas_laborables;
    private String direccionCronograma;
    private int codUsuario;
    private String email;
    private int vacations;

    @SuppressWarnings("unused")
    public Empleado(String nombre, String primer_apellido, String segundo_apellido, String nif, String numero_afiliacion,
                    int horas_laborables, int cod_empresa) {
        this.nombre = new SimpleStringProperty(nombre);
        this.primer_apellido = new SimpleStringProperty(primer_apellido);
        this.segundo_apellido = new SimpleStringProperty(segundo_apellido);
        this.nif = new SimpleStringProperty(nif);
        this.numero_afiliacion = new SimpleStringProperty(numero_afiliacion);
        this.horas_laborables = new SimpleIntegerProperty(horas_laborables);
        this.cod_empresa = new SimpleIntegerProperty(cod_empresa);
        this.nombre_empresa = new SimpleStringProperty(new EmpresaDaoImpl().getEmpresaNombreByCod(cod_empresa));
        this.codUsuario = 0;
    }

    public Empleado(int cod_empleado, String nombre, String primer_apellido, String segundo_apellido, String nif,
                    String numero_afiliacion, int horas_laborables, int cod_empresa, String direccionCronograma,
                    int codUsuario, String email, int vacations) {
        this.cod_empleado = new SimpleIntegerProperty(cod_empleado);
        this.nombre = new SimpleStringProperty(nombre);
        this.primer_apellido = new SimpleStringProperty(primer_apellido);
        this.segundo_apellido = new SimpleStringProperty(segundo_apellido);
        this.nif = new SimpleStringProperty(nif);
        this.numero_afiliacion = new SimpleStringProperty(numero_afiliacion);
        this.horas_laborables = new SimpleIntegerProperty(horas_laborables);
        this.cod_empresa = new SimpleIntegerProperty(cod_empresa);
        this.nombre_empresa = new SimpleStringProperty(new EmpresaDaoImpl().getEmpresaNombreByCod(cod_empresa));
        this.direccionCronograma = direccionCronograma;
        this.codUsuario = codUsuario;
        this.email = email;
        this.vacations = vacations;

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
        this.direccionCronograma = "";
        this.email = "";
        this.vacations = 0;
    }




    public int getCod_empleado() {
        return cod_empleado.get();
    }

    @SuppressWarnings("unused")
    public SimpleIntegerProperty cod_empleadoProperty() {
        return cod_empleado;
    }

    @SuppressWarnings("unused")
    public void setCod_empleado(int cod_empleado) {
        this.cod_empleado.set(cod_empleado);
    }

    public String getNombre() {
        return nombre.get();
    }

    @SuppressWarnings("unused")
    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getPrimer_apellido() {
        return primer_apellido.get();
    }

    @SuppressWarnings("unused")
    public SimpleStringProperty primer_apellidoProperty() {
        return primer_apellido;
    }

    public void setPrimer_apellido(String primer_apellido) {
        this.primer_apellido.set(primer_apellido);
    }

    public String getSegundo_apellido() {
        return segundo_apellido.get();
    }

    @SuppressWarnings("unused")
    public SimpleStringProperty segundo_apellidoProperty() {
        return segundo_apellido;
    }

    public void setSegundo_apellido(String segundo_apellido) {
        this.segundo_apellido.set(segundo_apellido);
    }

    public String getNif() {
        return nif.get();
    }

    @SuppressWarnings("unused")
    public SimpleStringProperty nifProperty() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif.set(nif);
    }

    public String getNumero_afiliacion() {
        return numero_afiliacion.get();
    }

    @SuppressWarnings("unused")
    public SimpleStringProperty numero_afiliacionProperty() {
        return numero_afiliacion;
    }

    public void setNumero_afiliacion(String numero_afiliacion) {
        this.numero_afiliacion.set(numero_afiliacion);
    }

    public int getCod_empresa() {
        return cod_empresa.get();
    }

    @SuppressWarnings("unused")
    public SimpleIntegerProperty cod_empresaProperty() {
        return cod_empresa;
    }

    public void setCod_empresa(int cod_empresa) {
        this.cod_empresa.set(cod_empresa);
    }

    public String getNombre_empresa() {
        return nombre_empresa.get();
    }

    @SuppressWarnings("unused")
    public SimpleStringProperty nombre_empresaProperty() {
        return nombre_empresa;
    }

    @SuppressWarnings("unused")
    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa.set(nombre_empresa);
    }

    public int getHoras_laborables() {
        return horas_laborables.get();
    }

    @SuppressWarnings("unused")
    public SimpleIntegerProperty horas_laborablesProperty() {
        return horas_laborables;
    }

    public void setHoras_laborables(int horas_laborables) {
        this.horas_laborables.set(horas_laborables);
    }



    public String getDireccionCronograma() {
        return direccionCronograma;
    }

    public void setDireccionCronograma(String direccionCronograma) {
        this.direccionCronograma = direccionCronograma;
    }


    public int getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(int codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getVacations() {
        return vacations;
    }

    public void setVacations(int vacations) {
        this.vacations = vacations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Empleado empleado = (Empleado) o;
        return codUsuario == empleado.codUsuario &&
                vacations == empleado.vacations &&
                cod_empleado.equals(empleado.cod_empleado) &&
                nombre.equals(empleado.nombre) &&
                primer_apellido.equals(empleado.primer_apellido) &&
                segundo_apellido.equals(empleado.segundo_apellido) &&
                nif.equals(empleado.nif) &&
                numero_afiliacion.equals(empleado.numero_afiliacion) &&
                cod_empresa.equals(empleado.cod_empresa) &&
                nombre_empresa.equals(empleado.nombre_empresa) &&
                horas_laborables.equals(empleado.horas_laborables) &&
                direccionCronograma.equals(empleado.direccionCronograma) &&
                email.equals(empleado.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cod_empleado, nombre, primer_apellido, segundo_apellido, nif, numero_afiliacion, cod_empresa, nombre_empresa, horas_laborables, direccionCronograma, codUsuario, email, vacations);
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "cod_empleado=" + cod_empleado +
                ", nombre=" + nombre +
                ", primer_apellido=" + primer_apellido +
                ", segundo_apellido=" + segundo_apellido +
                ", nif=" + nif +
                ", numero_afiliacion=" + numero_afiliacion +
                ", cod_empresa=" + cod_empresa +
                ", nombre_empresa=" + nombre_empresa +
                ", horas_laborables=" + horas_laborables +
                ", direccionCronograma='" + direccionCronograma + '\'' +
                ", codUsuario=" + codUsuario +
                ", email='" + email + '\'' +
                ", vacations=" + vacations +
                '}';
    }
}
