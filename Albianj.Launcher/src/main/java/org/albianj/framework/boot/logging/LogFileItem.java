package org.albianj.framework.boot.logging;

import org.albianj.framework.boot.servants.ConvertServant;
import org.albianj.framework.boot.servants.DailyServant;
import org.albianj.framework.boot.servants.StringServant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogFileItem {
    private String logName;
    private String logFolder;
    private long maxFilesizeB;
    private long busyB;
    private FileOutputStream fos;

    public LogFileItem(String logName, String path, String maxFilesize) {
        if (!path.endsWith(File.separator)) {
            logFolder = path + File.separator;
        }
        this.maxFilesizeB = ConvertServant.Instance.toFileSize(maxFilesize, 10 * 1024 * 1024);
        this.busyB = 0;
        this.logName = logName;
        File p = new File(path);
        if (!p.exists()) {
            p.mkdirs();
        }
        String filename = StringServant.Instance.join(logFolder, logName, "_", DailyServant.Instance.datetimeLongStringWithoutSep(), ".log");
        try {
            fos = new FileOutputStream(filename,true);
        } catch (FileNotFoundException e) {

        }
    }

    public void write(String buffer){
        byte[] bytes = StringServant.Instance.StringToBytes(buffer);
        synchronized (this){
            try {
                fos.write(bytes);
                fos.flush();
                busyB += bytes.length;
                if(busyB >= maxFilesizeB){
                    fos.close();
                    String filename = StringServant.Instance.join(logFolder, logName, "_", DailyServant.Instance.datetimeLongStringWithoutSep(), ".log");
                    try {
                        fos = new FileOutputStream(filename,true);
                    } catch (FileNotFoundException e) {

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        synchronized (this) {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}