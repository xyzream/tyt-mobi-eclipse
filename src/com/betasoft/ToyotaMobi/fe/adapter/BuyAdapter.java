package com.betasoft.ToyotaMobi.fe.adapter;
	
	
	import java.util.List;

	import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.JavaBeans.SparePartsBean;
	import com.betasoft.ToyotaMobi.fe.fragment.Search;
	import com.betasoft.ToyotaMobi.fe.fragment.SparePartFullDetail;




	import android.app.Activity;
	import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
	import android.os.Bundle;
	import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
	import android.widget.ArrayAdapter;
import android.widget.Button;
	import android.widget.PopupWindow;
import android.widget.TextView;

public class BuyAdapter extends ArrayAdapter<SparePartsBean>{
		 private Activity activity;
		    private  Activity context;
		    private  Integer[] imgid;
		    List<SparePartsBean> partsList;
		    LayoutInflater layoutInflater;
		    PopupWindow popup;
		   
		    public BuyAdapter(Activity ctx, List<SparePartsBean> list) {
		        super(ctx, R.layout.list_row, list);
		        this.context=ctx;
		      
		        partsList = list;
		       // inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        //imageLoader=new ImageLoader(activity.getApplicationContext());
		    }
		  
		    public long getItemId(int position) {
		        return position;
		    }
		    
		    
		    public View getView(final int position,View view,ViewGroup parent) {
		    	 layoutInflater = (LayoutInflater) context
		    		     .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	 View rowView=layoutInflater.inflate(R.layout.list_parts_row, null,true);
		    	 
		    	 TextView txtTitle = (TextView) rowView.findViewById(R.id.partnum);
		    	 TextView txtCost = (TextView) rowView.findViewById(R.id.partcost);
		    
//		    	 TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
		    	 
		    	 txtTitle.setText(txtTitle.getText()+partsList.get(position).sparepart_name);
		    	 txtCost.setText(txtCost.getText()+partsList.get(position).sparepart_price);
		    	 Button buyPartBtn = (Button) rowView.findViewById(R.id.buypart);
		    	 Button detailBtn = (Button) rowView.findViewById(R.id.detail);
		    	 buyPartBtn.setOnClickListener(new View.OnClickListener() {
		    	
		        @Override

		    	 public void onClick(View v) {
		        	Fragment confirmFragment = new Search();
		        	FragmentTransaction tx1 =context.getFragmentManager().beginTransaction();
		           Bundle bun = new Bundle();
		        	bun.putString("itemName",partsList.get(position).sparepart_name);
		        	bun.putString("itemPrice",partsList.get(position).sparepart_price);
		        	bun.putString("itemNumber",partsList.get(position).part_number);
		        	confirmFragment.setArguments(bun);
		        	tx1.replace(R.id.content_frame,confirmFragment);
		            TextView tv=(TextView)context.getActionBar().getCustomView().findViewById(R.id.actiontitle);
		            tv.setText("Buy Parts");
		            tx1.addToBackStack("vehicle");
		         /* ImageButton img=(ImageButton)context.getActionBar().getCustomView().findViewById(R.id.search);
		          img.setBackgroundResource(R.drawable.search_icon);*/
		            
		            
		            tx1.commit();	
		        	
//		        	Intent in = new Intent(context,BuySparePart.class);
//		        	Bundle bun = new Bundle();
//		        	bun.putString("itemName",partsList.get(position).sparepart_name);
//		        	bun.putString("itemPrice",partsList.get(position).sparepart_price);
//		        	in.putExtras(bun);
//		        	
//		        	context.startActivity(in);
		        	
		    	}

		    	 });
		    	 
		    	 detailBtn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Bundle b=new Bundle();
						b.putString("itemName",partsList.get(position).sparepart_name);
						b.putString("itemNum",partsList.get(position).part_number);
						b.putString("itemPrice",partsList.get(position).sparepart_price);
						b.putString("imagePath",partsList.get(position).image_id);
						Fragment fragment = new SparePartFullDetail();
						fragment.setArguments(b);
						FragmentTransaction tx1 =context.getFragmentManager().beginTransaction();
						tx1.replace(R.id.content_frame,fragment);
			            TextView tv=(TextView)context.getActionBar().getCustomView().findViewById(R.id.actiontitle);
//			            tv.setText("Spare Part Details");
			            tx1.addToBackStack("vehicle");
			       /*   ImageButton img=(ImageButton)context.getActionBar().getCustomView().findViewById(R.id.search);
			          img.setBackgroundResource(R.drawable.search_icon);
			       */     
			            
			            tx1.commit();		
			    	
						
					}
				});
		    	 
		    	 
		 			//	
				return rowView;
		 			
//	    	 extratxt.setText("Description "+itemname[position]);
	    	
	    	 
			 			//	
			 			
//		    	 extratxt.setText("Description "+itemname[position]);
		    	 
		    	 
		    	 }
	

}
