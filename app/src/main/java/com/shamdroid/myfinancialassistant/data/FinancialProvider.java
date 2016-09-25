package com.shamdroid.myfinancialassistant.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

public class FinancialProvider extends ContentProvider {
    public FinancialProvider() {
    }


    public static final int CATEGORY_CODE = 100;
    public static final int CATEGORY_ID_CODE = 101;

    public static final int SOURCE_CODE = 200;
    public static final int SOURCE_ID_CODE = 201;

    public static final int TRANSACTION_CODE = 300;
    public static final int TRANSACTION_ID_CODE = 301;


    static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        uriMatcher.addURI(FinancialContract.AUTHORITY, FinancialContract.CategoryEntry.CATEGORIES_TABLE, CATEGORY_CODE);
        uriMatcher.addURI(FinancialContract.AUTHORITY, FinancialContract.CategoryEntry.CATEGORIES_TABLE + "/#", CATEGORY_ID_CODE);

        uriMatcher.addURI(FinancialContract.AUTHORITY, FinancialContract.SourceEntry.SOURCES_TABLE, SOURCE_CODE);
        uriMatcher.addURI(FinancialContract.AUTHORITY, FinancialContract.SourceEntry.SOURCES_TABLE + "/#", SOURCE_ID_CODE);

        uriMatcher.addURI(FinancialContract.AUTHORITY, FinancialContract.TransactionEntry.TRANSACTIONS_TABLE, TRANSACTION_CODE);
        uriMatcher.addURI(FinancialContract.AUTHORITY, FinancialContract.TransactionEntry.TRANSACTIONS_TABLE + "/#", TRANSACTION_ID_CODE);
    }


    FinancialSQLiteHelper sqLiteHelper;


    @Override
    public boolean onCreate() {
        sqLiteHelper = new FinancialSQLiteHelper(getContext());
        return true;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int returnedId = -1;

        switch (uriMatcher.match(uri)) {

            case CATEGORY_CODE:
                returnedId = sqLiteHelper.getWritableDatabase().delete(FinancialContract.CategoryEntry.CATEGORIES_TABLE,
                        selection, selectionArgs);
                break;

            case SOURCE_CODE:
                returnedId = sqLiteHelper.getWritableDatabase().delete(FinancialContract.SourceEntry.SOURCES_TABLE,
                        selection, selectionArgs);
                break;
            case TRANSACTION_CODE:
                returnedId = sqLiteHelper.getWritableDatabase().delete(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE,
                        selection, selectionArgs);
                break;
            case TRANSACTION_ID_CODE:
                returnedId = sqLiteHelper.getWritableDatabase()
                        .delete(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE,
                                FinancialContract.TransactionEntry.ID + " =? ",
                                new String[]{String.valueOf(FinancialContract.getIdFromUri(uri))});

                Log.d("HHHHHHHHHH", FinancialContract.getIdFromUri(uri) + "\n" + uri.toString());
                break;

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }


        getContext().getContentResolver().notifyChange(uri, null);

        return returnedId;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CATEGORY_CODE:
                return FinancialContract.CategoryEntry.CONTENT_DIR_TYPE;
            case CATEGORY_ID_CODE:
                return FinancialContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case SOURCE_CODE:
                return FinancialContract.SourceEntry.CONTENT_DIR_TYPE;

            case SOURCE_ID_CODE:
                return FinancialContract.SourceEntry.CONTENT_ITEM_TYPE;

            case TRANSACTION_CODE:
                return FinancialContract.TransactionEntry.CONTENT_DIR_TYPE;

            case TRANSACTION_ID_CODE:
                return FinancialContract.TransactionEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {


        Uri returedUri = null;

        switch (uriMatcher.match(uri)) {

            case CATEGORY_CODE:
                int catId = (int) sqLiteHelper.getWritableDatabase().insert(FinancialContract.CategoryEntry.CATEGORIES_TABLE, null, values);

                if (catId > -1) {
                    returedUri = FinancialContract.CategoryEntry.buildCategoryIdUri(catId);
                } else {
                    throw new SQLiteException("Failed to insert into table : " + FinancialContract.CategoryEntry.CATEGORIES_TABLE);
                }

                break;
            case SOURCE_CODE:
                int sId = (int) sqLiteHelper.getWritableDatabase().insert(FinancialContract.SourceEntry.SOURCES_TABLE, null, values);

                if (sId > -1) {
                    returedUri = FinancialContract.SourceEntry.buildSourceIdUri(sId);
                } else {
                    throw new SQLiteException("Failed to insert into table : " + FinancialContract.SourceEntry.SOURCES_TABLE);
                }

                break;
            case TRANSACTION_CODE:
                int opId = (int) sqLiteHelper.getWritableDatabase().insert(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE, null, values);

                if (opId > -1) {
                    returedUri = FinancialContract.TransactionEntry.buildTransactionIdUri(opId);
                } else {
                    throw new SQLiteException("Failed to insert into table : " + FinancialContract.CategoryEntry.CATEGORIES_TABLE);
                }
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }


        getContext().getContentResolver().notifyChange(uri, null);

        return returedUri;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case CATEGORY_CODE:
                return sqLiteDatabase.query(FinancialContract.CategoryEntry.CATEGORIES_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
            case CATEGORY_ID_CODE:
                return sqLiteDatabase.query(FinancialContract.CategoryEntry.CATEGORIES_TABLE,
                        projection,
                        FinancialContract.CategoryEntry.ID + "=?",
                        new String[]{String.valueOf(FinancialContract.getIdFromUri(uri))},
                        null, null, null);
            case SOURCE_CODE:
                return sqLiteDatabase.query(FinancialContract.SourceEntry.SOURCES_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
            case SOURCE_ID_CODE:

                return sqLiteDatabase.query(FinancialContract.SourceEntry.SOURCES_TABLE,
                        projection,
                        FinancialContract.CategoryEntry.ID + "=?",
                        new String[]{String.valueOf(FinancialContract.getIdFromUri(uri))},
                        null, null, null);
            case TRANSACTION_CODE:
                return sqLiteDatabase.query(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
            case TRANSACTION_ID_CODE:

                return sqLiteDatabase.query(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE,
                        projection,
                        FinancialContract.CategoryEntry.ID + "=?",
                        new String[]{String.valueOf(FinancialContract.getIdFromUri(uri))},
                        null, null, null);

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case CATEGORY_CODE:
                return sqLiteDatabase.update(FinancialContract.CategoryEntry.CATEGORIES_TABLE,
                        values,
                        selection,
                        selectionArgs);
            case CATEGORY_ID_CODE:
                return sqLiteDatabase.update(FinancialContract.CategoryEntry.CATEGORIES_TABLE,
                        values,
                        FinancialContract.CategoryEntry.ID + "=?",
                        new String[]{String.valueOf(FinancialContract.getIdFromUri(uri))});
            case SOURCE_CODE:
                return sqLiteDatabase.update(FinancialContract.SourceEntry.SOURCES_TABLE,
                        values,
                        selection,
                        selectionArgs);
            case SOURCE_ID_CODE:

                return sqLiteDatabase.update(FinancialContract.SourceEntry.SOURCES_TABLE,
                        values,
                        FinancialContract.CategoryEntry.ID + "=?",
                        new String[]{String.valueOf(FinancialContract.getIdFromUri(uri))});
            case TRANSACTION_CODE:
                return sqLiteDatabase.update(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE,
                        values,
                        selection,
                        selectionArgs);
            case TRANSACTION_ID_CODE:

                return sqLiteDatabase.update(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE,
                        values,
                        FinancialContract.CategoryEntry.ID + "=?",
                        new String[]{String.valueOf(FinancialContract.getIdFromUri(uri))});

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
