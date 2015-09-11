package com.betasoft.ToyotaMobi.fe;





import com.betasoft.ToyotaMobi.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SpareParts extends Activity {
	
	 Button s;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_vehicle_screen);
		s=(Button)findViewById(R.id.searchicon);
		s.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	Intent i=new Intent(SpareParts.this,Search.class);
			//	startActivity(i);
			}
		});
	}
}



		
	


