package com.shamdroid.myfinancialassistant.UI;

import android.animation.Animator;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.shamdroid.myfinancialassistant.Models.CategorySource;
import com.shamdroid.myfinancialassistant.Models.Transaction;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.floatingActionButton)
    FloatingActionButton fab;

    @BindView(R.id.monthsExpenses)
    TextView txtMonthsExpensesTitle;

    @BindView(R.id.monthsIncomes)
    TextView txtMonthsIncomesTitle;

    @BindView(R.id.monthNet)
    TextView txtMonthNetTitle;

    @BindView(R.id.txtBalance)
    TextView txtBalance;

    @BindView(R.id.txtMonthExpenses)
    TextView txtMonthExpenses;

    @BindView(R.id.txtMonthIncomes)
    TextView txtMonthIncomes;

    @BindView(R.id.txtMonthNet)
    TextView txtMonthNet;

    @BindView(R.id.pieChart)
    PieChart pieChart;

    @BindView(R.id.noDataMessage)
    LinearLayout noDataMessage;


    @BindView(R.id.selectedDataContainer)
    CardView selectedDataContainer;

    @BindView(R.id.chartSelectedData)
    LinearLayout chartSelectedData;

    @BindView(R.id.txtSelectItemMessage)
    TextView txtSelectItemMessage;

    @BindView(R.id.txtChartCatName)
    TextView txtChartSelectedCat;
    @BindView(R.id.txtTotalExpensesVal)
    TextView txtChartSelectedEx;
    @BindView(R.id.txtPercentOfExpensesVal)
    TextView txtChartSelectedPercentOfEx;
    @BindView(R.id.txtPercentOfIncomesVal)
    TextView txtChartSelectedPercentOfIn;
    @BindView(R.id.txtAllInMonth)
    TextView txtAllInMonth;


    public final int LOADER_ID = 100;
    public final int CATEGORIES_LOADER_ID = 101;

    boolean transitionsLoaded = false;
    boolean categoriesLoaded = false;

    ArrayList<PieEntry> chartEntries;

    ArrayList<CategorySource> categories;
    String dollarSign;
    private String thisMonth;


    float balance;
    float monthExpenses;
    float monthIncomes;

    ArrayList<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!SharedPreferencesManager.isLoggedIn(this)) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }

        ButterKnife.bind(this);


        thisMonth = new SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().getTime());

        txtMonthsExpensesTitle.setText(getString(R.string.month_expenses, thisMonth));
        txtMonthsIncomesTitle.setText(getString(R.string.month_incomes, thisMonth));
        txtMonthNetTitle.setText(getString(R.string.month_net, thisMonth));

        txtAllInMonth.setText(getString(R.string.allInMonth, thisMonth));


        fab.setOnClickListener(this);


        categories = new ArrayList<>();
        transactions = new ArrayList<>();

        chartEntries = new ArrayList<>();

        dollarSign = getString(R.string.dollarSign);

        pieChart.setUsePercentValues(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(thisMonth +" "+ getString(R.string.expenses));

        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.setDescription("");

        pieChart.setDrawEntryLabels(false);


        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(5);


    }


    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    void updateData() {


        categoriesLoaded = false;
        transitionsLoaded = false;

        float balance = SharedPreferencesManager.getBalance(this);

        txtBalance.setText(balance + dollarSign);

        txtBalance.setTextColor(Util.getColor(this, balance < 0 ? R.color.red : R.color.green));

        transactions.clear();
        categories.clear();
        chartEntries.clear();

        getLoaderManager().restartLoader(LOADER_ID, null, this);
        getLoaderManager().restartLoader(CATEGORIES_LOADER_ID, null, this);
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


        switch (i) {
            case LOADER_ID:
                Calendar calendar = Calendar.getInstance();

                String whereStatment = FinancialContract.TransactionEntry.MONTH + "=? AND "
                        + FinancialContract.TransactionEntry.YEAR + "=?";

                String wherArgs[] = {String.valueOf(calendar.get(Calendar.MONTH)),
                        String.valueOf(calendar.get(Calendar.YEAR))};

                return new CursorLoader(this, FinancialContract.TransactionEntry.CONTENT_URI
                        , null
                        , whereStatment, wherArgs, null);
            case CATEGORIES_LOADER_ID:
                return new CursorLoader(this, FinancialContract.CategoryEntry.CONTENT_URI, null, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        switch (loader.getId()) {
            case LOADER_ID:

                transactions.clear();

                monthExpenses = 0.0f;
                monthIncomes = 0.0f;


                if (cursor.moveToFirst()) {
                    do {


                        Transaction transaction = Transaction.fromCursor(cursor);
                        transactions.add(transaction);


                        if (transaction.getType() == Transaction.EXPENSE_TYPE)
                            monthExpenses += transaction.getAmount();
                        else
                            monthIncomes += transaction.getAmount();
                    } while (cursor.moveToNext());
                }

                txtMonthExpenses.setText(monthExpenses + dollarSign);
                txtMonthIncomes.setText(monthIncomes + dollarSign);

                float net = monthIncomes - monthExpenses;

                txtMonthNet.setText(net + dollarSign);

                txtMonthNet.setTextColor(Util.getColor(this, net < 0 ? R.color.red : R.color.green));


                transitionsLoaded = true;

                if (categoriesLoaded)
                    loadDataIntoChart();

                break;
            case CATEGORIES_LOADER_ID:

                categories.clear();
                if (cursor.moveToFirst()) {


                    do {
                        CategorySource category = CategorySource.categoryFromCursor(cursor);
                        categories.add(category);

                    } while (cursor.moveToNext());

                }

                categoriesLoaded = true;

                if (transitionsLoaded)
                    loadDataIntoChart();

                break;
        }
    }

    CategorySource getCategoryById(int id) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == id)
                return categories.get(i);
        }
        return null;
    }

    private void loadDataIntoChart() {


        for (int i = 0; i < transactions.size(); i++) {

            Transaction transaction = transactions.get(i);

            if (transaction.getType() == Transaction.EXPENSE_TYPE) {

                CategorySource category = getCategoryById(transaction.getCategorySourceId());

                fetchData(category.getName(), transaction.getAmount());

            }
        }

        if (chartEntries.size() == 0) {

            pieChart.setVisibility(View.GONE);
            noDataMessage.setVisibility(View.VISIBLE);
            return;
        }else{

            pieChart.setVisibility(View.VISIBLE);
            noDataMessage.setVisibility(View.GONE);

        }

        PieDataSet pieDataSet = new PieDataSet(chartEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setSliceSpace(2);


        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);

        pieChart.setUsePercentValues(true);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueTextSize(11);

        pieChart.invalidate();
        pieChart.animateY(1500);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(final Entry e, final Highlight h) {

                blinkChartSelectedContainer(new AnimationEnd() {
                    @Override
                    public void onAnimationEnd() {

                        PieEntry pieEntry = (PieEntry) e;

                        txtChartSelectedCat.setText(pieEntry.getLabel());

                        txtChartSelectedCat.setTextColor(ColorTemplate.MATERIAL_COLORS[(int) h.getX()]);

                        float value = pieEntry.getValue();

                        float percentOfExpenses = value / monthExpenses * 100;

                        txtChartSelectedEx.setText(String.valueOf(value)+dollarSign);

                        String roundPercentOfExpenses = Math.round(percentOfExpenses * 10) / 10.0f + "%"; // Round float to 2 decimal places

                        txtChartSelectedPercentOfEx.setText(roundPercentOfExpenses);


                        if (monthIncomes != 0) {
                            float percentOfIncomes = value / monthIncomes * 100;
                            String roundPercentOfIncomes = Math.round(percentOfIncomes * 10) / 10.0f + "%";
                            txtChartSelectedPercentOfIn.setText(roundPercentOfIncomes);
                        } else {
                            txtChartSelectedPercentOfIn.setText(getString(R.string.noIncomesInMonth, thisMonth));
                        }


                        chartSelectedData.setVisibility(View.VISIBLE);
                        txtSelectItemMessage.setVisibility(View.GONE);

                    }
                });


            }

            @Override
            public void onNothingSelected() {

                blinkChartSelectedContainer(new AnimationEnd() {
                    @Override
                    public void onAnimationEnd() {

                        chartSelectedData.setVisibility(View.GONE);
                        txtSelectItemMessage.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
    }

    void fetchData(String name, float amount) {


        for (int i = 0; i < chartEntries.size(); i++) {

            PieEntry pieEntry = chartEntries.get(i);

            if (pieEntry.getLabel().equals(name)) {
                float newValue = pieEntry.getValue() + amount;
                pieEntry.setY(newValue);
                return;
            }

        }
        chartEntries.add(new PieEntry(amount, name));

    }


    void blinkChartSelectedContainer(final AnimationEnd end){

        selectedDataContainer.animate().alpha(0.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                end.onAnimationEnd();
                selectedDataContainer.animate().alpha(1.0f).setDuration(300).start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();

    }


    interface AnimationEnd{
        void onAnimationEnd();
    }




    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
