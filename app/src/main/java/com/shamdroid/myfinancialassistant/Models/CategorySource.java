package com.shamdroid.myfinancialassistant.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shamdroid.myfinancialassistant.data.FinancialContract;
import com.shamdroid.myfinancialassistant.data.FirebaseUtils;
import com.shamdroid.myfinancialassistant.data.SharedPreferencesManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohammad on 20/09/16.
 */

public class CategorySource {

    public static final int TYPE_SRC = 0;
    public static final int TYPE_CAT = 1;

    int id;
    String name;
    int type;

    String firebaseReference;

    boolean savedToFirebase = true ;
    boolean updatedInFirebase = true ;

    public String getFirebaseReference() {
        return firebaseReference;
    }

    public CategorySource(int id, String name, int type, String firebaseReference, boolean savedToFirebase, boolean updatedInFirebase) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.firebaseReference = firebaseReference;
        this.savedToFirebase = savedToFirebase;
        this.updatedInFirebase = updatedInFirebase;
    }

    public void setFirebaseReference(String firebaseReference) {
        this.firebaseReference = firebaseReference;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CategorySource(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public Map<String, Object> toMap() {

        Map<String, Object> map = new HashMap<>();

        if (type == TYPE_CAT) {
            map.put(FinancialContract.CategoryEntry.ID, id);
            map.put(FinancialContract.CategoryEntry.NAME, name);
        } else {
            map.put(FinancialContract.SourceEntry.ID, id);
            map.put(FinancialContract.SourceEntry.NAME, name);
        }

        return map;
    }

    public String saveNewToFirebase(Context context) {


        if (type == TYPE_CAT) {
            DatabaseReference categories = FirebaseUtils.getCategoriesRef(context);

            DatabaseReference newCategory = categories.push();
            newCategory.setValue(toMap());
            return newCategory.getKey();
        } else {
            DatabaseReference sources = FirebaseUtils.getSourcesRef(context);

            DatabaseReference newSource = sources.push();
            newSource.setValue(toMap());
            return newSource.getKey();

        }
    }

    public void updateFirebase (Context context){


        if (type == TYPE_CAT) {


            DatabaseReference selectedCategory = FirebaseUtils.getCategoryRef(context,firebaseReference);
            selectedCategory.setValue(toMap());
        } else {

            DatabaseReference selectedSource = FirebaseUtils.getSourceRef(context,firebaseReference);
            selectedSource.setValue(toMap());

        }
    }

    public void deleteFromFirebase(Context context){

        if (firebaseReference==null || firebaseReference.length() == 0)
            return;

        if (type == TYPE_CAT) {


            DatabaseReference selectedCategory = FirebaseUtils.getCategoryRef(context,firebaseReference);
            selectedCategory.removeValue();
        } else {

            DatabaseReference selectedSource = FirebaseUtils.getSourceRef(context,firebaseReference);
            selectedSource.removeValue();

        }


    }

    public ContentValues toContentValues (){
        ContentValues contentValues = new ContentValues();
        if(type == TYPE_CAT){

            contentValues.put(FinancialContract.CategoryEntry.NAME,name);
            contentValues.put(FinancialContract.CategoryEntry.FIREBASE_REFERENCE,firebaseReference);
            contentValues.put(FinancialContract.CategoryEntry.SAVED_IN_FIREBASE,savedToFirebase?1:0);
            contentValues.put(FinancialContract.CategoryEntry.UPDATE_IN_FIREBASE,updatedInFirebase);
        }else{
            contentValues.put(FinancialContract.SourceEntry.NAME,name);
            contentValues.put(FinancialContract.SourceEntry.FIREBASE_REFERENCE,firebaseReference);
            contentValues.put(FinancialContract.SourceEntry.SAVED_IN_FIREBASE,savedToFirebase?1:0);
            contentValues.put(FinancialContract.SourceEntry.UPDATE_IN_FIREBASE,updatedInFirebase);

        }
        return contentValues;
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

    public static CategorySource categoryFromCursor(Cursor cursor) {
        int id = cursor.getInt(FinancialContract.CategoryEntry.ID_INDEX);
        String name = cursor.getString(FinancialContract.CategoryEntry.NAME_INDEX);
        String firebaseRef = cursor.getString(FinancialContract.CategoryEntry.FIREBASE_REFERENCE_INDEX);
        boolean savedToFirebase = cursor.getInt(FinancialContract.CategoryEntry.SAVED_IN_FIREBASE_INDEX) == 1;
        boolean updatedToFirebase = cursor.getInt(FinancialContract.CategoryEntry.UPDATED_IN_FIREBASE) == 1;

        return new CategorySource(id, name,TYPE_CAT,firebaseRef,savedToFirebase,updatedToFirebase);
    }


    public static CategorySource sourceFromCursor(Cursor cursor) {
        int id = cursor.getInt(FinancialContract.SourceEntry.ID_INDEX);
        String name = cursor.getString(FinancialContract.SourceEntry.NAME_INDEX);
        String firebaseRef = cursor.getString(FinancialContract.SourceEntry.FIREBASE_REFERENCE_INDEX);
        boolean savedToFirebase = cursor.getInt(FinancialContract.SourceEntry.SAVED_IN_FIREBASE_INDEX) == 1;
        boolean updatedToFirebase = cursor.getInt(FinancialContract.SourceEntry.UPDATED_IN_FIREBASE) == 1;

        return new CategorySource(id, name,TYPE_SRC,firebaseRef,savedToFirebase,updatedToFirebase);
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
