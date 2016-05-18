
package com.ishare.www.andaroidsharepro.demo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ishare.www.andaroidsharepro.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowImgActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri imgUrl;
    private FileOutputStream b = null;
    private Bitmap bm;
    private  File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_img);

        imageView = (ImageView) findViewById(R.id.imageView);

    }

    public void gogo(View view) {
        switch (view.getId()) {
            case R.id.gallery:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,801);
                break;
            case R.id.camera:
                Intent intent2=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imgUrl=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                startActivityForResult(intent2,802);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 801://从相册获取图片
                Uri uri = data.getData();
                Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                String imgPath = cursor.getString(1);//获取图片路径
                cursor.close();
                 file = new File(imgPath);
                 bm = BitmapFactory.decodeFile(imgPath);
                imageView.setImageBitmap(bm);
                break;
            case 802://从照相机获取图片，先保存到内存卡再读出显示
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    Log.v("TestFile", "SD card is not avaiable/writeable right now.");
                    return;
                }
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

                file = new File("/sdcard/ishare/");
                file.mkdirs();// 创建文件夹，名称为pk4fun // 照片的命名，目标文件夹下，以当前时间数字串为名称，即可确保每张照片名称不相同。网上流传的其他Demo这里的照片名称都写死了，则会发生无论拍照多少张，后一张总会把前一张照片覆盖。细心的同学还可以设置这个字符串，比如加上“ＩＭＧ”字样等；然后就会发现sd卡中myimage这个文件夹下，会保存刚刚调用相机拍出来的照片，照片名称不会重复。
                String str = "userhead";
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");// 获取当前时间，进一步转化为字符串
                date = new Date(resultCode);
                str = format.format(date);
                String fileName = "/sdcard/ishare/" + str + ".jpg";

                // sendBroadcast(fileName);
                try {
                    b = new FileOutputStream(fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        b.flush();
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                 file = new File(fileName);
                  bm = BitmapFactory.decodeFile(fileName);
                imageView.setImageBitmap(bm);
                break;
        }

    }




}
