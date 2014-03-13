package il.ac.shenkar.remember_to_do;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Concentrate Useful methods
 * @author Dror Afargan & Ran Nahmijas
 */
public class Utilities {

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
     * decodes image and scales it to reduce memory consumption
     * @param f
     * @return
     */
    public static Bitmap decodeFile(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=70;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
}
