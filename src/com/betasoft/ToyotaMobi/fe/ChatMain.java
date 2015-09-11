package com.betasoft.ToyotaMobi.fe;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;
import com.betasoft.ToyotaMobi.JavaBeans.MessageBean;
import com.betasoft.ToyotaMobi.fe.emoji.EmoticonsGridAdapter.KeyClickListener;
import com.betasoft.ToyotaMobi.fe.fragment.ChatMainCustom;
import com.betasoft.emojicon.EmojiconEditText;
import com.betasoft.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import com.betasoft.emojicon.EmojiconsPopup;
import com.betasoft.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import com.betasoft.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import com.betasoft.emojicon.emoji.Emojicon;
import com.loopj.android.http.RequestParams;

// this class is using for Mechanic side Chat
public class ChatMain extends Activity implements KeyClickListener {
    Uri mLastPhoto;
    public Handler mHandler;
    private ListView listChat;
    int REQUEST_TAKE_PICTURE = 0;
    ProgressDialog simpleWaitDialog;
    int serverResponseCode = 0;
    private EmojiconEditText edtChat;
    String chat;
    public static String savePath = null;
    private ImageView imgSend, imgCamera;
    public static String p;
    String iconName = null;
    ChatMainCustom adapter, newAdapter;
    ImageButton locationBtn, homeBtn;
    RequestParams imageParams = new RequestParams();
    String imagePath, encodedString, fileName;
    KeyClickListener mlistener;
    String userLocation;
    Bitmap bitmap;
    String settingImagePath, userName;
    TextView userNameTV;
    public static ChatMain instance;
    private static int RESULT_LOAD_IMG = 1;
    Button nextbtn;
    LinkedList<MessageBean> chat_list = new LinkedList<MessageBean>();
    LinkedList<MessageBean> new_chat_list;
    String receiveEntity;
    String params = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/start_clt_mecha_chat/?";
    String responsefromserver;
    String senderID, receiverID;

    // used to get messages from server after a time interval
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            receiveData(); // this function retrieve messages from server
            mHandler.postDelayed(mStatusChecker, 5000); // this is time interval, 1000 = 1 second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_menu_screenr);
        getActionBar().hide();
        final View rootView = findViewById(R.id.root_view);
        RelativeLayout headerLayout = (RelativeLayout) findViewById(R.id.header_layout);
        headerLayout.setVisibility(RelativeLayout.VISIBLE);

        mHandler = new Handler();
        // getting name,senderID,reciverID from previous screen or intent
        userName = getIntent().getExtras().getString("userName");
        senderID = getIntent().getExtras().getString("senderID");
        receiverID = getIntent().getExtras().getString("receiverID");
        // showing one default message for user
        chat_list.add(new MessageBean(senderID, receiverID,
                "Hey, How may I help You.", "", "", ""));

        locationBtn = (ImageButton) findViewById(R.id.location_btn);
        homeBtn = (ImageButton) findViewById(R.id.home_btn);
        userNameTV = (TextView) findViewById(R.id.user_name_tv);
        userNameTV.setText(userName);
        listChat = (ListView) findViewById(R.id.list_chat);

        // code for emojicons
        final ImageView emoticonsButton = (ImageView) findViewById(R.id.smile);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
        popup.setSizeForSoftKeyboard();

