package itlapps.team8.childrenchat.model;

import com.google.firebase.database.PropertyName;

public class Mensaje {
    @PropertyName("mensaje")
    public String mensaje;
    @PropertyName("key_contacto")
    public String keyContacto;

    public Mensaje() {}
}
