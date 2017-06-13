package com.example.anjaline.facebookgooglesignin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.ViewUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.anjaline.facebookgooglesignin.R.id.btn_add;

/**
 * Created by anjaline on 12/6/17.
 */

public class Database_Intermediate extends AppCompatActivity {
    //HandleDatabase myDb;
    HandleDatabase myDb=new HandleDatabase(this);
    EditText s_id, sname, sdata, semail;
    TextView id, name, data, mail;
    Button add,delete,show,update;
    private String name_=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database);


        s_id=(EditText)findViewById(R.id.edittext_id);
        sname=(EditText)findViewById(R.id.edittext_name);
        sdata=(EditText)findViewById(R.id.edittext_data);
        semail=(EditText)findViewById(R.id.edittext1);
        id=(TextView)findViewById(R.id.textView_id);
        name=(TextView)findViewById(R.id.textView2);
        data=(TextView)findViewById(R.id.textView_data);
        mail=(TextView)findViewById(R.id.text_email);
        add=(Button)findViewById(btn_add);
        delete=(Button)findViewById(R.id.delete_btn);
        show=(Button)findViewById(R.id.view_all);
        update=(Button)findViewById(R.id.update_btn);
           Intent i=getIntent();
        Bundle bundle=i.getExtras();
        if(bundle!=null) {
             name_ = bundle.getString("details");
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInserted = myDb.addTable(s_id.getText().toString(), sdata.getText().toString(), sname.getText().toString(), semail.getText().toString());
                if(isInserted == true)
                    Toast.makeText(Database_Intermediate.this,"Data Inserted",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(Database_Intermediate.this,"Data not Inserted",Toast.LENGTH_LONG).show();

            }
        });

          update.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  boolean isUpdate = myDb.updateData(s_id.getText().toString(),
                          sname.getText().toString(),
                          sdata.getText().toString(),semail.getText().toString());
                  if(isUpdate == true)
                      Toast.makeText(Database_Intermediate.this,"Data Update",Toast.LENGTH_LONG).show();
                  else
                      Toast.makeText(Database_Intermediate.this,"Data not Updated",Toast.LENGTH_LONG).show();
              }

          });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer deletedRows = myDb.deleteData(s_id.getText().toString());
                if(deletedRows > 0)
                    Toast.makeText(Database_Intermediate.this,"Data Deleted",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(Database_Intermediate.this,"Data not Deleted",Toast.LENGTH_LONG).show();
            }

        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor res = myDb.getAllData();
                if(res.getCount() == 0) {
                    // show message
                    showMessage("Error","Nothing found");
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("Id :"+ res.getString(0)+"\n");
                    buffer.append("Name :"+ res.getString(1)+"\n");
                    buffer.append("data :"+ res.getString(2)+"\n");
                    buffer.append("email :"+ res.getString(3)+"\n\n");
                }

                // Show all data
                showMessage("Data",buffer.toString());
            }
        });


    }
    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
}

}