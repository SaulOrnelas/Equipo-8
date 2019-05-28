package itlapps.team8.childrenchat.extras;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import itlapps.team8.childrenchat.firebase.Storage;

public class CCUtil {

    /**
     * Calcula la edad del usuario a partir de su fecha de nacimiento
     *
     * @param fechaNacimiento
     * @return
     */
    public static String edad(String fechaNacimiento) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = format.parse(fechaNacimiento);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        Calendar fechaNacimientoUsuario = Calendar.getInstance();
        fechaNacimientoUsuario.setTime(date);

        Calendar hoy = Calendar.getInstance();

        long diferencia = hoy.getTimeInMillis() - fechaNacimientoUsuario.getTimeInMillis();

        long anios = (diferencia / (24 * 60 * 60 * 1000)) / 365;

        return String.valueOf(anios);
    }


    /**
     * Toma los bytes de la imagen seleccionada y almacena la imagen en la base de datos
     */
    public static void guardarImagenPerfilUsuario(CircleImageView circleImage, String keyOfUser) {
        circleImage.setDrawingCacheEnabled(true);
        circleImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) circleImage.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dataBytes = baos.toByteArray();
        String nombreArchivo = keyOfUser + ".jpg";

        //lkmkmkmks

        Storage.guardarImagenPerfilUsuario(nombreArchivo, dataBytes);
    }

}
