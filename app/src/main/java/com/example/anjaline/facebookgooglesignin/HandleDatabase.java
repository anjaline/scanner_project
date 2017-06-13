package com.example.anjaline.facebookgooglesignin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Button;

/**
 * Created by anjaline on 9/6/17.
 */

public class HandleDatabase extends SQLiteOpenHelper {
    public static final String Database_Name = "Scanner.db";
   public static final String Table_Name = " scanner_data ";
    public static final String id = "pid";
    public static final  String col_1 = "data";
    public static final String col_2 = "name";
    public static final String col_3 = "email";
    HandleDatabase myDb;
    public SQLiteDatabase db;


    public HandleDatabase(Context context) {
        super(context, Database_Name, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =( " CREATE TABLE " + Table_Name +" ( PID INTEGER PRIMARY KEY AUTOINCREMENT,DATA TEXT,NAME TEXT,EMAIL TEXT) ");
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + Table_Name);
        onCreate(db);

    }

    public boolean addTable(String pid,String data,String name,String email) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id,pid);
        values.put(col_1, data);
        values.put(col_2, name);
        values.put(col_3,email );

        //values.put(col_1, data);
       // db.insert(Table_Name, null, values);
        long result = db.insert(Table_Name,null ,values);
        if(result == -1)
            return false;
        else
            return true;

    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + Table_Name, null);
        return res;
    }

    public boolean updateData(String pid, String data, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(id, pid);
        contentValues.put(col_1, data);
        contentValues.put(col_2,name);
        contentValues.put(col_3, email);
        db.update(Table_Name, contentValues, "pid = ?" , new String[] {id});
        return true;
    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Table_Name, "pid= ?", new String[] {id});
    }
}
