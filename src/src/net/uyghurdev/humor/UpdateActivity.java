package net.uyghurdev.humor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class UpdateActivity extends Activity {

	TextView localCount;
	TextView webCount;
	ImageButton update;
	ImageButton returns;
	Worker worker;
	ConnectivityManager cm;
	NetworkInfo networkInfo;
	boolean internet = true;
	boolean zeroUpdate = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatelayout);
		
		initialize();
		
		int downCount = worker.getItemCount();
		String updateCount;
		if (networkInfo != null
				&& networkInfo.getState() == NetworkInfo.State.CONNECTED) {
			internet = true;
			worker.checkFavoriteAndAdd();
			Configs.New_Item_Count = worker.getNewItemCount();
			if(Configs.New_Item_Count == 0){
				zeroUpdate = true;
			}
			updateCount = getString(R.string.new_item_count) + ": " + Configs.New_Item_Count;
		} else {
			internet = false;
			updateCount = getString(R.string.not_connected);
		}
		
		localCount.setText(getString(R.string.db_item_count) + ": " + downCount);
		webCount.setText(updateCount);
		if(internet && ! zeroUpdate){
			update.setEnabled(true);
			//update.setBackgroundResource(R.drawable.update_button_enable);
		}else{
			update.setEnabled(false);
			//update.setBackgroundResource(R.drawable.update_button_desible);
		}
		
		update.setOnClickListener(new ImageButton.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if(internet && !zeroUpdate){
					worker.update();
					worker.serverIdToArray();
				}
				showUpdateResult();
			}

			
			
		});
		returns.setOnClickListener(new ImageButton.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				UpdateActivity.this.finish();
				
			}

			
			
		});
		
	}
	
	private void initialize(){
		localCount = (TextView)findViewById(R.id.localCount);
		webCount = (TextView)findViewById(R.id.webCount);
		update = (ImageButton)findViewById(R.id.update);
		returns=(ImageButton)findViewById(R.id.returnBtn2);
		worker = new Worker(this);
		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = cm.getActiveNetworkInfo();
	}
	
	private void showUpdateResult() {
		// TODO Auto-generated method stub
		AlertDialog updateDialog = new AlertDialog.Builder(this).create();
		updateDialog.setTitle(getText(R.string.update_title));
		if (internet){
			if (zeroUpdate){
				updateDialog.setMessage(getString(R.string.zero_update));
			}else{
				updateDialog.setMessage(getString(R.string.updated));
			}
		}else{
			updateDialog.setMessage(getString(R.string.no_connection));
		}
		
		updateDialog.setButton(getString(R.string.close), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		updateDialog.show();
	
	}

}
