package com.mithraw.howwasyourday.Helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/*
Save data to your drive
 */
public class ThreadSyncDatas extends Thread {
    GoogleSignInAccount mGoogleSignInAccount;
    DriveHelper mDriveHelper;
    OnFailureListener failureListener;

    public static void reSchedule(int amountOfMinutes) {
        Resources res = App.getApplication().getResources();
        String syncIntentAction = res.getString(R.string.syncIntentAction);
        Intent intent = new Intent(App.getApplication().getApplicationContext(), TimeAlarm.class);
        intent.setAction(syncIntentAction);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.MINUTE, amountOfMinutes);
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) App.getApplication().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(App.getApplication().getApplicationContext(), 0, intent, 0);
        alarmMgr.cancel(alarmIntent);
        if (amountOfMinutes != 0) {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
        }

    }

    public void OnFinalSuccess() {
        reSchedule(PreferenceHelper.getSyncTime());
    }

    public ThreadSyncDatas(GoogleSignInAccount googleSignInAccount) {
        mGoogleSignInAccount = googleSignInAccount;
        failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : onFailure : <" + e.getMessage() + ">"));
                reSchedule(5);
            }
        };
    }

    @Override
    public void run() {
        //Init Drive API
        if (mGoogleSignInAccount != null) {
            mDriveHelper = new DriveHelper(mGoogleSignInAccount);
        } else {
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
                        getImageFolder(driveFolder);
                        return driveFolderTask;
                    }
                }).addOnFailureListener(failureListener);
            }
        }).addOnFailureListener(failureListener);
    }

    private void syncImagesFolders(final DriveFolder driveFolder) {
        Query q = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE))
                .build();
        Task<MetadataBuffer> taskMetaDatas = mDriveHelper.getDriveResourceClient().queryChildren(driveFolder, q);
        taskMetaDatas
                .addOnSuccessListener(
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : syncImagesFolders queryChildren Success"));
                                if (metadataBuffer.getCount() != 0) {
                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : syncImagesFolders queryChildren Success : " + metadataBuffer.getCount() + " child"));
                                    //Retreive file
                                    for (Metadata metadata : metadataBuffer) {
                                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : syncImagesFolders queryChildren : " + metadata.getTitle()));
                                        if (metadata.isFolder())
                                            retrieveOrReplaceImages(metadata.getDriveId().asDriveFolder(), metadata.getTitle());
                                    }
                                    sendImages(driveFolder);
                                } else {
                                    sendImages(driveFolder);
                                }
                                metadataBuffer.release();
                            }
                        })
                .addOnFailureListener(failureListener);
    }

    private void sendImages(final DriveFolder driveFolder) {
        for (final File imageDir : BitmapHelper.listImageDir()) {
            Query q = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, imageDir.getName()))
                    .build();
            Task<MetadataBuffer> taskMetaDatas = mDriveHelper.getDriveResourceClient().queryChildren(driveFolder, q);
            taskMetaDatas
                    .addOnSuccessListener(
                            new OnSuccessListener<MetadataBuffer>() {
                                @Override
                                public void onSuccess(MetadataBuffer metadataBuffer) {
                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : sendImages queryChildren Success : " + imageDir.getName()));
                                    if (metadataBuffer.getCount() != 0) {
                                        if (metadataBuffer.get(0).isFolder())
                                            openDirAndSendImages(metadataBuffer.get(0).getDriveId().asDriveFolder(), imageDir);

                                    } else {
                                        createDirAndSendImages(driveFolder, imageDir);
                                    }
                                    metadataBuffer.release();
                                }
                            })
                    .addOnFailureListener(failureListener);

        }
    }

    private void createDirAndSendImages(final DriveFolder driveFolder, final File imageDir) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(imageDir.getName())
                .setMimeType(DriveFolder.MIME_TYPE)
                .build();
        Task<DriveFolder> createImagesDays = mDriveHelper.getDriveResourceClient().createFolder(driveFolder, changeSet);
        createImagesDays
                .addOnSuccessListener(
                        new OnSuccessListener<DriveFolder>() {
                            @Override
                            public void onSuccess(DriveFolder imagesDaysFolder) {
                                Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : createDirAndSendImages ok : " + imageDir.getName()));
                                openDirAndSendImages(imagesDaysFolder, imageDir);
                            }
                        })
                .addOnFailureListener(failureListener);
    }

    private void openDirAndSendImages(final DriveFolder driveFolder, File imageDir) {
        for (File imageB : BitmapHelper.listFileInImageDirectory(imageDir)) {
            final File image = imageB;
            Query q = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, image.getName()))
                    .build();
            Task<MetadataBuffer> taskMetaDatas = mDriveHelper.getDriveResourceClient().queryChildren(driveFolder, q);
            taskMetaDatas
                    .addOnSuccessListener(
                            new OnSuccessListener<MetadataBuffer>() {
                                @Override
                                public void onSuccess(MetadataBuffer metadataBuffer) {
                                    if (metadataBuffer.getCount() == 0) {
                                        //START SENDING NEW FILE
                                        final Task<DriveContents> createContentsTask = mDriveHelper.getDriveResourceClient().createContents();
                                        createContentsTask.addOnSuccessListener(new OnSuccessListener<DriveContents>() {
                                            @Override
                                            public void onSuccess(DriveContents contents) {
                                                OutputStream outputStream = contents.getOutputStream();
                                                try {
                                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO :openDirAndSendImages  createContents : " + image.getName()));
                                                    FileInputStream fileInput = new FileInputStream(image);
                                                    InputStream cis = CryptHelper.getCipherInputStream(fileInput);
                                                    long totalSizeWriten = 0;
                                                    if (cis != null) {
                                                        byte[] buffer = new byte[1024];
                                                        int bufferLength;
                                                        while ((bufferLength = cis.read(buffer)) > 0) {
                                                            totalSizeWriten += bufferLength;
                                                            outputStream.write(buffer, 0, bufferLength);
                                                        }
                                                        cis.close();
                                                    }
                                                    fileInput.close();
                                                    outputStream.close();
                                                } catch (Exception e) {
                                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : openDirAndSendImages Exception :" + e.getMessage()));
                                                }

                                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                        .setTitle(image.getName())
                                                        .setLastViewedByMeDate(new Date(image.lastModified()))
                                                        .setMimeType("image/png")
                                                        .build();

                                                Task<DriveFile> driveFile = mDriveHelper.getDriveResourceClient().createFile(driveFolder, changeSet, contents);
                                                driveFile.addOnSuccessListener(new OnSuccessListener<DriveFile>() {
                                                    @Override
                                                    public void onSuccess(DriveFile driveFile) {
                                                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : openDirAndSendImages OK "));
                                                        OnFinalSuccess();
                                                    }
                                                }).addOnFailureListener(failureListener);
                                            }
                                        }).addOnFailureListener(failureListener);
                                        //END SENDING NEW FILE

                                    } else {
                                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : openDirAndSendImages already sync by retrieveOrReplaceImages"));
                                    }
                                    metadataBuffer.release();
                                }
                            })
                    .addOnFailureListener(failureListener);

        }
    }

    //Retrieve images from a folder, if the file already exist on the device, we send it if the local file is more recent
    private void retrieveOrReplaceImages(final DriveFolder driveFolder, final String folderName) {
        Query q = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "image/png"))
                .build();
        Task<MetadataBuffer> taskMetaDatas = mDriveHelper.getDriveResourceClient().queryChildren(driveFolder, q);
        taskMetaDatas
                .addOnSuccessListener(
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                for (Metadata metadata : metadataBuffer) {
                                    String imagePath = folderName;
                                    File imageDir = new File(BitmapHelper.getImagesDir() + imagePath);
                                    imageDir.mkdirs();
                                    //if image already exists, we don't pick it
                                    File image = new File(imageDir, metadata.getTitle());
                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveOrReplaceImages : " + metadata.getTitle()));
                                    if (!image.exists()) {
                                        //save that the file just has been retrieved
                                        retrieveImage(metadata.getDriveId().asDriveFile(), image);
                                    } else {
                                        Date dateDistant = metadata.getLastViewedByMeDate();
                                        Date dateLocal = new Date(image.lastModified());
                                        if (dateDistant.before(dateLocal))
                                            sendImage(metadata.getDriveId().asDriveFile(), image);
                                        else
                                            Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveOrReplaceImages image not newer "));
                                    }
                                }
                                metadataBuffer.release();
                            }
                        })
                .addOnFailureListener(failureListener);
    }

    private void sendImage(DriveFile driveFile, final File image) {
        Task<DriveContents> openTask = mDriveHelper.getDriveResourceClient().openFile(driveFile, DriveFile.MODE_WRITE_ONLY);
        openTask.continueWithTask(
                new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) {
                        final DriveContents driveContents = task.getResult();
                        try {
                            Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : sendImage : " + image));
                            OutputStream outputStream = driveContents.getOutputStream();
                            long sizeOfTheFile = image.length();
                            FileInputStream fileInput = new FileInputStream(image);
                            InputStream cis = CryptHelper.getCipherInputStream(fileInput);
                            if (cis != null) {
                                byte[] buffer = new byte[1024];
                                int bufferLength;
                                if (sizeOfTheFile != 0) {
                                    while ((bufferLength = cis.read(buffer)) > 0) {
                                        outputStream.write(buffer, 0, bufferLength);
                                    }
                                    cis.close();
                                }
                                fileInput.close();
                                outputStream.close();
                            }
                        } catch (Exception e) {
                            Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : sendImage Exception :" + e.getMessage()));
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setLastViewedByMeDate(new Date(image.lastModified()))
                                .build();
                        Task<Void> commitTask = mDriveHelper.getDriveResourceClient().commitContents(driveContents, changeSet).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : sendImage commited"));
                                OnFinalSuccess();
                            }
                        }).addOnFailureListener(failureListener);
                        return commitTask;
                    }
                });
    }

    private void retrieveImage(DriveFile driveFile, final File image) {
        Task<DriveContents> openTask = mDriveHelper.getDriveResourceClient().openFile(driveFile, DriveFile.MODE_READ_ONLY);
        openTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<DriveContents> task) {
                try {
                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveImage " + image));
                    final DriveContents driveContents = task.getResult();
                    InputStream inputStream = driveContents.getInputStream();
                    long totalSize = driveContents.getParcelFileDescriptor().getStatSize();
                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveImage size : " + totalSize));
                    File file = App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile();
                    file.delete();
                    if (totalSize != 0) {
                        FileOutputStream fileOutput = new FileOutputStream(image);
                        OutputStream cos = CryptHelper.getCipherOutputStream(fileOutput);
                        if (cos != null) {
                            byte[] buffer = new byte[1024];
                            int bufferLength;

                            while ((bufferLength = inputStream.read(buffer)) > 0) {
                                cos.write(buffer, 0, bufferLength);
                            }
                            cos.flush();
                            cos.close();
                            fileOutput.close();
                            inputStream.close();
                        }
                    }
                } catch (Exception e) {
                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveImage Exception: " + e.getMessage()));
                }
                return null;
            }
        });
    }

    // Load images-days or create it
    private void getImageFolder(final DriveFolder driveFolder) {
        Query q = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "images-days"))
                .build();
        Task<MetadataBuffer> taskMetaDatas = mDriveHelper.getDriveResourceClient().queryChildren(driveFolder, q);
        taskMetaDatas
                .addOnSuccessListener(
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                if (metadataBuffer.getCount() != 0) {
                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : getImageFolder queryChildren Success : " + metadataBuffer.getCount() + " child"));
                                    //Retreive file
                                    for (Metadata metadata : metadataBuffer) {
                                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : getImageFolder queryChildren  : " + metadata.getTitle()));
                                        syncImagesFolders(metadata.getDriveId().asDriveFolder());
                                    }
                                } else {
                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle("images-days")
                                            .setMimeType(DriveFolder.MIME_TYPE)
                                            .build();
                                    Task<DriveFolder> createImagesDays = mDriveHelper.getDriveResourceClient().createFolder(driveFolder, changeSet);
                                    createImagesDays
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<DriveFolder>() {
                                                        @Override
                                                        public void onSuccess(DriveFolder imagesDaysFolder) {
                                                            Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : getImageFolder ok"));
                                                            syncImagesFolders(imagesDaysFolder);
                                                        }
                                                    })
                                            .addOnFailureListener(failureListener);

                                }
                                metadataBuffer.release();
                            }
                        })
                .addOnFailureListener(failureListener);
    }

    private void searchForDatabase(final DriveFolder driveFolder) {
        final Task<DriveContents> createContentsTask = mDriveHelper.getDriveResourceClient().createContents();
        createContentsTask.addOnSuccessListener(new OnSuccessListener<DriveContents>() {
            @Override
            public void onSuccess(DriveContents contents) {
                Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO :searchForDatabase  createContents Success"));
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle(cal.getTimeInMillis() + ".connectiontag")
                        .setMimeType("text/plain")
                        .build();

                Task<DriveFile> driveFile = mDriveHelper.getDriveResourceClient().createFile(driveFolder, changeSet, contents);
                driveFile.addOnSuccessListener(new OnSuccessListener<DriveFile>() {
                    @Override
                    public void onSuccess(DriveFile driveFile) {
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
                                                metadataBuffer.release();
                                            }
                                        })
                                .addOnFailureListener(failureListener);
                    }
                }).addOnFailureListener(failureListener);
            }
        }).addOnFailureListener(failureListener);

    }

    private void retrieveDb(final DriveFile driveFile) {
        Task<DriveContents> openTask = mDriveHelper.getDriveResourceClient().openFile(driveFile, DriveFile.MODE_READ_ONLY);
        openTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<DriveContents> task) {
                try {
                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDb continueWithTask"));
                    final DriveContents driveContents = task.getResult();
                    InputStream inputStream = driveContents.getInputStream();
                    long totalSize = driveContents.getParcelFileDescriptor().getStatSize();
                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDb size: " + totalSize));
                    final File file = App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile();
                    try {
                        file.delete();
                        file.createNewFile();
                    } catch (Exception e) {
                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDb After Delete Exception: " + e.getMessage()));
                    }
                    if (totalSize != 0) {
                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDb backup size before write: " + file.length()));
                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDb backup path: " + file));
                        final FileObserver fo = new FileObserver(file.getAbsolutePath()) {
                            @Override
                            public void onEvent(int event, @Nullable String path) {
                                if ((event == CLOSE_WRITE)||(event == CLOSE_NOWRITE)) {
                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDb FileObserver: CLOSE_WRITE"));
                                    this.stopWatching();
                                    updateDb(driveFile, file);
                                }
                            }
                        };
                        fo.startWatching();
                        FileOutputStream fileOutput = new FileOutputStream(file);
                        OutputStream cos = CryptHelper.getCipherOutputStream(fileOutput);
                        int totalLengthWritten = 0;
                        if (cos != null) {
                            byte[] buffer = new byte[1024];
                            int bufferLength;
                            while ((bufferLength = inputStream.read(buffer)) > 0) {
                                totalLengthWritten += bufferLength;
                                cos.write(buffer, 0, bufferLength);
                            }
                            cos.flush();
                            cos.close();
                            fileOutput.flush();
                            fileOutput.close();
                            inputStream.close();
                        }else
                            fileOutput.close();

                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDb size written: " + totalLengthWritten));
                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDb backup size after write: " + App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile().length()));
                    }

                } catch (Exception e) {
                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : retrieveDb Exception: " + e.getMessage()));
                }
                return null;
            }
        });
    }

    private void updateDb(DriveFile driveFile, final File file) {
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
                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : updateDb size of the Database Backup Before:" + file.length()));
                                } catch (Exception e) {
                                }
                                final FileObserver fo = new FileObserver(file.getAbsolutePath()) {
                                    int numberOfClose = 0;
                                    @Override
                                    public void onEvent(int event, @Nullable String path) {
                                        if ((event == CLOSE_WRITE)||(event == CLOSE_NOWRITE)) {
                                            Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : updateDb FileObserver: CLOSE_WRITE"));
                                            numberOfClose++;
                                            //this.stopWatching();
                                        }
                                    }
                                };
                                fo.startWatching();
                                DaysDatabase.copyBackupToDatabase(App.getContext());
                                DaysDatabase.copyDatabaseToBackup(App.getContext());
                                DaysDatabase.cleanDatabase(DaysDatabase.getInstance(App.getContext()));
                                DaysDatabase.cleanDatabase(DaysDatabase.getBackupInstance(App.getContext()));
                                DaysDatabase.getBackupInstance(App.getContext()).close();

                                try {
                                    OutputStream outputStream = driveContents.getOutputStream();
                                    long totalSize = driveContents.getParcelFileDescriptor().getStatSize();
                                    File file = App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile();
                                    long sizeOfTheFile = file.length();
                                    FileInputStream fileInput = new FileInputStream(App.getContext().getDatabasePath(DaysDatabase.getDatabaseBackupName()).getAbsoluteFile());
                                    InputStream cis = CryptHelper.getCipherInputStream(fileInput);
                                    if (cis != null) {
                                        byte[] buffer = new byte[1024];
                                        int bufferLength;
                                        try {
                                            Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : updateDb before write:" ));
                                        } catch (Exception e) {
                                        }
                                        long totalSizeWriten = 0;
                                        if (sizeOfTheFile != 0) {
                                            while ((bufferLength = cis.read(buffer)) > 0) {
                                                totalSizeWriten += bufferLength;
                                                outputStream.write(buffer, 0, bufferLength);
                                            }
                                            cis.close();
                                        }
                                        fileInput.close();
                                        outputStream.flush();
                                        outputStream.close();
                                    }
                                } catch (Exception e) {
                                    Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : updateDb Exception :" + e.getMessage()));
                                }

                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .build();
                                Task<Void> commitTask = mDriveHelper.getDriveResourceClient().commitContents(driveContents, changeSet).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : updateDb commited"));
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
                        Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO :copyAndUploadDb  createContents Success"));
                        OutputStream outputStream = contents.getOutputStream();
                        try {
                            File dbFile = App.getContext().getDatabasePath(databaseName).getAbsoluteFile();
                            Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO :copyAndUploadDb  origin file size : " + dbFile.length()));
                            FileInputStream fileInput = new FileInputStream(dbFile);
                            InputStream cis = CryptHelper.getCipherInputStream(fileInput);
                            long totalSizeWriten = 0;
                            if (cis != null) {
                                byte[] buffer = new byte[1024];
                                int bufferLength;
                                while ((bufferLength = cis.read(buffer)) > 0) {
                                    totalSizeWriten += bufferLength;
                                    outputStream.write(buffer, 0, bufferLength);
                                }
                                Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO :copyAndUploadDb  size written : " + totalSizeWriten));
                                cis.close();
                            }
                            fileInput.close();
                            outputStream.close();
                        } catch (Exception e) {
                            Logger.getLogger("ThreadSyncDatas").log(new LogRecord(Level.INFO, "FMORALDO : copyAndUploadDb Exception :" + e.getMessage()));
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
                    }
                }).addOnFailureListener(failureListener);
            }
        }.start();
    }
}

