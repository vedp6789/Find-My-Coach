package com.findmycoach.mentor.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.findmycoach.mentor.util.ImageLoadTask;
import com.findmycoach.mentor.util.StorageHelper;
import com.fmc.mentor.findmycoach.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by IgluLabs on 1/23/2015.
 */
public class ChatWidgetAdapter  extends ArrayAdapter<String> {


    private Context context;
    // Getting all messages in a list
    private ArrayList<String> messageList;
    // For determining messages are received or sent (by mapping with messageList. 0=sent, 1=received)
    private ArrayList<Integer> senderList;
    // For determining message type i.e. text/image/video (0=text, 1=image, 2=video)
    private ArrayList<Integer> messageType;
    // For mapping the downloaded files in storage with received or sent files
    private ArrayList<String> fileNames;

    private String storagePathImage, storagePathVideo;

    public ChatWidgetAdapter(Context context, ArrayList<String> messageList, ArrayList<Integer> sender, ArrayList<Integer> messageType ) {
        super(context, R.layout.signle_chat_cointainer_sent, messageList);
        this.context = context;
        this.messageList = messageList;
        this.senderList = sender;
        this.messageType = messageType;

        /*Creating/Checking folder for media storage*/
        StorageHelper.createAppMediaFolders(context);
        storagePathImage = StorageHelper.getUserDetails(context,"image_path");
        storagePathVideo = StorageHelper.getUserDetails(context,"video_path");

        Log.d("FMC",storagePathImage);
        Log.d("FMC",storagePathVideo);

        File imageFolder = new File(StorageHelper.getUserDetails(context,"image_path"));
        File videoFolder = new File(StorageHelper.getUserDetails(context,"video_path"));
        String[] images = imageFolder.list();
        String[] videos = videoFolder.list();

        fileNames = new ArrayList<String>();
        for(String image : images)
            fileNames.add(image.trim());
        for(String video : videos)
            fileNames.add(video.trim());
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try{
            if(senderList.get(position) == 1 && messageType.get(position) == 0) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_received, parent, false);
                showTextMsg(rowView, position);
            }
            else if(senderList.get(position) == 1 && messageType.get(position) == 1) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_received_image_video, parent, false);
                showImageMsg(rowView, position);
            }
            else if(senderList.get(position) == 1 && messageType.get(position) == 2) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_received_image_video, parent, false);
                showVideoMsg(rowView, position);
            }
            else if(senderList.get(position) == 0 && messageType.get(position) == 0) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_sent, parent, false);
                showTextMsg(rowView, position);
            }
            else if(senderList.get(position) == 0 && messageType.get(position) == 1) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_sent_image_video, parent, false);
                showImageMsg(rowView, position);
            }
            else if(senderList.get(position) == 0 && messageType.get(position) == 2) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_sent_image_video, parent, false);
                showVideoMsg(rowView, position);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.gc();
        }
        return rowView;
    }

    private void showTextMsg(View v, int position){
        final TextView msgTextView = (TextView) v.findViewById(R.id.messageTV);
        msgTextView.setText(messageList.get(position));
    }

    private void showImageMsg(View v, int position){
        final ImageView imageView = (ImageView) v.findViewById(R.id.mediaIV);

        String[] fileName = messageList.get(position).split("/");
        final String imageName = fileName[fileName.length-1].trim();

        if(fileNames.contains(imageName)) {
            final File imageFile = new File(storagePathImage + "/" + fileNames.get(fileNames.indexOf(imageName)));
            if(imageFile.exists()){
                try{
                    imageView.setImageBitmap(decodeFileImage(imageFile));
                }catch (Exception e){
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + imageFile.getAbsolutePath()), "image/*");
                        context.startActivity(intent);
                    }
                });
            }
        }else {
            new ImageLoadTask(messageList.get(position), context, imageName, storagePathImage, fileNames).execute();
            Picasso.with(context).load(messageList.get(position)).into(imageView);
        }
    }

    private Bitmap decodeFileImage(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=50;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSi
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            Bitmap bitmap = null;
            try{
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            }catch (OutOfMemoryError e){
                System.gc();
                try{
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
                }catch (OutOfMemoryError ex){
                    ex.printStackTrace();
                    System.gc();
                    return null;
                }
            }
            return bitmap;
        } catch (FileNotFoundException e) {e.printStackTrace();}
        return null;
    }

    private void showVideoMsg(View v, int position){
        final ImageView imageView = (ImageView) v.findViewById(R.id.mediaIV);

        String[] fileName = messageList.get(position).split("/");
        String videoName = fileName[fileName.length-1].trim();
        if(fileNames.contains(videoName)) {
            final File videoFile = new File(storagePathVideo + "/" + fileNames.get(fileNames.indexOf(videoName)));
            if(videoFile.exists()){
                try{
                    imageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoFile.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND));

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse("file://" + videoFile.getAbsolutePath()), "video/*");
                            context.startActivity(intent);
                        }
                    });
                }catch (Exception e){
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
                }
            }

        }else{
            new ImageLoadTask(messageList.get(position), context, videoName, storagePathVideo, fileNames).execute();
        }

    }

    public void updateMessageList(String msg, int sender, int messageType){
        this.messageList.add(messageList.size(),msg);
        this.senderList.add(sender);
        this.messageType.add(messageType);
    }
}
