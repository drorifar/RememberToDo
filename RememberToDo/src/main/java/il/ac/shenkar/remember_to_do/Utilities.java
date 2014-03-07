package il.ac.shenkar.remember_to_do;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Concentrate Useful methods
 */
public class Utilities {

    public static Uri createUriFromPhotoIntentForHtcDesireHD( Activity activity, Intent intent, Uri uri ) {
        FileOutputStream fos = null;
        try {
            Bitmap bitmap = (Bitmap) intent.getExtras().get( "data" );
            File outputDir = activity.getCacheDir();
            File outputFile = File.createTempFile( "Photo-", ".jpg", outputDir );
            fos = new FileOutputStream( outputFile );
            bitmap.compress( Bitmap.CompressFormat.JPEG, 90, fos );
            uri = Uri.fromFile( outputFile );
        } catch ( IOException e ) {
        } finally {
            try {
                if ( fos != null ) {
                    fos.close();
                }
            } catch ( IOException e ) {
            }
        }
        return uri;
    }

    /**
     * Gets the current time and converts it to String using DateFormat
     * @return String that represent current time
     */
    public static String getDateTime(Calendar cal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd/MM/yyyy, kk:mm:ss", Locale.getDefault());
        Date d = cal.getTime();
        return dateFormat.format(d);
    }
}
