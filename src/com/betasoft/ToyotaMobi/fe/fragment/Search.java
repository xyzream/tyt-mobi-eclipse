package com.betasoft.ToyotaMobi.fe.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.JavaBeans.SparePartsBean;

public class Search extends Fragment implements OnClickListener {

	private static final String TAG = "Register";

	private View mainView;

	private TextView txtInfo;

	public static Search instance;
	Button accessories,electrical,engine,other;
	Button confirmBtn,cancelBtn;
	EditText confirmET,quantityET;
	String partName,partPrice,partNumber;
	public static Search getInstance() {
		if (instance == null) {
			instance = new Search();
		}
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	

			
			mainView = inflater.inflate(R.layout.confirm_part, null);
			confirmBtn=(Button)mainView.findViewById(R.id.cnfm);
			cancelBtn=(Button)mainView.findViewById(R.id.cancel);
			cancelBtn.setOnClickListener(this);
			confirmET=(EditText)mainView.findViewById(R.id.partno);
			quantityET=(EditText)mainView.findViewById(R.id.quantity_txt);
			confirmBtn.setOnClickListener(this);
			TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
	         tv.setText("Confirm Spare Part");
	/*         ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
	         img.setBackgroundResource(android.R.color.transparent);*/
	         
	        partName = getArguments().getString("itemName");
	        partPrice = getArguments().getString("itemPrice");
	        partNumber = getArguments().getString("itemNumber");
		//	accessories=(Button)mainView.findViewById(R.id.accessories);
		//	electrical=(Button)mainView.findViewById(R.id.electricparts);
		//	engine=(Button)mainView.findViewById(R.id.engineparts);
		///	other=(Button)mainView.findViewById(R.id.otherparts);
			
	//accessories.setOnClickListener(this);
//	electrical.setOnClickListener(this);
//	engine.setOnClickListener(this);
	//other.setOnClickListener(this);
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
		switch(v.getId())
		{
		case R.id.cnfm:
			if(!confirmET.getText().toString().isEmpty() || !quantityET.getText().toString().isEmpty())
			{
				if(confirmET.getText().toString().matches(partNumber))
				{
				
					SparePartsBean partBean = new SparePartsBean();
					partBean.sparepart_name = partName;
					partBean.part_number = partNumber;
					partBean.sparepart_price = partPrice;
					partBean.partQuantity = quantityET.getText().toString();
					MainMenu.buyingProductList.add(partBean);
					Fragment fm  = new ShoppingCart();
					FragmentTransaction tx2 = getFragmentManager().beginTransaction();
					 tx2.replace(R.id.content_frame,fm);
					 TextView tv1=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
				     tv1.setText("Shopping Cart");
/*				   ImageButton img1=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
				   img1.setBackgroundResource(R.drawable.search_icon);*/
					 
					 tx2.addToBackStack("vehicle");
					 tx2.commit();
					
//					Toast.makeText(getActivity().getApplicationContext(),"Matches", 100).show();
				}
				else
				{
					Toast.makeText(getActivity().getApplicationContext(),"Part Number is not valid.", Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				Toast.makeText(getActivity().getApplicationContext(),"All Fields are Mandetory.", Toast.LENGTH_LONG).show();
			}
			
//			FragmentTransaction tx = getFragmentManager().beginTransaction();
//			tx.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.BuyPart" ));
//			 
//			 TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
//	         tv.setText("Buy Parts");
//	       ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
//	       img.setBackgroundResource(R.drawable.search_icon);
//	       tx.addToBackStack("buy part");
//			tx.commit();
			break;
			
		case R.id.cancel:
		{
			Fragment fm = new SpareParts();
			FragmentTransaction tx2 = getFragmentManager().beginTransaction();
			 tx2.replace(R.id.content_frame,fm);
			 TextView tv1=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
		     tv1.setText("Vehicle Parts");
/*		   ImageButton img1=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
		   img1.setBackgroundResource(R.drawable.search_icon);*/
			 
			 tx2.addToBackStack("vehicle");
			 tx2.commit();
			break;
		}
	/*	case R.id.accessories:
			FragmentTransaction tx1 = getFragmentManager().beginTransaction();
			tx1.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.Accessories" ));
			 tx1.addToBackStack("accesssories");
			tx1.commit();
			break;
		case R.id.engineparts:
			FragmentTransaction tx2 = getFragmentManager().beginTransaction();
			tx2.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.EngineParts" ));
			 tx2.addToBackStack("engine");
			tx2.commit();
			break;
		case R.id.electricparts:
	
			FragmentTransaction tx3 = getFragmentManager().beginTransaction();
			tx3.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.ElectricalParts" ));
			 tx3.addToBackStack("electrical");
			tx3.commit();
			break;
		case R.id.otherparts:
		
			FragmentTransaction tx4 = getFragmentManager().beginTransaction();
			tx4.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.OtherParts" ));
			 tx4.addToBackStack("other");
			tx4.commit();
			break;*/
				
		}
		
	}

	
	
	
}

