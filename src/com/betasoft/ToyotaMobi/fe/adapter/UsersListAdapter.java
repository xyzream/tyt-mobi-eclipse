package com.betasoft.ToyotaMobi.fe.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.JavaBeans.UserInfoBean;

public class UsersListAdapter extends ArrayAdapter<UserInfoBean>{
	Activity ctx;
	List<UserInfoBean> productsData;
	TextView txtName,txtContact;
		public UsersListAdapter(Activity context, int resource, List<UserInfoBean> objects) {
			super(context, resource, objects);
			ctx=context;
			productsData=objects;
			
		}
	    public long getItemId(int position) {
	        return position;
	    }
	    public View getView(int position,View view,ViewGroup parent) {
	   	 LayoutInflater inflater=ctx.getLayoutInflater();
	   	 View rowView=inflater.inflate(R.layout.user_list_item, null,true);
	   	 
	   	txtName = (TextView) rowView.findViewById(R.id.heading_name);
	   	txtContact = (TextView) rowView.findViewById(R.id.heading_contact);
	   	 
	   	txtName.setText(productsData.get(position).fullName);
		txtContact.setText(productsData.get(position).phoneNum);
	   	 return rowView;
	   	 
	   	 }


	}