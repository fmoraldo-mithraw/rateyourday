package com.mithraw.howwasyourday.Helpers;

import android.content.Context;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;

import com.mithraw.howwasyourday.Activities.ImportExportActivity;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.Tools.ImportResult;
import com.mithraw.howwasyourday.databases.Day;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

import io.reactivex.annotations.Nullable;


public class DataBaseHelper {
    public static void getCsv(final File file, final Handler handler) {
        new Thread() {
            @Override
            public void run() {
                try {
                    DaysDatabase db = DaysDatabase.getInstance(App.getContext());
                    List<Day> days = db.dayDao().getAll();
                    FileOutputStream fo = new FileOutputStream(file);
                    for (Day d : days) {
                        String str = "\"" + d.getDate_time() + "\";\"" + d.getRating() + "\";\"" + d.getTitleText().replace("\"", "").replace(";", "") + "\";\"" + d.getLog().replace("\n", "{{{CRLF}}}").replace("\"", "").replace(";", "") + "\"\n";
                        fo.write(str.getBytes());
                    }
                    fo.flush();
                    fo.close();
                } catch (FileNotFoundException e) {
                    Message msg = Message.obtain();
                    msg.what = ImportExportActivity.MSG_ID.FILENOTFOUNDEXCEPTION.ordinal();
                    msg.obj = e;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    Message msg = Message.obtain();
                    msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
                    msg.obj = e;
                    handler.sendMessage(msg);
                }

            }
        }.start();
    }

    public static void getDb(final File file, final Handler handler) {
        new Thread() {
            @Override
            public void run() {
                try {
                    DaysDatabase.copyDatabaseToBackup(App.getContext());
                    File dbFile = App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile();
                    FileInputStream fi = new FileInputStream(dbFile);
                    FileOutputStream fo = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int bufferLength;
                    while ((bufferLength = fi.read(buffer)) > 0) {
                        fo.write(buffer, 0, bufferLength);
                    }
                    fi.close();
                    fo.flush();
                    fo.close();

                } catch (FileNotFoundException e) {
                    Message msg = Message.obtain();
                    msg.what = ImportExportActivity.MSG_ID.FILENOTFOUNDEXCEPTION.ordinal();
                    msg.obj = e;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    Message msg = Message.obtain();
                    msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
                    msg.obj = e;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    public static void importCsv(final InputStream stream, final Handler mHandler) {
        new Thread() {
            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                int count = 0;
                int failed = 0;
                try {
                    while (reader.ready()) {
                        String line = reader.readLine();
                        String[] parts = line.split("\";\"");
                        if (parts.length >= 4) {
                            Calendar cal = Calendar.getInstance();
                            String time = parts[0].substring(1, parts[0].length());
                            cal.setTimeInMillis(Long.getLong(time, 0));
                            Day d = new Day(cal.get(Calendar.DAY_OF_WEEK),
                                    cal.get(Calendar.DAY_OF_MONTH),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.WEEK_OF_YEAR),
                                    cal.getTimeInMillis(),
                                    Integer.decode(parts[1].replace("\"","")),
                                    parts[2].replace("\"",""),
                                    parts[3].replace("{{{CRLF}}}", "\n").replace("\"",""),
                                    0,
                                    0,
                                    false);
                            try {
                                DaysDatabase.getInstance(App.getContext()).dayDao().insertDay(d);
                                count++;
                            } catch (Exception e) {
                                failed++;
                            }
                        } else {
                            failed++;
                        }
                    }
                } catch (IOException e) {
                    Message msg = Message.obtain();
                    msg.obj = e;
                    msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.obj = new ImportResult(failed, count);
                msg.what = ImportExportActivity.MSG_ID.CSV_IMPORTED.ordinal();
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    public static void importDb(final InputStream inputStream, final Handler mHandler) {
        new Thread() {
            @Override
            public void run() {

                try {
                    File file = App.getContext().getDatabasePath(DaysDatabase.getDatabaseImportName()).getAbsoluteFile();
                    file.delete();
                    file.createNewFile();
                    FileObserver fo = new FileObserver(file.getAbsolutePath()) {
                        @Override
                        public void onEvent(int event, @Nullable String path) {
                            if ((event == CLOSE_WRITE) || (event == CLOSE_NOWRITE)) {
                                this.stopWatching();
                                int count = 0;
                                int failed = 0;
                                count = DaysDatabase.copyImportToDatabase(App.getContext());
                                Message msg = Message.obtain();
                                msg.obj = new ImportResult(failed, count);
                                msg.what = ImportExportActivity.MSG_ID.DB_IMPORTED.ordinal();
                                mHandler.sendMessage(msg);
                            }
                        }
                    };
                    fo.startWatching();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int bufferLength;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, bufferLength);
                    }
                    fos.flush();
                    fos.close();
                    inputStream.close();
                } catch (IOException e) {
                    Message msg = Message.obtain();
                    msg.obj = e;
                    msg.what = ImportExportActivity.MSG_ID.IOEXCEPTION.ordinal();
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }
}
