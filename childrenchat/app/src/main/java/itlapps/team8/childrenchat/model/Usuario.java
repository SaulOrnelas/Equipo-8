package itlapps.team8.childrenchat.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;


public class Usuario {
    @PropertyName("propiedades")
    public UsuarioPropiedades propiedades;
    @PropertyName("chats")
    public ChatDelNodoUsuario chats;

    public Usuario() {}
}
