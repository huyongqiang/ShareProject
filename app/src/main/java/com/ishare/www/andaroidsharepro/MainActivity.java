package com.ishare.www.andaroidsharepro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity extends AppCompatActivity {
    public static IWXAPI api;
    private String WXAPP_ID="";//申请到的APPID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api= WXAPIFactory.createWXAPI(this,WXAPP_ID,true);
        final SendAuth.Req req = new SendAuth.Req();
        req.scope="snsapi_userinfo";
        req.state="none";
        api.sendReq(req);
    }
}
