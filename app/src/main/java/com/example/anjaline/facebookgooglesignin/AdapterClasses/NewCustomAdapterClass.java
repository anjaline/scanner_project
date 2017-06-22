package com.example.anjaline.facebookgooglesignin.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anjaline.facebookgooglesignin.PojoClasses.UserData;
import com.example.anjaline.facebookgooglesignin.PojoClasses.UserScannedData;
import com.example.anjaline.facebookgooglesignin.R;

import java.util.ArrayList;

/**
 * Created by anjaline on 21/6/17.
 */

public class NewCustomAdapterClass extends BaseAdapter {
    private Context s_context;
    private ArrayList<UserScannedData> userScannedDataArrayList;

    public NewCustomAdapterClass(Context s_context, ArrayList<UserScannedData> userScannedDataArrayList) {
        this.s_context = s_context;
        this.userScannedDataArrayList = userScannedDataArrayList;
    }


    @Override
    public int getCount() {
        return userScannedDataArrayList.size();

    }

    @Override
    public Object getItem(int position) {

        return userScannedDataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) s_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_row_list, null);
        }
        TextView txtVwID = (TextView) convertView.findViewById(R.id.item_row_txt_id);
        TextView txtVwName = (TextView) convertView.findViewById(R.id.item_row_txt_name);
        TextView txtVwEmail = (TextView) convertView.findViewById(R.id.item_row_txt_email);

        UserScannedData userScannedData = userScannedDataArrayList.get(position);
        txtVwID.setText(userScannedData.getScan_id());
        txtVwName.setText(userScannedData.getScan_user_id());
        txtVwEmail.setText(userScannedData.getScan_data());

        return convertView;
    }
}
