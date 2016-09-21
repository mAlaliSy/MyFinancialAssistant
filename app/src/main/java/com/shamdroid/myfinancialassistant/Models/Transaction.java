package com.shamdroid.myfinancialassistant.Models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.shamdroid.myfinancialassistant.data.FinancialContract;

/**
 * Created by mohammad on 20/09/16.
 */

public class Transaction implements Parcelable {


    public final static int INCOME_TYPE=0;
    public final static int EXPENSE_TYPE=1;

    int id;
    int type;
    int categorySourceId;
    float amount;
    int day,month,year;

    String note;



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

    protected Transaction(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        categorySourceId = in.readInt();
        amount = in.readFloat();
        day = in.readInt();
        month = in.readInt();
        year = in.readInt();
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

    public ContentValues toContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FinancialContract.TransactionEntry.AMOUNT,amount);
        contentValues.put(FinancialContract.TransactionEntry.TYPE,type);
        contentValues.put(FinancialContract.TransactionEntry.SOURCE_CATEGORY,categorySourceId);
        contentValues.put(FinancialContract.TransactionEntry.DAY,day);
        contentValues.put(FinancialContract.TransactionEntry.MONTH,month);
        contentValues.put(FinancialContract.TransactionEntry.YEAR,year);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(type);
        parcel.writeInt(categorySourceId);
        parcel.writeFloat(amount);
        parcel.writeInt(day);
        parcel.writeInt(month);
        parcel.writeInt(year);
    }
}
