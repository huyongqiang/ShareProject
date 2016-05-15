package com.ishare.www.andaroidsharepro.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.ishare.www.andaroidsharepro.MainActivity;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    // IWXAPI 是第三方app和微信通信的openapi接口
    //private IWXAPI api;
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    private String code;
    private String tokenJson;//access_token串
    private boolean accesss_token_effective;//access_token是否有效
    private String openId;//openId
    private String refresh_token;
    private String access_token="";
     private String unionid;
    private String userInfoJson;//用户信息json串
    private String WXAPP_ID="";//申请到的APPID
    private String WXAppSecret="";//申请到的Secret



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_wxentry);
        MainActivity.api.handleIntent(getIntent(), this);


    }

    @Override
    public void onReq(BaseReq req) {
        Toast.makeText(this, "onReq方法被调用！", Toast.LENGTH_LONG).show();
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                //goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }

    }

    private void goToGetMsg() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
        finish();
    }



    @Override
    public void onResp(BaseResp baseResp) {
        Toast.makeText(this, "onResp方法被调用！", Toast.LENGTH_LONG).show();
        int result = 0;

        //String id=baseResp.openId;
        Log.e("ErrorCode", "结果---" + baseResp.errCode);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:

                switch (baseResp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的code,立马再去请求access_token
                        code = ((SendAuth.Resp) baseResp).code;
                        Log.e("ErrorCode", "code---" + code);
                        getAccess_token(code);

                        //获取用户信息
                        getUserInfo();


                        break;

                    case RETURN_MSG_TYPE_SHARE:
                        //AppData.showToast("微信分享成功");
                        Toast.makeText(this, "微信分享成功", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:

                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:

                break;
            default:

                break;
        }



    }

    /**
     * 通过code获取access_token
     * @param code
     * @return
     */
    private String getAccess_token(String code) {
        final String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WXAPP_ID + "&secret=" +WXAppSecret +
                "&code=" + code + "&grant_type=authorization_code";
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result =getURLResponse(url);
                tokenJson = result;
                Log.e("tokenJson", "tokenJson====" + tokenJson);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    access_token = jsonObject.getString("access_token");
                    refresh_token = jsonObject.getString("refresh_token");
                    openId = jsonObject.getString("openid");
                    unionid = jsonObject.getString("unionid");
                    Log.e("access_token", "access_token====" + access_token);
                    Thread.currentThread().sleep(1000);//毫秒
                } catch (JSONException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }


            }
        }).start();

        return tokenJson;
    }

    /**
     * 由于access_token有效期为两小时，所以进行下一步操作前最好进行一次检查
     * 判断token是否有效
     *
     * @param access_token
     * @param openId
     * @return
     */
    private boolean checkToken(String access_token, String openId) {
        final String url = "https://api.weixin.qq.com/sns/auth?access_token=" + access_token + "&openid=" + openId;

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = getURLResponse(url);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("errcode").equals("0")) {
                        //返回0表示accesss_token有效
                        accesss_token_effective=true;
                    }else if(jsonObject.getString("errcode").equals("40003")){
                        //返回40003表示accesss_token无效
                        accesss_token_effective=false;

                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }).start();
        return accesss_token_effective;
    }


    /**
     * 刷新重新获取accesss_token
     * @return
     */
    private String getRefresh_token(){
        final String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+WXAPP_ID+"&grant_type=refresh_token&refresh_token="+refresh_token;

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = getURLResponse(url);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                   access_token=jsonObject.getString("access_token");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }).start();

        return access_token;
    }

    private String getUserInfo() {
        //access_token为空，延时500毫秒，再回调自己，等上个线程获取access_token为空
        //此处还应该判断
        if (access_token.equals("")) {
            Timer timer=new Timer();//实例化Timer类
            timer.schedule(new TimerTask(){
                public void run(){
                    System.out.println("退出");
                    this.cancel();}},500);//五百毫秒
            getUserInfo();
        } else {
        final String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openId;

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = getURLResponse(url);
                userInfoJson = result;
                //打出用户信息
                Log.e("userInfo", "userInfo====" + userInfoJson);

            }
        }).start();
    }
        return userInfoJson;
    }


    /**
     * 获取指定URL的响应字符串
     * @param urlString
     * @return
     */
    public String getURLResponse(String urlString){
        HttpURLConnection conn = null; //连接对象
        InputStream is = null;
        String resultData = "";
        try {
            URL url = new URL(urlString); //URL对象
            conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接
            conn.setDoInput(true); //允许输入流，即允许下载
            conn.setDoOutput(true); //允许输出流，即允许上传
            conn.setUseCaches(false); //不使用缓冲
            conn.setRequestMethod("GET"); //使用get请求
            is = conn.getInputStream();   //获取输入流，此时才真正建立链接
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine  = "";
            while((inputLine = bufferReader.readLine()) != null){
                resultData += inputLine + "\n";
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(conn != null){
                conn.disconnect();
            }
        }

        return resultData;
    }





}



