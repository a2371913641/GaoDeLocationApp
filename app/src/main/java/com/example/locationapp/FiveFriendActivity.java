package com.example.locationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.locationapp.util.FriendListAdapter;
import com.example.locationapp.util.GongGongZiYuan;

public class FiveFriendActivity extends AppCompatActivity {

    private Button backButton;
    private TextView newFriendTextView;
    private ListView friendListView;
    private GongGongZiYuan gongGongZiYuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_friend);
        init();

    }

    private void init(){
        friendListView=(ListView) findViewById(R.id.five_friend_listview);
        newFriendTextView=(TextView) findViewById(R.id.five_new_friend_button);
        backButton=(Button) findViewById(R.id.five_back_button);
        gongGongZiYuan=new GongGongZiYuan();
        FriendListAdapter friendListAdapter=new FriendListAdapter(this,gongGongZiYuan.getHaoYouList());
        friendListView.setAdapter(friendListAdapter);
    }


}