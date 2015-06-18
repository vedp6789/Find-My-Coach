package com.findmycoach.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.util.ImageLoadTask;
import com.findmycoach.app.util.StorageHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ShekharKG on 1/23/2015.
 */
public class ChatWidgetAdapter extends ArrayAdapter<String> {


    private Context context;
    // Getting all messages in a list
    private ArrayList<String> messageList;
    // Getting all messages time stamp a list
    private ArrayList<String> timeStampList;
    // For determining messages are received or sent (by mapping with messageList. 0=sent, 1=received)
    private ArrayList<Integer> senderList;
    // For determining message type i.e. text/image/video (0=text, 1=image, 2=video)
    private ArrayList<Integer> messageType;
    // For mapping the downloaded files in storage with received or sent files
    public ArrayList<String> fileNames;

    private String storagePathImage, storagePathVideo;

    private ArrayList<Integer> mediaTempList;

    public ChatWidgetAdapter(Context context, ArrayList<String> messageList,
                             ArrayList<Integer> sender, ArrayList<Integer> messageType,
                             ArrayList<String> timeStampList) {
        super(context, R.layout.signle_chat_cointainer_sent, messageList);
        this.context = context;
        this.messageList = messageList;
        this.senderList = sender;
        this.messageType = messageType;
        this.timeStampList = timeStampList;

        /*Creating/Checking folder for media storage*/
        StorageHelper.createAppMediaFolders(context);
        storagePathImage = StorageHelper.getUserDetails(context, "image_path");
        storagePathVideo = StorageHelper.getUserDetails(context, "video_path");

        Log.d("FMC", storagePathImage);
        Log.d("FMC", storagePathVideo);

        File imageFolder = new File(StorageHelper.getUserDetails(context, "image_path"));
        File videoFolder = new File(StorageHelper.getUserDetails(context, "video_path"));
        String[] images = imageFolder.list();
        String[] videos = videoFolder.list();

        fileNames = new ArrayList<>();
        mediaTempList = new ArrayList<>();
        for (String image : images)
            fileNames.add(image.trim());
        for (String video : videos)
            fileNames.add(video.trim());
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            if (senderList.get(position) == 1 && messageType.get(position) == 0) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_received, parent, false);
                showTextMsg(rowView, position);
            } else if (senderList.get(position) == 1 && messageType.get(position) == 1) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_received_image_video,
                        parent, false);
                showImageMsg(rowView, position);
            } else if (senderList.get(position) == 1 && messageType.get(position) == 2) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_received_image_video,
                        parent, false);
                showVideoMsg(rowView, position);
            } else if (senderList.get(position) == 0 && messageType.get(position) == 0) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_sent, parent, false);
                showTextMsg(rowView, position);
            } else if (senderList.get(position) == 0 && messageType.get(position) == 1) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_sent_image_video,
                        parent, false);
                showImageMsg(rowView, position);
            } else if (senderList.get(position) == 0 && messageType.get(position) == 2) {
                rowView = inflater.inflate(R.layout.signle_chat_cointainer_sent_image_video,
                        parent, false);
                showVideoMsg(rowView, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
        }
        return rowView;
    }

    private void showTextMsg(View v, int position) {
        final TextView msgTextView = (TextView) v.findViewById(R.id.messageTV);
        final TextView timeStampTextView = (TextView) v.findViewById(R.id.timeStamp);
        msgTextView.setText(messageList.get(position));

        Date date = new Date(Long.parseLong(timeStampList.get(position)));
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String[] times = formatter.format(date).split(":");
        if (Integer.parseInt(times[0]) > 12)
            timeStampTextView.setText(Integer.parseInt(times[0]) % 12 + ":" + times[1] + " pm");
        else
            timeStampTextView.setText(Integer.parseInt(times[0]) + ":" + times[1] + " am");
    }

    private void showImageMsg(View v, int position) {
        final ImageView imageView = (ImageView) v.findViewById(R.id.mediaIV);

        String[] fileName = messageList.get(position).split("/");
        final String imageName = fileName[fileName.length - 1].trim();

        if (fileNames.contains(imageName)) {
            final File imageFile = new File(storagePathImage + "/" +
                    fileNames.get(fileNames.indexOf(imageName)));
            if (imageFile.exists()) {
                try {
                    Picasso.with(context).load(imageFile).into(imageView);
                } catch (Exception e) {
                    imageView.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_launcher));
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" +
                                imageFile.getAbsolutePath()), "image/*");
                        context.startActivity(intent);
                    }
                });
            }
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.image_thumb));
            final File imageFile = new File(messageList.get(position));
            ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.mediaProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            if (imageFile.exists()) {
                try {
                    Picasso.with(context).load(imageFile).into(imageView);
                    mediaTempList.add(position);
                } catch (Exception e) {
                    imageView.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_launcher));
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" +
                                imageFile.getAbsolutePath()), "image/*");
                        context.startActivity(intent);
                    }
                });
            }
            new ImageLoadTask(messageList.get(position), context, imageName,
                    storagePathImage, fileNames, position, messageList).execute();
        }
    }

    private void showVideoMsg(View v, int position) {
        final ImageView imageView = (ImageView) v.findViewById(R.id.mediaIV);

        String[] fileName = messageList.get(position).split("/");
        String videoName = fileName[fileName.length - 1].trim();
        if (fileNames.contains(videoName)) {
            final File videoFile = new File(storagePathVideo + "/" +
                    fileNames.get(fileNames.indexOf(videoName)));
            if (videoFile.exists()) {
                try {
                    imageView.setImageBitmap(ThumbnailUtils
                            .createVideoThumbnail(videoFile.getAbsolutePath(),
                                    MediaStore.Images.Thumbnails.MINI_KIND));

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse("file://" +
                                    videoFile.getAbsolutePath()), "video/*");
                            context.startActivity(intent);
                        }
                    });
                } catch (Exception e) {
                    imageView.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_launcher));
                }
            }

        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.video_thumb));
            final File videoFile = new File(messageList.get(position));
            ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.mediaProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            if (videoFile.exists()) {
                try {
                    imageView.setImageBitmap(ThumbnailUtils
                            .createVideoThumbnail(videoFile.getAbsolutePath(),
                                    MediaStore.Images.Thumbnails.MINI_KIND));
                    mediaTempList.add(position);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse("file://" +
                                    videoFile.getAbsolutePath()), "video/*");
                            context.startActivity(intent);
                        }
                    });
                } catch (Exception e) {
                    imageView.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_launcher));
                }
            }
            new ImageLoadTask(messageList.get(position), context, videoName,
                    storagePathVideo, fileNames, position, messageList).execute();
        }

    }

    public void updateMessageList(String msg, int sender, int messageType, String timeStamp) {
        this.messageList.add(messageList.size(), msg);
        this.senderList.add(sender);
        this.messageType.add(messageType);
        this.timeStampList.add(timeStamp);
    }

    public void downloadFile(String fileUrl, String type) {
        String[] fileName = fileUrl.split("/");
        String fName = fileName[fileName.length - 1].trim();
        new ImageLoadTask(fileUrl, context, fName, type.equals("image") ? storagePathImage :
                storagePathVideo, fileNames, mediaTempList.get(0), messageList).execute();
        mediaTempList.remove(0);
    }
}
