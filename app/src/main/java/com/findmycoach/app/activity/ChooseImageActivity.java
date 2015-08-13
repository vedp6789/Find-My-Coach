package com.findmycoach.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;
import com.findmycoach.app.R;
import com.findmycoach.app.util.BinaryForImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ChooseImageActivity extends Activity {

    /**
     * Static final constants
     */
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    /**
     * Instance variables
     */
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    private static final int REQUEST_CODE = 101;
    private CropImageView cropImageView;
    private Button chooseImage;
    private Button rotateButton;
    private Button removeImageButton;
    private boolean isImageSelected = false;

    /**
     * Saves the state upon rotating the screen/restarting the activity
     */
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    /**
     * Restores the state upon rotating the screen/restarting the activity
     */
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_image);
        initialize();
        applyAction();
    }

    /**
     * Getting references of views
     */
    private void initialize() {
        chooseImage = (Button) findViewById(R.id.select_picture);
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        try {
            cropImageView.setImageBitmap(BinaryForImage.getBitmapFromBinaryString(getIntent().getStringExtra("BitMap")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
        rotateButton = (Button) findViewById(R.id.Button_rotate);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.update_image));
    }

    private void applyAction() {
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
            }
        });
        findViewById(R.id.Button_crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passImage();
            }
        });

        /** Starting intent to choose image from storage */
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CODE);
            }
        });

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        removeImageButton = (Button) findViewById(R.id.buttonRemove);
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("removeImage", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        if (getIntent().getStringExtra("removeImageOption") != null && getIntent().getStringExtra("removeImageOption").equals(getResources().getString(R.string.add_photo)))
            removeImageButton.setVisibility(View.GONE);
        else
            isImageSelected = true;
    }

    /**
     * Passing back cropped image to calling class
     */
    private void passImage() {
        try {
            Bitmap croppedImage = cropImageView.getCroppedCircleImage();
            croppedImage = Bitmap.createScaledBitmap(croppedImage, 175, 175, false);
            Intent intent = new Intent();
            intent.putExtra("image", croppedImage);
            if (isImageSelected)
                setResult(RESULT_OK, intent);
            finish();
            onBackPressed();
        } catch (Exception exception) {
            Toast.makeText(this, getResources().getString(R.string.too_big_image), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Setting selected image from device in the image view
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            Log.e("Image URI :", selectedImage.getPath());
            String imgDecodableString = "";

           try{
               String url = data.getData().toString();
               Bitmap bm = null;
               if (url.startsWith("content://com.google.android.apps.photos.content")){
                   try {
                       InputStream is = getContentResolver().openInputStream(Uri.parse(url));
                       bm = BitmapFactory.decodeStream(is);
                   } catch (FileNotFoundException e) {
                       e.printStackTrace();
                   }
               }

               if(bm != null){
                   cropImageView.setImageBitmap(getResizedBitmap(bm, 500));
                   isImageSelected = true;
                   removeImageButton.setVisibility(View.VISIBLE);
                   return;
               }
           }catch (Exception ignored){
           }

            try{
                imgDecodableString = getRealPathFromURI(selectedImage);
            }catch (Exception e){
                Toast.makeText(this, getResources().getString(R.string.error_picking_image), Toast.LENGTH_SHORT).show();
            }
            Log.e("Absolute path :", imgDecodableString);
            /** Set the Image in ImageView after decoding the String */
            try {
                cropImageView.setImageBitmap(decodeFile(new File(imgDecodableString)));
                isImageSelected = true;
                removeImageButton.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                Toast.makeText(this, getResources().getString(R.string.error_picking_image), Toast.LENGTH_SHORT).show();
                try {
                    cropImageView.setImageBitmap(BinaryForImage.getBitmapFromBinaryString(getIntent().getStringExtra("BitMap")));
                    isImageSelected = true;
                    removeImageButton.setVisibility(View.VISIBLE);
                } catch (Exception ignored) {
                }
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.image_not_picked),
                    Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String getRealPathFromURI(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private Bitmap decodeFile(File f) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_SIZE = 500;

            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}

