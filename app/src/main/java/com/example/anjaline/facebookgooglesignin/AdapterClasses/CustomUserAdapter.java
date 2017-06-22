package com.example.anjaline.facebookgooglesignin.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anjaline.facebookgooglesignin.PojoClasses.UserData;
import com.example.anjaline.facebookgooglesignin.R;

import java.util.ArrayList;

/**
 * Created by anjaline on 21/6/17.
 */

public class CustomUserAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserData> userDataArrayList;

    public CustomUserAdapter(Context context, ArrayList<UserData> userDataArrayList) {
        this.mContext = context;
        this.userDataArrayList = userDataArrayList;
    }

    @Override
    public int getCount() {
        return userDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return userDataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_row_list, null);
        }
        TextView txtVwID = (TextView) convertView.findViewById(R.id.item_row_txt_id);
        TextView txtVwName = (TextView) convertView.findViewById(R.id.item_row_txt_name);
        TextView txtVwEmail = (TextView) convertView.findViewById(R.id.item_row_txt_email);

        UserData userData = userDataArrayList.get(position);
        txtVwID.setText(userData.getUser_id());
        txtVwName.setText(userData.getUser_name());
        txtVwEmail.setText(userData.getUser_email());

        return convertView;
    }
}
