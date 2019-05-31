package itlapps.team8.childrenchat.model;

import com.google.firebase.database.PropertyName;

public class Solicitud {
    @PropertyName("key")
    public String key;
    @PropertyName("key_contacto_enviado")
    public String keyUsuarioEnviado;
    @PropertyName("key_contacto_recibido")
    public String keyUsuarioRecibido;

    public Solicitud() {}
}
