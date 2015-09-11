package com.betasoft.ToyotaMobi.fe.fragment;

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

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;
import com.betasoft.ToyotaMobi.JavaBeans.MessageBean;
import com.betasoft.ToyotaMobi.gps.GPSTracker;
import com.betasoft.emojicon.EmojiconEditText;
import com.betasoft.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import com.betasoft.emojicon.EmojiconsPopup;
import com.betasoft.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import com.betasoft.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import com.betasoft.emojicon.emoji.Emojicon;

@SuppressLint("NewApi")
public class ChatMain extends Fragment implements OnClickListener {
    public static String p;
    public ProgressDialog simpleWaitDialog;
    private static final String TAG = "Register";
    int serverResponseCode = 0;
    private View mainView;
    String chat;
    private ListView listChat;
    boolean hasFocus = false;
    private TextView txtInfo;
    String iconName = null;
    public Handler mHandler;
    public StringBuilder userLocation;
    private EmojiconEditText edtChat;
    private ImageView imgSend, imgCamera;
    public static ChatMainCustom adapter;
    public static ChatMain instance;
    GPSTracker gps;
    Button nextbtn;
//	RequestParams imageParams = new RequestParams();

    private static int RESULT_LOAD_IMG = 1;

    LinkedList<MessageBean> chat_list = new LinkedList<MessageBean>(), new_chat_list;
    String receiveEntity, imagePath, fileName, settingImagePath;

    public static ChatMain getInstance() {
        if (instance == null) {
            instance = new ChatMain();

        }
        return instance;
    }

    String params = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/start_clt_mecha_chat/?";
    String responsefromserver;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            receiveData(); //this function can change value of mInterval.
//	    	Toast.makeText(getActivity().getApplicationContext(),"Hey",100).show();
            mHandler.postDelayed(mStatusChecker, 5000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mainView = inflater.inflate(R.layout.chat_menu_screenr, container, false);
        final View rootView = mainView.findViewById(R.id.root_view);


//			 turnGPSOn();
        TextView tv = (TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
        tv.setText("Chat");
        if (MainMenu.isGpsOn)
            userLocation = setUserLocation();

        if (userLocation == null)
            userLocation = new StringBuilder("Can't get location");
//	        userLocation =userLocation ==null?new StringBuilder("Location is unknown"):userLocation;
        mHandler = new Handler();

        chat_list.add(new MessageBean(SplashActivity.mechanicId, SplashActivity.usersDetailBean.userId, "Hey, How may I help You.", "", "", userLocation.toString()));
        listChat = (ListView) mainView.findViewById(R.id.list_chat);
        final ImageView emoticonsButton = (ImageView) mainView.findViewById(R.id.smile);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this.getActivity());
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
                    edtChat.append(emojicon.getEmoji());
                } else {
                    edtChat.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                    try {
                        iconName = URLEncoder.encode(edtChat.getText().toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //Log.v("emoji==== ", iconName);
                }
            }
        });


        popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                edtChat.dispatchKeyEvent(event);
            }
        });


        receiveData();
        mStatusChecker.run();
        listChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

