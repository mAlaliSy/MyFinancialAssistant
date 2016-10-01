package com.shamdroid.myfinancialassistant.UI;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.shamdroid.myfinancialassistant.Models.CategorySource;
import com.shamdroid.myfinancialassistant.Models.Transaction;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class EditCategories extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CategoriesAdapter.CategoriesCallback,AddCategorySourceDialog.CategoryAddCallback {

    @BindView(R.id.recyclerCats)
    RecyclerView recyclerView;
    @BindView(R.id.progressLoadingCategories)
    ProgressBar progressBar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    final int LOADER_ID = 101;

    final int TRAN_TO_DELETE_LOADER = 202;

    int type;


    Context context;

    public EditCategories(Context context,int type) {

        this.type = type;

        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_categories, container, false);

        ButterKnife.bind(this, view);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCategorySourceDialog addCategorySourceDialog = new AddCategorySourceDialog(context,type,EditCategories.this);
                addCategorySourceDialog.show();
            }
        });


        getLoaderManager().initLoader(LOADER_ID, null, this);


        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = type == CategorySource.TYPE_CAT ? FinancialContract.CategoryEntry.CONTENT_URI
                : FinancialContract.SourceEntry.CONTENT_URI;

        return new CursorLoader(context, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        CategoriesAdapter categoriesAdapter = (CategoriesAdapter) recyclerView.getAdapter();

        if (categoriesAdapter == null) {



            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            CategoriesAdapter adapter = new CategoriesAdapter(data, context, type, this);

            recyclerView.setAdapter(adapter);

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {

            categoriesAdapter.swapCursor(data);

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public void refreshCursor() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onDeleteClick(final CategorySource categorySource) {

        final boolean isCat = categorySource.getType() == CategorySource.TYPE_CAT;

        getLoaderManager().restartLoader(TRAN_TO_DELETE_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>() {

            String where = FinancialContract.TransactionEntry.TYPE + " =? AND " + FinancialContract.TransactionEntry.SOURCE_CATEGORY + " =? ";

            String[] arguments = {
                    String.valueOf(isCat? Transaction.EXPENSE_TYPE : Transaction.INCOME_TYPE),
                    String.valueOf(categorySource.getId())};


            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {



                return new CursorLoader(context, FinancialContract.TransactionEntry.CONTENT_URI,null,where,arguments,null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data.moveToFirst()){
                    do{

                        Transaction transaction = Transaction.fromCursor(data);
                        transaction.deleteFromFirebase(context);

                    }while(data.moveToNext());

                    context.getContentResolver().delete(FinancialContract.TransactionEntry.CONTENT_URI,where,arguments);

                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });

        int id = categorySource.getId();
        Uri uri = type == CategorySource.TYPE_CAT ? FinancialContract.CategoryEntry.buildCategoryIdUri(id) :
                FinancialContract.SourceEntry.buildSourceIdUri(id);


        context.getContentResolver().delete(uri, null, null);


        categorySource.deleteFromFirebase(context);

        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onCategoryAdded(CategorySource categorySource) {
        getLoaderManager().restartLoader(LOADER_ID,null,this);
    }
}
