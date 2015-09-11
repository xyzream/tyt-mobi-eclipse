package com.betasoft.ToyotaMobi.fe.adapter;

import java.util.List;

import com.betasoft.ToyotaMobi.R;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductAdapter extends ArrayAdapter<String>{
Activity ctx;
List<String> productsData;
TextView txtTitle;
	public ProductAdapter(Activity context, int resource, List<String> objects) {
		super(context, resource, objects);
		ctx=context;
		productsData=objects;
		
	}
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position,View view,ViewGroup parent) {
   	 LayoutInflater inflater=ctx.getLayoutInflater();
   	 View rowView=inflater.inflate(R.layout.list_row_without_image, null,true);
   	 
   	 txtTitle = (TextView) rowView.findViewById(R.id.heading);
   	 
   	 txtTitle.setText(productsData.get(position));
   	 return rowView;
   	 
   	 }


}