package com.betasoft.ToyotaMobi.fe.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.JavaBeans.MyOrderBeanClass;

public class MyOrderCustomAdapter extends ArrayAdapter<MyOrderBeanClass> 
{
	Activity context;
	int res;
	List<MyOrderBeanClass> orderList;

	public  MyOrderCustomAdapter(Activity context, List<MyOrderBeanClass> orderList) {
		super(context, android.R.layout.simple_list_item_1, orderList);
		this.context=context;

		this.orderList=orderList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater=context.getLayoutInflater();
	   	convertView=inflater.inflate(R.layout.my_order, null,true);

	   	TextView txt_items =(TextView) convertView.findViewById(R.id.my_order_items);
	   	TextView orderTxt = (TextView) convertView.findViewById(R.id.my_order_status);
	   	TextView amountTxt = (TextView) convertView.findViewById(R.id.my_order_cost);
		TextView txt_total_cost =(TextView) convertView.findViewById(R.id.my_order_total_cost);
		TextView txt_status =(TextView) convertView.findViewById(R.id.my_order_dynamic_status);
		TextView txt_date =(TextView) convertView.findViewById(R.id.order_date);

		txt_items.setText(orderList.get(position).Items);
		txt_total_cost.setText(orderList.get(position).Amount);
		txt_status.setText(orderList.get(position).Status);
		txt_date.setText(orderList.get(position).Date);


		return convertView;
	}

}