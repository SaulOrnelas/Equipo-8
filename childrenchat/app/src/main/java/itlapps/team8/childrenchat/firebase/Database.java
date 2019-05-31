package itlapps.team8.childrenchat.firebase;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import itlapps.team8.childrenchat.model.ChatDelNodoUsuario;

public class Database {
    private static FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    public static DatabaseReference USERS = DATABASE.getReference("users");
    public static DatabaseReference CHATS = DATABASE.getReference("chats");

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

    public static void eliminarHijo(String uidPadre, String uidHijo) {
        USERS.child(uidPadre).child("contactos").child(uidHijo).removeValue();
        USERS.child(uidPadre).child("hijos").child(uidHijo).removeValue();
        //Borrar chat con ese hijo
        USERS.child(uidPadre).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        ChatDelNodoUsuario chatDelNodoUsuario = dataSnapshot1.getValue(ChatDelNodoUsuario.class);

                        if (chatDelNodoUsuario.keyContacto.equals(uidHijo)) {
                            CHATS.child(chatDelNodoUsuario.key).setValue(null);
                            USERS.child(uidPadre).child("chats").child(dataSnapshot1.getKey()).setValue(null);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        USERS.child(uidHijo).removeValue();
    }

    public static DatabaseReference obtenerUsuario(String uid) {
        return USERS.child(uid);
    }

    public static DatabaseReference obtenerHijos(String uid) {
        return USERS.child(uid).child("hijos");
    }

    public static DatabaseReference obtenerContactos(String uid) {
        return USERS.child(uid).child("contactos");
    }

    public static DatabaseReference obtenerChats(String uid) {
        return USERS.child(uid).child("chats");
    }

    public static void enviarMensaje(String mensajeString, String keyOfContact1, String keyOfContact2) {
        Log.e("key_1", keyOfContact1);
        Log.e("key_2", keyOfContact2);

        final boolean[] esta = {false};

        USERS.child(keyOfContact1).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        ChatDelNodoUsuario chatDelNodoUsuario = dataSnapshot1.getValue(ChatDelNodoUsuario.class);

                        if (chatDelNodoUsuario.keyContacto.equals(keyOfContact2)) {
                            DatabaseReference mensaje = CHATS.child(chatDelNodoUsuario.key).child("mensajes").push();
                            mensaje.child("key_contacto").setValue(keyOfContact1);
                            mensaje.child("mensaje").setValue(mensajeString);
                            esta[0] = true;
                            break;
                        } /*else {

                        }*/
                    }

                    if (!esta[0]) {
                        DatabaseReference chatReference = CHATS.push();
                        DatabaseReference mensaje = CHATS.child(chatReference.getKey()).child("mensajes").push();
                        mensaje.child("key_contacto").setValue(keyOfContact1);
                        mensaje.child("mensaje").setValue(mensajeString);

                        DatabaseReference chatOfUser1Reference = USERS.child(keyOfContact1).child("chats").push();
                        chatOfUser1Reference.child("key").setValue(chatReference.getKey());
                        chatOfUser1Reference.child("key_contacto").setValue(keyOfContact2);

                        DatabaseReference chatOfUser2Reference = USERS.child(keyOfContact2).child("chats").push();
                        chatOfUser2Reference.child("key").setValue(chatReference.getKey());
                        chatOfUser2Reference.child("key_contacto").setValue(keyOfContact1);

                    }

                } else {
                    DatabaseReference chatReference = CHATS.push();
                    DatabaseReference mensaje = CHATS.child(chatReference.getKey()).child("mensajes").push();
                    mensaje.child("key_contacto").setValue(keyOfContact1);
                    mensaje.child("mensaje").setValue(mensajeString);

                    DatabaseReference chatOfUser1Reference = USERS.child(keyOfContact1).child("chats").push();
                    chatOfUser1Reference.child("key").setValue(chatReference.getKey());
                    chatOfUser1Reference.child("key_contacto").setValue(keyOfContact2);

                    DatabaseReference chatOfUser2Reference = USERS.child(keyOfContact2).child("chats").push();
                    chatOfUser2Reference.child("key").setValue(chatReference.getKey());
                    chatOfUser2Reference.child("key_contacto").setValue(keyOfContact1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void enviarSolicitud(String keyContactoEnviado, String keyContactoRecibido) {
        //Creamos la solicitud en el nodo del usuario que envia
        DatabaseReference nuevaSolicitudContactoEnviado = USERS.child(keyContactoEnviado).child("solicitudes_enviadas").push();
        nuevaSolicitudContactoEnviado.child("key").setValue(nuevaSolicitudContactoEnviado.getKey());
        nuevaSolicitudContactoEnviado.child("key_contacto_enviado").setValue(keyContactoEnviado);
        nuevaSolicitudContactoEnviado.child("key_contacto_recibido").setValue(keyContactoRecibido);

        //Creamos la solicitud en el nodo del usuario que recibe
        DatabaseReference nuevaSolicitudContactoRecibido = USERS.child(keyContactoRecibido).child("solicitudes_recibidas").push();
        nuevaSolicitudContactoRecibido.child("key").setValue(nuevaSolicitudContactoRecibido.getKey());
        nuevaSolicitudContactoRecibido.child("key_contacto_enviado").setValue(keyContactoEnviado);
        nuevaSolicitudContactoRecibido.child("key_contacto_recibido").setValue(keyContactoRecibido);
    }

}
