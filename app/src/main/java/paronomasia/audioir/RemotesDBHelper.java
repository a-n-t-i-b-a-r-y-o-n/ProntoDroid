package paronomasia.audioir;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.List;

/**
 * Braden - 8/31/17.
 */

public class RemotesDBHelper extends SQLiteOpenHelper {

    /*
    TODO
        - Finish implementing the DB handler
        - Sort out implementations using ArrayList<E> vs List<E>
        - Implement tagging buttons by ID (all are stored as value 0 right now)
     */

    // Extensive help from here: github.com/codepath/android_guides/wiki/Local-Databases-with-SQLiteOpenHelper

    // DB Info
    private static final String DBNAME = "remoteDatabase";
    private static final int DBVERSION = 1;

    // Table names
    private static final String TABLE_REMOTES = "remotes";
    private static final String TABLE_VENDORS = "vendor";
    private static final String TABLE_CODES = "codes";

    // Remote Columns ?
    private static final String KEY_REMOTE_ID = "_id"; //this needs to auto-increment
    private static final String KEY_REMOTE_NAME = "name";
    private static final String KEY_REMOTE_TYPE = "type";
    private static final String KEY_REMOTE_VENDOR = "vendorID";

    // Code Columns ?
    private static final String KEY_CODE = "code";
    private static final String KEY_CODE_BUTTON = "button";
    private static final String KEY_CODE_REMOTE_ID_FK = "remoteID"; //join this on remotes.id

    // Vendor Columns ?
    private static final String KEY_VENDOR_ID = "_id";
    private static final String KEY_VENDOR_NAME = "name";


    public RemotesDBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    // This is called when the connection is being configured...
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the db is created for the FIRST time
    // NOT called if the database exists on disk

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_REMOTE_TABLE = "CREATE TABLE " + TABLE_REMOTES +
                "(" +
                    KEY_REMOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    KEY_REMOTE_NAME + " TEXT, " +
                    KEY_REMOTE_TYPE + " TEXT, " +
                    KEY_REMOTE_VENDOR + " INTEGER " +
                //REFERENCES " + TABLE_VENDORS +
                ")";

        String CREATE_CODES_TABLE = "CREATE TABLE " + TABLE_CODES +
                "(" +
                    KEY_CODE_REMOTE_ID_FK + " INTEGER REFERENCES " + TABLE_REMOTES + ", " +
                    KEY_CODE + " TEXT, " +
                    KEY_CODE_BUTTON + " INTEGER " +
                ")";

        String CREATE_VENDORS_TABLE = "CREATE TABLE " + TABLE_VENDORS +
                "(" +
                    KEY_VENDOR_ID + " INTEGER PRIMARY KEY, " +
                    KEY_VENDOR_NAME + " TEXT " +
                ")";

