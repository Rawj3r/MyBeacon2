package com.equidais.mybeacon.controller.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.equidais.mybeacon.R;
import com.equidais.mybeacon.controller.main.MainActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRCode extends AppCompatActivity {


    private Toolbar toolbar;

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

    public final static String STR = "Your discount for this product is R5.00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ImageView imageView = (ImageView) findViewById(R.id.imageQRCode);

        try {
            Bitmap bitmap = encodeAsBitmap(STR);
            imageView.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }

    }

    Bitmap encodeAsBitmap(String str) throws WriterException{
        BitMatrix result;
        try{
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        }catch (IllegalArgumentException e){
            // unsupported format
            return null;
        }

        int w = result.getWidth();
        int h = result.getHeight();

        int[] pixels = new int[w * h];

        for (int y =0; y<h; y++){
            int offset = y * w;
            for (int x = 0; x < w; x++){
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.navigate){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void finish() {
//        super.finish();
//    }
}

