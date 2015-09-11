package com.betasoft.ToyotaMobi.fe.fragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.JavaBeans.MessageBean;
import com.betasoft.emojicon.EmojiconTextView;

public class ChatMainCustom extends ArrayAdapter<MessageBean> {
    //	 AlertDialog.Builder builder;
    List<MessageBean> chatArrayList;
    public ProgressDialog simpleWaitDialog;
    //   boolean isImageFitToScreen;
    String savePath;
    int clickPosition, newPosition;
    String sender_ID, receiver_ID;
    Activity context;

    //   ViewHolder holder;
    public ChatMainCustom(Activity context, List<MessageBean> chatArrayList, String sender_ID, String receiver_ID)
            throws Exception {
        super(context, android.R.layout.simple_list_item_1, chatArrayList);
        this.chatArrayList = chatArrayList;
        this.sender_ID = sender_ID;
        this.receiver_ID = receiver_ID;
        this.context = context;


    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return super.getItemId(position);
    }

    @Override
    public int getViewTypeCount() {

        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Log.e("000000000 ", "Hey");
        try {
            ViewHolder holder;
            if (convertView == null) {
                convertView = context.getLayoutInflater().inflate(R.layout.chat_menu_screenr_custom, parent, false);
                holder = new ViewHolder();
//		 holder = new ViewHolder(); 
                holder.txtuser = (EmojiconTextView) convertView.findViewById(R.id.user);
                holder.txtmechanic = (EmojiconTextView) convertView.findViewById(R.id.mechanic);
                holder.mechanicimg = (ImageView) convertView.findViewById(R.id.mechanic_icon);
                holder.containerimg = (ImageView) convertView.findViewById(R.id.usercontainer);
                holder.mchUpldImg = (ImageView) convertView.findViewById(R.id.mechanic_iv);
                holder.usrUpldImg = (ImageView) convertView.findViewById(R.id.user_iv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            boolean isMe = false;
            if (position < chatArrayList.size()) {
                MessageBean beanObj = chatArrayList.get(position);
                isMe = sender_ID.equals(beanObj.senderID);

//	 final ViewHolder holder = (ViewHolder)convertView.getTag();
                if (isMe) {
                    if (!(beanObj.uploadImageName.matches("") && !beanObj.imgPath.matches("imagePath"))) {
                        File imgFile = new File(beanObj.imgPath);
                        if (imgFile.exists()) {

//				 Bitmap d = new BitmapDrawable(getContext().getResources() , imgFile.getAbsolutePath()).getBitmap();
                            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//               File file = new File(SplashActivity.usersDetailBean.profilePath);
//               if(file.exists())
//               {

                            int rotate = 0;
                            ExifInterface exif;

                            exif = new ExifInterface(imgFile.getAbsolutePath());


                            int orientation = exif.getAttributeInt(
                                    ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_NORMAL);

                            switch (orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotate = 270;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotate = 180;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotate = 90;
                                    break;
                            }


                            Bitmap d = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),
                                    bitmapOptions);
//				 int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
//				 Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
                            Matrix matrix = new Matrix();
                            matrix.postRotate(rotate);
                            int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
                            Bitmap scaled = Bitmap.createScaledBitmap(d, 400, 400, true);
                            Bitmap cropped = Bitmap.createBitmap(scaled, 0, 0, 400, 400, matrix, true);
                            holder.usrUpldImg.setImageBitmap(cropped);
                        }
                        holder.txtuser.setVisibility(View.GONE);
                        holder.usrUpldImg.setVisibility(ImageView.VISIBLE);

                    } else {
                        holder.txtuser.setVisibility(View.VISIBLE);
                        holder.usrUpldImg.setVisibility(ImageView.GONE);
                        holder.txtuser.setBackgroundResource(R.drawable.chat_bubble_red);


                        holder.txtuser.setText(URLDecoder.decode(chatArrayList.get(position).messages, "UTF-8"));
                    }

                    holder.containerimg.setVisibility(View.VISIBLE);
                    holder.mechanicimg.setVisibility(View.GONE);
                    holder.txtmechanic.setVisibility(TextView.GONE);
                    holder.mchUpldImg.setVisibility(ImageView.GONE);
                    holder.mechanicimg.setVisibility(ImageView.GONE);
                } else {
                    if (!(beanObj.uploadImageName.matches("") && !beanObj.imgPath.matches("imagePath"))) {
                        // Log.v("+++++++   ","Inside else inside image if");
                        if (!beanObj.downloadedImagePath.isEmpty()) {
                            File imgFile = new File(beanObj.downloadedImagePath);
                            if (imgFile.exists()) {
//				 Bitmap d = new BitmapDrawable(getContext().getResources() , imgFile.getAbsolutePath()).getBitmap();

                                int rotate = 0;
                                ExifInterface exif;
                                exif = new ExifInterface(imgFile.getAbsolutePath());


                                int orientation = exif.getAttributeInt(
                                        ExifInterface.TAG_ORIENTATION,
                                        ExifInterface.ORIENTATION_NORMAL);

                                switch (orientation) {
                                    case ExifInterface.ORIENTATION_ROTATE_270:
                                        rotate = 270;
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_180:
                                        rotate = 180;
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_90:
                                        rotate = 90;
                                        break;
                                }
                                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                                Bitmap d = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),
                                        bitmapOptions);
//					 int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
//					 Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
                                Matrix matrix = new Matrix();
                                matrix.postRotate(rotate);
                                int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
                                Bitmap scaled = Bitmap.createScaledBitmap(d, 400, 400, true);
                                Bitmap cropped = Bitmap.createBitmap(scaled, 0, 0, 400, 400, matrix, true);
//					int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
//				 Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
                                holder.mchUpldImg.setImageBitmap(scaled);
                            } else {
                                holder.mchUpldImg.setBackgroundResource(0);
                                holder.mchUpldImg.setBackgroundResource(R.drawable.icon_download);
                            }
                        } else {
                            holder.mchUpldImg.setBackgroundResource(0);
                            holder.mchUpldImg.setBackgroundResource(R.drawable.icon_download);
                        }
                        holder.mchUpldImg.setVisibility(ImageView.VISIBLE);
                        holder.txtmechanic.setVisibility(View.GONE);
                    } else {
                        holder.txtmechanic.setVisibility(View.VISIBLE);
                        holder.txtuser.setBackgroundResource(R.drawable.chat_bubble_grey);
                        holder.mchUpldImg.setVisibility(ImageView.GONE);
                        try {
                            holder.txtmechanic.setText(URLDecoder.decode(chatArrayList.get(position).messages, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    holder.mechanicimg.setVisibility(View.VISIBLE);
                    holder.txtuser.setVisibility(TextView.GONE);
                    holder.containerimg.setVisibility(ImageView.GONE);
                    holder.usrUpldImg.setVisibility(ImageView.GONE);
                }

                holder.mchUpldImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
//			alert.show();
                        if (position < chatArrayList.size()) {
                            File imgFile = new File(chatArrayList.get(position).downloadedImagePath);
                            if (imgFile.exists()) {
//					showPhoto(Uri.parse("file://"+chatArrayList.get(position).downloadedImagePath));
                                showPhoto(Uri.parse("file://ToyotaMobi Images/" + chatArrayList.get(position).downloadedImagePath));
                            } else {
//					Toast.makeText(getContext(),"pos== "+chatArrayList.get(position).uploadImageName.toString(),100).show();
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//				 Calendar cal = Calendar.getInstance();
                                StrictMode.setThreadPolicy(policy);
                                newPosition = position;

                                File folder = new File(Environment.getExternalStorageDirectory(), "ToyotaMobi Images");
                                File storagePath = null;
                                if (folder.exists()) {
                                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                                        storagePath = new File(android.os.Environment.getExternalStorageDirectory(), "/ToyotaMobi Images/");
                                        savePath = android.os.Environment.getExternalStorageDirectory() + "/ToyotaMobi Images/" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//						
                                    }
                                } else {

                                    storagePath = new File(android.os.Environment.getExternalStorageDirectory() + "/ToyotaMobi Images/");
                                    storagePath.mkdirs();
                                    savePath = android.os.Environment.getExternalStorageDirectory() + "/ToyotaMobi Images/" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//							
                                }


//				savePath = android.os.Environment.getExternalStorageDirectory()+"/ToyotaMobi Images/"+String.valueOf(System.currentTimeMillis())+".jpg";
//				savePath = "/storage/emulated/0/DCIM/Camera/"+cal.getTime().toString()+".jpg";
                                ChangeImagePath sendDataToServer = new ChangeImagePath();
                                sendDataToServer.execute("www.toyotamobi.com/toyotamobi/admin/Webservices/change_image_path?");

//				new ImageDownloader().execute(chatArrayList.get(position).uploadImageName.toString(),ChatMain.savePath);


                                clickPosition = position;
//				new ImageDownloader().execute(chatArrayList.get(position).uploadImageName.toString(),savePath);
//				Bitmap map = DownloadImage(new URL(chatArrayList.get(position).uploadImageName.toString()),savePath);
//				notifyDataSetChanged();
                            }
                        }


                    }
                });

                holder.usrUpldImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position < chatArrayList.size()) {
//        		 Toast.makeText(getContext(),chatArrayList.get(position).imgPath,100).show();
                            showPhoto(Uri.parse("file://" + chatArrayList.get(position).imgPath));
                        }
                    }
                });
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return convertView;

    }

    public void showPhoto(Uri photoUri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(photoUri, "image/*");
        context.startActivity(intent);
    }


