package com.mithraw.howwasyourday.Helpers;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mithraw.howwasyourday.Tools.OnGoogleSignInSuccessListener;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/*
Implementation of the GoogleSignInListener that launch a function when the sign in is ok
Here we launch the threadSync
 */
public class SyncLauncher implements OnGoogleSignInSuccessListener {
    @Override
    public void SignInSuccess(GoogleSignInAccount googleSignInAccount) {
        Logger.getLogger("SyncLauncher").log(new LogRecord(Level.INFO, "FMORALDO : SyncLauncher : Launch sync"));
        //Launch Thread Sync
        new ThreadSyncDatas(googleSignInAccount).start();
    }
}
