package com.example.anjaline.facebookgooglesignin;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.example.anjaline.facebookgooglesignin.PojoClasses.UserData;
import com.example.anjaline.facebookgooglesignin.PojoClasses.UserScannedData;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by anjaline on 9/6/17.
 */

public class HandleDatabase extends SQLiteOpenHelper {
    public static final String Database_Name = "scanner.db";

    //Table1
    public static final String Table_Name = " scanner_data ";

    public static final String id = "pid";
    public static final String col_1 = "user_id";
    public static final String col_2 = "name";
    public static final String col_3 = "email";

    public static final String New_Table_Name = " new_data_table ";

    public static final String col_01 = "table_id";
    public static final String col_02 = "new_user_id";
    public static final String col_03 = "data_";
    public static final String col_04 = "content";
    HandleDatabase myDb;
    public SQLiteDatabase db;

    public HandleDatabase(Context context) {
        super(context, Database_Name, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_TABLE = (" CREATE TABLE " + Table_Name + " ( PID INTEGER PRIMARY KEY AUTOINCREMENT ,USER_ID TEXT ,NAME TEXT ,EMAIL TEXT) ");
            db.execSQL(CREATE_TABLE);
            String CREATE_new_TABLE = ("CREATE TABLE " + New_Table_Name + " ( TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT ,NEW_USER_ID TEXT,DATA_ TEXT ,CONTENT TEXT) ");
            db.execSQL(CREATE_new_TABLE);
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + Table_Name);
        db.execSQL("DROP TABLE IF EXISTS" + New_Table_Name);
        onCreate(db);

    }

    public boolean addTable(String user_id, String name, String email) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final String pref_id = preferences.getString("id", null);
            final String _name = preferences.getString("Name", null);
            final String _email = preferences.getString("Email", null);

            values.put(col_1, pref_id);
            values.put(col_2, _name);
            values.put(col_3, _email);
            long result = db.insert(Table_Name, null, values);
            if (result == -1)
                return false;
            else
                return true;
        } catch (Exception ex) {
            ex.getMessage();
        }

        return false;
    }

    public boolean addTableTwo(String data_) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final String user_id = preferences.getString("id", null);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(col_02, user_id);
            values.put(col_03, data_);
            // values.put(col_04, content);
            long result = db.insert(New_Table_Name, null, values);
            return result != -1;

        } catch (Exception ex) {
            ex.getMessage();
        }

        return false;

    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + Table_Name, null);

        return res;
    }

    ArrayList<UserData> getAllUserData() {
        ArrayList<UserData> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + Table_Name, null);
        if (res != null) {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                UserData userData = new UserData();
                userData.setUser_id(res.getString(1));
                userData.setUser_name(res.getString(2));
                userData.setUser_email(res.getString(3));
                arrayList.add(userData);
                res.moveToNext();
            }
            res.close();
            db.close();
        }
        return arrayList;
    }

    public Cursor getAllDataforTableTwo() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res1;
        res1 = db.rawQuery("select * from " + New_Table_Name, null);
        return res1;
    }

    ArrayList<UserScannedData> getScannedData() {
        ArrayList<UserScannedData> scanList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res1 = db.rawQuery("select * from " + New_Table_Name, null);
        if (res1 != null) {
            res1.moveToFirst();
            while (!res1.isAfterLast()) {
                UserScannedData userScannedData = new UserScannedData();
                userScannedData.setScan_id(res1.getString(0));
                userScannedData.setScan_user_id(res1.getString(1));
                userScannedData.setScan_data(res1.getString(2));
                scanList.add(userScannedData);
                res1.moveToNext();
            }
            res1.close();
            db.close();
        }
        return scanList;
    }


    public boolean updateData(String pid, String user_id, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1, user_id);
        contentValues.put(col_2, name);
        contentValues.put(col_3, email);
        db.update(Table_Name, contentValues, "pid = ? ", new String[]{pid});
        return true;
    }

    public boolean updateDataTwo(String table_id, String new_user_id, String data_, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_02, new_user_id);
        contentValues.put(col_03, data_);
        contentValues.put(col_04, content);
        db.update(New_Table_Name, contentValues, "table_id = ?", new String[]{table_id});
        return true;
    }

    public Integer deleteData(String pid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Table_Name, "pid= ? ", new String[]{pid});
    }

    public Integer deleteDataTwo(String table_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(New_Table_Name, "table_id= ? ", new String[]{table_id});
    }
}
