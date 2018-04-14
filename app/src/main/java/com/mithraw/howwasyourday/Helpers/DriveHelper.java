package com.mithraw.howwasyourday.Helpers;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;
import com.mithraw.howwasyourday.App;


public class DriveHelper {
    DriveResourceClient mDriveResourceClient;
    DriveClient mDriveClient;
    GoogleSignInAccount mGoogleSignInAccount;

    public DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }

    public DriveClient getDriveClient() {
        return mDriveClient;
    }

    public DriveHelper(GoogleSignInAccount googleSignInAccount) {
        mGoogleSignInAccount = googleSignInAccount;
        mDriveClient = Drive.getDriveClient(App.getContext(), googleSignInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(App.getContext(), googleSignInAccount);
    }
}
