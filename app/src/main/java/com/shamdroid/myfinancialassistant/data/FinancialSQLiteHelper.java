package com.shamdroid.myfinancialassistant.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shamdroid.myfinancialassistant.R;

/**
 * Created by mohammad on 19/09/16.
 */

public class FinancialSQLiteHelper extends SQLiteOpenHelper {

    private static String name = "my_financial_assistant.db";
    private static int version = 1;
    Context context;

    public FinancialSQLiteHelper(Context context) {
        super(context, name, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String QUERY = "CREATE TABLE IF NOT EXISTS " + FinancialContract.CategoryEntry.CATEGORIES_TABLE + " ( "
                + FinancialContract.CategoryEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                FinancialContract.CategoryEntry.NAME + " VARCHAR(255) NOT NULL , "
                + FinancialContract.TransactionEntry.FIREBASE_REFERENCE + " TEXT , "
                + FinancialContract.TransactionEntry.SAVED_IN_FIREBASE + " INTEGER  , "
                + FinancialContract.TransactionEntry.UPDATE_IN_FIREBASE + " INTEGER  ) ;";
        sqLiteDatabase.execSQL(QUERY);

        QUERY = "CREATE TABLE IF NOT EXISTS " + FinancialContract.SourceEntry.SOURCES_TABLE + " ( "
                + FinancialContract.SourceEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                FinancialContract.SourceEntry.NAME + " VARCHAR(255) NOT NULL , "
                + FinancialContract.TransactionEntry.FIREBASE_REFERENCE + " TEXT , "
                + FinancialContract.TransactionEntry.SAVED_IN_FIREBASE + " INTEGER  , "
                + FinancialContract.TransactionEntry.UPDATE_IN_FIREBASE + " INTEGER  ) ;";
        sqLiteDatabase.execSQL(QUERY);

        QUERY = "CREATE TABLE IF NOT EXISTS " + FinancialContract.TransactionEntry.TRANSACTIONS_TABLE + " ( "
                + FinancialContract.TransactionEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + FinancialContract.TransactionEntry.TYPE + " INTEGER NOT NULL , "
                + FinancialContract.TransactionEntry.SOURCE_CATEGORY + " INTEGER NOT NULL , "
                + FinancialContract.TransactionEntry.AMOUNT + " REAL NOT NULL , "
                + FinancialContract.TransactionEntry.NOTE + " TEXT , "
                + FinancialContract.TransactionEntry.DAY + " INTEGER NOT NULL , "
                + FinancialContract.TransactionEntry.MONTH + " INTEGER NOT NULL , "
                + FinancialContract.TransactionEntry.YEAR + " INTEGER NOT NULL , "
                + FinancialContract.TransactionEntry.FIREBASE_REFERENCE + " TEXT , "
                + FinancialContract.TransactionEntry.SAVED_IN_FIREBASE + " INTEGER  , "
                + FinancialContract.TransactionEntry.UPDATE_IN_FIREBASE + " INTEGER  ) ;";

        sqLiteDatabase.execSQL(QUERY);

        QUERY = "CREATE TABLE IF NOT EXISTS " + FinancialContract.DeleteFromFirebaseEntry.DELETE_FROM_FIREBASE_TABLE +"( "
                + FinancialContract.DeleteFromFirebaseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL  , "
                + FinancialContract.DeleteFromFirebaseEntry.REFERENCE + " TEXT NOT NULL , "
                + FinancialContract.DeleteFromFirebaseEntry.TYPE + " TEXT NOT NULL );";
        sqLiteDatabase.execSQL(QUERY);

        String[] defaultCategories = context.getResources().getStringArray(R.array.defaultCategories);

        for (int i = 0; i < defaultCategories.length; i++) {
            ContentValues values = new ContentValues();
            values.put(FinancialContract.CategoryEntry.NAME, defaultCategories[i]);
            sqLiteDatabase.insert(FinancialContract.CategoryEntry.CATEGORIES_TABLE, null, values);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
