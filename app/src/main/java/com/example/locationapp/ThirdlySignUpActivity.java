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

public class ThirdlySignUpActivity extends AppCompatActivity {
    private String TAG="ThirdlySignInActivity";

    private Handler handler;
    private EditText accountEditText,adminEditText,nameEditText, confirmAdminEditText;
    private Button backButton,signUpButton;
    private ReceiveListener receiveListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdly_sign_in);
        init();
        setSignInButton();
    }

    private void init(){
        handler=new Handler();
        accountEditText=(EditText) findViewById(R.id.thirdly_account);
        adminEditText=(EditText) findViewById(R.id.thirdly_admin);
        confirmAdminEditText =(EditText) findViewById(R.id.thirdly_queren_admin);
        nameEditText=(EditText) findViewById(R.id.thirdly_name);
        backButton=(Button) findViewById(R.id.thirdly_Back_button);
        signUpButton=(Button) findViewById(R.id.thirdly_sign_in_button);
        setReceiveListener();
    }

    private void setSignInButton(){
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!accountEditText.getText().equals(" ")&&accountEditText.getText()!=null&&
                !adminEditText.getText().equals("")&&adminEditText.getText()!=null&&
                !nameEditText.getText().equals("")&&nameEditText.getText()!=null&&
                !confirmAdminEditText.getText().equals("")&&confirmAdminEditText.getText()!=null){
                    if(confirmAdminEditText.getText().equals(adminEditText.getText())){
                        Toast.makeText(ThirdlySignUpActivity.this,"两次密码不一样！",Toast.LENGTH_SHORT).show();
                    }else {
                        GongGongZiYuan.sendMsg("signUp:/n" + accountEditText.getText() + "/n" + adminEditText.getText() +
                                "/n" + nameEditText.getText());
                    }
                }else{
                    Toast.makeText(ThirdlySignUpActivity.this,"请填写完整信息！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setReceiveListener(){
        receiveListener=new ReceiveListener() {
            @Override
            public void onReceive(String s) {
                String[] strings = s.split("/n");
                switch (strings[0]){
                    case "signUpOK:":
                        Toast.makeText(ThirdlySignUpActivity.this,strings[1],Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(ThirdlySignUpActivity.this,SixLogActivity.class);
                        intent.putExtra("account",accountEditText.getText());
                        intent.putExtra("admin",adminEditText.getText());
                        startActivity(intent);
                        break;

                    case "signUpNot:":
                        Toast.makeText(ThirdlySignUpActivity.this,strings[1],Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }


}