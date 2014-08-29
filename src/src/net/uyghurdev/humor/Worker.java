package net.uyghurdev.humor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Worker {
	
	Context cxt;
	DataHelper dbHelper;
	
	Worker(Context context){
		cxt = context;
		dbHelper = new DataHelper(context);
	}

	public void checkFavoriteAndAdd() {
		// TODO Auto-generated method stub
		int[] serverIds = dbHelper.getToFavorite();
		if (serverIds ==null){
			return;
		}
		String sent = sendToServerAsFavorite(serverIds);
		if(sent.equalsIgnoreCase("true")){
			for(int h=0; h<serverIds.length;h++){
				dbHelper.favoriteSent(serverIds[h]);
			}
		}
	}

	private String sendToServerAsFavorite(int[] serverIds) {
		// TODO Auto-generated method stub
		String result = "";
		URL jurl;
		try {

			
//			jurl = new URL(Configs.Server_Favorite_Send + i);
//			HttpURLConnection urlConn = (HttpURLConnection) jurl
//					.openConnection();
//			InputStream inputStream = urlConn.getInputStream();
//			BufferedReader r = new BufferedReader(new InputStreamReader(
//					inputStream));
//			StringBuilder total = new StringBuilder();
//			String line;
//			while ((line = r.readLine()) != null) {
//				total.append(line);
//			}
//
//			str = total.toString();
			
			
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			HttpPost httpost = new HttpPost(Configs.Server_Favorite_Send);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("servarID", ""+ serverIds));
		
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httpost.setHeader("User-Agent", Configs.Model + "; " + Configs.Version + "; " + Configs.Language + "; " + Configs.AppName);
			response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);

			
			
			
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			Log.d("Exep",e1.toString());
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void serverIdToArray() {
		// TODO Auto-generated method stub
		Configs.ServerId = dbHelper.getServerIds();
	}

	public ItemObject getItem(int i) {
		// TODO Auto-generated method stub
		return dbHelper.getItem(i);
	}

	public void addFavoriteSent(int i, String title) {
		// TODO Auto-generated method stub

//		sendToServerAsFavorite(i);
		dbHelper.addFavoriteSent(i, title);

	}

	public void addFavoriteNotSent(int i, String title) {
		// TODO Auto-generated method stub
		dbHelper.addFavoriteNotSent(i, title);
	}

	public void removeFavorite(int i) {
		// TODO Auto-generated method stub
		dbHelper.setAsNotFavorate(i);
		dbHelper.removeFavorite(i);
	}
	
	public void setAsFavorite(int i) {
		// TODO Auto-generated method stub
		dbHelper.setAsFavorate(i);
	}

	public void update() {
		// TODO Auto-generated method stub
		int serverId = dbHelper.lastServerId();
		addNewItems(serverId);
	}

	
	public int getNewItemCount() {
		// TODO Auto-generated method stub
		int serverId = dbHelper.lastServerId();
		String count = urlToString(Configs.Get_NewItems_Count+serverId);
		return Integer.valueOf(count);
	}


	private void addNewItems(int id) {
		// TODO Auto-generated method stub
		String jsonString = urlToString(Configs.Get_NewItems_JSON + id);

		try {
			// this will break the JSON messages into an array
			JSONArray aryJSONStrings = new JSONArray(jsonString);
			// loop through the array
			for (int i = 0; i < aryJSONStrings.length(); i++) {
				int serverId = aryJSONStrings.getJSONObject(i).getInt("id");
				String title = aryJSONStrings.getJSONObject(i).getString(
						"title");
				String content = aryJSONStrings.getJSONObject(i)
						.getString("content");
				dbHelper.addItem(serverId, title, content);
			}
	
		} catch (JSONException e) {
			Log.d("json", e.toString());
		}
	}
	
	private String urlToString(String appSource) {
		// TODO Auto-generated method stub
		String str = "";
		URL jurl;
		try {

			
			jurl = new URL(appSource);
			HttpURLConnection urlConn = (HttpURLConnection) jurl
					.openConnection();
			urlConn.setRequestProperty("User-Agent",Configs.Model + "; " + Configs.Version + "; " + Configs.Language + "; " + Configs.AppName);
			InputStream inputStream = urlConn.getInputStream();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					inputStream));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}

			str = total.toString();

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			Log.d("Exep",e1.toString());
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

	public ArrayList<FavoriteItem> getFavoriteArray() {
		// TODO Auto-generated method stub
		return dbHelper.getFavoriteArray();
	}

	public void clearFavorite(ArrayList<FavoriteItem> array) {
		// TODO Auto-generated method stub
		dbHelper.clearFavorite();
		if(array.size()>0){
			for (int i=0;i<array.size();i++){
				dbHelper.setAsNotFavorate(array.get(i).getID());
			}
		}
	}

	public void showFavoriteItem(ItemObject object) {
		// TODO Auto-generated method stub
		AlertDialog itemDialog = new AlertDialog.Builder(cxt).create();
		itemDialog.setTitle(object.getTitle());
		itemDialog.setMessage(object.getContent());
		itemDialog.setButton(cxt.getString(R.string.close), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
			
		});
		itemDialog.show();
	}

	public void objectRead(int i) {
		// TODO Auto-generated method stub
		dbHelper.setAsRead(i);
	}

	public void deleteFavorite(int id) {
		// TODO Auto-generated method stub
		dbHelper.removeFavorite(id);
	}

	public int getItemCount() {
		// TODO Auto-generated method stub
		return dbHelper.getItemCount();
	}

	public void setAppInfo() {
		// TODO Auto-generated method stub
		Configs.Model = android.os.Build.MODEL;
		Configs.Version = "Android" + android.os.Build.VERSION.RELEASE;
		Configs.Language = Locale.getDefault().getDisplayLanguage();
	}

	
	
}
