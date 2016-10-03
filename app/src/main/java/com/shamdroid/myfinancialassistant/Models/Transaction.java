package com.shamdroid.myfinancialassistant.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.FirebaseUtils;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Transaction implements Parcelable {


    public final static int INCOME_TYPE = 0;
    public final static int EXPENSE_TYPE = 1;

    int _id;
    int type;
    int source_category;
    float amount;
    String note;
    int day, month, year;

    String firebaseReference;

    boolean savedToFirebase = true;
    boolean updatedInFirebase = true;


    public Transaction(){

    }

    public Transaction(int id, int type, int categorySourceId, float amount, String note, int day, int month, int year, String firebaseReference, boolean savedToFirebase, boolean updatedInFirebase) {
        this._id = id;
        this.type = type;
        this.source_category = categorySourceId;
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
        this._id = id;
        this.type = type;
        this.source_category = categorySourceId;
        this.amount = amount;
        this.note = note;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Map<String, Object> toMap() {

        Map<String, Object> map = new HashMap<>();

        map.put(FinancialContract.TransactionEntry.ID, _id);
        map.put(FinancialContract.TransactionEntry.TYPE, type);
        map.put(FinancialContract.TransactionEntry.SOURCE_CATEGORY, source_category);
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

        newTransition.keepSynced(true);

        return newTransition.getKey();
    }





    public void updateToFirebase(Context context){

        DatabaseReference selectedTransaction = FirebaseUtils.getTransitionRef(context,firebaseReference);

        selectedTransaction.setValue(toMap());
    }



    public void deleteFromFirebase(Context context){

        DatabaseReference databaseReference =  FirebaseUtils.getTransitionRef(context,firebaseReference);

        databaseReference.removeValue();

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
        _id = in.readInt();
        type = in.readInt();
        source_category = in.readInt();
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
        dest.writeInt(_id);
        dest.writeInt(type);
        dest.writeInt(source_category);
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
        boolean savedToFirebase = cursor.getInt(FinancialContract.TransactionEntry.SAVED_IN_FIREBASE_INDEX) == 1 ;
        boolean updatedInFirebase = cursor.getInt(FinancialContract.TransactionEntry.UPDATED_IN_FIREBASE_INDEX) == 1;

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
        contentValues.put(FinancialContract.TransactionEntry.SOURCE_CATEGORY, source_category);
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
        return _id;
    }

    public int getType() {
        return type;
    }

    public int getSource_category() {
        return source_category;
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
        this._id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSource_category(int source_category) {
        this.source_category = source_category;
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
