package com.shamdroid.myfinancialassistant.Models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.shamdroid.myfinancialassistant.data.FinancialContract;

import java.util.Calendar;

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
        note = in.readString();
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

    public static Transaction fromCursor(Cursor cursor) {

        int id = cursor.getInt(FinancialContract.TransactionEntry.ID_INDEX);
        int type = cursor.getInt(FinancialContract.TransactionEntry.TYPE_INDEX);
        int catSrcId = cursor.getInt(FinancialContract.TransactionEntry.SOURCE_CATEFORY_INDEX);
        float amount = cursor.getFloat(FinancialContract.TransactionEntry.AMOUNT_INDEX);
        String note = cursor.getString(FinancialContract.TransactionEntry.NOTE_INDEX);
        int day = cursor.getInt(FinancialContract.TransactionEntry.DAY_INDEX);
        int month = cursor.getInt(FinancialContract.TransactionEntry.MONTH_INDEX);
        int year = cursor.getInt(FinancialContract.TransactionEntry.YEAR_INDEX);

        return new Transaction(id, type, catSrcId, amount, note, day, month, year);
    }

    public Calendar getCalendar(){

        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        return calendar;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FinancialContract.TransactionEntry.AMOUNT, amount);
        contentValues.put(FinancialContract.TransactionEntry.TYPE, type);
        contentValues.put(FinancialContract.TransactionEntry.SOURCE_CATEGORY, categorySourceId);
        contentValues.put(FinancialContract.TransactionEntry.NOTE,note);
        contentValues.put(FinancialContract.TransactionEntry.DAY, day);
        contentValues.put(FinancialContract.TransactionEntry.MONTH, month);
        contentValues.put(FinancialContract.TransactionEntry.YEAR, year);
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
        parcel.writeString(note);
        parcel.writeInt(day);
        parcel.writeInt(month);
        parcel.writeInt(year);
    }
}
