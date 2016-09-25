package com.shamdroid.myfinancialassistant.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.FirebaseUtils;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohammad on 20/09/16.
 */

public class Transaction implements Parcelable {


    public final static int INCOME_TYPE = 0;
    public final static int EXPENSE_TYPE = 1;

    int id;
    int type;
    int categorySourceId;
    float amount;
    String note;
    int day, month, year;

    String firebaseReference;

    boolean savedToFirebase;
    boolean updatedInFirebase;


    public Transaction(int id, int type, int categorySourceId, float amount, String note, int day, int month, int year, String firebaseReference, boolean savedToFirebase, boolean updatedInFirebase) {
        this.id = id;
        this.type = type;
        this.categorySourceId = categorySourceId;
        this.amount = amount;
        this.note = note;
        this.day = day;
        this.month = month;
        this.year = year;
        this.firebaseReference = firebaseReference;
        this.savedToFirebase = savedToFirebase;
        this.updatedInFirebase = updatedInFirebase;
    }


    public Transaction(int id, int type, int categorySourceId, float amount, String note, int day, int month, int year) {
        this.id = id;
        this.type = type;
        this.categorySourceId = categorySourceId;
        this.amount = amount;
        this.note = note;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Map<String, Object> toMap() {

        Map<String, Object> map = new HashMap<>();

        map.put(FinancialContract.TransactionEntry.ID, id);
        map.put(FinancialContract.TransactionEntry.TYPE, type);
        map.put(FinancialContract.TransactionEntry.SOURCE_CATEGORY, categorySourceId);
        map.put(FinancialContract.TransactionEntry.AMOUNT, amount);
        map.put(FinancialContract.TransactionEntry.NOTE, note);
        map.put(FinancialContract.TransactionEntry.DAY, day);
        map.put(FinancialContract.TransactionEntry.MONTH, month);
        map.put(FinancialContract.TransactionEntry.YEAR, year);

        return map;
    }

    public String saveNewToFireBase(Context context) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();



        DatabaseReference databaseReference = firebaseDatabase.getReference();

        DatabaseReference users = databaseReference.child(FirebaseUtils.USERS_CHILD);
        DatabaseReference user = users.child(SharedPreferencesManager.getFirebaseUserId(context));

        DatabaseReference transactions = user.child(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE);

        DatabaseReference newTransition = transactions.push();

        newTransition.setValue(toMap());


        return newTransition.getKey();
    }



    public void updateToFirebase(Context context){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference();

        DatabaseReference users = databaseReference.child(FirebaseUtils.USERS_CHILD);
        DatabaseReference user = users.child(SharedPreferencesManager.getFirebaseUserId(context));

        DatabaseReference transactions = user.child(FinancialContract.TransactionEntry.TRANSACTIONS_TABLE);

        DatabaseReference selectedTransaction = transactions.child(firebaseReference);

        selectedTransaction.setValue(toMap());
    }



    public String getFirebaseReference() {
        return firebaseReference;
    }

    public void setFirebaseReference(String firebaseReference) {
        this.firebaseReference = firebaseReference;
    }

    public boolean isSavedToFirebase() {
        return savedToFirebase;
    }

    public void setSavedToFirebase(boolean savedToFirebase) {
        this.savedToFirebase = savedToFirebase;
    }

    public boolean isUpdatedInFirebase() {
        return updatedInFirebase;
    }

    public void setUpdatedInFirebase(boolean updatedInFirebase) {
        this.updatedInFirebase = updatedInFirebase;
    }

    protected Transaction(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        categorySourceId = in.readInt();
        amount = in.readFloat();
        note = in.readString();
        day = in.readInt();
        month = in.readInt();
        year = in.readInt();
        firebaseReference = in.readString();
        savedToFirebase = in.readByte() != 0;
        updatedInFirebase = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(type);
        dest.writeInt(categorySourceId);
        dest.writeFloat(amount);
        dest.writeString(note);
        dest.writeInt(day);
        dest.writeInt(month);
        dest.writeInt(year);
        dest.writeString(firebaseReference);
        dest.writeByte((byte) (savedToFirebase ? 1 : 0));
        dest.writeByte((byte) (updatedInFirebase ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public static Transaction fromCursor(Cursor cursor) {

        int id = cursor.getInt(FinancialContract.TransactionEntry.ID_INDEX);
        int type = cursor.getInt(FinancialContract.TransactionEntry.TYPE_INDEX);
        int catSrcId = cursor.getInt(FinancialContract.TransactionEntry.SOURCE_CATEFORY_INDEX);
        float amount = cursor.getFloat(FinancialContract.TransactionEntry.AMOUNT_INDEX);
        String note = cursor.getString(FinancialContract.TransactionEntry.NOTE_INDEX);
        int day = cursor.getInt(FinancialContract.TransactionEntry.DAY_INDEX);
        int month = cursor.getInt(FinancialContract.TransactionEntry.MONTH_INDEX);
        int year = cursor.getInt(FinancialContract.TransactionEntry.YEAR_INDEX);
        String firebaseReference = cursor.getString(FinancialContract.TransactionEntry.FIREBASE_REFERENCE_INDEX);
        boolean savedToFirebase = cursor.getInt(FinancialContract.TransactionEntry.SAVED_IN_FIREBASE_INDEX) == 1 ? true : false;
        boolean updatedInFirebase = cursor.getInt(FinancialContract.TransactionEntry.UPDATED_IN_FIREBASE_INDEX) == 1 ? true : false;

        return new Transaction(id, type, catSrcId, amount, note, day, month, year, firebaseReference, savedToFirebase, updatedInFirebase);
    }

    public Calendar getCalendar() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FinancialContract.TransactionEntry.AMOUNT, amount);
        contentValues.put(FinancialContract.TransactionEntry.TYPE, type);
        contentValues.put(FinancialContract.TransactionEntry.SOURCE_CATEGORY, categorySourceId);
        contentValues.put(FinancialContract.TransactionEntry.NOTE, note);
        contentValues.put(FinancialContract.TransactionEntry.DAY, day);
        contentValues.put(FinancialContract.TransactionEntry.MONTH, month);
        contentValues.put(FinancialContract.TransactionEntry.YEAR, year);
        contentValues.put(FinancialContract.TransactionEntry.FIREBASE_REFERENCE, firebaseReference);
        contentValues.put(FinancialContract.TransactionEntry.SAVED_IN_FIREBASE, savedToFirebase ? 1 : 0);
        contentValues.put(FinancialContract.TransactionEntry.UPDATE_IN_FIREBASE, updatedInFirebase?1:0);
        return contentValues;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getCategorySourceId() {
        return categorySourceId;
    }

    public float getAmount() {
        return amount;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCategorySourceId(int categorySourceId) {
        this.categorySourceId = categorySourceId;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }


}
