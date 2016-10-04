package com.shamdroid.myfinancialassistant.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.shamdroid.myfinancialassistant.Models.Transaction;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.UI.AddEditTransactionActivity;
import com.shamdroid.myfinancialassistant.UI.MainActivity;
import com.shamdroid.myfinancialassistant.UI.Util;
import com.shamdroid.myfinancialassistant.data.ContentProviderHelper;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UpdateWidgetService extends IntentService {

    public UpdateWidgetService() {
        super("UpdateWidgetService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int appIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this, AppWidget.class));

        for (int appId : appIds) {

            float balance = SharedPreferencesManager.getBalance(this);

            float expenses = 0.0f;
            float income = 0f;

            Cursor cursor = ContentProviderHelper.getCurrentMonthTransactions(this);


            if (cursor.moveToFirst()) {
                do {

                    Transaction transaction = Transaction.fromCursor(cursor);

                    if (transaction.getType() == Transaction.EXPENSE_TYPE)
                        expenses += transaction.getAmount();
                    else
                        income += transaction.getAmount();

                } while (cursor.moveToNext());
            }


            cursor.close();

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.app_widget);


            String dollarSign = getString(R.string.dollarSign);

            float monthNet = income - expenses;
            remoteViews.setTextViewText(R.id.txtWidgetBalance, balance + dollarSign);
            remoteViews.setTextViewText(R.id.txtWidgetMonthExpensesValue, expenses + dollarSign);
            remoteViews.setTextViewText(R.id.txtWidgetMonthIncomeValue, income + dollarSign);
            remoteViews.setTextViewText(R.id.txtWidgetMonthNetValue, monthNet + dollarSign);

            int green = Util.getColor(this, R.color.green);
            int red = Util.getColor(this, R.color.red);


            remoteViews.setTextColor(R.id.txtWidgetMonthNetValue, monthNet>=0?green:red);

            remoteViews.setTextColor(R.id.txtWidgetBalance, balance >= 0 ? green : red);


            String monthName = new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime());


            String monthsExpenses = getString(R.string.month_expenses, monthName);
            String monthsIncome = getString(R.string.month_incomes, monthName);
            String monthsNet = getString(R.string.month_net, monthName);

            remoteViews.setTextViewText(R.id.txtWidgetMonthNet, monthsNet);
            remoteViews.setTextViewText(R.id.txtWidgetMonthExpenses, monthsExpenses);
            remoteViews.setTextViewText(R.id.txtWidgetMonthIncome, monthsIncome);


            Intent addIncome = new Intent(this, AddEditTransactionActivity.class);

            addIncome.putExtra(AddEditTransactionActivity.IS_EDITING, false);

            addIncome.putExtra(AddEditTransactionActivity.TYPE_KEY, Transaction.INCOME_TYPE);


            PendingIntent addIncomePendingIntent = PendingIntent.getActivity(this, 0, addIncome, 0);

            remoteViews.setOnClickPendingIntent(R.id.btnWidgetAddIncome, addIncomePendingIntent);


            Intent addExpense = new Intent(this, AddEditTransactionActivity.class);
            addExpense.putExtra(AddEditTransactionActivity.TYPE_KEY, Transaction.EXPENSE_TYPE);

            addExpense.putExtra(AddEditTransactionActivity.IS_EDITING, false);

            PendingIntent addExpensePendingIntent = PendingIntent.getActivity(this, 1, addExpense, 0);

            remoteViews.setOnClickPendingIntent(R.id.btnWidgetAddExpense, addExpensePendingIntent);


            Intent main = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, main, 0);

            remoteViews.setOnClickPendingIntent(R.id.appWidget, pendingIntent);

            appWidgetManager.updateAppWidget(appId, remoteViews);

        }
    }

}
