package com.mithraw.howwasyourday.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mithraw.howwasyourday.Activities.MainActivity;
import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;
import com.mithraw.howwasyourday.Tools.OnGoogleSignInSuccessListener;

/*
Manage Google Sign in main handlers
Send the Sign in intent
Used that way :
    GoogleSignInHelper.getInstance(mActivity).doSignIn(new SyncLauncher());

Retrieve the intent Activity by doing :
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GoogleSignInHelper.GOOGLE_SIGNIN_ACTIVITY_ID){
            Resources res = App.getApplication().getResources();
            if (resultCode == Activity.RESULT_OK) {
                // App is authorized, you can go back to sending the API request
                GoogleSignInHelper.getInstance(this).doSignIn(new SyncLauncher());
            } else {
                Snackbar.make(getCurrentFocus(), res.getString(R.string.issue_google_sign_in), 2000).setAction(R.string.reconnect_google_sign_in, new ReconnectListener()).show();
            }
        }
    }

 */
public class GoogleSignInHelper {

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount mGoogleSignInAccount = null;
    static Activity mActivity;
    static GoogleSignInHelper mInstance = null;
    public static int GOOGLE_SIGNIN_ACTIVITY_ID = 666;

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    private Activity getActivity() {
        return mActivity;
    }

    public static GoogleSignInHelper getInstance(Activity act) {
        mActivity = act;
        if (mInstance == null) {
            mInstance = new GoogleSignInHelper(act);
            return mInstance;
        }
        if (mInstance.getActivity() != act)
            mInstance.setActivity(act);
        return mInstance;
    }
    public static GoogleSignInHelper getInstance() {
        if (mInstance == null) {
            mInstance = new GoogleSignInHelper(mActivity);
            return mInstance;
        }
        return mInstance;
    }

    public GoogleSignInAccount getGoogleSignInAccount() {
        if(mGoogleSignInAccount == null) {
            if (mGoogleSignInClient == null) {
                mGoogleSignInClient = buildGoogleSignInClient();
            }
            Task<GoogleSignInAccount> task = mGoogleSignInClient.silentSignIn();
            if (task.isSuccessful()) {
                mGoogleSignInAccount = task.getResult();
            }

        }
        return mGoogleSignInAccount;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    private GoogleSignInHelper(Activity act) {
        mActivity = act;
    }

    public void doSignIn(OnGoogleSignInSuccessListener onGoogleSignInSuccessListener) {
        buildGoogleSignInClient();
        connectToSignInAccount(onGoogleSignInSuccessListener);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .build();
        if(mActivity == null)
            mActivity = MainActivity.getmActivity();
        mGoogleSignInClient = GoogleSignIn.getClient(App.getContext(), signInOptions);
        return mGoogleSignInClient;
    }

    public void connectToSignInAccount(final OnGoogleSignInSuccessListener onGoogleSignInSuccessListener) {
        Task<GoogleSignInAccount> task = mGoogleSignInClient.silentSignIn();
        task.addOnSuccessListener(
                new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        // Build a drive client.
                        mGoogleSignInAccount = googleSignInAccount;
                        Toast.makeText(mActivity.getBaseContext(), R.string.ok_google_sign_in, Toast.LENGTH_SHORT).show();
                        // Launch the Drive things
                        onGoogleSignInSuccessListener.SignInSuccess(googleSignInAccount);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Intent in = getGoogleSignInClient().getSignInIntent();
                                if (mActivity != null)
                                    mActivity.startActivityForResult(in, GOOGLE_SIGNIN_ACTIVITY_ID);
                            }
                        });
    }
}
