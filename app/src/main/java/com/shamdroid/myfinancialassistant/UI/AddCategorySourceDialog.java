package com.shamdroid.myfinancialassistant.UI;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shamdroid.myfinancialassistant.Models.CategorySource;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.Utils.Utils;
import com.shamdroid.myfinancialassistant.data.FinancialContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mohammad on 20/09/16.
 */

public class AddCategorySourceDialog extends Dialog implements View.OnClickListener {

    Context context;

    @BindView(R.id.txtCatSrc)
    TextView txtCatSrc;

    @BindView(R.id.etxtCategoryName)
    EditText etxtCategoryName;

    @BindView(R.id.btnAdd)
    Button btnAdd;

    int type;

    boolean isEditing = false;


    CategorySource categorySource;
    private CategoryAddCallback categoryAddCallback;


    public AddCategorySourceDialog(Context context, int type, CategoryAddCallback categoryAddCallback) {
        super(context);
        this.context = context;
this.categoryAddCallback=categoryAddCallback;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_category_dialog);
        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this);

        txtCatSrc.setText(context.getString(type == CategorySource.TYPE_CAT ? R.string.category : R.string.source));

        btnAdd.setOnClickListener(this);

        if (isEditing) {
            etxtCategoryName.setText(categorySource.getName());
        }

        btnAdd.setText(context.getString(isEditing ? R.string.save : R.string.add));


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                String name = etxtCategoryName.getText().toString();

                if (name.length() == 0) {
                    Toast.makeText(context, context.getString(R.string.emptyString), Toast.LENGTH_LONG).show();
                    return;
                }

                if (isEditing) {
                    Uri uri = type == CategorySource.TYPE_CAT ? FinancialContract.CategoryEntry.buildCategoryIdUri(categorySource.getId()) :
                            FinancialContract.SourceEntry.buildSourceIdUri(categorySource.getId());

                    categorySource.setName(name);
                    categorySource.updateFirebase(context);
                    ContentValues contentValues = categorySource.toContentValues();

                    context.getContentResolver().update(uri, contentValues, null, null);


                } else {

                    CategorySource categorySource = new CategorySource(-1, name);

                    categorySource.setType(type);

                    Uri uri;
                    if (type == CategorySource.TYPE_CAT) {


                        if (Utils.isConnected(context)) {
                            String firebaseReference = categorySource.saveNewToFirebase(context);

                            categorySource.setFirebaseReference(firebaseReference);
                        } else
                            categorySource.setSavedToFirebase(false);


                        ContentValues contentValues = categorySource.toContentValues();

                        uri = context.getContentResolver().insert(FinancialContract.CategoryEntry.CONTENT_URI, contentValues);


                        if (Utils.isConnected(context)) {
                            int id = (int) FinancialContract.getIdFromUri(uri);

                            categorySource.setId(id);

                            categorySource.updateFirebase(context);
                        }
                    } else {

                        if (Utils.isConnected(context)) {
                            String firebaseReference = categorySource.saveNewToFirebase(context);

                            categorySource.setFirebaseReference(firebaseReference);

                        } else {
                            categorySource.setSavedToFirebase(false);
                        }
                        ContentValues contentValues = categorySource.toContentValues();

                        uri = context.getContentResolver().insert(FinancialContract.SourceEntry.CONTENT_URI, contentValues);

                        if (Utils.isConnected(context)) {
                            int id = (int) FinancialContract.getIdFromUri(uri);

                            categorySource.setId(id);

                            categorySource.updateFirebase(context);
                        }


                    }


                    categoryAddCallback.onCategoryAdded(categorySource);

                }

                this.dismiss();

                break;

        }
    }


    public interface CategoryAddCallback {
        void onCategoryAdded(CategorySource categorySource);
    }


    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;

    }

    public CategorySource getCategorySource() {
        return categorySource;
    }

    public void setCategorySource(CategorySource categorySource) {
        this.categorySource = categorySource;
    }


    public String getName() {
        return etxtCategoryName.getText().toString();
    }

    public void setName(String name) {
        etxtCategoryName.setText(name);
    }
}
