package com.shamdroid.myfinancialassistant.Utils;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.shamdroid.myfinancialassistant.Models.CategorySource;
import com.shamdroid.myfinancialassistant.Models.Transaction;
import com.shamdroid.myfinancialassistant.UI.MainActivity;
import com.shamdroid.myfinancialassistant.UI.Util;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.FinancialSQLiteHelper;
import com.shamdroid.myfinancialassistant.data.FirebaseUtils;

/**
 * Created by mohammad on 27/09/16.
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    Context context;

    ContentResolver contentResolver;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Utils.isConnected(context)) {
            this.context = context.getApplicationContext();


            contentResolver = context.getContentResolver();

            pushNewTransitions();

            pushNewCategories();

            pushNewSources();


            updateTransactions();

            updateCategories();

            updateSources();


            delete();
        }

    }

    private void delete() {

        Cursor cursor = contentResolver.query(FinancialContract.DeleteFromFirebaseEntry.CONTENT_URI, null, null, null, null);

        DatabaseReference userRef = FirebaseUtils.getUserReference(context);
        if (cursor.moveToFirst()) {

            do {
                String type = cursor.getString(FinancialContract.DeleteFromFirebaseEntry.TYPE_INDEX);
                String ref = cursor.getString(FinancialContract.DeleteFromFirebaseEntry.REF_INDEX);

                DatabaseReference reference = userRef.child(type).child(ref);
                reference.removeValue();


            } while (cursor.moveToNext());


            contentResolver.delete(FinancialContract.DeleteFromFirebaseEntry.CONTENT_URI, null, null);

            SQLiteDatabase sqLiteDatabase= new FinancialSQLiteHelper(context).getWritableDatabase();
            sqLiteDatabase.execSQL("TRUNCATE TABLE " + FinancialContract.DeleteFromFirebaseEntry.DELETE_FROM_FIREBASE_TABLE);

        }


    }

    private void updateSources() {

        Cursor toUpdate = contentResolver.query(FinancialContract.SourceEntry.CONTENT_URI
                , null, FinancialContract.SourceEntry.UPDATE_IN_FIREBASE + " = 0 ",null, null);


        if (toUpdate.moveToFirst()) {

            do {

                CategorySource source = CategorySource.sourceFromCursor(toUpdate);
                source.updateFirebase(context);


            } while (toUpdate.moveToNext());

            ContentValues contentValues = new ContentValues();
            contentValues.put(FinancialContract.SourceEntry.UPDATE_IN_FIREBASE, 1);

            contentResolver.update(FinancialContract.SourceEntry.CONTENT_URI, contentValues, null, null);

        }


    }

    private void updateCategories() {


        Cursor toUpdate = contentResolver.query(FinancialContract.CategoryEntry.CONTENT_URI
                , null, FinancialContract.SourceEntry.UPDATE_IN_FIREBASE + " = 0",
                null, null);


        if (toUpdate.moveToFirst()) {

            do {

                CategorySource source = CategorySource.categoryFromCursor(toUpdate);
                source.updateFirebase(context);


            } while (toUpdate.moveToNext());

            ContentValues contentValues = new ContentValues();
            contentValues.put(FinancialContract.SourceEntry.UPDATE_IN_FIREBASE, 1);

            contentResolver.update(FinancialContract.CategoryEntry.CONTENT_URI, contentValues, null, null);
            context.startActivity(new Intent(context, MainActivity.class));

        }

    }

    private void updateTransactions() {


        Cursor toUpdate = contentResolver.query(FinancialContract.TransactionEntry.CONTENT_URI
                , null, FinancialContract.SourceEntry.UPDATE_IN_FIREBASE + " = 0 ",
                null, null);


        if (toUpdate.moveToFirst()) {

            do {

                Transaction transaction = Transaction.fromCursor(toUpdate);

                transaction.updateToFirebase(context);

            } while (toUpdate.moveToNext());

            ContentValues contentValues = new ContentValues();
            contentValues.put(FinancialContract.SourceEntry.UPDATE_IN_FIREBASE, 1);

            contentResolver.update(FinancialContract.TransactionEntry.CONTENT_URI, contentValues, null, null);


        }

    }

    private void pushNewSources() {


        Cursor newEntries = contentResolver.query(FinancialContract.SourceEntry.CONTENT_URI
                , null, FinancialContract.SourceEntry.SAVED_IN_FIREBASE + " = 0",null, null);


        if (newEntries.moveToFirst()) {

            do {

                CategorySource source = CategorySource.sourceFromCursor(newEntries);
                String ref = source.saveNewToFirebase(context);

                ContentValues contentValues = new ContentValues();

                contentValues.put(FinancialContract.SourceEntry.FIREBASE_REFERENCE, ref);

                contentResolver.update(FinancialContract.SourceEntry.buildSourceIdUri(source.getId()), contentValues, null, null);


            } while (newEntries.moveToNext());

            ContentValues contentValues = new ContentValues();
            contentValues.put(FinancialContract.SourceEntry.SAVED_IN_FIREBASE, 1);

            contentResolver.update(FinancialContract.SourceEntry.CONTENT_URI, contentValues, null, null);

        }


    }

    private void pushNewCategories() {


        Cursor newEntries = contentResolver.query(FinancialContract.CategoryEntry.CONTENT_URI
                , null, FinancialContract.CategoryEntry.SAVED_IN_FIREBASE + " = 0 ",null, null);


        if (newEntries.moveToFirst()) {

            do {

                CategorySource category = CategorySource.categoryFromCursor(newEntries);

                String ref = category.saveNewToFirebase(context);

                ContentValues contentValues = new ContentValues();

                contentValues.put(FinancialContract.CategoryEntry.FIREBASE_REFERENCE, ref);

                contentResolver.update(FinancialContract.CategoryEntry.buildCategoryIdUri(category.getId()), contentValues, null, null);

            } while (newEntries.moveToNext());

            ContentValues contentValues = new ContentValues();
            contentValues.put(FinancialContract.CategoryEntry.SAVED_IN_FIREBASE, 1);

            contentResolver.update(FinancialContract.CategoryEntry.CONTENT_URI, contentValues, null, null);

        }


    }

    private void pushNewTransitions() {
        Cursor newEntries = contentResolver.query(FinancialContract.TransactionEntry.CONTENT_URI
                , null, FinancialContract.TransactionEntry.SAVED_IN_FIREBASE + " = 0 ",
                null, null);


        if (newEntries.moveToFirst()) {

            do {


                Transaction transaction = Transaction.fromCursor(newEntries);

                String ref = transaction.saveNewToFireBase(context);

                ContentValues contentValues = new ContentValues();

                contentValues.put(FinancialContract.TransactionEntry.FIREBASE_REFERENCE, ref);

                contentResolver.update(FinancialContract.TransactionEntry.buildTransactionIdUri(transaction.getId()), contentValues, null, null);



            } while (newEntries.moveToNext());

            ContentValues contentValues = new ContentValues();
            contentValues.put(FinancialContract.TransactionEntry.SAVED_IN_FIREBASE, 1);

            contentResolver.update(FinancialContract.TransactionEntry.CONTENT_URI, contentValues, null, null);


        }

    }
}
