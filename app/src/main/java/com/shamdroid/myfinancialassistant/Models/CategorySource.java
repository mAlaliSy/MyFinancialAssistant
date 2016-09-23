package com.shamdroid.myfinancialassistant.Models;

import android.database.Cursor;

import com.shamdroid.myfinancialassistant.data.FinancialContract;

/**
 * Created by mohammad on 20/09/16.
 */

public class CategorySource {

    public static final int TYPE_SRC = 0;
    public static final int TYPE_CAT = 1;

    int id;
    String name;


    public CategorySource(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CategorySource fromCursor(Cursor cursor) {

        int id = cursor.getInt(FinancialContract.CategoryEntry.ID_INDEX);
        String name = cursor.getString(FinancialContract.CategoryEntry.NAME_INDEX);
        return new CategorySource(id, name);

    }


    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return name;
    }
}