//private Bitmap DownloadImage(URL url, String fileSavePath)
//{
//	
//	Bitmap bmImg = null;
//    try {
//    HttpURLConnection conn= (HttpURLConnection)url.openConnection();
//    conn.setDoInput(true);
//    conn.connect();
//    InputStream is = conn.getInputStream();
//    BufferedInputStream bis = new BufferedInputStream(is);
//    /*
//     * Read bytes to the Buffer until there is nothing more to read(-1).
//     */
//    ByteArrayBuffer baf = new ByteArrayBuffer(50);
//    int current = 0;
//    while ((current = bis.read()) != -1) {
//            baf.append((byte) current);
//    }
//
//    FileOutputStream fos = new FileOutputStream(fileSavePath);
//    fos.write(baf.toByteArray());
//    fos.close();
//    bmImg = BitmapFactory.decodeStream(is);
////    int nh = (int) ( bmImg.getHeight() * (512.0 / bmImg.getWidth()) );
////	 bmImg = Bitmap.createScaledBitmap(bmImg, 512, nh, true);
//
//    
//    } catch (IOException e) {
//    // TODO Auto-generated catch block
//    e.printStackTrace();
//    return null;
//    }
//
//    return bmImg;
//}


    private class ChangeImagePath extends AsyncTask<String, Integer, Double> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            try {

                simpleWaitDialog = ProgressDialog.show(context,
                        "Wait", "Downloading Image");
                simpleWaitDialog.show();

                new Thread(new Runnable() {
                    public void run() {
                        context.runOnUiThread(new Runnable() {
                            public void run() {
////                             tv.setText("uploading started.....");
                            }
                        });


                        new ImageDownloader().execute(chatArrayList.get(clickPosition).uploadImageName.toString(), savePath);

                    }
                }).start();

                //		Bitmap map = DownloadImage(new URL(chatArrayList.get(clickPosition).uploadImageName.toString()),savePath);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//	notifyDataSetChanged();

        }

        @Override
        protected Double doInBackground(String... params) {

            postText();


            return null;
        }

        protected void onPostExecute(Double result) {
            // pb.setVisibility(View.GONE);

//                 simpleWaitDialog.dismiss();
        }

        protected void onProgressUpdate(Integer... progress) {

            //   pb.setProgress(progress[0]);
        }


        // this will post our text data
        private void postText() {
            try {
                // url where the data will be posted
                String postReceiverUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/change_image_path/?";
                //  Log.v("toyota", "postURL: " + postReceiverUrl);

                // HttpClient
                HttpClient httpClient = new DefaultHttpClient();

                // post header
                HttpPost httpPost = new HttpPost(postReceiverUrl);
                // Log.v("chat id== ",chatArrayList.get(newPosition).chatID);
                //  Log.v("downloaded path== ",savePath);
                // add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("downloaded_image_path", savePath));
                nameValuePairs.add(new BasicNameValuePair("chat_id", chatArrayList.get(newPosition).chatID));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            // execute HTTP post request
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {

                    String responseStr = EntityUtils.toString(resEntity).trim();
                    //  Log.v("toyota", "Response: " +  responseStr);
//                responsefromserver=responseStr;

                    // you can add an if statement here and do other actions based on the response
                }
//             receiveData();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private static class ViewHolder {
        public ImageView mechanicimg;
        public ImageView containerimg;
        public ImageView mchUpldImg;
        public ImageView usrUpldImg;
        public EmojiconTextView txtuser, txtmechanic;
    }


    private class ImageDownloader extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... param) {
            // TODO Auto-generated method stub
            Bitmap map = null;
            try {
                map = DownloadImage(new URL(param[0]), param[1]);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPreExecute() {
            //	Log.i("Async-Example", "onPreExecute Called");

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //Log.i("Async-Example", "onPostExecute Called");
//		downloadedImg.setImageBitmap(result);
//		simpleWaitDialog.closeOptionsMenu();
//		if(result!=null)
            simpleWaitDialog.dismiss();

        }

        private Bitmap DownloadImage(URL url, String fileSavePath) {

            Bitmap bmImg = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
        /*
	     * Read bytes to the Buffer until there is nothing more to read(-1).
	     */
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                FileOutputStream fos = new FileOutputStream(fileSavePath);
                fos.write(baf.toByteArray());
                fos.close();
                bmImg = BitmapFactory.decodeStream(is);
//	    int nh = (int) ( bmImg.getHeight() * (512.0 / bmImg.getWidth()) );
//		 bmImg = Bitmap.createScaledBitmap(bmImg, 512, nh, true);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }

            return bmImg;
        }


    }


}