//		     adapter=new ChatMainCustom(getActivity(),chat_list,SplashActivity.usersDetailBean.userId,SplashActivity.mechanicId);
//			listChat.setAdapter(adapter);
//			listChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//			listChat.setStackFromBottom(true);
//				listChat.invalidate();
        emoticonsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emoticonsButton, R.drawable.ic_action_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        edtChat.setFocusableInTouchMode(true);
                        edtChat.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(edtChat, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emoticonsButton, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        edtChat = (EmojiconEditText) mainView.findViewById(R.id.chat);
        imgSend = (ImageView) mainView.findViewById(R.id.send);
        imgCamera = (ImageView) mainView.findViewById(R.id.camera);
        imgCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (!(getActivity()).isFinishing()) {
                    //your code
                    p = String.valueOf(System.currentTimeMillis()) + ".jpg";
                    File folder = new File(Environment.getExternalStorageDirectory(), "ToyotaMobi Images");
                    File storagePath = null;
                    if (folder.exists()) {
                        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                            storagePath = new File(android.os.Environment.getExternalStorageDirectory(), "/ToyotaMobi Images/");
                        }
                    } else {

                        storagePath = new File(android.os.Environment.getExternalStorageDirectory() + "/ToyotaMobi Images/");
                        storagePath.mkdirs();
                    }

//							 File storagePath = new File(Environment.getExternalStorageDirectory()+ "/ToyotaMobi Images");
//								storagePath.mkdirs();
                    File file = new File(storagePath, p);
//								Toast.makeText(getActivity().getApplicationContext(),"path== "+file.getAbsolutePath().toString(),100).show();
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(cameraIntent, RESULT_LOAD_IMG);
                }

            }
        });

        edtChat.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                if (cs.toString().length() == 0) {
                    imgCamera.setVisibility(ImageView.VISIBLE);
                } else {
                    imgCamera.setVisibility(ImageView.INVISIBLE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }

        });

        imgSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (!edtChat.getText().toString().isEmpty()) {
                    if (isNetworkAvailable()) {
                        if (iconName == null)
                            chat = edtChat.getText().toString();
                        else
                            chat = iconName;
                        if (MainMenu.isGpsOn)
                            userLocation = setUserLocation();
                        if (userLocation == null)
                            userLocation = new StringBuilder("Can't get location");
//						userLocation =userLocation ==null?new StringBuilder("Location is unknown"):userLocation;
                        MyAsyncTask sendDataToServer = new MyAsyncTask();
                        sendDataToServer.execute(params);
//						 	adapter.notifyDataSetChanged();
//					         listChat.invalidate();			 	

                        edtChat.setText("");
//			        receiveData();
                    } else {
                        //Toast.makeText(getActivity().getApplicationContext(),"Sorry!! You are not Connected to Internet!",100).show();
                    }
                } else {
                    //	Toast.makeText(getActivity(),"Please enter message",100).show();
                }
            }
        });


        return mainView;
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    public StringBuilder setUserLocation() {
        new Thread(new Runnable() {

            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                    }
                });
            }
        }).start();
        StringBuilder loc = null;
        // create class object
        gps = new GPSTracker(getActivity());
        try {
            // check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                Geocoder geocoder;
                List<Address> addresses;
//		        geocoder = new Geocoder(this, Locale.getDefault());
                addresses = new Geocoder(getActivity().getApplicationContext()).getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    loc = new StringBuilder(address + "  " + city + "   " + state + "  " + country + "   " + postalCode);
                } else {
                    loc = new StringBuilder("User Location unkown");
                }
                // \n is for new line

//	        	Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " + city + "\nLong: " + country, Toast.LENGTH_LONG).show();	
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),100).show();
            e.printStackTrace();
            loc = new StringBuilder("Can't get Location");
        } // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        return loc;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (!(getActivity()).isFinishing()) {

                // When an Image is picked

                //Log.v("code== ", Integer.toString(requestCode));
                //Log.v("code== ", Integer.toString(resultCode));
                if (requestCode == 1 && resultCode == -1) {
//					&& null != data) {
//				Toast.makeText(getActivity().getApplicationContext(),"Pos== "+p,100).show();

                    File f = new File(Environment.getExternalStorageDirectory() + "/ToyotaMobi Images");

                    for (File temp : f.listFiles()) {
                        //Log.v("co== ","Inside file");
                        if (temp.getName().equals(p)) {
                            //	Log.v("co== ","Inside");
                            f = temp;
                            break;
                        }
                    }

                    Bitmap bitmap;

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();


                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),

                            bitmapOptions);
                    FileOutputStream outFile = new FileOutputStream(f);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outFile);
//pic=file;
                    outFile.flush();

                    outFile.close();
                    imagePath = f.getAbsolutePath().toString();


                    //Log.v("code== ", imagePath);
//				Toast.makeText(getActivity().getApplicationContext(),"Path== "+imagePath,100).show();
                    //Log.v("Image Path==  ",imagePath);

                    fileName = p;
                    // Put file name in Async Http Post Param which will used in Php web app
//				imageParams.put("filename", fileName);


                    uploadImage();

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Activity not running",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    // When Upload button is clicked
    public void uploadImage() {
        if (isNetworkAvailable()) {
            simpleWaitDialog = new ProgressDialog(getActivity());
            simpleWaitDialog.setMessage("Image Uploading...");
            simpleWaitDialog.setIndeterminate(false);
            simpleWaitDialog.setCancelable(false);
            simpleWaitDialog.show();
            // When Image is selected from Gallery
            if (imagePath != null && !imagePath.isEmpty()) {

                settingImagePath = imagePath;
//			 	chat_list.add(new MessageBean(senderID, receiverID,fileName));
//				adapter.notifyDataSetChanged();
//				listChat.invalidate();
                new Thread(new Runnable() {
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
////	                                 tv.setText("uploading started.....");
                            }
                        });

                        new UploadFileToServer().execute(imagePath);
//	                  System.out.println("RES : " + response);                         
                    }
                }).start();

            }
        } else {
            //	Toast.makeText(getActivity().getApplicationContext(),"Sorry!! You are not Connected to Internet!",100).show();
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


    private void receiveData() {
        if (isNetworkAvailable()) {
            chat_list.clear();
            if (MainMenu.isGpsOn)
                userLocation = setUserLocation();
            if (userLocation == null)
                userLocation = new StringBuilder("Can't get location");
//	userLocation =userLocation ==null?new StringBuilder("Location is unknown"):userLocation;
            chat_list.add(new MessageBean(SplashActivity.mechanicId, SplashActivity.usersDetailBean.userId, "Hey, How may I help You.", "", "", userLocation.toString()));

            MyServiceReceiveData myserrecdata = new MyServiceReceiveData();
            myserrecdata.execute();
        }

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),chat_list);
    }

    @Override
    public void onResume() {
        super.onResume();
//		mStatusChecker.run();
        //Delay to resume the thread
        //mHandler.postDelayed(mStatusChecker,5000);
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onStop() {
        //good remove
//		 mHandler.removeCallbacks(mStatusChecker);
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        mHandler.removeCallbacks(mStatusChecker);
        super.onDestroy();
    }

    private class MyServiceReceiveData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
//			wv.setVisibility(WebView.VISIBLE);
        }

        ;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String receiveDataUrl = null;
            if (SplashActivity.usersDetailBean.userType == 3) {
                receiveDataUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/chat_details/?sender_id=" + SplashActivity.usersDetailBean.userId + "&receiver_id=" + SplashActivity.mechanicId;
            } else {
                receiveDataUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/chat_details/?sender_id=" + SplashActivity.mechanicId + "&receiver_id=" + SplashActivity.usersDetailBean.userId;

            }

