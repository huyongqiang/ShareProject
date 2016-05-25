package com.ishare.www.andaroidsharepro.Intent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ishare.www.andaroidsharepro.R;

public class Firstctivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstctivity);
    }

    public void gogo(View view) {
        switch (view.getId()) {
            case R.id.button6:
                Intent intent = new Intent();
                intent.setClass(Firstctivity.this, SecondActivity.class);//显示Intent
                startActivity(intent);
               // startActivityForResult(intent,100); 启动另外一个Activity获取结果
                break;
            case R.id.button7:
                Intent intent2 = new Intent();
                intent2.setAction("com.ishareAndroid.intent");//隐式Intent,没有指定要启动哪个组件,只要intent-filter符合com.ishareAndroid.intent的都会被触发启动，多个符合时需用户选择启动哪个
                startActivity(intent2);
                break;
            case R.id.button8:
                MyIntentService intentService=new MyIntentService();
                Intent intent3 = new Intent();
                intentService.startService(intent3);

                break;

        }
    }
}
