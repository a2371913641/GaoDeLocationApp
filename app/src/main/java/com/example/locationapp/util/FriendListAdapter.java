package com.example.locationapp.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.locationapp.FiveFriendActivity;
import com.example.locationapp.HaoYou;
import com.example.locationapp.MainActivity;
import com.example.locationapp.R;

import java.util.List;

public class FriendListAdapter extends BaseAdapter {

    private Context context;
    private List<HaoYou> haoYouList;

    public FriendListAdapter(Context context,List<HaoYou> haoYouList){
        this.context=context;
        this.haoYouList=haoYouList;
    }

    @Override
    public int getCount() {
        return haoYouList.size();
    }

    @Override
    public Object getItem(int position) {
        return haoYouList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=View.inflate(context, R.layout.five_firend_item_layout,null);
        }
        ImageView imageView=(ImageView) convertView.findViewById(R.id.five_friend_image);
        TextView nameTextView=(TextView) convertView.findViewById(R.id.five_friend_name);
        TextView accountTextView=(TextView) convertView.findViewById(R.id.five_friend_account);
        Button findButton=(Button)convertView.findViewById(R.id.five_find_friend_location_button) ;

        imageView.setImageResource(haoYouList.get(position).getImage());
        nameTextView.setText(haoYouList.get(position).getName());
        accountTextView.setText(haoYouList.get(position).getAccount());
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra("latitude",haoYouList.get(position).getLatitude());
                intent.putExtra("longitude",haoYouList.get(position).getLongitude());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
