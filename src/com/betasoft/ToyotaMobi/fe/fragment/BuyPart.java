package com.betasoft.ToyotaMobi.fe.fragment;


import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.JavaBeans.SparePartsBean;
import com.betasoft.ToyotaMobi.fe.adapter.BuyAdapter;

public class BuyPart extends Fragment implements OnClickListener {

    private static final String TAG = "Register";
    View mainView;
    String receiveEntity;
    Button nextbtn;
    BuyAdapter adapter;
    public static String shillingToUSD;

    ArrayList<String> productList;
    String partDetail = "";
    ListView list;
    Button buybtn;
    private TextView txtInfo;

    public static BuyPart instance;
    Button buy;
    Button detail;
    public ProgressDialog progressDialog;
    ArrayList<SparePartsBean> sparePartsList;

    public static BuyPart getInstance() {
        if (instance == null) {
            instance = new BuyPart();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mainView = inflater.inflate(R.layout.activity_common_list, null);
        sparePartsList = new ArrayList<SparePartsBean>();  //Raj
        progressDialog = new ProgressDialog(getActivity());

        TextView tv = (TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
        if (tv.getText().toString().matches("Confirm Spare Part"))
            partDetail = "";
        else
            partDetail = tv.getText().toString();
        list = (ListView) mainView.findViewById(R.id.common_list);
        adapter = new BuyAdapter(getActivity(), sparePartsList);
        list.setAdapter(adapter);
        if (isNetworkAvailable()) {
            MyServiceReceiveData myserrecdata = new MyServiceReceiveData();
            myserrecdata.execute();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Sorry!! You are not Connected to Internet!", Toast.LENGTH_LONG).show();
        }
        //nextbtn=(Button)mainView.findViewById(R.id.nextbt);
        //	nextbtn.setOnClickListener(this);

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
        Fragment fm = new Search();
        FragmentTransaction tx1 = getFragmentManager().beginTransaction();
        tx1.replace(R.id.content_frame, fm);
        TextView tv = (TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
        tv.setText("Search Parts");
        tx1.addToBackStack(null);
         /* ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
	      img.setBackgroundResource(R.drawable.search_icon);*/


        tx1.commit();




        //	FragmentTransaction tx1 = getFragmentManager().beginTransaction();
        //  tx1.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.MainMenu" ));
        //  tx1.commit();
    }


    // Raj code Start
    private class MyServiceReceiveData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            progressDialog.show();
            //Rahul code end

        }

        ;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            //String receiveDataUrl="http://www.toyotamobi.com/toyotamobi/admin/Webservices/spare_part_details?part_name_or_number="+partDetail;
            String receiveDataUrl;
            if (partDetail.matches("Spare Parts")) {
                receiveDataUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/spare_part_details?part_name_or_number=";

            } else {
                receiveDataUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/spare_part_details?part_name_or_number="
                        + partDetail;
            }

            //Log.v("link== ", receiveDataUrl);
            HttpPost httppost = new HttpPost(receiveDataUrl);
            DefaultHttpClient dhtpc = new DefaultHttpClient();
            try {
                HttpResponse httpresponse = dhtpc.execute(httppost);
                HttpEntity httpentity = httpresponse.getEntity();
                if (httpentity != null) {
                    receiveEntity = EntityUtils.toString(httpentity);
                    JSONObject jObj = new JSONObject(receiveEntity);
                    JSONArray jsonArray = jObj.getJSONArray("TblSparepart");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject ob = jsonArray.getJSONObject(i);
                        if (ob.getString("sparepart_name") != null) {
                          //  Log.v("id== ", ob.getString("sparepart_name"));
                            SparePartsBean spBean = new SparePartsBean(ob.getString("sparepart_name"),
                                    ob.getString("id"), ob.getString("sparepart_price"), ob.getString("part_number"), ob.getString("image_path"));
//										spBean.shilling_to_USD = ob.getString("shilling_to_USD");
                            shillingToUSD = ob.getString("shilling_to_USD");

                            sparePartsList.add(spBean);
                            //
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
//						wv.setVisibility(WebView.GONE);
            adapter.notifyDataSetChanged();
            list.invalidate();
            //Rahul code start
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            //Rahul code end

        }

    }

    // Raj code End

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
		
		
	



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	