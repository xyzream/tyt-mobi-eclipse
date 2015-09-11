package com.betasoft.ToyotaMobi.fe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ug.co.yo.payments.YoPaymentsAPIClient;
import ug.co.yo.payments.YoPaymentsResponse;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;
import com.betasoft.ToyotaMobi.fe.fragment.BuyPart;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.view.View;

public class MobileMoneyPayment extends Activity {
    public static final String TAG = "Mobile Money ";
    public EditText phone;
    public EditText amt;
    public TextView decs;
    public TextView msg;
    public Button actionCall;
    //public static final String SERVICE_URL = "http://41.220.12.206/services/yopaymentsdev/task.php";
    public static final String SERVICE_URL = "https://paymentsapi1.yo.co.ug/ybs/task.php";
    public YoPaymentsAPIClient yoPaymentsClient;
    private ProgressDialog pDialog;

    String name, price, deliveryAddress, transactionID, responseData, payemtMethod;
    private int paymentOpt;
    byte needService = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_money_payment_activity);
        // getActionBar().hide();
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");

        yoPaymentsClient = new YoPaymentsAPIClient("100814654060", "4ert-rZbv-srhh-d6Rk-6Lap-27Ll-ufhc-gXcp");
        //yoPaymentsClient = new YoPaymentsAPIClient("90005142126", "1178700810");
        phone = (EditText) findViewById(R.id.phone);
        amt = (EditText) findViewById(R.id.amt);
        amt.setEnabled(false);

        decs = (TextView) findViewById(R.id.reason);
        msg = (TextView) findViewById(R.id.msg);
        actionCall = (Button) findViewById(R.id.sendPayment);

        // get extras
        name = getIntent().getExtras().getString("itemName");
        price = getIntent().getExtras().getString("itemPrice");
        needService = getIntent().getExtras().getByte("needService");
        deliveryAddress = getIntent().getExtras().getString("deliveryAddress");
        this.paymentOpt = getIntent().getExtras().getInt("paymentOpt");

        initVal();

        actionCall.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                String exeCode = RandomStringUtils.randomAlphanumeric(20);

                final String inputXML = yoPaymentsClient.createDepositXml(
                        Double.parseDouble(amt.getText().toString()), phone
                                .getText().toString(),
                        "DAKS - TOYOTAmobi Part No." + name, exeCode);

                Log.d(TAG, "REF CODE : >" + exeCode);
                try {

                    pDialog.setMessage("Processing Payment . . .");
                    showpDialog();
                    // make request
                    // callVolleyRequest( inputXML);
                    msg.setVisibility(View.VISIBLE);
                    msg.setText("Processing payment");
                    MobileMoneyPaymentAsyncTask xx = new MobileMoneyPaymentAsyncTask();
                    xx.execute(inputXML);

                } catch (Exception e) {
                    Log.d("PAYMENT", "Error . . ");
                    msg.setText("Payment Failed :" + e.getMessage());
                    e.printStackTrace();
                }

            }

        });

    }


    private void initVal() {
        // TODO Auto-generated method stub
        decs.setText("Total Price :" + price + " UGX , Spare Part(s):" + name);
        amt.setText(price);
    }

    private void showpDialog() {
        pDialog.show();
    }

    private void hidepDialog() {
        pDialog.dismiss();
    }

    public void notifySYS(String title, String msg) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title).setContentText(msg);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(Integer.parseInt(RandomStringUtils.randomNumeric(4)), mBuilder.build());
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

        @Override
        protected Double doInBackground(String... params) {
            postText();
            return null;
        }

        protected void onPostExecute(Double result) {
            // after sending detail to server, app goes to MainPage

        }

        protected void onProgressUpdate(Integer... progress) {
            // pb.setProgress(progress[0]);
        }

        // this will post our text data
        private void postText() {
            StringBuilder builder = new StringBuilder();

            try {
                // url where the data will be posted
                String postReceiverUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/manage_order?";
                Log.v("toyota", "postURL: " + postReceiverUrl);
                Log.v("TransID== ", transactionID);
                // HttpClient
                HttpClient httpClient = new DefaultHttpClient();

                // post header
                HttpPost httpPost = new HttpPost(postReceiverUrl);

                // add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        6);
                nameValuePairs.add(new BasicNameValuePair("client_id",
                        SplashActivity.usersDetailBean.userId));
                nameValuePairs.add(new BasicNameValuePair("sparepart_items",
                        name));
                String shillingPrice = Long
                        .toString((Long.parseLong(price) * Long
                                .parseLong(BuyPart.shillingToUSD)));

                Log.d("Money Value ", shillingPrice);

                nameValuePairs.add(new BasicNameValuePair("total_price",
                        price));
                nameValuePairs.add(new BasicNameValuePair("payment_opt",
                        "MOBILE_MONEY"));
                nameValuePairs.add(new BasicNameValuePair("delivery_address",
                        deliveryAddress));
                nameValuePairs.add(new BasicNameValuePair("transaction_id",
                        transactionID));
                if (needService == 1)
                    nameValuePairs.add(new BasicNameValuePair("service", "1"));
                else
                    nameValuePairs.add(new BasicNameValuePair("service", "0"));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // execute HTTP post request
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();
                // StatusLine line = response.getStatusLine();
                InputStream content = resEntity.getContent();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));

                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    Log.v("Build value", line);
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Log.v("Build value", builder.toString());
                JSONObject jObj = new JSONObject(builder.toString());
                JSONArray nameObj = jObj.getJSONArray("TblOrder");

                String response = ((JSONObject) nameObj.get(0))
                        .getString("200");
                Log.v("response== ", response);
                responseData = response;

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
        }

    }

    // mobile money payment . . .
    private class MobileMoneyPaymentAsyncTask extends
            AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String txt = postText(params[0]);

            return txt;
        }

        protected void onPostExecute(String response) {
            hidepDialog();
            // after sending detail to server, app goes to MainPage

            // get data
            YoPaymentsResponse yoPaymentsResponse = new YoPaymentsResponse(
                    response);
            String status = yoPaymentsResponse.getStatus();
            if ("OK".equals(status)) {
                msg.setText("Payment was successfull");
                notifySYS("Mobile Payment ", "Payment was successful " + name);
                msg.setText("PAYMENT SUCCESSFUL");
                String transactionRef = yoPaymentsResponse.getTransactionReference();
                transactionID = transactionRef;
                MyAsyncTask tsk = new MyAsyncTask();
                tsk.execute();

            } else {
                switch (yoPaymentsResponse.getStatusCode()) {
                    case -34:
                        msg.setText("YOU HAVE INSUFFICIENT FUNDS");
                        notifySYS("Mobile Payment ", "YOU HAVE INSUFFICIENT FUNDS");
                        break;
                    case -40:
                        msg.setText("WE ONLY SUPPORT MTN MOBILE PAYMENT ");
                        notifySYS("Mobile Payment ", "WE ONLY SUPPORT MTN MOBILE PAYMENT");
                        break;
                    case -39:
                        msg.setText("WE ONLY SUPPORT MTN MOBILE PAYMENT ");
                        notifySYS("Mobile Payment ", "WE ONLY SUPPORT MTN MOBILE PAYMENT");
                        break;
                    default:
                        msg.setText("PAYMENT FAILED ");
                        notifySYS("Mobile Payment ", "PAYMENT FAILED");

                        break;
                }

                String transactionRef = yoPaymentsResponse.getTransactionReference();
            }


        }

        protected void onProgressUpdate(Integer... progress) {
            // pb.setProgress(progress[0]);
        }

        // this will post our text data
        private String postText(String inputXML) {
            StringBuilder builder = new StringBuilder();
            Log.d("price", inputXML);
            try {
                // url where the data will be posted
                String postReceiverUrl = SERVICE_URL;
                Log.v("toyota", "postURL: " + postReceiverUrl);

                // HttpClient
                HttpClient httpClient = new DefaultHttpClient();

                StringEntity entity = new StringEntity(inputXML, "UTF-8");

                // post header
                HttpPost httppost = new HttpPost(postReceiverUrl);
                httppost.setEntity(entity);
                httppost.setHeader("Content-type", "text/xml");
                httppost.setHeader("Content-transfer-encoding", "text");

                // execute HTTP post request
                HttpResponse response = httpClient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                // StatusLine line = response.getStatusLine();
                InputStream content = resEntity.getContent();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));

                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    Log.v("Build value", line);
                }
                return builder.toString();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

    }

}
