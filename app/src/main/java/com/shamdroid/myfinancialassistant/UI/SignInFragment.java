package com.shamdroid.myfinancialassistant.UI;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shamdroid.myfinancialassistant.Models.CategorySource;
import com.shamdroid.myfinancialassistant.Models.Transaction;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.FinancialSQLiteHelper;
import com.shamdroid.myfinancialassistant.data.FirebaseUtils;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SignInFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    @BindView(R.id.btnSignIn)
    SignInButton btnSignIn;


    public final int REQUEST_CODE = 0;
    GoogleApiClient googleApiClient;

    ProgressDialog progressLoadingData;

    boolean transLoaded, catLoaded, srcLoaded, balanceLoaded;
    private boolean hasAccount;


    Context context ;
    
    public SignInFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);


        ButterKnife.bind(this, view);

        context = getActivity();
        
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnSignIn.setOnClickListener(this);


        btnSignIn.setSize(SignInButton.SIZE_WIDE);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            progressLoadingData = new ProgressDialog(context);
            progressLoadingData.setMessage(getString(R.string.loading_data));
            progressLoadingData.show();
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(googleSignInResult);
        }


    }

    private void handleGoogleSignInResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    String userId = firebaseAuth.getCurrentUser().getUid();

                    SharedPreferencesManager.setFirebaseUserId(context, userId);

                    retrieveFromFirebase();


                }
            });


            SharedPreferencesManager.setIsLoggedIn(context, true);
            SharedPreferencesManager.setEmail(context, googleSignInAccount.getEmail());
            SharedPreferencesManager.setName(context, googleSignInAccount.getDisplayName());
            SharedPreferencesManager.setProfileImage(context, googleSignInAccount.getPhotoUrl().toString());

            FirebaseDatabase.getInstance().setPersistenceEnabled(false);


        }
    }

    private void retrieveFromFirebase() {


        final Query alreadyLoggedIn = FirebaseUtils.getAlreadyLoggedInRef(context);

        alreadyLoggedIn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean alreadyLoggedInValue = dataSnapshot.getValue(Boolean.class);

                alreadyLoggedIn.removeEventListener(this);


                if (alreadyLoggedInValue == null) {
                    FinancialSQLiteHelper financialSQLiteHelper = new FinancialSQLiteHelper(context);

                    financialSQLiteHelper.initTables();

                    hasAccount = false;

                    alreadyLoggedIn.getRef().setValue(true);


                } else {

                    hasAccount = true;
                    loadUserData();

                }


                checkAllLoaded();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    void loadUserData() {

        DatabaseReference userReference = FirebaseUtils.getUserReference(context);

        final Query transactions = userReference.child(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE).orderByChild(FinancialContract.TransactionEntry.ID);

        transactions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot d :
                        dataSnapshot.getChildren()) {
                    Transaction transaction = d.getValue(Transaction.class);


                    transaction.setSavedToFirebase(true);
                    transaction.setUpdatedInFirebase(true);
                    transaction.setFirebaseReference(d.getKey());

                    ContentValues values = transaction.toContentValues();
                    values.put(FinancialContract.TransactionEntry.ID, transaction.getId());

                    context.getContentResolver().insert(FinancialContract.TransactionEntry.CONTENT_URI, values);

                }


                transactions.removeEventListener(this);

                transLoaded = true;

                checkAllLoaded();


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Query categories = userReference.child(FinancialContract.CategoryEntry.CATEGORIES_TABLE).orderByChild(FinancialContract.CategoryEntry.ID);

        categories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d :
                        dataSnapshot.getChildren()) {
                    CategorySource category = d.getValue(CategorySource.class);
                    category.setFirebaseReference(d.getKey());
                    category.setSavedToFirebase(true);
                    category.setUpdatedInFirebase(true);
                    category.setType(CategorySource.TYPE_CAT);

                    ContentValues contentValues = category.toContentValues();
                    contentValues.put(FinancialContract.CategoryEntry.ID, category.getId());

                    context.getContentResolver().insert(FinancialContract.CategoryEntry.CONTENT_URI, contentValues);

                }

                categories.removeEventListener(this);

                catLoaded = true;

                checkAllLoaded();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Query sources = userReference.child(FinancialContract.SourceEntry.SOURCES_TABLE).orderByChild(FinancialContract.CategoryEntry.ID);

        sources.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d :
                        dataSnapshot.getChildren()) {
                    CategorySource category = d.getValue(CategorySource.class);
                    category.setFirebaseReference(d.getKey());
                    category.setSavedToFirebase(true);
                    category.setUpdatedInFirebase(true);
                    category.setType(CategorySource.TYPE_SRC);

                    ContentValues contentValues = category.toContentValues();
                    contentValues.put(FinancialContract.SourceEntry.ID, category.getId());

                    context.getContentResolver().insert(FinancialContract.SourceEntry.CONTENT_URI, contentValues);

                }

                sources.removeEventListener(this);

                srcLoaded = true;

                checkAllLoaded();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Query balance = FirebaseUtils.getBalanceRef(context);


        balance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float b = dataSnapshot.getValue(Float.class);

                SharedPreferencesManager.setBalance(context, b);

                balance.removeEventListener(this);

                balanceLoaded = true;
                checkAllLoaded();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    void checkAllLoaded() {
        if (!hasAccount || transLoaded && catLoaded && srcLoaded && balanceLoaded) {

            progressLoadingData.dismiss();

            getActivity().finish();
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        
    }
}
