package com.shamdroid.myfinancialassistant.data;

import android.content.Context;
import android.database.Cursor;

import java.util.Calendar;

/**
 * Created by mohammad on 02/10/16.
 */

public class ContentProviderHelper {


    public static Cursor getCurrentMonthTransactions(Context context) {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        String whereStatement = FinancialContract.TransactionEntry.YEAR + " = ? AND "
                + FinancialContract.TransactionEntry.MONTH + " = ? ";

        String[] args = new String[]{String.valueOf(year), String.valueOf(month)};

        return context.getContentResolver().query(FinancialContract.TransactionEntry.CONTENT_URI, null, whereStatement, args, null);

    }

}
