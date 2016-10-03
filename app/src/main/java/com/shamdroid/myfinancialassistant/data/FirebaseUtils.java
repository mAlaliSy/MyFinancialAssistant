package com.shamdroid.myfinancialassistant.data;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mohammad on 24/09/16.
 */

public class FirebaseUtils {

    public static final String USERS_CHILD = "users";
    public static final String ALREADY_LOGGED_IN = "already_logged_in";

    public static DatabaseReference getUserReference (Context context){

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference usersRef = database.getReference().child(USERS_CHILD);

        String userId= SharedPreferencesManager.getFirebaseUserId(context);



        return usersRef.child(userId);

    }


    public static DatabaseReference getTransitionsRef (Context context){

        DatabaseReference userRef = getUserReference(context);

        return userRef.child(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE);

    }


    public static DatabaseReference getTransitionRef(Context context, String key){

        DatabaseReference transitionsRef = getTransitionsRef(context);

        return transitionsRef.child(key);

    }


    public static DatabaseReference getCategoriesRef (Context context){

        DatabaseReference userRef = getUserReference(context);

        return userRef.child(FinancialContract.CategoryEntry.CATEGORIES_TABLE);

    }


    public static DatabaseReference getCategoryRef(Context context, String key){

        DatabaseReference catsRef = getCategoriesRef(context);

        return catsRef.child(key);

    }


    public static DatabaseReference getSourcesRef (Context context){

        DatabaseReference userRef = getUserReference(context);

        return userRef.child(FinancialContract.SourceEntry.SOURCES_TABLE);

    }


    public static DatabaseReference getSourceRef(Context context, String key){

        DatabaseReference srcsTable = getSourcesRef(context);

        return srcsTable.child(key);

    }




    public static DatabaseReference getBalanceRef (Context context){
        return getUserReference(context).child(SharedPreferencesManager.BALANCE_KEY);
    }

    public static void setBalanceValue(Context context,float balance){
        DatabaseReference databaseReference = getBalanceRef(context);
        databaseReference.setValue(balance);
    }


    public static DatabaseReference getAlreadyLoggedInRef (Context context){

        DatabaseReference databaseReference = getUserReference(context);

        return databaseReference.child(ALREADY_LOGGED_IN);
    }

}
