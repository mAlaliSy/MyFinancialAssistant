package com.shamdroid.myfinancialassistant.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.FinancialSQLiteHelper;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;

/**
 * Created by mohammad on 26/09/16.
 */

public class Utils {

    static GoogleApiClient googleApiClient;

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();

    }

    public static void truncateAllTables(Context context) {

        FinancialSQLiteHelper sqLiteHelper = new FinancialSQLiteHelper(context);
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();


        String tablesName[] = {FinancialContract.CategoryEntry.CATEGORIES_TABLE, FinancialContract.SourceEntry.SOURCES_TABLE, FinancialContract.TransactionEntry.TRANSACTIONS_TABLE};


        String query = "DROP TABLE ";

        for (int i = 0; i < tablesName.length; i++) {
            sqLiteDatabase.execSQL(query + tablesName[i]);
        }

        sqLiteHelper.onCreate(sqLiteDatabase);


    }

    public static void logOut(Context context) {

        truncateAllTables(context);

        SharedPreferencesManager.setIsLoggedIn(context, false);

        SharedPreferencesManager.setName(context, null);
        SharedPreferencesManager.setEmail(context, null);
        SharedPreferencesManager.setProfileImage(context, null);
        SharedPreferencesManager.setFirebaseUserId(context, null);

        FirebaseAuth.getInstance().signOut();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Auth.GoogleSignInApi.signOut(googleApiClient);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
    }

}