//			chat_list.add(new MessageBean(SplashActivity.mechanicId,SplashActivity.usersDetailBean.userId,"Hey, How may I help You.","","",userLocation.toString()));
            //	Log.v("link== ", receiveDataUrl);
            HttpPost httppost = new HttpPost(receiveDataUrl);
            DefaultHttpClient dhtpc = new DefaultHttpClient();
            try {
                HttpResponse httpresponse = dhtpc.execute(httppost);
                HttpEntity httpentity = httpresponse.getEntity();
                if (httpentity != null) {
                    receiveEntity = EntityUtils.toString(httpentity);

                    JSONObject jObj = new JSONObject(receiveEntity);
                    JSONArray main_arr = jObj.optJSONArray("Message");
                    chat_list.clear();
                    chat_list.add(new MessageBean(SplashActivity.mechanicId, SplashActivity.usersDetailBean.userId, "Hey, How may I help You.", "", "", userLocation.toString()));
                    for (int i = 0; i < main_arr.length(); i++) {
                        JSONObject main_obj = main_arr.optJSONObject(i);
                        if (main_obj != null) {
//						JSONObject tblChat=main_obj.optJSONObject("TblChat");
//						sender_ID=tblChat.getString("sender_id");
//						receiver_ID=tblChat.getString("receiver_id");
                            if (!main_obj.getString("message").matches("No Chat Found")) {
                                MessageBean mBean = new MessageBean(main_obj.getString("sender_id"), main_obj.getString("receiver_id"), main_obj.getString("message"), main_obj.getString("image"), main_obj.getString("image_path"), userLocation.toString());
                                mBean.downloadedImagePath = main_obj.getString("downloaded_image_path");
                                mBean.chatID = main_obj.getString("id");
                                chat_list.add(mBean);
                                // Log.v("Message== ", main_obj.getString("message"));
                            }
                        }
                    }


                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String receiveDataUrl) {
            // TODO Auto-generated method stub
//			wv.setVisibility(WebView.GONE);
//			adapter.notifyDataSetChanged();
//		       listChat.invalidate();	 	
            new_chat_list = chat_list;
            synchronized (ChatMain.getInstance()) {
                try {
                    ChatMainCustom newAdapter = new ChatMainCustom(getActivity(), new_chat_list, SplashActivity.usersDetailBean.userId, SplashActivity.mechanicId);
                    listChat.setAdapter(newAdapter);

                } catch (Exception e) {
                    listChat.setAdapter(null);
                }
//			adapter.notifyDataSetChanged();
                listChat.invalidate();
            }
        }

    }


    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

        @Override
        protected Double doInBackground(String... params) {

            postText();


            return null;
        }

        protected void onPostExecute(Double result) {
            // pb.setVisibility(View.GONE);

            new_chat_list = chat_list;
            try {

                ChatMainCustom newAdapter = new ChatMainCustom(getActivity(), new_chat_list, SplashActivity.usersDetailBean.userId,
                        SplashActivity.mechanicId);
                listChat.setAdapter(newAdapter);
                listChat.invalidate();
            } catch (Exception e) {
                listChat.setAdapter(null);
                Log.d("remove adapter error ", e.getMessage());
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            //   pb.setProgress(progress[0]);
        }


        // this will post our text data
        private void postText() {
            try {
                // url where the data will be posted
                String postReceiverUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/start_clt_mecha_chat/?";
                //  Log.v("toyota", "postURL: " + postReceiverUrl);

                // HttpClient
                HttpClient httpClient = new DefaultHttpClient();

                // post header
                HttpPost httpPost = new HttpPost(postReceiverUrl);

                // add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("sender_id", SplashActivity.usersDetailBean.userId));
                nameValuePairs.add(new BasicNameValuePair("message", chat));
                nameValuePairs.add(new BasicNameValuePair("receiver_id", SplashActivity.mechanicId));
                nameValuePairs.add(new BasicNameValuePair("image", fileName));
                nameValuePairs.add(new BasicNameValuePair("image_path", imagePath));
                nameValuePairs.add(new BasicNameValuePair("location", userLocation.toString()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//                 if(fileName == null && imagePath == null)
//                 {
                chat_list.add(new MessageBean(SplashActivity.usersDetailBean.userId, SplashActivity.mechanicId, chat, "", "", userLocation.toString()));
//                 }
//                 else
//                 {
//                chat_list.add(new MessageBean(SplashActivity.usersDetailBean.userId,SplashActivity.mechanicId,chat,fileName,imagePath,userLocation.toString()));
//                 }
//                 fileName = null;
//                 imagePath = null;
                iconName = null;
                // execute HTTP post request
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {

                    String responseStr = EntityUtils.toString(resEntity).trim();
                    //   Log.v("toyota", "Response: " +  responseStr);
                    responsefromserver = responseStr;

                    // you can add an if statement here and do other actions based on the response
                }
//                 receiveData();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private class UploadFileToServer extends AsyncTask<String, String, String> {
        //		private ProgressDialog pdia;
        @Override
        protected void onPreExecute() {
        }

        ;

        @Override
        protected String doInBackground(String... params) {
            String res;
            res = Integer.toString(uploadFile(params[0]));
            return res;
            // TODO Auto-generated method stub
        }

        @Override
        protected void onPostExecute(String receiveDataUrl) {
            // TODO Auto-generated method stub
//			wv.setVisibility(WebView.GONE);
            p = null;
            imagePath = null;
            fileName = null;
            new_chat_list = chat_list;

            try {
                ChatMainCustom newAdapter = new ChatMainCustom(getActivity(), new_chat_list,
                        SplashActivity.usersDetailBean.userId, SplashActivity.mechanicId);
                listChat.setAdapter(newAdapter);
            } catch (Exception e) {
                listChat.setAdapter(null);
                Log.d("The Code Love ",e.getMessage());

            }

// adapter.notifyDataSetChanged();
            listChat.invalidate();
//			adapter.notifyDataSetChanged();
//		       listChat.invalidate();	 
            simpleWaitDialog.dismiss();
//		       simpleWaitDialog.dismiss();
        }

        public int uploadFile(String sourceFileUri) {
//	        String upLoadServerUri = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/save_chat_image";
            String upLoadServerUri = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/main_chat";
            String fileName = sourceFileUri;
            // Log.v("Chat Link== ",upLoadServerUri);
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
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
//	             conn.setRequestProperty("request_parameters",SplashActivity.usersDetailBean.userId);
//	             Log.v("sender== ", senderID);
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(url.toString());
//	             
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sender_id", SplashActivity.usersDetailBean.userId));
                params.add(new BasicNameValuePair("receiver_id", SplashActivity.mechanicId));
                params.add(new BasicNameValuePair("image", p));
                params.add(new BasicNameValuePair("image_path", imagePath));
                params.add(new BasicNameValuePair("location", userLocation.toString()));


                chat_list.add(new MessageBean(SplashActivity.usersDetailBean.userId, SplashActivity.mechanicId, chat, p, imagePath, userLocation.toString()));
                postRequest.setEntity(new UrlEncodedFormEntity(params));
//	             HttpResponse newResponse = httpClient.execute(postRequest);
//	             params.add(new BasicNameValuePair("thirdParam", paramValue3));

//	             conn.setRequestProperty("sender_id", SplashActivity.usersDetailBean.userId);
//	             conn.setRequestProperty("receiver_id", SplashActivity.mechanicId);
//	              conn.setRequestProperty("make",new UrlEncodedFormEntity(params).toString());
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"request_parameters\";filename=\"" + SplashActivity.usersDetailBean.userId + "\"" + lineEnd);

                dos.writeBytes(lineEnd);
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(dos, "UTF-8"));
                writer.write(getQuery(params));

                bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

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
                // Log.v("url=== ",conn.getURL().toString());
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
//	           p = null;
//	             imagePath = null; 
                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                if (serverResponseCode == 200) {
                    HttpResponse newResponse = httpClient.execute(postRequest);
                }

                //close the streams //
//	             writer.flush();
//	            	writer.close();
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
//	            dialog.dismiss();  
                ex.printStackTrace();
//	            Toast.makeText(ChatMain.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
//	            dialog.dismiss();  
                e.printStackTrace();
//	            Toast.makeText(ChatMain.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                // Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            }
//	        dialog.dismiss();       
            return serverResponseCode;
        }

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
