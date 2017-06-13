package com.example.anjaline.facebookgooglesignin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static android.R.attr.value;

/**
 * Created by anjaline on 9/6/17.
 */

public class DisplaySelectedItem extends AppCompatActivity{
    TextView item;
    private String value_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewlistitem_scannerfile);
    item=(TextView)findViewById(R.id.item_scannerlist);
         Intent intent=getIntent();
         Bundle bundle=intent.getExtras();
         if(bundle!=null){
             value_=bundle.getString("item_key");
         }

         item.setText(value_);
}}
