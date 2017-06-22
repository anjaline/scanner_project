package com.example.anjaline.facebookgooglesignin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.anjaline.facebookgooglesignin.R.id.btn_add;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by anjaline on 12/6/17.
 */

public class Database_Intermediate extends AppCompatActivity {
    //HandleDatabase myDb;
    HandleDatabase myDb = new HandleDatabase(this);
    EditText s_id, sname, sdata, semail;
    TextView id, name, data, mail;
    Button add, delete, show, update, show2;
    public String name_ = null;
    public String scan_value = null;
    public String media_data = null;
    public String media_id = null;
    public String media_name = null;
    public String media_email = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database);
        s_id = (EditText) findViewById(R.id.edittext_id);
        sname = (EditText) findViewById(R.id.edittext_name);
        sdata = (EditText) findViewById(R.id.edittext_data);
        semail = (EditText) findViewById(R.id.edittext1);
        id = (TextView) findViewById(R.id.textView_id);
        name = (TextView) findViewById(R.id.textView2);
        data = (TextView) findViewById(R.id.textView_data);
        mail = (TextView) findViewById(R.id.text_email);
        add = (Button) findViewById(btn_add);
        delete = (Button) findViewById(R.id.delete_btn);
        show = (Button) findViewById(R.id.view_all_table_1);
        show2 = (Button) findViewById(R.id.view_all_table_2);
        update = (Button) findViewById(R.id.update_btn);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            name_ = bundle.getString("details");
            scan_value = (String) bundle.get("scan");
            media_id = (String) bundle.get("user_id");
            media_name = (String) bundle.get("user_name");
            media_email = (String) bundle.get("user_email");

            //Toast.makeText(Database_Intermediate.this,scan_value.toString(),Toast.LENGTH_SHORT).show();
        }

                final String strName = sname.getText().toString().trim();
                final String strID = s_id.getText().toString().trim();
                final String strData = sdata.getText().toString().trim();
                 final String strEmail = semail.getText().toString().trim();

                insertIntoTable(strID, strData, strName,strEmail);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteIntoTable(s_id.getText().toString().trim());
                DeleteIntoTableTwo(s_id.getText().toString().trim());
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateIntoTable(s_id.getText().toString().trim(), sdata.getText().toString().trim(),
                        sname.getText().toString().trim(), semail.getText().toString().trim());
                UpdateIntoTableTwo(s_id.getText().toString().trim(), sdata.getText().toString().trim(),
                        sname.getText().toString().trim(), semail.getText().toString().trim());
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllfor_tableone();
            }
        });
        show2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllfor_tabletwo();
            }
        });

    }

//    public void save(String id_data) {
//        SharedPreferences mPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = mPrefs.edit();
//        editor.putString("id", id_data);
//        editor.commit();
//    }

    public void insertIntoTable(String id, String userID, String name, String email) {

        if (myDb.addTable( userID, name, email)) {
            Toast.makeText(Database_Intermediate.this, "Data Inserted in table 1", Toast.LENGTH_LONG).show();
          //  media_data = userID;
            insertIntoTableTwo("", " ", " ", email);
        } else {
            Toast.makeText(Database_Intermediate.this, "Data not Inserted in table 1", Toast.LENGTH_LONG).show();
        }
    }

    public void insertIntoTableTwo(String table_id, String new_userID, String data, String data1) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String scan_value = preferences.getString("s_can", null);

        boolean result = myDb.addTableTwo( scan_value);
        if (result) {
            Toast.makeText(Database_Intermediate.this, "Data Inserted in table two", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Database_Intermediate.this, "Data not Inserted", Toast.LENGTH_LONG).show();
        }
    }

    public void UpdateIntoTable(String id, String userID, String name, String email) {
        if (myDb.updateData(id, userID, name, email))
            Toast.makeText(Database_Intermediate.this, "Data Update", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(Database_Intermediate.this, "Data not Updated", Toast.LENGTH_LONG).show();
    }

    public void UpdateIntoTableTwo(String table_id, String new_userID, String data, String data1) {
        if (myDb.updateDataTwo(table_id, new_userID, data, data1))
            Toast.makeText(Database_Intermediate.this, "Data Update", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(Database_Intermediate.this, "Data not Updated", Toast.LENGTH_LONG).show();
    }


    public void DeleteIntoTable(String user_id) {
        int deletedRows = myDb.deleteData(user_id);
        if (deletedRows > 0)
            Toast.makeText(Database_Intermediate.this, "Data Deleted", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(Database_Intermediate.this, "Data not Deleted", Toast.LENGTH_LONG).show();
    }

    public void DeleteIntoTableTwo(String table_id) {
        int deletedRows = myDb.deleteDataTwo(table_id);
        if (deletedRows > 0)
            Toast.makeText(Database_Intermediate.this, "Data Deleted", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(Database_Intermediate.this, "Data not Deleted", Toast.LENGTH_LONG).show();
    }

    public void showAllfor_tableone() {

        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Id :        " + res.getString(0) + "\n");
            buffer.append("User_id  :  " + res.getString(1) + "\n");
            buffer.append("Name :      " + res.getString(2) + "\n");
            buffer.append("email :     " + res.getString(3) + "\n\n");
        }

        showMessage("Data", buffer.toString());
    }

    public void showAllfor_tabletwo() {

        Cursor res = myDb.getAllDataforTableTwo();
        if (res.getCount() == 0) {
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Id :       " + res.getString(0) + "\n");
            buffer.append("User_id  : " + res.getString(1) + "\n");
            buffer.append("data:      " + res.getString(2) + "\n");
            buffer.append("Entry :    " + res.getString(3) + "\n\n");
        }

        showMessage("Data2", buffer.toString());
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

}