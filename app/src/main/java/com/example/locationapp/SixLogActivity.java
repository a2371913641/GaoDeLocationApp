package com.example.locationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.locationapp.util.GongGongZiYuan;
import com.example.locationapp.util.ReceiveListener;

public class SixLogActivity extends AppCompatActivity {

    private EditText accountEditText,adminEditText;
    private Button logButton,registerButton;
    private ReceiveListener receiveListener;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six_log);
        init();
        setAccountAdminEdit(getIntent());
        setLogButton();
        setRegisterButton();
    }

    private void init(){
        handler=new Handler();
        accountEditText=(EditText) findViewById(R.id.six_account);
        adminEditText=(EditText) findViewById(R.id.six_admin);
        logButton=(Button) findViewById(R.id.six_log);
        registerButton=(Button) findViewById(R.id.six_register);

        receiveListener=new ReceiveListener() {
            @Override
            public void onReceive(String s) {
                String[] strings = s.split("/n");
                switch (strings[0]){
                    case "loginOK:":

                        Toast.makeText(SixLogActivity.this,strings[1],Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(SixLogActivity.this,SecondSetPersonalInformationActivity.class);
                        startActivity(intent);

                    case "loginNot:":
                        Toast.makeText(SixLogActivity.this,strings[1],Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void setLogButton(){
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accountEditText.getText()!=null&&!accountEditText.getText().equals(" ")&&
                adminEditText.getText()!=null&&!adminEditText.getText().equals(" ")) {
                    GongGongZiYuan.sendMsg("Login:/n" +accountEditText+"/n"+adminEditText);
                }else {
                    Toast.makeText(SixLogActivity.this,"请填写账号密码！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setRegisterButton(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SixLogActivity.this, ThirdlySignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setAccountAdminEdit(Intent intent){
        if(intent!=null){
            accountEditText.setText(intent.getStringExtra("account"));
            adminEditText.setText(intent.getStringExtra("admin"));
        }
    }
}