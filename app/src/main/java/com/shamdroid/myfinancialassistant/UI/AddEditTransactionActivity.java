package com.shamdroid.myfinancialassistant.UI;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shamdroid.myfinancialassistant.Models.CategorySource;
import com.shamdroid.myfinancialassistant.Models.Transaction;
import com.shamdroid.myfinancialassistant.R;
import com.shamdroid.myfinancialassistant.Utils.Utils;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;
import com.shamdroid.myfinancialassistant.widget.AppWidget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditTransactionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AddCategorySourceDialog.CategoryAddCallback, DatePickerDialog.OnDateSetListener, View.OnClickListener {


    private ArrayList<CategorySource> catsSrcs;

    @BindView(R.id.txtCatSrc)
    TextView txtCatSrc;

    @BindView(R.id.spinCat)
    Spinner spinCat;

    @BindView(R.id.etxtAmount)
    EditText etxtAmount;

    @BindView(R.id.txtSelectedDate)
    TextView txtSelectedDate;

    @BindView(R.id.ibtnSelectDate)
    ImageButton ibtnSelectDate;

    @BindView(R.id.etxtNote)
    EditText etxtNote;

    @BindView(R.id.btnAdd)
    Button btnAdd;


    Calendar selectedDate;
    SimpleDateFormat simpleDateFormat;

    DatePickerDialog datePickerDialog;

    AddCategorySourceDialog addCategorySourceDialog;


    public static final String YEAR_KEY = "year";
    public static final String MONTH_KEY = "month";
    public static final String DAY_KEY = "day";
    public static final String AMOUNT_KEY = "amount";
    public static final String CATEGORY_KEY = "category";
    public static final String NOTE_KEY = "note";

    int savedCatPos = -1;


    public static final String IS_EDITING = "is_editing";
    public static final String TRANSACTION_KEY = "transaction";
    public static final String TYPE_KEY = "type";


    private boolean isEditing;
    private int type;

    Transaction currentTransaction; // If editing an existing transaction.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_transaction);

        ButterKnife.bind(this);

        ibtnSelectDate.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        Intent intent = getIntent();

        type = intent.getExtras().getInt(TYPE_KEY);

        isEditing = intent.getBooleanExtra(IS_EDITING, false);

        if (type == Transaction.EXPENSE_TYPE) {
            if (isEditing)
                setTitle(getString(R.string.edit_expense));
            else
                setTitle(getString(R.string.add_expense));

            txtCatSrc.setText(getString(R.string.category));

        } else {

            if (isEditing)
                setTitle(getString(R.string.edit_income));
            else
                setTitle(getString(R.string.add_income));

            txtCatSrc.setText(getString(R.string.source));

        }





        catsSrcs = new ArrayList<>();

        simpleDateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
        selectedDate = Calendar.getInstance();

        if (isEditing) {
            // Load data from Intent
            currentTransaction = intent.getParcelableExtra(TRANSACTION_KEY);

            etxtAmount.setText(String.valueOf(currentTransaction.getAmount()));

            selectedDate.set(currentTransaction.getYear(), currentTransaction.getMonth(), currentTransaction.getDay());
            updateTextDate();


            etxtNote.setText(currentTransaction.getNote());

            btnAdd.setText(getString(R.string.save));
        } else {
            selectedDate.setTimeInMillis(System.currentTimeMillis());
            updateTextDate();
        }

        datePickerDialog = new DatePickerDialog(this, this, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));

        getLoaderManager().restartLoader(0, null, this);

    }


    // Get category position in spinner from its id.
    int getCatSrcPosFromId(int catId) {
        for (int i = 0; i < catsSrcs.size(); i++) {
            if (catsSrcs.get(i).getId() == catId)
                return i;
        }
        return -1;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (type == Transaction.EXPENSE_TYPE)
            return new CursorLoader(this, FinancialContract.CategoryEntry.CONTENT_URI, null, null, null, null);
        else
            return new CursorLoader(this, FinancialContract.SourceEntry.CONTENT_URI, null, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            if (type == CategorySource.TYPE_CAT) {
                do {
                    CategorySource categorySource = CategorySource.categoryFromCursor(cursor);
                    catsSrcs.add(categorySource);
                } while (cursor.moveToNext());

            }else{
                do {
                    CategorySource categorySource = CategorySource.sourceFromCursor(cursor);
                    catsSrcs.add(categorySource);
                } while (cursor.moveToNext());

            }
        }

        String newCatSrc;
        if (type == Transaction.EXPENSE_TYPE) {
            newCatSrc = getString(R.string.new_category);
        } else {
            newCatSrc = getString(R.string.new_source);
        }

        CategorySource categorySource = new CategorySource(-1, newCatSrc);

        catsSrcs.add(categorySource);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, catsSrcs);
        spinCat.setAdapter(arrayAdapter);

        if (isEditing)
            spinCat.setSelection(getCatSrcPosFromId(currentTransaction.getSource_category()));


        // If there is saved instance state for selected categorySource restore it
        if (savedCatPos != -1) {
            spinCat.setSelection(savedCatPos);
        }

        spinCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (catsSrcs.get(i).getId() == -1) {
                    addCategorySourceDialog = new AddCategorySourceDialog(AddEditTransactionActivity.this,
                            type == Transaction.EXPENSE_TYPE ? CategorySource.TYPE_CAT
                                    : CategorySource.TYPE_SRC,AddEditTransactionActivity.this);

                    addCategorySourceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            spinCat.setSelection(0);
                        }
                    });
                    addCategorySourceDialog.show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onCategoryAdded(CategorySource categorySource) {
        int newCatIndex = catsSrcs.size() - 1;
        CategorySource newCat = catsSrcs.get(newCatIndex);
        catsSrcs.set(newCatIndex, categorySource);
        catsSrcs.add(newCat);
        ((ArrayAdapter) spinCat.getAdapter()).notifyDataSetChanged();
        spinCat.setSelection(newCatIndex);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(FinancialContract.CategoryEntry.NAME)) {
            String name = savedInstanceState.getString(FinancialContract.CategoryEntry.NAME);
            addCategorySourceDialog = new AddCategorySourceDialog(AddEditTransactionActivity.this,
                    type == Transaction.EXPENSE_TYPE ? CategorySource.TYPE_CAT
                            : CategorySource.TYPE_SRC,this);
            addCategorySourceDialog.show();
            addCategorySourceDialog.setName(name);
        }

        int year = savedInstanceState.getInt(YEAR_KEY);
        int month = savedInstanceState.getInt(MONTH_KEY);
        int day = savedInstanceState.getInt(DAY_KEY);

        String note = savedInstanceState.getString(NOTE_KEY);

        etxtNote.setText(note);

        float amount = savedInstanceState.getFloat(AMOUNT_KEY);

        savedCatPos = savedInstanceState.getInt(CATEGORY_KEY);

        selectedDate.set(year, month, day);
        updateTextDate();

        etxtAmount.setText(amount + "");


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (addCategorySourceDialog != null && addCategorySourceDialog.isShowing())
            outState.putString(FinancialContract.CategoryEntry.NAME, addCategorySourceDialog.getName());

        outState.putInt(YEAR_KEY, selectedDate.get(Calendar.YEAR));
        outState.putInt(MONTH_KEY, selectedDate.get(Calendar.MONTH));
        outState.putInt(DAY_KEY, selectedDate.get(Calendar.DAY_OF_MONTH));

        outState.putFloat(AMOUNT_KEY, Float.parseFloat(etxtAmount.getText().toString()));
        outState.putInt(CATEGORY_KEY, spinCat.getSelectedItemPosition());
        outState.putString(NOTE_KEY, etxtNote.getText().toString());

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        selectedDate.set(year, month, day);
        updateTextDate();
    }

    void updateTextDate() {
        txtSelectedDate.setText(simpleDateFormat.format(selectedDate.getTime()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ibtnSelectDate:
                datePickerDialog.show();
                break;
            case R.id.btnAdd:

                float amount = Float.parseFloat(etxtAmount.getText().toString());
                int catId = catsSrcs.get(spinCat.getSelectedItemPosition()).getId();
                String note = etxtNote.getText().toString();
                int day = selectedDate.get(Calendar.DAY_OF_MONTH);
                int month = selectedDate.get(Calendar.MONTH);
                int year = selectedDate.get(Calendar.YEAR);

                if (amount == 0.0f) {
                    Toast.makeText(this, getString(R.string.amount_cant_be_zero), Toast.LENGTH_LONG).show();
                    return;
                }


                // Update Balance

                float balance = SharedPreferencesManager.getBalance(this);

                if (type == Transaction.EXPENSE_TYPE) {
                    if (isEditing) {
                        balance += currentTransaction.getAmount(); // Remove the old amount
                    }

                    balance -= amount;

                } else {
                    if (isEditing) {
                        balance -= currentTransaction.getAmount(); // Remove the old amount

                    }

                    balance += amount;

                }


                SharedPreferencesManager.setBalance(this, balance);


                if (!isEditing) {

                    Transaction transaction = new Transaction(-1/*Temporary*/, type, catId, amount, note, day, month, year);

                    if (Utils.isConnected(this)) {

                        String firebaseReference = transaction.saveNewToFireBase(this); // Store it in firebase

                        transaction.setFirebaseReference(firebaseReference); // Set the firebase reference to store it in SQLite

                    } else {
                        transaction.setSavedToFirebase(false);
                    }
                    ContentValues contentValues = transaction.toContentValues();


                    Uri uri = getContentResolver().insert(FinancialContract.TransactionEntry.CONTENT_URI, contentValues);


                    if (Utils.isConnected(this)) {
                        int id = (int) FinancialContract.getIdFromUri(uri); // Get the id from the insert uri



                        transaction.setId(id); // Update the id
                        transaction.updateToFirebase(this); // Update the firebase id

                    }

                } else {

                    currentTransaction.setAmount(amount);
                    currentTransaction.setSource_category(catId);
                    currentTransaction.setNote(note);
                    currentTransaction.setDay(day);
                    currentTransaction.setMonth(month);
                    currentTransaction.setYear(year);


                    if (Utils.isConnected(this)) {
                        currentTransaction.updateToFirebase(this);
                    } else {
                        currentTransaction.setUpdatedInFirebase(false);
                    }

                    getContentResolver().update(FinancialContract.TransactionEntry.buildTransactionIdUri(currentTransaction.getId()), currentTransaction.toContentValues(), null, null);
                }


                sendBroadcast(new Intent(AddEditTransactionActivity.this,AppWidget.class));

                finish();

                break;

        }
    }
}
