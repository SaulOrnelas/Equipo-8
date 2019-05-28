package itlapps.team8.childrenchat.firebase;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {
    private static FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    public static DatabaseReference USERS = DATABASE.getReference("users");

    public static void registrarPadre(String uid, String nombre, String fechaCumple, String genero, String curp, String email) {
        DatabaseReference referencia = USERS.child(uid).child("propiedades");
        referencia.child("key").setValue(uid);
        referencia.child("nombre").setValue(nombre);
        referencia.child("fecha_cumple").setValue(fechaCumple);
        referencia.child("genero").setValue(genero);
        referencia.child("curp").setValue(curp);
        referencia.child("email").setValue(email);
        referencia.child("tipo").setValue("padre");
    }

    public static void registrarHijo(String uidHijo, String uidPadre, String nombre, String fechaCumple, String genero, String curp, String email) {
        //Registrar un nuevo usuario de tipo hijo
        DatabaseReference referencia = USERS.child(uidHijo);
        DatabaseReference referenciaPropiedades = referencia.child("propiedades");
        referenciaPropiedades.child("key").setValue(referencia.getKey());
        referenciaPropiedades.child("padre").setValue(uidPadre);
        referenciaPropiedades.child("nombre").setValue(nombre);
        referenciaPropiedades.child("fecha_cumple").setValue(fechaCumple);
        referenciaPropiedades.child("genero").setValue(genero);
        referenciaPropiedades.child("curp").setValue(curp);
        referenciaPropiedades.child("email").setValue(email);
        referenciaPropiedades.child("tipo").setValue("hijo");

        //Añadimos al padre el hijo
        USERS.child(uidPadre).child("hijos").child(referencia.getKey()).setValue(referencia.getKey());

        //Añadimos al padre el contacto del hijo
        USERS.child(uidPadre).child("contactos").child(referencia.getKey()).setValue(referencia.getKey());

        //Añadimos al hijo el contacto del padre
        USERS.child(referencia.getKey()).child("contactos").child(uidPadre).setValue(uidPadre);
        //Esto es un comentario
    }
    public static void eliminarHijo(String uidPadre, String uidHijo){
        USERS.child(uidPadre).child("contactos").child(uidHijo).removeValue();
        USERS.child(uidPadre).child("hijos").child(uidHijo).removeValue();
        USERS.child(uidHijo).removeValue();
    }

    public static DatabaseReference obtenerUsuario(String uid) {
        return USERS.child(uid);
    }

    public static DatabaseReference obtenerHijos(String uid) {
        return USERS.child(uid).child("hijos");
    }



}
