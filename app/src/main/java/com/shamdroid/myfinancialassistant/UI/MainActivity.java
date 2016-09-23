package com.shamdroid.myfinancialassistant.UI;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shamdroid.myfinancialassistant.Models.Transaction;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.floatingActionButton)
    FloatingActionButton fab;

    @BindView(R.id.txtBalance)
    TextView txtBalance;

    @BindView(R.id.txtMonthExpenses)
    TextView txtMonthExpenses;

    @BindView(R.id.txtMonthIncomes)
    TextView txtMonthIncomes;

    @BindView(R.id.txtMonthNet)
    TextView txtMonthNet;

    public static final int LOADER_ID = 100;


    String dollarSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!SharedPreferencesManager.isLoggedIn(this)) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }

        ButterKnife.bind(this);

        fab.setOnClickListener(this);

        dollarSign = getString(R.string.dollarSign);

        updateOverview();
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        updateOverview();
    }

    void updateOverview() {
        float balance = SharedPreferencesManager.getBalance(this);

        txtBalance.setText(balance + dollarSign);

        txtBalance.setTextColor(Util.getColor(this, balance < 0 ? R.color.red : R.color.green));

        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.floatingActionButton:

                AlertDialog alertDialog = createDialog();
                alertDialog.show();

                break;

        }

    }


    AlertDialog createDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setItems(new String[]{
                        getString(R.string.add_expense),
                        getString(R.string.add_income)
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(MainActivity.this, AddEditTransactionActivity.class);

                        intent.putExtra(AddEditTransactionActivity.TYPE_KEY,
                                i == 0 ? Transaction.EXPENSE_TYPE : Transaction.INCOME_TYPE);

                        intent.putExtra(AddEditTransactionActivity.IS_EDITING, false);
                        startActivity(intent);
                    }
                }).create();

        return alertDialog;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        String whereStatment = FinancialContract.TransactionEntry.MONTH + "=? AND "
                + FinancialContract.TransactionEntry.YEAR + "=?";

        String wherArgs[] = {String.valueOf(calendar.get(Calendar.MONTH)),
                String.valueOf(calendar.get(Calendar.YEAR))};

        return new CursorLoader(this, FinancialContract.TransactionEntry.CONTENT_URI
                , new String[]{FinancialContract.TransactionEntry.TYPE
                , FinancialContract.TransactionEntry.AMOUNT}
                , whereStatment, wherArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        float expenses = 0.0f, incomes = 0.0f;

        if (cursor.moveToFirst()) {
            do {

                int type = cursor.getInt(cursor.getColumnIndex(FinancialContract.TransactionEntry.TYPE));
                float amount = cursor.getFloat(cursor.getColumnIndex(FinancialContract.TransactionEntry.AMOUNT));

                if (type == Transaction.EXPENSE_TYPE)
                    expenses += amount;
                else
                    incomes += amount;
            } while (cursor.moveToNext());
        }

        txtMonthExpenses.setText(expenses + dollarSign);
        txtMonthIncomes.setText(incomes + dollarSign);

        float net = incomes - expenses;

        txtMonthNet.setText(net + dollarSign);

        txtMonthNet.setTextColor(Util.getColor(this, net < 0 ? R.color.red : R.color.green));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
