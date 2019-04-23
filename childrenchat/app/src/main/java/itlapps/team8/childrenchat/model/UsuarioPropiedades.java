package itlapps.team8.childrenchat.model;

import com.google.firebase.database.PropertyName;

public class UsuarioPropiedades {
    @PropertyName("key")
    public String key;
    @PropertyName("nombre")
    public String nombre;
    @PropertyName("fecha_cumple")
    public String fechaCumple;
    @PropertyName("genero")
    public String genero;
    @PropertyName("curp")
    public String curp;
    @PropertyName("email")
    public String email;
    @PropertyName("tipo")
    public String tipo;

    public UsuarioPropiedades() {}
}