        popup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emoticonsButton, R.drawable.smiley_icon);
            }
        });

        popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (edtChat == null || emojicon == null) {
                    return;
                }

                int start = edtChat.getSelectionStart();
                int end = edtChat.getSelectionEnd();
                if (start < 0) {

                    // Toast.makeText(getApplicationContext(),emojicon.getEmoji(),100).show();
                    edtChat.append(emojicon.getEmoji());
                } else {
                    edtChat.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                    try {
                        iconName = URLEncoder.encode(edtChat.getText()
                                .toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.v("emoji==== ", iconName);

                }
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0,
                        0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                edtChat.dispatchKeyEvent(event);
            }
        });
        emoticonsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // If popup is not showing => emoji keyboard is not visible, we
                // need to show it
                if (!popup.isShowing()) {

                    // If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emoticonsButton,
                                R.drawable.ic_action_keyboard);
                    }

                    // else, open the text keyboard first and immediately after
                    // that show the emoji popup
                    else {
                        edtChat.setFocusableInTouchMode(true);
                        edtChat.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(edtChat,
                                InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emoticonsButton,
                                R.drawable.ic_action_keyboard);
                    }
                }

                // If popup is showing, simply dismiss it to show the undelying
                // text keyboard
                else {
                    popup.dismiss();
                }
            }
        });
        receiveData();

        mStatusChecker.run();
        edtChat = (EmojiconEditText) findViewById(R.id.chat);

        imgSend = (ImageView) findViewById(R.id.send);
        imgCamera = (ImageView) findViewById(R.id.camera);

        // function is calling when user click on Camera Icon btn in Chat
        imgCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!(ChatMain.this).isFinishing()) {
                    //creating name of Image by sytem time unit
                    p = String.valueOf(System.currentTimeMillis()) + ".jpg";
                    // creating folder to store images into internal storage of Mobile device
                    File folder = new File(Environment
                            .getExternalStorageDirectory(), "ToyotaMobi Images");
                    File storagePath = null;
                    if (folder.exists()) {
                        if (android.os.Environment.getExternalStorageState()
                                .equals(android.os.Environment.MEDIA_MOUNTED)) {
                            storagePath = new File(android.os.Environment
                                    .getExternalStorageDirectory(),
                                    "/ToyotaMobi Images/");
                        }
                    } else {

                        storagePath = new File(android.os.Environment
                                .getExternalStorageDirectory()
                                + "/ToyotaMobi Images/");
                        storagePath.mkdirs();
                    }
                    File file = new File(storagePath, p);
                    // calling service of Camera to open Camera in App
                    Intent cameraIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(file));
                    // called after capturing image from camera
                    startActivityForResult(cameraIntent, RESULT_LOAD_IMG);

                }

            }
        });

        // used to visibility of Camera Icon
        // user can either send image or text in chat
        edtChat.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {

                if (cs.toString().length() == 0) {
                    imgCamera.setVisibility(ImageView.VISIBLE);
                } else {

                    imgCamera.setVisibility(ImageView.INVISIBLE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }

        });

        //calling while clicking on send Icon to send text in chat
        imgSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!edtChat.getText().toString().isEmpty()) {
                    if (isNetworkAvailable())//checking for internet connection
                    {
                        //checks for is it emoji icon or simple text message
                        if (iconName == null)
                            chat = edtChat.getText().toString();
                        else
                            chat = iconName;

                        Log.v("emo= ", chat);
                        // calling webservice of sending chat messages to server
                        MyAsyncTask sendDataToServer = new MyAsyncTask();
                        sendDataToServer.execute(params);
                        new_chat_list = chat_list;
                        try {
                            ChatMainCustom newAdapter = new ChatMainCustom(ChatMain.this, new_chat_list, senderID,
                                    receiverID);
                            listChat.setAdapter(newAdapter);

                        } catch (Exception e) {
                            listChat.setAdapter(null);

                        }
                        listChat.invalidate();
                        edtChat.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry!! You are not Connected to Internet!", Toast.LENGTH_LONG).show();
                    }
                    // receiveData();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter message", Toast.LENGTH_LONG).show();
                }
            }
        });

        //used to get location of particular user.
        locationBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //creating and showing dialog to show location of user with whom mechanic is chatting
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ChatMain.this);
                builder.setMessage(userLocation)
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                    }
                                });

                // Creating dialog box
                AlertDialog alert = builder.create();
                // Setting the title manually
                alert.setTitle("Location:");
                alert.show();
                // Toast.makeText(getApplicationContext(),"Location   "+userLocation,100).show();

            }
        });

        // it takes mechanic to UserList Screen
        homeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent in = new Intent(ChatMain.this, UsersListActivity.class);
                startActivity(in);

            }
        });

    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged,
                                         int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }


    //this method is called when image is captured from camera and saved in app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == -1) {
                File f = new File(Environment.getExternalStorageDirectory()
                        + "/ToyotaMobi Images");

                for (File temp : f.listFiles()) {
                    Log.v("co== ", "Inside file");
                    if (temp.getName().equals(p)) {
                        Log.v("co== ", "Inside");
                        f = temp;
                        break;
                    }
                }

                //code for resizing captured image
                Bitmap bitmap;

                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                FileOutputStream outFile = new FileOutputStream(f);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outFile);
                // pic=file;
                outFile.flush();

                outFile.close();
                imagePath = f.getAbsolutePath().toString();

                // Toast.makeText(getApplicationContext(),"Path== "+imagePath,100).show();
                Log.v("Image Path==  ", imagePath);
                fileName = p;

                // Put file name in Async Http Post Param which will used in Php
                // web app
                imageParams.put("filename", fileName);
                uploadImage();// send image to server
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    // called to send image to server
    public void uploadImage() {
        if (isNetworkAvailable()) {
            simpleWaitDialog = new ProgressDialog(ChatMain.this);
            simpleWaitDialog.setMessage("Image Uploading...");
            simpleWaitDialog.setIndeterminate(false);
            simpleWaitDialog.setCancelable(false);
            simpleWaitDialog.show();
            if (imagePath != null && !imagePath.isEmpty()) {
                settingImagePath = imagePath;
                listChat.invalidate();
                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // tv.setText("uploading started.....");
                            }
                        });
                        // calling webservices of image uploading to server
                        new UploadFileToServer().execute(imagePath);
                    }
                }).start();

            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "You must capture image before you try to upload",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Sorry!! You are not Connected to Internet!", Toast.LENGTH_LONG).show();
        }
    }

    private String getQuery(List<NameValuePair> params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    //used to get all chat between sender and receiver
    private void receiveData() {
        // TODO Auto-generated method stub
        chat_list.clear();
        chat_list.add(new MessageBean(senderID, receiverID,
                "Hi, How may I help you?.", "", "", ""));
        if (isNetworkAvailable()) {
            MyServiceReceiveData myserrecdata = new MyServiceReceiveData();
            myserrecdata.execute();
        }

    }


    //receiving messages from server webservice
    private class MyServiceReceiveData extends
            AsyncTask<String, String, String> {
        //called before calling service
        @Override
        protected void onPreExecute() {
        }

        ;

        @Override
        protected String doInBackground(String... params) {
            String receiveDataUrl = null;
            receiveDataUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/chat_details/?sender_id="
                    + senderID + "&receiver_id=" + receiverID;
            chat_list.clear();
            Log.v("link== ", receiveDataUrl);
            HttpPost httppost = new HttpPost(receiveDataUrl);
            DefaultHttpClient dhtpc = new DefaultHttpClient();
            try {
                HttpResponse httpresponse = dhtpc.execute(httppost);
                HttpEntity httpentity = httpresponse.getEntity();
                if (httpentity != null) {
                    receiveEntity = EntityUtils.toString(httpentity);
                    // getting response in JSON Array Form from server
                    JSONObject jObj = new JSONObject(receiveEntity);
                    JSONArray main_arr = jObj.optJSONArray("Message");
                    chat_list.clear();
                    chat_list.add(new MessageBean(senderID, receiverID,
                            "Hey, How may I help You.", "", "", ""));
                    for (int i = 0; i < main_arr.length(); i++) {
                        JSONObject main_obj = main_arr.optJSONObject(i);
                        if (main_obj != null) {
                            MessageBean mBean = new MessageBean(
                                    main_obj.getString("sender_id"),
                                    main_obj.getString("receiver_id"),
                                    main_obj.getString("message"),
                                    main_obj.getString("image"),
                                    main_obj.getString("image_path"),
                                    main_obj.getString("location"));
                            mBean.chatID = main_obj.getString("id");
                            mBean.downloadedImagePath = main_obj
                                    .getString("downloaded_image_path");
                            chat_list.add(mBean);
                            if (!main_obj.getString("location").matches(""))
                                userLocation = main_obj.getString("location");
                            Log.v("Message== ",
                                    main_obj.getString("sender_id")
                                            + "    "
                                            + main_obj.getString("receiver_id")
                                            + "    "
                                            + main_obj.getString("message")
                                            + "    "
                                            + main_obj.getString("image")
                                            + "   "
                                            + main_obj.getString("image_path")
                                            + "     "
                                            + main_obj.getString("location")
                                            + "    "
                                            + main_obj
                                            .getString("downloaded_image_path"));
                        }
                    }

                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        // called after executing webservice
        @Override
        protected void onPostExecute(String receiveDataUrl) {
            new_chat_list = chat_list;
            try {
                ChatMainCustom newAdapter = new ChatMainCustom(ChatMain.this, new_chat_list, senderID, receiverID);
                listChat.setAdapter(newAdapter);

                listChat.setAdapter(newAdapter);
                listChat.invalidate();

            } catch (Exception e) {
                listChat.setAdapter(null);
            }

        }

    }

    //used for sending messages to server
    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

        @Override
        protected Double doInBackground(String... params) {

            postText();

            return null;
        }

        protected void onPostExecute(Double result) {
            new_chat_list = chat_list;
            listChat.setAdapter(newAdapter);
            try {
                ChatMainCustom newAdapter = new ChatMainCustom(ChatMain.this, new_chat_list, senderID,
                        receiverID);
                listChat.setAdapter(newAdapter);

            } catch (Exception e) {
                listChat.setAdapter(null);

            }

            listChat.invalidate();
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        // this will post our text data
        private void postText() {
            try {
                // url where the data will be posted
                String postReceiverUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/start_clt_mecha_chat/?";
                Log.v("toyota", "postURL: " + postReceiverUrl);

                // HttpClient
                HttpClient httpClient = new DefaultHttpClient();

                // post header
                HttpPost httpPost = new HttpPost(postReceiverUrl);

                // add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        2);
                nameValuePairs
                        .add(new BasicNameValuePair("sender_id", senderID));
                nameValuePairs.add(new BasicNameValuePair("message", chat));
                nameValuePairs.add(new BasicNameValuePair("receiver_id",
                        receiverID));
                nameValuePairs.add(new BasicNameValuePair("image", fileName));
                nameValuePairs.add(new BasicNameValuePair("image_path",
                        imagePath));

                chat_list.add(new MessageBean(senderID, receiverID, chat, "",
                        "", ""));
                iconName = null;
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // execute HTTP post request
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {

                    String responseStr = EntityUtils.toString(resEntity).trim();
                    Log.v("toyota", "Response: " + responseStr);
                    responsefromserver = responseStr;
                }

                settingImagePath = null;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mStatusChecker);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mStatusChecker);
        super.onDestroy();
    }

    @Override
    public void keyClickedIndex(String index) {
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mStatusChecker.run();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            Intent in = new Intent(ChatMain.this, UsersListActivity.class);
            startActivity(in);
            return super.onKeyDown(keyCode, event);
        }

        return true;
    }

    //used to send image to server
    private class UploadFileToServer extends AsyncTask<String, String, String> {
        // private ProgressDialog pdia;
        @Override
        protected void onPreExecute() {
        }

        ;

        @Override
        protected String doInBackground(String... params) {
            String res;
            res = Integer.toString(uploadFile(params[0]));
            return res;
        }

        @Override
        protected void onPostExecute(String receiveDataUrl) {
            p = null;
            imagePath = null;
            fileName = null;

            new_chat_list = chat_list;
            try {
                ChatMainCustom newAdapter = new ChatMainCustom(ChatMain.this, new_chat_list, senderID,
                        receiverID);
                listChat.setAdapter(newAdapter);

            } catch (Exception e) {
                listChat.setAdapter(null);

            }
            listChat.invalidate();
            simpleWaitDialog.dismiss();
        }

        public int uploadFile(String sourceFileUri) {
            String upLoadServerUri = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/main_chat";
            String fileName = sourceFileUri;
            Log.v("Chat Link== ", upLoadServerUri);
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);
            if (!sourceFile.isFile()) {
                Log.e("uploadFile", "Source File Does not exist");
                return 0;
            }
            try { // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(
                        sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection(); // Open a HTTP
                // connection
                // to the
                // URL
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                // conn.setRequestProperty("request_parameters",SplashActivity.usersDetailBean.userId);
                // Log.v("sender== ", senderID);
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(url.toString());
                //
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sender_id", senderID));
                params.add(new BasicNameValuePair("receiver_id", receiverID));
                params.add(new BasicNameValuePair("image", p));
                params.add(new BasicNameValuePair("image_path", imagePath));
                params.add(new BasicNameValuePair("location", userLocation
                        .toString()));

                chat_list.add(new MessageBean(senderID, receiverID, chat, p,
                        imagePath, userLocation.toString()));
                postRequest.setEntity(new UrlEncodedFormEntity(params));
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"request_parameters\";filename=\""
                        + SplashActivity.usersDetailBean.userId
                        + "\""
                        + lineEnd);

                dos.writeBytes(lineEnd);
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(dos, "UTF-8"));
                writer.write(getQuery(params));

                bytesAvailable = fileInputStream.available(); // create a buffer
                // of maximum
                // size

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                Log.v("url=== ", conn.getURL().toString());
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                // p = null;
                // imagePath = null;
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                if (serverResponseCode == 200) {
                    HttpResponse newResponse = httpClient.execute(postRequest);
                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Upload file to server Exception",
                        "Exception : " + e.getMessage(), e);
            }
            return serverResponseCode;
        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
