package com.mithraw.howwasyourday.Tools;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/*
Interface for when google sign in is ok
 */
public interface OnGoogleSignInSuccessListener {
    public void SignInSuccess(GoogleSignInAccount googleSignInAccount);
}
