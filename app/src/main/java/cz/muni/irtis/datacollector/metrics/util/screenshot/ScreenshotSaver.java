package cz.muni.irtis.datacollector.metrics.util.screenshot;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

class ScreenshotSaver {
    private static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyyMMddHHmmssSSS")
            .toFormatter();

    /**
     * Save PNG image to local storage & return absolute path.
     * File has 64 char long random UUID name.
     * @param png image
     * @param context context
     * @return absolute path to file
     */
    static String processImage(final byte[] png, final Context context) {
        String fileName = formatter.print(DateTime.now()) + ".png";
        File output = new File(context.getExternalFilesDir(null), fileName);
        String absolutePath = null;
        try {
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(png);
            fos.flush();
            fos.getFD().sync();
            fos.close();

            absolutePath = output.getAbsolutePath();
            Log.d("INFO: ", absolutePath);
        }
        catch (Exception e) {
            Log.e("ScreenshotSaver", "Exception writing out screenshot", e);
        }
        return absolutePath;
    }

    // TODO: delete
    public static String processImage_Threaded(final byte[] png, final String dir, final Context context) {
        final String[] absolutePath = new String[1];
        new Thread() {
            @Override
            public void run() {
                Random rand = new Random();
                int n = rand.nextInt(10000) + 1;

                File output=new File(dir,"screenshot_" + n  + ".png");

                try {
                    FileOutputStream fos=new FileOutputStream(output);

                    fos.write(png);
                    fos.flush();
                    fos.getFD().sync();
                    fos.close();

                    MediaScannerConnection.scanFile(context,
                            new String[] {output.getAbsolutePath()},
                            new String[] {"image/png"},
                            null);

                    Log.i("INFO: ", output.getAbsolutePath());
                    absolutePath[0] = output.getAbsolutePath();
                }
                catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Exception writing out screenshot", e);
                }
            }
        }.start();
        return absolutePath[0];
    }
}
