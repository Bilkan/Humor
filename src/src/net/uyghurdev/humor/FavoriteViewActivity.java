package net.uyghurdev.humor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class FavoriteViewActivity extends Activity {
	private ListView myListView;
	private ImageButton clearBtn;
	private ImageButton backBtn;
	Worker worker;
	ArrayList<FavoriteItem> array;
	
	//private List<Map<String, Object>> favorateItem;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.favoritelist);
	        
	        init();
	        
	        showList();
	      
	        registerForContextMenu(myListView);
	        
	        myListView.setOnItemClickListener(new ListView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> view, View v,
						int position, long id) {
					// TODO Auto-generated method stub
					ItemObject object = new ItemObject();
					object = worker.getItem(array.get(position).getID());
					worker.showFavoriteItem(object);
				}
	        	
	        });
	        
	        clearBtn.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					worker.clearFavorite(array);
					showList();
				}
			});
	        backBtn.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					Intent intent = new Intent();
//					intent.setClass(FavorateViewActivity.this, ViewContentActivity.class);
//					startActivity(intent);
					FavoriteViewActivity.this.finish();
				}
			});
       
	    }

	 private void showList() {
		// TODO Auto-generated method stub
		  
	        array = worker.getFavoriteArray();
	        String[] items = new String[array.size()];
	        for (int n=0; n<array.size();n++){
	        	items[n] = array.get(n).getTitle();
	        }
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
	        myListView.setAdapter(adapter);
	}

	private void init() {
		// TODO Auto-generated method stub
		myListView = (ListView)findViewById(R.id.listView1);
		clearBtn=(ImageButton)findViewById(R.id.button1);
		backBtn=(ImageButton)findViewById(R.id.returnBtn);
		myListView.setCacheColorHint(0);
		worker = new Worker(this);
		//favorateItem=getListData();
		//ListViewAdapter viewAdapter = new ListViewAdapter(FavorateViewActivity.this,favorateItem);
       // myListView.setAdapter(viewAdapter);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub

		super.onCreateContextMenu(menu, v, menuInfo);
			menu.add(0, 0, 0, R.string.context_menu_delete);
		
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			worker.deleteFavorite(array.get(info.position).getID());
			showList();
			return true;
		
		default:
			return super.onContextItemSelected(item);
		}
	}

}
