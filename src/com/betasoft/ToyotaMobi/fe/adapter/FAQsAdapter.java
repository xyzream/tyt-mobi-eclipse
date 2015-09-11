package com.betasoft.ToyotaMobi.fe.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.JavaBeans.FAQsBean;

public class FAQsAdapter  extends ArrayAdapter<FAQsBean>{
	Activity ctx;
	List<FAQsBean> productsData;
	TextView txtName,txtContact;
	ImageView arrow;
		public FAQsAdapter(Activity context, int resource, List<FAQsBean> objects) {
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
	   	 arrow = (ImageView) rowView.findViewById(R.id.arrow_icon);
	   	txtName = (TextView) rowView.findViewById(R.id.heading_name);
	   	txtContact = (TextView) rowView.findViewById(R.id.heading_contact);
	   	 arrow.setVisibility(ImageView.GONE);
	   	txtName.setText(productsData.get(position).faqQuestion);
		txtContact.setText(productsData.get(position).faqAnswer);
	   	 return rowView;
	   	 
	   	 }


	}