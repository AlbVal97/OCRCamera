package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

class InternalStorageManager {
    private String dirName;
    private Context context;
    private String fileName;
    private String path;

    /**
     *
     * @param context application context
     * @param dirName the directory where the file will be saved
     * @param fileName  name of the file
     * @author Luca Moroldo
     */
    InternalStorageManager(Context context, String dirName, String fileName) {
        this.dirName = dirName;
        this.context = context;
        this.fileName = fileName;

        SharedPreferences sharedPref = context.getSharedPreferences("manager", Context.MODE_PRIVATE);
        this.path = sharedPref.getString(fileName, "");


    }


    /**
     * Saves a bitmap inside internal storage, using filename and directory provided on construction
     * @param bitmap image to save inside internal storage
     * @modify shared preferences to store the image's path
     * @author Luca Moroldo
     */
    void saveBitmapToInternalStorage(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(this.dirName, Context.MODE_PRIVATE);
        File file = new File(directory, fileName);


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.path = directory.getAbsolutePath();


        SharedPreferences sharedPref = context.getSharedPreferences("manager", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(fileName, this.path);
        editor.apply();
    }

    /**
     *
     * @return Bitmap loaded from internal storage if exists, null otherwise
     * @author Luca Moroldo
     */
    Bitmap loadBitmapFromInternalStorage() {
        try {
            File f=new File(this.path, fileName);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * @return Boolean true if the file exists
     * @author Luca Moroldo
     */
    Boolean existsFile() {
        File f=new File(this.path, fileName);
        return f.exists();
    }

    String getDirName(){
        return dirName;
    }

    String getPath(){
        return this.path;
    }
}