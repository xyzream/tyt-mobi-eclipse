package com.betasoft.ToyotaMobi.fe.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.fe.BuySparePart;

public class PaymentGateway extends Fragment {
	private View mainView;
	RadioButton mobileMoney, creditCard;
	Button nextBtn, backBtn;
	EditText deliveryDetailsET;
	RadioGroup radioGroup;
	String totalAmount, itemsDetail;
	byte needService = 0;
	public static PaymentGateway instance;

	public static PaymentGateway getInstance() {
		if (instance == null) {
			instance = new PaymentGateway();
		}
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView = inflater.inflate(R.layout.payment_gateway, null);
		needService = getArguments().getByte("NeedService");
		totalAmount = getArguments().getString("TotalAmount");
		itemsDetail = getArguments().getString("ItemsDetail");
		mobileMoney = (RadioButton) mainView
				.findViewById(R.id.mobile_money_rbtn);
		creditCard = (RadioButton) mainView
				.findViewById(R.id.cradit_dabit_card_rbtn);
		nextBtn = (Button) mainView.findViewById(R.id.shopping_cart_next_btn);
		backBtn = (Button) mainView.findViewById(R.id.shopping_cart_back_btn);
		deliveryDetailsET = (EditText) mainView
				.findViewById(R.id.type_in_details_edt);
		radioGroup = (RadioGroup) mainView.findViewById(R.id.payment_option);

		nextBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!deliveryDetailsET.getText().toString().isEmpty()) {

					int paymentOpt = radioGroup.getCheckedRadioButtonId();
					
					int opt=R.string.paypal;
					if(paymentOpt==R.id.cradit_dabit_card_rbtn){
						opt=R.string.paypal;
					}else if(paymentOpt==R.id.mobile_money_rbtn){
						opt=R.string.mobilemoney;
					} 
					
					
					Intent in = new Intent(getActivity(), BuySparePart.class);
					Bundle bun = new Bundle();
					bun.putString("itemName", itemsDetail);
					bun.putString("itemPrice", totalAmount);
					bun.putString("deliveryAddress", deliveryDetailsET.getText().toString());
					bun.putByte("needService", needService);
					bun.putInt("paymentOpt", opt); 
					in.putExtras(bun);
					startActivity(in);
				} else {
					Toast.makeText(getActivity().getApplicationContext(),
							"Please enter Delivery Address Detail", Toast.LENGTH_LONG).show();
				}
			}
		});
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Fragment fragment = new ShoppingCart();
				FragmentTransaction tx1 = getActivity().getFragmentManager()
						.beginTransaction();
				tx1.replace(R.id.content_frame, fragment);
				TextView tv = (TextView) getActivity().getActionBar()
						.getCustomView().findViewById(R.id.actiontitle);
				tv.setText("Shopping Cart");
				// tv.setText("Spare Part Details");
				tx1.addToBackStack(null);
				/*
				 * ImageButton
				 * img=(ImageButton)getActivity().getActionBar().getCustomView
				 * ().findViewById(R.id.search);
				 * img.setBackgroundResource(R.drawable.search_icon);
				 */
				tx1.commit();
			}
		});
		return mainView;
	}

}
