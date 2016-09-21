package com.shamdroid.myfinancialassistant.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mohammad on 19/09/16.
 */

public class FinancialContract {

    public static final String AUTHORITY = "com.shamdroid.myfinancialassistant";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);


    public static class CategoryEntry implements BaseColumns {
        public static final String CATEGORIES_TABLE = "categories";
        public static final String ID = "_id";
        public static final String NAME = "name";


        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(CATEGORIES_TABLE).build();

        public static Uri buildCategoryIdUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }



        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + CATEGORIES_TABLE ;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + CATEGORIES_TABLE ;

    }

    public static class SourceEntry implements BaseColumns {
        public static final String SOURCES_TABLE = "sources";
        public static final String ID = "_id";
        public static final String NAME = "name";

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(SOURCES_TABLE).build();

        public static Uri buildSourceIdUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }


        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + SOURCES_TABLE ;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + SOURCES_TABLE ;
    }


    public static class TransactionEntry implements BaseColumns{
        public static final String TRANSACTIONS_TABLE ="transactions";
        public static final String ID="_id";
        public static final String TYPE = "type";
        public static final String SOURCE_CATEGORY = "source_category";
        public static final String AMOUNT = "amount";
        public static final String NOTE = "note";
        public static final String DAY="day";
        public static final String MONTH="month";
        public static final String YEAR="year";

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(TRANSACTIONS_TABLE).build();

        public static Uri buildTransactionIdUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }


        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + TRANSACTIONS_TABLE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + TRANSACTIONS_TABLE;

    }

    public static long getIdFromUri(Uri uri){
        return ContentUris.parseId(uri);
    }



}