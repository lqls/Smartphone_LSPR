package com.lqSPRimaging.util;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

public class SaveUtil {

    static final File parentPath = Environment.getExternalStorageDirectory();
    private static final String FOLDER_NAME = "LSPR_Data";
    private String storagePath = "";
    private FileOutputStream fos = null;
    private PrintWriter pw = null;

    public void saveToSDcard(int time, double[] data, File file) {

        if (fos == null) {
            try {
                System.out.println("fos is null");
                fos = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            pw = new PrintWriter(fos);
        }

        pw.print(time);
        for (int i = 0; i < data.length; i++) {
            pw.print("\t");
            pw.print(data[i]);

        }
        pw.print("\r\n");

    }


    public void closeOutputStream() {

        if (pw != null) {
            pw.close();
            pw = null;
        }
        if (fos != null) {
            try {
                fos.close();
                fos = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File createFile() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        storagePath = parentPath.getAbsolutePath() + "/" + FOLDER_NAME + "/"
                + month + "_" + day + "_" + hour + minute;

        System.out.println(storagePath);

        File f = new File(storagePath);
        boolean createFile = f.mkdirs();


        File file = new File(storagePath, "spr_data" + ".txt");
        return file;
    }

}
