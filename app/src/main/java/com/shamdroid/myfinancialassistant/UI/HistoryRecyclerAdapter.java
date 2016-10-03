package com.shamdroid.myfinancialassistant.UI;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shamdroid.myfinancialassistant.Models.Transaction;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.Utils.Utils;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;
import com.shamdroid.myfinancialassistant.widget.AppWidget;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mohammad on 21/09/16.
 */

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements LoaderManager.LoaderCallbacks<Cursor> {


    private final int TYPE_LOADING = 1;
    private final int TYPE_RECORD = 2;

    private Context context;

    private Cursor data;

    private RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;

    private final int LOADER_ID = 101;
    private final int CATEGORY_LOADER = 102;
    private final int SOURCE_LOADER = 103;


    private final String LOAD_DATA_TYPE_KEY = "load_type";

    private final int LOAD_MORE_LOADER = 0;
    private final int REFRESH_DATA_LOADER = 1;


    static final int EDITING_ACTIVITY_CODE = 201;


    private static final int LOAD_LIMIT = 5;

    private int loadedCount = 0;


    private boolean isLoading = false;

    private boolean isAllLoaded = false;

    private SimpleDateFormat monthFormatter;
    private SimpleDateFormat monthYearFormatter;
    private SimpleDateFormat dayFormatter;


    private Cursor categories;
    private Cursor sources;


    private int currentYear;

    private int greenColor;
    private int redColor;

    private String expense;
    private String income;

    private String category;
    private String source;

    private String dollarSign;


    HistoryRecyclerAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;

        monthFormatter = new SimpleDateFormat("MMMM");
        monthYearFormatter = new SimpleDateFormat("MMMM, yyyy");
        dayFormatter = new SimpleDateFormat("EEE, dd");


        // Cache data used in onBindViewHolder for better performance

        currentYear = Calendar.getInstance().get(Calendar.YEAR);

        greenColor = Util.getColor(context, R.color.green);
        redColor = Util.getColor(context, R.color.red);

        expense = context.getString(R.string.expense);
        income = context.getString(R.string.income);

        category = context.getString(R.string.category);
        source = context.getString(R.string.source);




        ((AppCompatActivity) context).getLoaderManager().initLoader(CATEGORY_LOADER, null, this);
        ((AppCompatActivity) context).getLoaderManager().initLoader(SOURCE_LOADER, null, this);


        dollarSign = context.getString(R.string.dollarSign);


        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisible = linearLayoutManager.findLastVisibleItemPosition();
                int itemCount = linearLayoutManager.getItemCount() - 1;


                if (!isLoading && !isAllLoaded && (lastVisible == itemCount)) {
                    loadData(LOAD_MORE_LOADER);
                    isLoading = true;
                }

            }

        });



    }

    interface DataLoaderListener {
        void onDataLoadedFirstTime();

        void noDataFound();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        loadData(LOAD_MORE_LOADER);

    }


    private void loadData(int loadType) {

        Bundle bundle = new Bundle();
        bundle.putInt(LOAD_DATA_TYPE_KEY, loadType);

        ((AppCompatActivity) context).getLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {


            case TYPE_LOADING:
                View loadingView = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item, parent, false);
                return new LoadingViewHolder(loadingView);
            default:
                View recordView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
                return new TransactionViewHolder(recordView);

        }
    }


    private void displayMonthDaySeparate(int position, Transaction transaction, TransactionViewHolder viewHolder) {

        boolean displayMonthSeparator = false;
        boolean displayDaySeparator = false;

        Calendar calendar = transaction.getCalendar();

        if (position == 0) {
            displayMonthSeparator = true;
            displayDaySeparator = true;
        } else {
            data.moveToPrevious();

            int dayIndex = FinancialContract.TransactionEntry.DAY_INDEX;
            int monthIndex = FinancialContract.TransactionEntry.MONTH_INDEX;
            int yearIndex = FinancialContract.TransactionEntry.YEAR_INDEX;

            int prevDay = data.getInt(dayIndex);
            int prevMonth = data.getInt(monthIndex);
            int prevYear = data.getInt(yearIndex);

            data.moveToNext();

            int positionDay = data.getInt(dayIndex);
            int positionMonth = data.getInt(monthIndex);
            int positionYear = data.getInt(yearIndex);

            if (prevDay != positionDay) {
                displayDaySeparator = true;
            }

            if (positionMonth != prevMonth || positionYear != prevYear) {
                displayMonthSeparator = true;
                displayDaySeparator = true;
            }
        }

        if (displayMonthSeparator) {
            int year = transaction.getYear();

            String formattedDate;
            if (year == currentYear) {
                formattedDate = monthFormatter.format(calendar.getTime());
            } else {
                formattedDate = monthYearFormatter.format(calendar.getTime());
            }

            viewHolder.txtMonthSeparate.setText(formattedDate);

            viewHolder.txtMonthSeparate.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtMonthSeparate.setVisibility(View.GONE);
        }


        if (displayDaySeparator) {
            String dayFormatted = dayFormatter.format(calendar.getTime());
            viewHolder.txtDate.setVisibility(View.VISIBLE);
            viewHolder.txtDate.setText(dayFormatted);
        } else {
            viewHolder.txtDate.setVisibility(View.GONE);
        }


    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof TransactionViewHolder) {


            data.moveToPosition(position);

            final TransactionViewHolder viewHolder = (TransactionViewHolder) holder;


            final Transaction transaction = Transaction.fromCursor(data);

            displayMonthDaySeparate(position, transaction, (TransactionViewHolder) holder);


            if (transaction.getType() == Transaction.EXPENSE_TYPE) {
                viewHolder.txtType.setText(expense);
                viewHolder.txtCatSrc.setText(category);

                viewHolder.txtType.setTextColor(redColor);
                viewHolder.txtAmount.setTextColor(redColor);

                if (categories != null) {
                    String category = getCategoryNameFromId(transaction.getSource_category());
                    if (category != null) {
                        viewHolder.txtCatSrcValue.setText(category);
                    }
                }


            } else {
                viewHolder.txtType.setText(income);
                viewHolder.txtCatSrc.setText(source);

                viewHolder.txtType.setTextColor(greenColor);
                viewHolder.txtAmount.setTextColor(greenColor);

                if (sources != null) {
                    String source = getSourceNameFromId(transaction.getSource_category());
                    viewHolder.txtCatSrcValue.setText(source);
                }

            }

            viewHolder.txtAmount.setText(transaction.getAmount() + dollarSign);


            String note = transaction.getNote();

            if (note == null || note.isEmpty()) {
                viewHolder.txtNote.setVisibility(View.GONE);
                viewHolder.txtNoteValue.setVisibility(View.GONE);
            } else {
                viewHolder.txtNote.setVisibility(View.VISIBLE);
                viewHolder.txtNoteValue.setVisibility(View.VISIBLE);
                viewHolder.txtNoteValue.setText(transaction.getNote());
            }

            viewHolder.ibtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AddEditTransactionActivity.class);
                    intent.putExtra(AddEditTransactionActivity.IS_EDITING, true);
                    intent.putExtra(AddEditTransactionActivity.TYPE_KEY, transaction.getType());
                    intent.putExtra(AddEditTransactionActivity.TRANSACTION_KEY, transaction);
                    ((AppCompatActivity) context).startActivityForResult(intent, EDITING_ACTIVITY_CODE);
                }
            });


            viewHolder.ibtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(context.getString(R.string.deleteMessage)).setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteTransaction(transaction, position);
                        }
                    }).create();
                    alertDialog.show();
                }
            });


        }
    }


    private void deleteTransaction(Transaction transaction, int position) {

        if(Utils.isConnected(context))
            transaction.deleteFromFirebase(context);
        else{

            String fireBaseRef = transaction.getFirebaseReference();
            String type = FinancialContract.TransactionEntry.TRANSACTIONS_TABLE;
            ContentValues contentValues = new ContentValues();
            contentValues.put(FinancialContract.DeleteFromFirebaseEntry.REFERENCE,fireBaseRef);
            contentValues.put(FinancialContract.DeleteFromFirebaseEntry.TYPE,type);

            if(fireBaseRef != null && fireBaseRef.length()!=0){
                context.getContentResolver().insert(FinancialContract.DeleteFromFirebaseEntry.CONTENT_URI, contentValues);
            }

        }
        context.getContentResolver().delete(FinancialContract.TransactionEntry.buildTransactionIdUri(transaction.getId()), null, null);
        if (transaction.getType() == Transaction.EXPENSE_TYPE) {
            float balance = SharedPreferencesManager.getBalance(context);

            balance += transaction.getAmount();

            SharedPreferencesManager.setBalance(context, balance);

        } else {

            float balance = SharedPreferencesManager.getBalance(context);

            balance -= transaction.getAmount();

            SharedPreferencesManager.setBalance(context, balance);

        }

        context.sendBroadcast(new Intent(context, AppWidget.class));

        loadData(REFRESH_DATA_LOADER);

    }


    void onEditResult() {
        loadData(REFRESH_DATA_LOADER);
    }


    private String getCategoryNameFromId(int id) {
        categories.moveToFirst();
        do {

            if (categories.getInt(FinancialContract.CategoryEntry.ID_INDEX) == id)
                return categories.getString(FinancialContract.CategoryEntry.NAME_INDEX);

        } while (categories.moveToNext());
        return null;

    }

    public String getSourceNameFromId(int id) {
        sources.moveToFirst();

        do {

            if (sources.getInt(FinancialContract.SourceEntry.ID_INDEX) == id)
                return sources.getString(FinancialContract.SourceEntry.NAME_INDEX);

        } while (sources.moveToNext());
        return null;

    }



    @Override
    public int getItemViewType(int position) {

        // Check if reached the bottom, loading view ..
        if (position == getItemCount() - 1 && !isAllLoaded)
            return TYPE_LOADING;

        return TYPE_RECORD;
    }

    public int getItemCount() {
        if (isAllLoaded)
            return data.getCount();

        // Data items with loading progress bar
        return data.getCount() + 1;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        switch (i) {
            case LOADER_ID:
                String orderLimit = FinancialContract.TransactionEntry.YEAR + " DESC , "
                        + FinancialContract.TransactionEntry.MONTH + " DESC , "
                        + FinancialContract.TransactionEntry.DAY + " DESC , "
                        + FinancialContract.TransactionEntry.ID + " DESC "
                        + " LIMIT " + (bundle.getInt(LOAD_DATA_TYPE_KEY) == LOAD_MORE_LOADER ? (loadedCount + LOAD_LIMIT) : loadedCount);

                return new CursorLoader(context, FinancialContract.TransactionEntry.CONTENT_URI, null, null, null, orderLimit);
            case CATEGORY_LOADER:

                return new CursorLoader(context, FinancialContract.CategoryEntry.CONTENT_URI, null, null, null, null);

            case SOURCE_LOADER:
                return new CursorLoader(context, FinancialContract.SourceEntry.CONTENT_URI, null, null, null, null);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {
            case LOADER_ID:
                int cursorCount = cursor.getCount();
                isLoading = false;

                if (cursorCount < loadedCount + LOAD_LIMIT)
                    isAllLoaded = true;
                data = cursor;

                if (data.moveToFirst()) {
                    ((DataLoaderListener) context).onDataLoadedFirstTime();
                } else {
                    ((DataLoaderListener) context).noDataFound();
                }


                loadedCount = cursorCount;

                break;
            case CATEGORY_LOADER:
                categories = cursor;
                break;
            case SOURCE_LOADER:
                sources = cursor;
                break;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public static class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }

    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.historyItemCard)
        CardView cardView;

        @BindView(R.id.txtHistoryType)
        TextView txtType;
        @BindView(R.id.txtHistoryAmount)
        TextView txtAmount;
        @BindView(R.id.txtHistoryCatSrc)
        TextView txtCatSrc;
        @BindView(R.id.txtHistoryCatSrcValue)
        TextView txtCatSrcValue;

        @BindView(R.id.txtHistoryNote)
        TextView txtNote;

        @BindView(R.id.txtHistoryNoteValue)
        TextView txtNoteValue;


        @BindView(R.id.txtHistoryDate)
        TextView txtDate;

        @BindView(R.id.ibtnEditHistoryRecord)
        ImageButton ibtnEdit;
        @BindView(R.id.ibtnDeleteHistoryRecord)
        ImageButton ibtnDelete;

        @BindView(R.id.txtMonthSeparate)
        TextView txtMonthSeparate;


        public TransactionViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);


        }
    }

}
