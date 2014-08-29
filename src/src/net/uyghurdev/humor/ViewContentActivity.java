package net.uyghurdev.humor;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ViewContentActivity extends Activity {
	/** Called when the activity is first created. */
	private ImageButton nextBtn;
	private ImageButton prevBtn;
	private ImageButton randomBtn;
	private ImageButton favoriteBtn;
	private TextView title;
	private TextView content;
	private SharedPreferences setPreferences;
	private SharedPreferences getPreferences;
	ItemObject object;
	Worker worker;
	int current = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();

		

		showObject();

		// ------------------------
		nextBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(current<Configs.ServerId.length-1){
					current++;
					
				}
				showObject();
			}
		});
		// --------------------------
		prevBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(current>0){
					current--;
				}
				showObject();
			}
		});
		// ----------------------------
		randomBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(Configs.ServerId.length>1){
					Random rnd = new Random();
					current = rnd.nextInt(Configs.ServerId.length - 1);
					showObject();
				}
			}
		});
		// -----------------------------
		favoriteBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (Configs.ServerId.length>0){
					if (object.getFavorate()){
						worker.removeFavorite(Configs.ServerId[current]);
						favoriteBtn.setBackgroundResource(R.drawable.notfavorited);
						object.setFavorate(false);
					}else{
						worker.addFavoriteNotSent(Configs.ServerId[current],object.getTitle());
						worker.setAsFavorite(Configs.ServerId[current]);
						object.setFavorate(true);
						favoriteBtn.setBackgroundResource(R.drawable.favorited);
						
					}
				}
				
			}
		});
		// ------------------------------

	}

	private void showObject() {
		// TODO Auto-generated method stub
		if (Configs.ServerId.length > 0) {
			object = worker.getItem(Configs.ServerId[current]);
			title.setText(object.getTitle());
			content.setText(object.getContent());
			if (object.getFavorate()) {
				favoriteBtn.setBackgroundResource(R.drawable.favorited);
			} else {
				favoriteBtn.setBackgroundResource(R.drawable.notfavorited);
			}
			worker.objectRead(Configs.ServerId[current]);
			Editor prefsPrivateEditor = setPreferences.edit();
			prefsPrivateEditor.putInt("serverId", Configs.ServerId[current]);
			prefsPrivateEditor.commit();
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		getPreferences = this.getSharedPreferences(Configs.SharedPreferences,
				Context.MODE_PRIVATE);
		setPreferences = getSharedPreferences(Configs.SharedPreferences, Context.MODE_PRIVATE);
		object = new ItemObject();
		worker = new Worker(this);
		worker.setAppInfo();
		nextBtn = (ImageButton) findViewById(R.id.nextBtn);
		prevBtn = (ImageButton) findViewById(R.id.prevBtn);
		randomBtn = (ImageButton) findViewById(R.id.randomBtn);
		favoriteBtn = (ImageButton) findViewById(R.id.favoriteBtn);
		title = (TextView) findViewById(R.id.txtTitle);
		content = (TextView) findViewById(R.id.txtContent);
		worker.serverIdToArray();
		current = getIndexOdServerId(getPreferences.getInt("serverId", 0));
		
	}

	private int getIndexOdServerId(int s) {
		// TODO Auto-generated method stub
		for (int x=0; x<Configs.ServerId.length; x++){
			if (Configs.ServerId[x]==s){
				return x;
			}
		}
		return 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, getString(R.string.remarks)).setIcon(
				R.drawable.about_icon);
		menu.add(0, 1, 0, getString(R.string.favorite)).setIcon(
				R.drawable.favorate_icon);
		menu.add(0, 2, 0, getString(R.string.updates)).setIcon(
				R.drawable.update_icon);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			about();
			break;
		case 1:
			showFavorite();
			break;
		case 2:
			showUpdate();
		}

		return false;

	}

	private void showUpdate() {
		// TODO Auto-generated method stub

		
		Intent intent = new Intent();
		intent.setClass(ViewContentActivity.this, UpdateActivity.class);
		startActivity(intent);
		
	}

	private void showFavorite() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(ViewContentActivity.this, FavoriteViewActivity.class);
		startActivity(intent);
	}

	public void about() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View textEntryView = inflater.inflate(R.layout.aboutlayout, null);

		final TextView txtlink = (TextView) textEntryView
				.findViewById(R.id.txtlink);
		txtlink.setText(getString(R.string.version) + "\n"
				+ getString(R.string.copywrite) + "\n"
				+ getString(R.string.webid) + "\n"
//				+ getString(R.string.add_humor) +"\n" 
//				+ getString(R.string.add_web)
				);
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				ViewContentActivity.this);
		builder.setCancelable(false);
		// builder.setIcon(R.drawable.icon);
		builder.setTitle(getResources().getString(R.string.app_name));
		builder.setView(textEntryView);
		builder.setNegativeButton(getString(R.string.closeBtn),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
		builder.show();
	}

}