package com.example.anjaline.facebookgooglesignin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by anjaline on 9/6/17.
 */

public class DisplaySelectedItem extends AppCompatActivity{
    TextView item;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scan);
    item=(TextView)findViewById(R.id.item_scannerlist);

}}
