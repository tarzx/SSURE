package nuim.ssure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dataHelper extends SQLiteOpenHelper {

	static final String TABLE_NAME_A = "SSURE_ACC_SENSOR";
	static final String TABLE_NAME_G = "SSURE_GRA_SENSOR";
	static final String COL_ID = "_ID";
	static final String COL_GX = "GX_VAL";
	static final String COL_GY = "GY_VAL";
	static final String COL_GZ = "GZ_VAL";
	static final String COL_AX = "AX_VAL";
	static final String COL_AY = "AY_VAL";
	static final String COL_AZ = "AZ_VAL";
	static final String COL_TIMESTAMP = "TIMESTAMP";
	
	private static final int DATABASE_VERSION = 1;	
	private static final String DATABASE_NAME = "SSURE_DATA.db";
	private static final String DEBUG_TAG = "dataHelper";
	private static final String DB_SCHEMA_A = "CREATE TABLE " + TABLE_NAME_A + "("
	        + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
	        + COL_AX + " REAL, " + COL_AY + " REAL, " + COL_AZ + " REAL, "
			+ COL_TIMESTAMP + " INTEGER NOT NULL " + "); ";
	private static final String DB_SCHEMA_G = "CREATE TABLE " + TABLE_NAME_G + "("
	        + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
	        + COL_GX + " REAL, " + COL_GY + " REAL, " + COL_GZ + " REAL, "
			+ COL_TIMESTAMP + " INTEGER NOT NULL " + ");"; 
	
	private SQLiteDatabase db;

    dataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_SCHEMA_A);
        Log.e("Database", "Acc Created");     
        db.execSQL(DB_SCHEMA_G); 
        Log.e("Database", "Gra Created");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DEBUG_TAG,
                "Warning: Dropping all tables; data migration not supported");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_A);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_G);
        onCreate(db);
    }
    
    // Open Database 
	public void open() {
		db = getWritableDatabase();	
	}
	
	// Close Database 
    public void close() {
        if (db != null) db.close();
    }
    
	public Cursor getAcc() {
		Cursor cursor = db.query(TABLE_NAME_A, null, null, null, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	public Cursor getGra() {
		Cursor cursor = db.query(TABLE_NAME_G, null, null, null, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
    
    public void insertDataA(int id, float AX, float AY, float AZ, long time) {
    	if (db!= null) {
    		Log.e("Database", "Opened");
	        ContentValues values = new ContentValues();
	        values.put(COL_ID, id);
	        values.put(COL_AX, AX);
	        values.put(COL_AY, AY);
	        values.put(COL_AZ, AZ);
	        values.put(COL_TIMESTAMP, time);
	        db.insert(TABLE_NAME_A, null, values);
    	} else { Log.e("Database", "Acce Error open"); }
    }
    
    public void insertDataG(int id, float GX, float GY, float GZ, long time) {
    	if (db!= null) {
    		Log.e("Database", "Opened");
	        ContentValues values = new ContentValues();
	        values.put(COL_ID, id);
	        values.put(COL_GX, GX);
	        values.put(COL_GY, GY);
	        values.put(COL_GZ, GZ);
	        values.put(COL_TIMESTAMP, time);
	        db.insert(TABLE_NAME_G, null, values);
    	} else { Log.e("Database", "Gra Error open"); }
    }
    
    public void reset() {
    	db.delete(TABLE_NAME_A, null, null);
    	db.delete(TABLE_NAME_G, null, null);
    }

}
