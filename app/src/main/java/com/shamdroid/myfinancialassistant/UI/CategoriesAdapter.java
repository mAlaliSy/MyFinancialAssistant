package com.shamdroid.myfinancialassistant.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.shamdroid.myfinancialassistant.Models.CategorySource;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.data.FinancialContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mohammad on 30/09/16.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {


    Cursor cursor;
    Context context;
    int type;
    CategoriesCallback categoriesCallback;

    public CategoriesAdapter(Cursor cursor, Context context, int type, CategoriesCallback categoriesCallback) {

        this.cursor = cursor;

        this.context = context;

        this.type = type;

        this.categoriesCallback = categoriesCallback;
    }


    @Override
    public CategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_categories_item,parent,false);

        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoriesViewHolder holder, int position) {

        cursor.moveToPosition(position);

        int nameIndex = type == CategorySource.TYPE_CAT ? FinancialContract.CategoryEntry.NAME_INDEX
                : FinancialContract.SourceEntry.NAME_INDEX;

        String name = cursor.getString(nameIndex);

        holder.txtCatName.setText(name);

    }

    public void swapCursor(Cursor newCursor) {

        if (cursor != null)
            cursor.close();

        cursor = newCursor;

        notifyDataSetChanged();

    }


    @Override
    public int getItemCount() {

        return cursor != null ? cursor.getCount() : 0;

    }

    class CategoriesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtCatName)
        TextView txtCatName;
        @BindView(R.id.btnDelete)
        ImageButton btnDelete;
        @BindView(R.id.btnEdit)
        ImageButton btnEdit;


        public CategoriesViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cursor.moveToPosition(getAdapterPosition());

                    CategorySource categorySource = type == CategorySource.TYPE_CAT ? CategorySource.categoryFromCursor(cursor)
                            : CategorySource.sourceFromCursor(cursor);


                    AddCategorySourceDialog addCategorySourceDialog = new AddCategorySourceDialog(context,type,null);
                    addCategorySourceDialog.setEditing(true);
                    addCategorySourceDialog.setCategorySource(categorySource);

                    addCategorySourceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            categoriesCallback.refreshCursor();
                        }
                    });

                    addCategorySourceDialog.show();


                }
            });


            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cursor.moveToPosition(getAdapterPosition());
                    final CategorySource categorySource = type == CategorySource.TYPE_CAT ? CategorySource.categoryFromCursor(cursor)
                            : CategorySource.sourceFromCursor(cursor);

                    AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(context.getString(R.string.deleteCategoryMessage)).setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            categoriesCallback.onDeleteClick(categorySource);
                        }
                    }).setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();

                    alertDialog.show();



                }
            });


        }
    }

    interface CategoriesCallback {


        void refreshCursor();
        void onDeleteClick(CategorySource categorySource);

    }


}
