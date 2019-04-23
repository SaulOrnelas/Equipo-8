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

    public static DatabaseReference obtenerUsuario(String uid) {
        return USERS.child(uid);
    }

}
