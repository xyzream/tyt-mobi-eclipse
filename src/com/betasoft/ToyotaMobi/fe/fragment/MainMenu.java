package com.betasoft.ToyotaMobi.fe.fragment;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;
import com.betasoft.ToyotaMobi.JavaBeans.SparePartsBean;
import com.betasoft.ToyotaMobi.app.AdsImageView;
import com.betasoft.ToyotaMobi.app.CustomVolleyRequestQueue;
import com.squareup.picasso.Transformation;

//this is the main fragment that shows app menus
public class MainMenu extends Fragment implements OnClickListener {
    private View mainView;
    public static boolean isGpsOn;
    public static String imageUrl, promotionUrl, imageName;
    private TextView txtInfo;
    public static ArrayList<SparePartsBean> buyingProductList;
    public static MainMenu instance;
    String savePath;
    Button nextbtn;
    RelativeLayout faq, myacc, vehiclepart, asknow;
    //ImageView banner;
    //NetworkImageView banner;
    AdsImageView banner;
    private ImageLoader mImageLoader;

    public static MainMenu getInstance() {
        if (instance == null) {
            instance = new MainMenu();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buyingProductList = new ArrayList<SparePartsBean>();
        //getting gps service
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //checks the status of gps is on or off
        //if gps is not on it will open a alert dialog and ask user to enable gps
        if (!statusOfGPS) {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            final String message = "Enable either GPS or any other location"
                    + " service to find current location.  Click OK to go to"
                    + " location services settings to let you do so.";

            builder.setMessage(message)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    getActivity().startActivity(new Intent(action));
                                    isGpsOn = true;
                                    d.dismiss();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                }
                            });
            builder.create().show();
        } else {
            isGpsOn = true;
        }

        mainView = inflater.inflate(R.layout.main_menu_screen, null);
        faq = (RelativeLayout) mainView.findViewById(R.id.FAQlayout);
        myacc = (RelativeLayout) mainView.findViewById(R.id.myaccountlayout);
        vehiclepart = (RelativeLayout) mainView.findViewById(R.id.vehiclepartlayout);
        asknow = (RelativeLayout) mainView.findViewById(R.id.asknowlayout);
        //banner = (ImageView) mainView.findViewById(R.id.footer);
        banner = (AdsImageView) mainView.findViewById(R.id.footer);

        banner.setOnClickListener(this);
        faq.setOnClickListener(this);
        myacc.setOnClickListener(this);
        vehiclepart.setOnClickListener(this);
        asknow.setOnClickListener(this);
        TextView tv = (TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
        tv.setText("Toyota Mobi");
        MyServiceReceiveData myserrecdata = new MyServiceReceiveData();
        myserrecdata.execute();
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //called when FAQ button clicked and takes user to FAQ page
            case R.id.FAQlayout:
                FragmentTransaction tx1 = getFragmentManager().beginTransaction();
                tx1.replace(R.id.content_frame, Fragment.instantiate(getActivity(), "com.betasoft.ToyotaMobi.fe.fragment.FAQ"));
                TextView tv = (TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
                tv.setText("FAQ");
                tx1.addToBackStack("faq");
                tx1.commit();

                break;
            //called when sparepart button clicked and takes user to SpareParts page
            case R.id.vehiclepartlayout:
                FragmentTransaction tx2 = getFragmentManager().beginTransaction();
                tx2.replace(R.id.content_frame, Fragment.instantiate(getActivity(), "com.betasoft.ToyotaMobi.fe.fragment.SpareParts"));
                TextView tv1 = (TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
                tv1.setText("Vehicle Parts");
                tx2.addToBackStack("vehicle");
                tx2.commit();
                break;
            //called when AskNow button clicked and takes user to ChatMain page
            case R.id.asknowlayout:
                Fragment fm = new ChatMain();
                FragmentTransaction tx3 = getFragmentManager().beginTransaction();
                tx3.replace(R.id.content_frame, fm);
                TextView tv2 = (TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
                tv2.setText("Chat");
                tx3.addToBackStack("ask");
                tx3.commit();
                break;
            //called when Service button clicked and takes user to Service page
            case R.id.myaccountlayout:
                FragmentTransaction tx4 = getFragmentManager().beginTransaction();
                tx4.replace(R.id.content_frame, Fragment.instantiate(getActivity(), "com.betasoft.ToyotaMobi.fe.fragment.ServiceFragment"));
                TextView tv4 = (TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
                tv4.setText("Book Vehicle Service");
                tx4.addToBackStack("servicefragment");
                tx4.commit();

                break;
            //when below displayed banner is clicked then it request to server and get url for promo then open in browser
            case R.id.footer:
                promotionUrl = promotionUrl == null ? "http://dakstoyota.com/" : promotionUrl;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(promotionUrl));
                startActivity(browserIntent);
//			MyServiceReceiveData myserrecdata=new  MyServiceReceiveData();
//		  	myserrecdata.execute();
                break;

        }


    }


    private class MyServiceReceiveData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

        }

        ;


        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String status = null;
            String receiveDataUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/promotions";
//		String receiveDataUrl="http://www.toyotamobi.com/toyotamobi/admin/Webservices/client_orders?client_id=554";
            //Log.v("link== ", receiveDataUrl);
            HttpPost httppost = new HttpPost(receiveDataUrl);
            DefaultHttpClient dhtpc = new DefaultHttpClient();
            try {
                HttpResponse httpresponse = dhtpc.execute(httppost);
                HttpEntity httpentity = httpresponse.getEntity();
                if (httpentity != null) {
                    String receiveEntity = EntityUtils.toString(httpentity);
                    JSONObject jObj = new JSONObject(receiveEntity);
                    JSONArray jsonArray = jObj.getJSONArray("Promotions");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject ob = jsonArray.getJSONObject(i);
                        if (ob.getString("image_path") != null) {
                            imageUrl = ob.getString("image_path");
                            promotionUrl = ob.getString("url");
                            imageName = ob.getString("image_name");
                        }

                    }
                    status = "Success";
                }
            } catch (ClientProtocolException e) {
                status = "Error";
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                status = "Error";
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                status = "Error";
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return status;
        }

        protected void onPostExecute(String receiveDataUrlx) {

            if (imageUrl != null) {
                Log.v("url=== ", imageUrl);
            }

            // Instantiate the RequestQueue.
            mImageLoader = CustomVolleyRequestQueue.getInstance(getActivity())
                    .getImageLoader();
            //Image URL - This can point to any image file supported by Android
            final String url = imageUrl;//"http://goo.gl/0rkaBz";
            /*mImageLoader.get(url, ImageLoader.getImageListener(banner,
                    R.drawable.error_logo, R.drawable.error_logo));
            banner.setImageUrl(url, mImageLoader);*/

            // Feed image
            if (url != null) {
                banner.setImageUrl(url, mImageLoader);
                banner.setVisibility(View.VISIBLE);
                banner .setResponseObserver(new AdsImageView.ResponseObserver() {
                    @Override
                    public void onError() {
                    }

                    @Override
                    public void onSuccess() {
                    }
                });
            } else {
                banner.setVisibility(View.GONE);
            }

            /*Picasso.with(getActivity())
                    .load(imageUrl)
                    .placeholder(R.drawable.error_logo)
                    .error(R.drawable.error_logo)
                    .fit().centerCrop()
                    .into(banner);*/
        }


        Transformation transformation = new Transformation() {

            @Override
            public Bitmap transform(Bitmap source) {
                //int targetWidth =MainMenu.this.getDeviceWidth(getActivity())[0];
                int targetWidth = banner.getWidth();

                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };

    }

    private int[] getDeviceWidth(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();

        int width = display.getWidth();
        int height = display.getHeight();
        int[] x = {width, height};
        return x;
    }

    //image downloading webservice to download image from our server
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
            Log.i("Async-Example", "onPreExecute Called");

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.i("Async-Example", "onPostExecute Called");
//			simpleWaitDialog.dismiss();
//			 BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//			 Bitmap d = BitmapFactory.decodeFile(savePath,
//                     bitmapOptions); 
            //using matrix to set image rotation
//				 Matrix matrix = new Matrix();
//               matrix.postRotate(rotate);
//				 int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
//               Bitmap scaled = Bitmap.createScaledBitmap(d,400,400, true);
//               Bitmap cropped = Bitmap.createBitmap(scaled,0,0, 400,400, matrix, true);
            //setting image at their position
//			banner.setBackgroundResource(0);
            if (result != null) {

                banner.setImageBitmap(result);
                SplashActivity.session.setBannerImageName(imageName);
            }
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
                ByteArrayBuffer baf = new ByteArrayBuffer(40);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                FileOutputStream fos = new FileOutputStream(fileSavePath);
                fos.write(baf.toByteArray());
                fos.close();
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(fileSavePath, bitmapOptions);
                int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                int nw = (int) (bitmap.getHeight() * (1224.0 / bitmap.getHeight()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, nw, nh, true);
                bmImg = scaled;
//		    is.reset();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return bmImg;
        }


    }


}