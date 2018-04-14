package com.mithraw.howwasyourday.Tools;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface OnGoogleSignInSuccessListener {
    public void SignInSuccess(GoogleSignInAccount googleSignInAccount);
}
