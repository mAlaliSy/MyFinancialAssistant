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

    public AddCategorySourceDialog(Context context, int type) {
        super(context);
        this.context = context;

        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_category_dialog);
        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this);

        txtCatSrc.setText(context.getString(type==CategorySource.TYPE_CAT?R.string.category:R.string.source));

        btnAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAdd:

                String name = etxtCategoryName.getText().toString();

                if (name.length()==0) {
                    Toast.makeText(context, context.getString(R.string.emptyString), Toast.LENGTH_LONG).show();
                }else{
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FinancialContract.CategoryEntry.NAME,name);

                    Uri uri;
                    if(type == CategorySource.TYPE_CAT)
                        uri = context.getContentResolver().insert(FinancialContract.CategoryEntry.CONTENT_URI,contentValues);
                    else
                        uri = context.getContentResolver().insert(FinancialContract.SourceEntry.CONTENT_URI,contentValues);

                    int id = (int) FinancialContract.getIdFromUri(uri);

                    this.dismiss();

                    CategorySource categorySource = new CategorySource(id,name);
                    ((CategoryAddCallback)context).onCategoryAdded(categorySource);
                }

                break;
        }
    }


    public interface CategoryAddCallback{
        void onCategoryAdded(CategorySource categorySource);
    }



    public String getName(){
        return etxtCategoryName.getText().toString();
    }

    public void setName(String name){
        etxtCategoryName.setText(name);
    }
}
