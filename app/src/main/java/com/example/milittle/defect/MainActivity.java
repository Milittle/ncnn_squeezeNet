package com.example.milittle.defect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private DefectNcnn defectNcnn = new DefectNcnn();

    private static final int SELECT_IMAGE = 1;

    private TextView infoResult;
    private ImageView imageView;
    private Bitmap yourSelectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            initSqueezeNcnn();
        }
        catch (IOException e)
        {
            Log.e("MainActivity", "initSqueezeNcnn error");
        }

        infoResult = (TextView) findViewById(R.id.result);
        imageView = (ImageView) findViewById(R.id.imageView);

        Button buttonImage = (Button) findViewById(R.id.button1);
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, SELECT_IMAGE);
            }
        });

        Button buttonDetect = (Button) findViewById(R.id.button2);
        buttonDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (yourSelectedImage == null)
                    return;

                String result = defectNcnn.Detect(yourSelectedImage);

                if (result == null)
                {
                    infoResult.setText("detect failed");
                }
                else
                {
                    infoResult.setText(result);
                }
            }
        });
    }

    private void initSqueezeNcnn() throws IOException
    {
        byte[] param = null;
        byte[] bin = null;
        byte[] words = null;

        {
            InputStream assetsInputStream = getAssets().open("squeezenet_v1.1.param.bin");
            int available = assetsInputStream.available();
            param = new byte[available];
            int byteCode = assetsInputStream.read(param);
            assetsInputStream.close();
        }
        {
            InputStream assetsInputStream = getAssets().open("squeezenet_v1.1.bin");
            int available = assetsInputStream.available();
            bin = new byte[available];
            int byteCode = assetsInputStream.read(bin);
            assetsInputStream.close();
        }
        {
            InputStream assetsInputStream = getAssets().open("synset_words.txt");
            int available = assetsInputStream.available();
            words = new byte[available];
            int byteCode = assetsInputStream.read(words);
            assetsInputStream.close();
        }

        defectNcnn.Init(param, bin, words);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            try
            {
                if (requestCode == SELECT_IMAGE) {
                    Bitmap bitmap = decodeUri(selectedImage);

                    Bitmap rgba = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                    // resize to 227x227
                    yourSelectedImage = Bitmap.createScaledBitmap(rgba, 227, 227, false);

                    imageView.setImageBitmap(yourSelectedImage);
                }
            }
            catch (FileNotFoundException e)
            {
                Log.e("MainActivity", "FileNotFoundException");
                return;
            }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException
    {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 400;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    // Used to load the 'native-lib' library on application startup.
    public native String stringFromJNI();

    static {
        System.loadLibrary("native-lib");
    }
}
