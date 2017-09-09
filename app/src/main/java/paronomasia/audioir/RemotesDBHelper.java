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
        - Implement tagging buttons by ID (all are stored as value 0 right now)
        - Implement using hashes to determine if a remote has changed (i.e. should be set to current)
     */

    // Extensive help from here: github.com/codepath/android_guides/wiki/Local-Databases-with-SQLiteOpenHelper

    // DB Info
    private static final String DBNAME = "remoteDatabase";
    private static final int DBVERSION = 1;

    // Table names
    private static final String TABLE_REMOTES = "remotes";
    private static final String TABLE_VENDORS = "vendor";
    private static final String TABLE_CODES = "codes";

    // Remote Table Columns
    private static final String KEY_REMOTE_ID = "_id";              // The auto-incrementing ID
    private static final String KEY_REMOTE_NAME = "name";           // Name of the remote
    private static final String KEY_REMOTE_TYPE = "type";           // Type of device the remote is for
    private static final String KEY_REMOTE_VENDOR = "vendorID";     // Vendor ID references _id from Vendor table
    private static final String KEY_CURRENT = "current";            // 1 or 0 INT (boolean) for whether this is the current remote.
    private static final String KEY_REMOTE_HASH = "hash";                  // ~ UNUSED YET ~ Hash of the remote values + codes to determine if it has changed.

    // Code Table Columns
    private static final String KEY_CODE = "code";                  // Actual code value, stored as string to preserve spaces and x's for Pronto notation
    private static final String KEY_CODE_BUTTON = "button";         // ~ UNUSED YET ~ this will determine which button on the controls this code goes with
    private static final String KEY_CODE_REMOTE_ID_FK = "remoteID"; // references the _id of a remote from the Remote table

    // Vendor Table Columns
    private static final String KEY_VENDOR_ID = "_id";              // Vendor ID number
    private static final String KEY_VENDOR_NAME = "name";           // Vendor name as a string


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
                    KEY_REMOTE_VENDOR + " INTEGER, " +
                    KEY_CURRENT + " INTEGER, " +
                    KEY_REMOTE_HASH + " TEXT " +
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
        Remote.deviceType type = Remote.deviceType.OTHER;
        try {
            if (cursor.moveToFirst()) {
                do {
                    // Match the type string pulled from the db to one of the enums
                    for(Remote.deviceType t : Remote.deviceType.values()) {
                        if(t.toString().equals(cursor.getString(cursor.getColumnIndex(KEY_REMOTE_TYPE))))
                            type = t;
                    }

                    // MONSTER of a query/constructor. Syntax:
                    // Remote(int id, ArrayList<String> codes, int vendor, deviceType type, String name, boolean current, String hash)

                    Remote remote = new Remote(cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_ID)), null, cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_VENDOR)), type,
                            cursor.getString(cursor.getColumnIndex(KEY_REMOTE_NAME)), (cursor.getInt(cursor.getColumnIndex(KEY_CURRENT)) == 1),
                            cursor.getString(cursor.getColumnIndex(KEY_REMOTE_HASH)));

                    // Retrieve codes for this remote
                    remote.setCodes(getCodesForRemote(cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_ID))));

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
            db.close();
        }
        return remotes;
    }

    // returns true if it worked, false if nah
    protected boolean addRemote(Remote remote){

        // Do the thing
        SQLiteDatabase db = getWritableDatabase();

        long status = -1;


        // Add all single values
        db.beginTransaction();
        try {
            ContentValues rinfo = new ContentValues();
            rinfo.put(KEY_REMOTE_NAME, remote.getName());
            rinfo.put(KEY_REMOTE_TYPE, remote.getType().toString());
            rinfo.put(KEY_REMOTE_VENDOR, remote.getVendorId());
            rinfo.put(KEY_CURRENT, remote.getCurrent() ? 1 : 0);
            rinfo.put(KEY_REMOTE_HASH, remote.getHash());
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

        // Add all codes
        for(String c : remote.getCodes() ){
            // No button values since I haven't implemented that yet.

            if(!addCode(status, c))
                Log.d("DB", "Problem adding code(s).");
        }

        return true;
    }

    protected boolean addCode(long status, String code){
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues cinfo = new ContentValues();
            cinfo.put(KEY_CODE, code);
            cinfo.put(KEY_CODE_REMOTE_ID_FK, status);
            // NOTE this is unused so far
            cinfo.put(KEY_CODE_BUTTON, 0);

            db.insertOrThrow(TABLE_CODES, null, cinfo);
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

    // Return all codes for a given remote
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

    // Return all codes for a given remote ID (probably lighter on memory resources)
    public ArrayList<String> getCodesForRemote(long id){
        ArrayList<String> codes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        final String GET_CODES_FOR_REMOTE = "SELECT * FROM " + TABLE_CODES +
                " WHERE " + KEY_CODE_REMOTE_ID_FK + " LIKE " + id;
        Cursor cursor = db.rawQuery(GET_CODES_FOR_REMOTE, null);
        try {
            if(cursor.moveToFirst()){
                do {
                    codes.add(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d("DB", "Error retrieving codes for remote ID: " + id);
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

        db.beginTransaction();
        try {
            ContentValues vinfo = new ContentValues();
            vinfo.put(KEY_VENDOR_ID, id);
            vinfo.put(KEY_VENDOR_NAME, name);
            db.insertOrThrow(TABLE_VENDORS, null, vinfo);
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

    // Return vendor ID for a given vendor name
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

    // Return name for a given vendor ID
    public String getVendor(int id){
        String name = "NO_NAME"; // Initial value so as not to return "", should be overwritten.
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