        db.execSQL(CREATE_REMOTE_TABLE);
        db.execSQL(CREATE_CODES_TABLE);
        db.execSQL(CREATE_VENDORS_TABLE);

    }

    // This method is only called when the database version changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        // Occam's razor: drop current tables and recreate them.
        if(oldVer != newVer){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CODES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMOTES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDORS);
            onCreate(db);
        }
    }

    // return all remotes
    protected ArrayList<Remote> getAllRemotes(){
        // Query the database ?
        ArrayList<Remote> remotes = new ArrayList<>();

        // SELECT * FROM REMOTES

        final String BASIC_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_REMOTES);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(BASIC_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Remote remote = new Remote();
                    remote.setName(cursor.getString(cursor.getColumnIndex(KEY_REMOTE_NAME)));
                    remote.setID(cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_ID)));
                    remote.setVendor(cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_VENDOR)));

                    // Match the type string pulled from the db to one of the enums
                    for(Remote.deviceType t : Remote.deviceType.values()) {
                        if(t.toString().equals(cursor.getString(cursor.getColumnIndex(KEY_REMOTE_TYPE))))
                            remote.setType(t);
                    }

                    // Retrieve codes for this remote
                    remote.setCodes(getCodesForRemote(remote));

                    remotes.add(remote);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("DB", "Error retrieving remote from db.");
            Log.d("e", e.getMessage());
            e.printStackTrace();
            return remotes;
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return remotes;
    }

    // returns true if it worked, false if nah
    protected boolean addRemote(Remote remote){

        // Do the thing
        SQLiteDatabase db = getWritableDatabase();

        long status = -1;


        db.beginTransaction();
        try {
            ContentValues rinfo = new ContentValues();
            rinfo.put(KEY_REMOTE_NAME, remote.name);
            rinfo.put(KEY_REMOTE_TYPE, remote.type.toString());
            rinfo.put(KEY_REMOTE_VENDOR, remote.vendor);
            status = db.insertOrThrow(TABLE_REMOTES, null, rinfo);
            db.setTransactionSuccessful();

        } catch (Exception e){
            Log.d("DB", "Error adding remote to db.");
            Log.d("e", e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }

        for(String c : remote.getCodes() ){
            // The button value is set to "null" since I haven't implemented that yet.

            if(!addCode(status, c))
                Log.d("DB", "Problem adding code(s).");
        }

        return true;
    }

    protected boolean addCode(long status, String code){
        SQLiteDatabase db = getWritableDatabase();
        //long status = -1;

        db.beginTransaction();
        try {
            ContentValues cinfo = new ContentValues();
            cinfo.put(KEY_CODE, code);
            cinfo.put(KEY_CODE_REMOTE_ID_FK, status);
            // NOTE this is unused so far
            cinfo.put(KEY_CODE_BUTTON, 0);

            status = db.insertOrThrow(TABLE_CODES, null, cinfo);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DB", "Error adding code(s) to db.");
            Log.d("e", e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }

    public ArrayList<String> getCodesForRemote(Remote remote){
        ArrayList<String> codes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        final String GET_CODES_FOR_REMOTE = "SELECT * FROM " + TABLE_CODES +
                    " WHERE " + KEY_CODE_REMOTE_ID_FK + " LIKE " + remote.getID();
        Cursor cursor = db.rawQuery(GET_CODES_FOR_REMOTE, null);
        try {
            if(cursor.moveToFirst()){
                do {
                    codes.add(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d("DB", "Error retrieving codes for remote ID: " + remote.getID());
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return codes;
    }

    public ArrayList<String> getAllVendors(){

        final String VENDOR_QUERY = "SELECT " + KEY_VENDOR_NAME + " from " + TABLE_VENDORS;

        ArrayList<String> vendors = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(VENDOR_QUERY, null);
        try {
            if(cursor.moveToFirst()) {
                do {
                    vendors.add(cursor.getString(cursor.getColumnIndex(KEY_VENDOR_NAME)));
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("DB", "Error getting vendor list");
            Log.d("e", e.getMessage());
            e.printStackTrace();
            return vendors;
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return vendors;

    }

    public boolean addVendor(int id, String name) {
        SQLiteDatabase db = getWritableDatabase();

        long vendorID = -1;

        db.beginTransaction();
        try {
            ContentValues vinfo = new ContentValues();
            vinfo.put(KEY_VENDOR_ID, id);
            vinfo.put(KEY_VENDOR_NAME, name);
            vendorID = db.insertOrThrow(TABLE_VENDORS, null, vinfo);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DB", "Error adding vendor to db");
            Log.d("e", e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }

    public int getVendor(String name) {
        int id = -1;
        SQLiteDatabase db = getReadableDatabase();
        final String GET_VENDOR_ID = "SELECT * FROM " + TABLE_VENDORS +
                " WHERE " + KEY_VENDOR_NAME + " LIKE \"" + name + "\"" +
                " LIMIT 1";
        Cursor cursor = db.rawQuery(GET_VENDOR_ID, null);
        try {
            if(cursor.moveToFirst()){
                id = cursor.getInt(cursor.getColumnIndex(KEY_VENDOR_ID));
            }
        } catch (Exception e){
            Log.d("DB", "Error retrieving vendor");
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return id;
    }

    public String getVendor(int id){
        String name = "";
        SQLiteDatabase db = getReadableDatabase();
        final String GET_VENDOR_NAME = "SELECT * FROM " + TABLE_VENDORS +
                " WHERE _id=\"" + id + "\"" + " LIMIT 1";
        Cursor cursor = db.rawQuery(GET_VENDOR_NAME, null);
        try {
            if(cursor.moveToFirst()){
                name = cursor.getString(cursor.getColumnIndex(KEY_VENDOR_NAME));
            }
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return name;

    }





    protected void purgeDB(){
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, 0, 1);
        Log.d("DB", "~ Purged DB ~");
    }
}



