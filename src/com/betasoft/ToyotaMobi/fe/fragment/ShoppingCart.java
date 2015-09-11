package com.betasoft.ToyotaMobi.fe.fragment;

import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;

public class ShoppingCart extends Fragment {

	private View mainView;
	CheckBox mechanicCB, tandcCB;
	Button proceedBtn, otherItemBtn;
	TextView sumTV;
	float totalAmount = 0;
	TableLayout tableLayout;
	StringBuilder itemsName = new StringBuilder();
	byte needService = 0;
	public static ShoppingCart instance;

	public static ShoppingCart getInstance() {
		if (instance == null) {
			instance = new ShoppingCart();
		}
		return instance;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView = inflater.inflate(R.layout.shopping_cart, null);
		mechanicCB = (CheckBox) mainView.findViewById(R.id.mechanic_rbtn);
		tandcCB = (CheckBox) mainView.findViewById(R.id.tandc_rbtn);
		proceedBtn = (Button) mainView.findViewById(R.id.proceed_btn);
		sumTV = (TextView) mainView.findViewById(R.id.Result_txt);
		otherItemBtn = (Button) mainView.findViewById(R.id.add_other_item_btn);
		tableLayout = (TableLayout) mainView.findViewById(R.id.table_layout);

		proceedBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (tandcCB.isChecked()) {
					Fragment fragment = new PaymentGateway();
					Bundle bun = new Bundle();
					bun.putByte("NeedService", needService);
					bun.putString("TotalAmount", sumTV.getText().toString());
					bun.putString("ItemsDetail", itemsName.toString());
					fragment.setArguments(bun);
					FragmentTransaction tx1 = getActivity()
							.getFragmentManager().beginTransaction();
					tx1.replace(R.id.content_frame, fragment);
					TextView tv = (TextView) getActivity().getActionBar()
							.getCustomView().findViewById(R.id.actiontitle);
					tv.setText("Select Payment Method");
					tx1.addToBackStack(null);
					tx1.commit();
				} else {

					Toast.makeText(getActivity().getApplicationContext(),
							"Please accept Terms & Conditions.", Toast.LENGTH_LONG).show();
				}
			}
		});
		mechanicCB.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mechanicCB.isChecked()) {
					needService = 1;
				} else {
					needService = 0;
				}
			}
		});
		otherItemBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Fragment fragment = new SpareParts();
				FragmentTransaction tx1 = getActivity().getFragmentManager()
						.beginTransaction();
				tx1.replace(R.id.content_frame, fragment);
				TextView tv = (TextView) getActivity().getActionBar()
						.getCustomView().findViewById(R.id.actiontitle);
				tv.setText("Select Another Product");
				tx1.addToBackStack(null);
				tx1.commit();

			}
		});

		TableRow headerRow = new TableRow(getActivity());
		headerRow.setLayoutParams(new LayoutParams(
		LayoutParams.FILL_PARENT,
		LayoutParams.WRAP_CONTENT));
        TextView partNumberHeader = new TextView(getActivity());
		partNumberHeader.setText("Part No.");
		partNumberHeader.setPadding(10, 0, 5, 0);
		partNumberHeader.setBackgroundResource(R.drawable.color);
		partNumberHeader.setTextSize(16F);
		partNumberHeader.setTextColor(Color.BLACK);
		headerRow.addView(partNumberHeader);
		TextView partNameHeader = new TextView(getActivity());
		partNameHeader.setPadding(10, 0, 5, 0);
		partNameHeader.setText("Part Name");
		partNameHeader.setWidth(250);
		partNameHeader.setBackgroundResource(R.drawable.color);
		partNameHeader.setTextSize(16F);
		partNameHeader.setTextColor(Color.BLACK);
		headerRow.addView(partNameHeader);

		TextView partQtyHeader = new TextView(getActivity());
		partQtyHeader.setText("Qty");
		partQtyHeader.setPadding(10, 0, 5, 0);
		partQtyHeader.setTextSize(16F);
		partQtyHeader.setBackgroundResource(R.drawable.color);
		partQtyHeader.setTextColor(Color.BLACK);
		headerRow.addView(partQtyHeader);

		TextView partAmountHeader = new TextView(getActivity());
		partAmountHeader.setText("Amount");
		partAmountHeader.setPadding(10, 0, 5, 0);
		partAmountHeader.setTextSize(16F);
		partAmountHeader.setBackgroundResource(R.drawable.color);
		partAmountHeader.setTextColor(Color.BLACK);
		headerRow.addView(partAmountHeader);

		// finally add this to the table row
		tableLayout.addView(headerRow, tableLayout.getLayoutParams());

		Integer count = 0;
		for (int k = 1; k <= MainMenu.buyingProductList.size(); k++) {
			TableRow tr = new TableRow(getActivity());
			if (count % 2 != 0)
				tr.setBackgroundColor(Color.GRAY);
			tr.setId(100 + count);
			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			// Create two columns to add as table data
			// Create a TextView to add date
			TextView partNumberTV = new TextView(getActivity());
			partNumberTV.setId(200 + count);
			partNumberTV.setText(MainMenu.buyingProductList.get(k - 1).part_number);
			partNumberTV.setPadding(10, 0, 5, 0);
			partNumberTV.setTextSize(13F);
			partNumberTV.setTextColor(Color.BLACK);
			tr.addView(partNumberTV);
			TextView partNameTV = new TextView(getActivity());
			partNameTV.setId(200 + count);
			partNameTV.setWidth(200);
			partNameTV.setPadding(10, 0, 5, 0);

			partNameTV.setText(MainMenu.buyingProductList.get(k - 1).sparepart_name);
			partNameTV.setTextSize(13F);
			partNameTV.setTextColor(Color.BLACK);
			tr.addView(partNameTV);

			TextView partQtyTV = new TextView(getActivity());
			partQtyTV.setId(200 + count);
			partQtyTV.setText(MainMenu.buyingProductList.get(k - 1).partQuantity);
			partQtyTV.setPadding(10, 0, 5, 0);
			partQtyTV.setTextSize(13F);
			partQtyTV.setTextColor(Color.BLACK);
			tr.addView(partQtyTV);
			float qty = Float.parseFloat(MainMenu.buyingProductList.get(k - 1).partQuantity);
			String pr = MainMenu.buyingProductList.get(k - 1).sparepart_price.replace(",", "");
			float amount = Float.parseFloat(pr);
			float totalProductAmount = qty * amount;

			totalAmount = totalAmount + totalProductAmount;
			TextView partAmountTV = new TextView(getActivity());
			partAmountTV.setId(200 + count);
			partAmountTV.setText(Float.toString(totalProductAmount));
			partAmountTV.setPadding(10, 0, 5, 0);
			partAmountTV.setTextSize(13F);
			partAmountTV.setTextColor(Color.BLACK);
			tr.addView(partAmountTV);

			// finally add this to the table row
			tableLayout.addView(tr, tableLayout.getLayoutParams());
			count++;

			itemsName.append(partNameTV.getText().toString() + "("
					+ partQtyTV.getText().toString() + ")" + "/"
					+ partNumberTV.getText().toString());
			itemsName.append(",");
		}

		sumTV.setText(Float.toString(totalAmount));

		return mainView;

	}

}
