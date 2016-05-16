package com.ishare.www.andaroidsharepro.Login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ishare.www.andaroidsharepro.R;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends AppCompatActivity  implements PlatformActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ShareSDK.initSDK(this);

    }

    public void gogo(View v) {
        switch (v.getId()) {
            case R.id.loginbyqq:
                //QQ Login
                Platform qq= ShareSDK.getPlatform(QQ.NAME);
                //QQ q=new QQ(this);
                //qq.SSOSetting(true);
                qq.setPlatformActionListener(this);
                qq.authorize();//
                qq.showUser(null);

                break;
            case R.id.loginbywx:
                Platform wx=ShareSDK.getPlatform(Wechat.NAME);
                wx.setPlatformActionListener(this);
                wx.authorize();

                break;
            case R.id.loginbysina:
                Platform sina=ShareSDK.getPlatform(SinaWeibo.NAME);
                sina.setPlatformActionListener(this);
                sina.SSOSetting(true);//not use SSO login
               // sina.authorize();
                sina.showUser(null);// get user info

                break;
        }
    }


    @Override
    public void onCancel(Platform platform, int i) {

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Log.e("shareSdk", "shareSdk err"+throwable);
    }

    /**
     * 获取用户信息后的回调函数，可以在这里作跳信息处理和跳转
     * @param platform
     * @param i
     * @param res
     */
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> res) {
        //解析部分用户资料字段
        if (i == Platform.ACTION_USER_INFOR) {
            PlatformDb platDB = platform.getDb();//获取数平台数据DB
            //通过DB获取各种数据
            String token=platDB.getToken();
            String userGender= platDB.getUserGender();
            String userIcon= platDB.getUserIcon();
            String userId= platDB.getUserId();
            String userName=   platDB.getUserName();
            Log.e("shareSdk", "shareSdk success\n"+
                    "token=="+token+
                    "\nuserIcon=="+userIcon+
                    "\nuserGender=="+userGender+
                    "\nuserId=="+userId+
                    "\nuserName=="+userName);
        }
    }
}
