package itlapps.team8.childrenchat.firebase;

import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Storage {
    private static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private static StorageReference storageReference = firebaseStorage.getReference();
    private static StorageReference userProfileImages = storageReference.child("users");

    public static void guardarImagenPerfilUsuario(String nombreArchivo, byte[] dataBytes) {
        UploadTask uploadTaskNormal = userProfileImages.child(nombreArchivo).putBytes(dataBytes);

        uploadTaskNormal.addOnFailureListener(e -> Log.e("Error", e.getMessage())).
                addOnSuccessListener(taskSnapshot -> Log.e("Success", "Exito"));
    }

    public static StorageReference obtenerReferenciaFotoPerfilUsuario(String uid) {
        return userProfileImages.child(uid);
    }
}
