package com.example.klasifikasipenyakitkulitwajah;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.klasifikasipenyakitkulitwajah.ml.ModelDa1Metadata;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button camera, gallery;
    ImageView imageview;
    TextView result;
    int imageSize=128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);

        result = findViewById(R.id.result);
        imageview = findViewById(R.id.imageView);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
            if (resultCode == 3){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getHeight(), image.getWidth());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImages(image);
            } else{
                Uri dat = data.getData();
                Bitmap image = null;
                try{
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch(IOException e){
                    e.printStackTrace();
                }
                imageview.setImageBitmap(image);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImages(image);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

    public void classifyImages(Bitmap image) {
            try {
                ModelDa1Metadata model = ModelDa1Metadata.newInstance(getApplicationContext());

                // Creates inputs for reference.
                TensorImage images = TensorImage.fromBitmap(images);

                // Runs model inference and gets result.
                ModelDa1Metadata.Outputs outputs = model.process(image);
                List<Category> probability = outputs.getProbabilityAsCategoryList();

                // Releases model resources if no longer used.
                model.close();
            } catch (IOException e) {
                // TODO Handle the exception
            }
        }
    }
}