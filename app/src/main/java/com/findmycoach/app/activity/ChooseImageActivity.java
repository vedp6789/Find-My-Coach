package com.findmycoach.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, REQUEST_CODE);
            }
        });

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                cropImageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }





}

