package com.ishare.www.andaroidsharepro.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.ishare.www.andaroidsharepro.R;

import java.util.Timer;
import java.util.TimerTask;


public class HandlerDemo extends AppCompatActivity {

    private ImageView imageView;
    //图片源
    private int[] imageSour=new int[]
            {
                    R.drawable.image1,
                    R.drawable.image2,
                    R.drawable.image3
            };
private int currentImg=0;//当前图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_demo);
        imageView = (ImageView) findViewById(R.id.imageView);


        //定时器，每3秒发送一次消息
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(0x1111);
            }
        }, 0, 3000);
    }

Handler myHandler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what==0x1111) {
            //接收到消息之后更换图片源
imageView.setImageResource(imageSour[currentImg++ % imageSour.length]);
        } else if (msg.what==0x1112) {
        }

    }
};

}
