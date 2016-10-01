package com.shamdroid.myfinancialassistant.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    @BindView(R.id.btnSignIn)
    SignInButton btnSignIn;


    public final int REQUEST_CODE = 0;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        btnSignIn.setOnClickListener(this);


        btnSignIn.setSize(SignInButton.SIZE_WIDE);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSignIn:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(googleSignInResult);
        }


    }

    private void handleGoogleSignInResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()){
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    String userId = firebaseAuth.getCurrentUser().getUid();

                    SharedPreferencesManager.setFirebaseUserId(SignInActivity.this,userId);


                }
            });


            SharedPreferencesManager.setIsLoggedIn(this,true);
            SharedPreferencesManager.setEmail(this,googleSignInAccount.getEmail());
            SharedPreferencesManager.setName(this,googleSignInAccount.getDisplayName());
            SharedPreferencesManager.setProfileImage(this,googleSignInAccount.getPhotoUrl().toString());



            FirebaseDatabase.getInstance().setPersistenceEnabled(true);




            // Sync with Firebase


            finish();
            startActivity(new Intent(SignInActivity.this,MainActivity.class));

        }
    }
}
