package com.mithraw.howwasyourday.Helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.TimeAlarm;
import com.mithraw.howwasyourday.databases.DaysDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ThreadSyncDatas extends Thread {
    static boolean isRunning = false;
    GoogleSignInAccount mGoogleSignInAccount;
    DriveHelper mDriveHelper;
    OnFailureListener failureListener;

    public static void reSchedule(int amoutOfMinutes) {
        Resources res = App.getApplication().getResources();
        String syncIntentAction = res.getString(R.string.syncIntentAction);
        Intent intent = new Intent(App.getApplication().getApplicationContext(), TimeAlarm.class);
        intent.setAction(syncIntentAction);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.MINUTE, amoutOfMinutes);
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) App.getApplication().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(App.getApplication().getApplicationContext(), 0, intent, 0);
        alarmMgr.cancel(alarmIntent);
        if (amoutOfMinutes != 0) {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
            Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : reSchedule for " + amoutOfMinutes + " minutes at < " + cal.getTime() + " >"));
        }

    }

    public void OnFinalSuccess() {
        isRunning = false;
        reSchedule(PreferenceHelper.getSyncTime());
    }

    public ThreadSyncDatas(GoogleSignInAccount googleSignInAccount) {
        mGoogleSignInAccount = googleSignInAccount;
        failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isRunning = false;
                Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : onFailure : <" + e.getMessage() + ">"));
                reSchedule(5);
            }
        };
    }

    @Override
    public void run() {
        if (isRunning)
            return;
        isRunning = true;
        //Init Drive API
        if (mGoogleSignInAccount != null) {
            mDriveHelper = new DriveHelper(mGoogleSignInAccount);
        } else {
            isRunning = false;
            return;
        }
        //Retreive datas
        final Task<Void> syncTask = mDriveHelper.getDriveClient().requestSync();
        syncTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final Task<DriveFolder> driveFolderTask = mDriveHelper.getDriveResourceClient().getAppFolder();
                Tasks.whenAll(driveFolderTask).continueWith(new Continuation<Void, Task<DriveFolder>>() {
                    @Override
                    public Task<DriveFolder> then(@NonNull Task<Void> task) throws Exception {
                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : getAppFolder Success"));
                        //Search for the file
                        DriveFolder driveFolder = driveFolderTask.getResult();
                        searchForDatabase(driveFolder);
                        return driveFolderTask;
                    }
                }).addOnFailureListener(failureListener);
            }
        }).addOnFailureListener(failureListener);
    }

    private void searchForDatabase(final DriveFolder driveFolder) {
        Query q = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, DaysDatabase.getDatabaseBackupName()))
                .build();
        Task<MetadataBuffer> taskMetaDatas = mDriveHelper.getDriveResourceClient().queryChildren(driveFolder, q);
        taskMetaDatas
                .addOnSuccessListener(
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : queryChildren Success"));
                                if (metadataBuffer.getCount() != 0) {
                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : queryChildren Success : " + metadataBuffer.getCount() + " child"));

                                    //Retreive file
                                    for (Metadata metadata : metadataBuffer)
                                        retrieveDb(metadata.getDriveId().asDriveFile());
                                } else {
                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : queryChildren Success : no children"));
                                    copyAndUploadDb(driveFolder, DaysDatabase.getDatabaseBackupName());
                                }
                            }
                        })
                .addOnFailureListener(failureListener);
    }

    private void retrieveDb(final DriveFile driveFile) {
        Task<DriveContents> openTask = mDriveHelper.getDriveResourceClient().openFile(driveFile, DriveFile.MODE_READ_ONLY);
        openTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<DriveContents> task) {
                try {
                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDbAndUpdate continueWithTask"));
                    final DriveContents driveContents = task.getResult();
                    InputStream inputStream = driveContents.getInputStream();
                    //Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDbAndUpdate ParcelFileDescriptor size: " + pfd.getStatSize()));
                    File file = App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile();
                    file.delete();

                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDbAndUpdate backup path: " + App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsolutePath()));
                    FileOutputStream fileOutput = new FileOutputStream(App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile());
                    byte[] buffer = new byte[1024];
                    int bufferLength;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                    }
                    fileOutput.close();
                    inputStream.close();
                    updateDb(driveFile);


                } catch (Exception e) {
                }
                return null;
            }
        });
    }

    private void updateDb(DriveFile driveFile) {
        Task<DriveContents> openTask = mDriveHelper.getDriveResourceClient().openFile(driveFile, DriveFile.MODE_WRITE_ONLY);
        openTask.continueWithTask(
                new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) {
                        final DriveContents driveContents = task.getResult();
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDbAndUpdate size of the Database Backup Before:" + App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile().length()));
                                } catch (Exception e) {
                                }

                                DaysDatabase.copyBackupToDatabase(App.getContext());
                                DaysDatabase.copyDatabaseToBackup(App.getContext());
                                DaysDatabase.cleanDatabase(DaysDatabase.getInstance(App.getContext()));
                                DaysDatabase.cleanDatabase(DaysDatabase.getBackupNewInstance(App.getContext()));
                                try {

                                    OutputStream outputStream = driveContents.getOutputStream();
                                    long totalSize = driveContents.getParcelFileDescriptor().getStatSize();
                                    FileInputStream fileInput = new FileInputStream(App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile());
                                    byte[] buffer = new byte[1024];
                                    int bufferLength;
                                    try {
                                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDbAndUpdate size of the Database Backup After:" + App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile().length()));
                                    } catch (Exception e) {
                                    }
                                    long totalSizeWriten = 0;
                                    while ((bufferLength = fileInput.read(buffer)) > 0) {
                                        totalSizeWriten += bufferLength;
                                        outputStream.write(buffer, 0, bufferLength);
                                    }

                                    fileInput.close();
                                    outputStream.close();
                                } catch (Exception e) {
                                }

                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .build();
                                Task<Void> commitTask = mDriveHelper.getDriveResourceClient().commitContents(driveContents, changeSet).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDbAndUpdate commited"));
                                        OnFinalSuccess();
                                    }
                                }).addOnFailureListener(failureListener);
                            }
                        }.start();
                        return null;
                    }
                });
    }


    private void copyAndUploadDb(final DriveFolder parent, final String databaseName) {
        new Thread() {
            @Override
            public void run() {
                DaysDatabase.copyDatabaseToBackup(App.getContext());
                final Task<DriveContents> createContentsTask = mDriveHelper.getDriveResourceClient().createContents();
                createContentsTask.addOnSuccessListener(new OnSuccessListener<DriveContents>() {
                    @Override
                    public void onSuccess(DriveContents contents) {
                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : createContents Success"));
                        OutputStream outputStream = contents.getOutputStream();
                        try {
                            FileInputStream fileInput = new FileInputStream(App.getContext().getDatabasePath(databaseName).getAbsoluteFile());
                            byte[] buffer = new byte[1024];
                            int bufferLength;
                            while ((bufferLength = fileInput.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, bufferLength);
                            }
                            fileInput.close();
                            outputStream.close();
                        } catch (Exception e) {
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(databaseName)
                                .setMimeType("application/x-sqlite3")
                                .build();

                        Task<DriveFile> driveFile = mDriveHelper.getDriveResourceClient().createFile(parent, changeSet, contents);
                        driveFile.addOnSuccessListener(new OnSuccessListener<DriveFile>() {
                            @Override
                            public void onSuccess(DriveFile driveFile) {
                                OnFinalSuccess();
                            }
                        }).addOnFailureListener(failureListener);
                        isRunning = false;
                    }
                }).addOnFailureListener(failureListener);
            }
        }.start();
    }
}

