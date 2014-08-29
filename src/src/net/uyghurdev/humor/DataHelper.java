package net.uyghurdev.humor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataHelper {

	private static final String DB_PATH = Configs.DB_Path; 
	private static final String DATABASE_NAME = Configs.DB_Name;
	private static final int DATABASE_VERSION = 3;
	private static final String TABLE_OBJECT = Configs.T_Object;
	private static final String TABLE_FAVORITE = Configs.T_Favorite;

	private Context context;
	private SQLiteDatabase db;

	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " + TABLE_OBJECT
			+ "(name) values (?)";

	public DataHelper(Context context) {
		this.context = context;
		if (!checkdatabase()) {
			try {
				copydatabase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("Copy Data", e.toString());
				e.printStackTrace();
			}
		}
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		// this.insertStmt = this.db.compileStatement(INSERT);
	}

	private boolean checkdatabase() {
		// SQLiteDatabase checkdb = null;
		boolean checkdb = false;
		try {
			String myPath = DB_PATH + DATABASE_NAME;
			File dbfile = new File(myPath);
			// checkdb =
			// SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
			checkdb = dbfile.exists();

		} catch (SQLiteException e) {
			System.out.println("Database doesn't exist");
		}

		return checkdb;
	}

	private void copydatabase() throws IOException {

		// Open your local db as the input stream
		InputStream myinput = context.getAssets().open(DATABASE_NAME);

		// Path to the just created empty db
		String outfilename = DB_PATH + DATABASE_NAME;
		File data = new File(DB_PATH);
		data.mkdirs();
		// Open the empty db as the output stream
		OutputStream myoutput = new FileOutputStream(outfilename);

		// transfer byte to inputfile to outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myinput.read(buffer)) > 0) {
			myoutput.write(buffer, 0, length);
		}

		// Close the streams
		myoutput.flush();
		myoutput.close();
		myinput.close();

	}

	public long insert(String name) {
		this.insertStmt.bindString(1, name);
		return this.insertStmt.executeInsert();
	}

	public void deleteAll() {
		this.db.delete(TABLE_OBJECT, null, null);
		this.db.delete(TABLE_FAVORITE, null, null);
	}

	public void clearFavorite() {
		// TODO Auto-generated method stub
		this.db.delete(TABLE_FAVORITE, null, null);
	}

	public List<String> selectAll() {
		List<String> list = new ArrayList<String>();
		Cursor cursor = this.db.query(TABLE_OBJECT, new String[] { "name" },
				null, null, null, null, "name desc");
		if (cursor.moveToFirst()) {
			do {
				list.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public ArrayList<FavoriteItem> getFavoriteArray() {
		// TODO Auto-generated method stub
		ArrayList<FavoriteItem> list = new ArrayList<FavoriteItem>();
		Cursor cursor = this.db.query(TABLE_FAVORITE, new String[] {
				"serverID", "title" }, null, null, null, null, null);
		cursor.moveToFirst();
		for (int m = 0; m < cursor.getCount(); m++) {
			FavoriteItem item = new FavoriteItem();
			item.setID(cursor.getInt(0));
			item.setTitle(cursor.getString(1));
			list.add(item);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public void favoriteSent(int i) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put("sent", 1);
		this.db.update(TABLE_FAVORITE, cv, "serverID=" + i, null);
	}

	public void setAsNotFavorate(int i) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put("isFavorite", 0);
		this.db.update(TABLE_OBJECT, cv, "serverID=" + i, null);
	}

	public void setAsFavorate(int i) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put("isFavorite", 1);
		this.db.update(TABLE_OBJECT, cv, "serverID=" + i, null);
	}

	public void setAsRead(int i) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put("read", 1);
		this.db.update(TABLE_OBJECT, cv, "serverID=" + i, null);
	}

	public void addFavoriteSent(int i, String title) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put("serverID", i);
		cv.put("title", title);
		cv.put("sent", 1);
		this.db.insert(TABLE_FAVORITE, "title", cv);
	}

	public void addFavoriteNotSent(int i, String title) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put("serverID", i);
		cv.put("title", title);
		cv.put("sent", 0);
		this.db.insert(TABLE_FAVORITE, "title", cv);
	}

	public void addItem(int serverId, String title, String content) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put("serverID", serverId);
		cv.put("title", title);
		cv.put("content", content);
		cv.put("read", 0);
		cv.put("isFavorite", 0);
		this.db.insert(TABLE_OBJECT, "serverID", cv);
	}

	public void removeFavorite(int i) {
		// TODO Auto-generated method stub
		this.db.delete(TABLE_FAVORITE, "serverID=" + i, null);
	}

	public int[] getToFavorite() {
		Cursor cursor = this.db.query(TABLE_FAVORITE,
				new String[] { "serverID" }, "sent=" + 0, null, null, null,
				null);
		if (cursor.getCount() == 0) {
			return null;
		}
		int[] list = new int[cursor.getCount()];
		cursor.moveToFirst();
		for (int j = 0; j < cursor.getCount(); j++) {
			list[j] = cursor.getInt(0);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public int[] getServerIds() {
		// TODO Auto-generated method stub
		Cursor cursor = this.db.query(TABLE_OBJECT,
				new String[] { "serverID" }, null, null, null, null,
				"serverID desc");
		int[] list = new int[cursor.getCount()];
		cursor.moveToFirst();
		for (int j = 0; j < cursor.getCount(); j++) {
			list[j] = cursor.getInt(0);
			cursor.moveToNext();
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public int getItemCount() {
		// TODO Auto-generated method stub
		Cursor cursor = this.db.query(TABLE_OBJECT,
				new String[] { "serverID" }, null, null, null, null,
				"serverID desc");
		int count = cursor.getCount();
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return count;
	}

	public int lastServerId() {
		// TODO Auto-generated method stub
		Cursor cursor = this.db.query(TABLE_OBJECT,
				new String[] { "serverID" }, null, null, null, null,
				"serverID desc");
		if (cursor.getCount() == 0) {
			return 0;
		}
		int id;
		cursor.moveToFirst();
		id = cursor.getInt(0);
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return id;
	}

	public ItemObject getItem(int i) {
		Cursor cursor = this.db.query(TABLE_OBJECT, null, "serverID=" + i,
				null, null, null, null);
		cursor.moveToFirst();
		ItemObject object = new ItemObject();
		object.setServerID(cursor.getInt(1));
		object.setTitle(cursor.getString(2));
		object.setContent(cursor.getString(3));
		if (cursor.getInt(4) == 1) {
			object.setRead(true);
		} else {
			object.setRead(false);
		}
		if (cursor.getInt(5) == 1) {
			object.setFavorate(true);
		} else {
			object.setFavorate(false);
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return object;
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
//			db.execSQL("CREATE TABLE "
//					+ TABLE_OBJECT
//					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, serverID INTEGER, title TEXT, content TEXT, read INTEGER, isFavorite INTEGER)");
//			db.execSQL("CREATE TABLE "
//					+ TABLE_FAVORITE
//					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, serverID INTEGER, title TEXT, sent INTEGER)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBJECT);
			onCreate(db);
		}

	}

}
