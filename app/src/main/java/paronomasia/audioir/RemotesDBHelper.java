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
        - Implement using hashes to determine if a remote has changed
            (i.e. to trigger updating the current in Controls.java when needed rather than doing it every time)
     */

    // Extensive help from here: github.com/codepath/android_guides/wiki/Local-Databases-with-SQLiteOpenHelper

    // DB Info
    private static final String DBNAME = "remoteDatabase";
    private static final int DBVERSION = 1;                         // WARNING: Changing this will trigger a DB purge.

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

    private static final String KEY_REMOTE_HASH = "hash";           // ~ UNUSED YET ~ Hash of the remote values + codes to determine if it has changed.

    // Code Table Columns
    private static final String KEY_CODE_ID = "_id";                // Code id, used for editing specific codes.
    private static final String KEY_CODE = "code";                  // Actual code value, stored as string to preserve spaces and x's for Pronto notation
    private static final String KEY_CODE_REMOTE_ID_FK = "remoteID"; // references the _id of a remote from the Remote table
    private static final String KEY_CODE_BUTTON = "button";         // determines which button on the controls this code goes with
    private static final String KEY_CODE_BUTTON_NAME = "name";      // if the above is -1, this is a string representing the button type

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
                    KEY_REMOTE_TYPE + " INTEGER, " +
                    KEY_REMOTE_VENDOR + " INTEGER, " +
                    KEY_CURRENT + " INTEGER, " +
                    KEY_REMOTE_HASH + " TEXT " +
                ")";

        String CREATE_CODES_TABLE = "CREATE TABLE " + TABLE_CODES +
                "(" +
                    KEY_CODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    KEY_CODE_REMOTE_ID_FK + " INTEGER REFERENCES " + TABLE_REMOTES + ", " +
                    KEY_CODE + " TEXT, " +
                    KEY_CODE_BUTTON + " INTEGER, " +
                    KEY_CODE_BUTTON_NAME + " TEXT " +
                ")";

        String CREATE_VENDORS_TABLE = "CREATE TABLE " + TABLE_VENDORS +
                "(" +
                    KEY_VENDOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
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
            purgeDB();
	    onCreate(db);
        }
    }

    // return all remotes
    protected ArrayList<Remote> getAllRemotes(){
        // Query the database ?
        ArrayList<Remote> remotes = new ArrayList<>();


        final String BASIC_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_REMOTES);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(BASIC_SELECT_QUERY, null);
        int type = -1;
        try {
            if (cursor.moveToFirst()) {
                do {

                    // MONSTER of a query/constructor. Syntax:
                    // Remote(int id, ArrayList<Code> codes, int vendor, int type, String name, boolean current, String hash)

                    Remote remote = new Remote(cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_ID)),
                            getCodesForRemote(cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_ID))),
                            cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_VENDOR)),
                            cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_TYPE)),
                            cursor.getString(cursor.getColumnIndex(KEY_REMOTE_NAME)),
                            (cursor.getInt(cursor.getColumnIndex(KEY_CURRENT)) == 1),
                            cursor.getString(cursor.getColumnIndex(KEY_REMOTE_HASH)));

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
        return (remotes.size() == 0) ? null : remotes;
    }

    // returns true if it worked, false if nah
    protected boolean addRemote(Remote remote){

        // Do the thing
        SQLiteDatabase db = getWritableDatabase();

        long remoteID = -1;

        if(remote.getCurrent()) {
            clearCurrent();
        }

        // Add all single values
        db.beginTransaction();
        try {
            ContentValues rinfo = new ContentValues();
            rinfo.put(KEY_REMOTE_NAME, remote.getName());
            rinfo.put(KEY_REMOTE_TYPE, remote.getType());
            rinfo.put(KEY_REMOTE_VENDOR, remote.getVendorId());
            rinfo.put(KEY_CURRENT, remote.getCurrent() ? 1 : 0);
            rinfo.put(KEY_REMOTE_HASH, remote.getHash());
            remoteID = db.insertOrThrow(TABLE_REMOTES, null, rinfo);
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
        if (remote.getCodes() != null) {
            for(Code c : remote.getCodes() ){
                if(!addCode(remoteID, c))
                    Log.d("DB", "Problem adding code(s).");
            }
        }

        return true;
    }

    protected boolean addCode(long remoteID, Code code){
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues cinfo = new ContentValues();
            cinfo.put(KEY_CODE, code.getHex());
            cinfo.put(KEY_CODE_REMOTE_ID_FK, remoteID); // This gets passed the proper value... right?
            cinfo.put(KEY_CODE_BUTTON, code.getType());
            cinfo.put(KEY_CODE_BUTTON_NAME, code.getName());

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

    protected boolean updateCode(Code code){
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues cinfo = new ContentValues();
            //Setting the id to -1 means it's a new code that hasn't been added to the db.
            if(code.getID() != -1)
                cinfo.put(KEY_CODE_ID, code.getID());
            cinfo.put(KEY_CODE, code.getHex());
            cinfo.put(KEY_CODE_BUTTON, code.getType());
            cinfo.put(KEY_CODE_BUTTON_NAME, code.getName());
            cinfo.put(KEY_CODE_REMOTE_ID_FK, code.getRemoteID());

            db.update(TABLE_CODES, cinfo, KEY_CODE_ID + " + ?",
                    new String[] { String.valueOf(code.getID()) });
        } catch (Exception e){
            Log.d("DB", "Error updating code id " + code.getID());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // Return all codes for a given remote
    public ArrayList<Code> getCodesForRemote(Remote remote){
        ArrayList<Code> codes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        final String GET_CODES_FOR_REMOTE = "SELECT * FROM " + TABLE_CODES +
                    " WHERE " + KEY_CODE_REMOTE_ID_FK + " LIKE " + remote.getID();
        Cursor cursor = db.rawQuery(GET_CODES_FOR_REMOTE, null);
        try {
            if(cursor.moveToFirst()){
                do {
                    Code code = new Code(cursor.getInt(cursor.getColumnIndex(KEY_CODE_ID)),
                            remote.getID(),
                            cursor.getString(cursor.getColumnIndex(KEY_CODE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_CODE_BUTTON)),
                            cursor.getString(cursor.getColumnIndex(KEY_CODE_BUTTON_NAME)));
                    codes.add(code);
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
    public ArrayList<Code> getCodesForRemote(long id){
        ArrayList<Code> codes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        final String GET_CODES_FOR_REMOTE = "SELECT * FROM " + TABLE_CODES +
                " WHERE " + KEY_CODE_REMOTE_ID_FK + "=" + id;
        Cursor cursor = db.rawQuery(GET_CODES_FOR_REMOTE, null);
        try {
            if(cursor.moveToFirst()){
                do {
                    Code code = new Code(cursor.getInt(cursor.getColumnIndex(KEY_CODE_ID)),
                            id,
                            cursor.getString(cursor.getColumnIndex(KEY_CODE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_CODE_BUTTON)),
                            cursor.getString(cursor.getColumnIndex(KEY_CODE_BUTTON_NAME)));
                    codes.add(code);
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

    public Code getCodeByID(int id){
        SQLiteDatabase db = getReadableDatabase();
        Code code = null;
        final String GET_CODES_BY_ID = "SELECT * FROM " + TABLE_CODES +
                " WHERE " + KEY_CODE_ID + "=" + id + " LIMIT 1";
        Cursor cursor = db.rawQuery(GET_CODES_BY_ID, null);
        try {
            if(cursor.moveToFirst()){
                code = new Code(cursor.getInt(cursor.getColumnIndex(KEY_CODE_ID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_CODE_REMOTE_ID_FK)),
                        cursor.getString(cursor.getColumnIndex(KEY_CODE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_CODE_BUTTON)),
                        cursor.getString(cursor.getColumnIndex(KEY_CODE_BUTTON_NAME)));
            }
        } catch (Exception e){
            Log.d("DB", "Error retrieving code by ID");
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return code;
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


    // Return the current remote or null
    public Remote getCurrentRemote(){

        SQLiteDatabase db = getReadableDatabase();
        Remote current = null;
        Remote.deviceType type = Remote.deviceType.OTHER;

        final String CURRENT_QUERY = "SELECT * FROM " + TABLE_REMOTES +
                " WHERE " + KEY_CURRENT + "=1 LIMIT 1";
        Cursor cursor = db.rawQuery(CURRENT_QUERY, null);
        try {
            if (cursor.moveToFirst()){

                // Remote(int id, ArrayList<Code> codes, int vendor, int type, String name, boolean current, String hash)
                current = new Remote(cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_ID)),
                        getCodesForRemote(cursor.getColumnIndex(KEY_REMOTE_ID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_VENDOR)),
                        cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_REMOTE_NAME)),
                        cursor.getInt(cursor.getColumnIndex(KEY_CURRENT)) == 1,
                        cursor.getString(cursor.getColumnIndex(KEY_REMOTE_HASH)));
            }
        } catch (Exception e) {
            Log.d("CURRENT", "Error getting current remote");
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return current;

    }

    // Set the current flag to 0 for all remotes
    public void clearCurrent() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            final String CLEAR_CURRENT = "UPDATE " + TABLE_REMOTES +
                    " SET " + KEY_CURRENT + "=0";
            db.execSQL(CLEAR_CURRENT);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("CURRENT", "Error setting current bits to 0");
        } finally {
            db.endTransaction();
        }
    }

    public int updateCurrent(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues rinfo = new ContentValues();
            rinfo.put(KEY_CURRENT, 1);

            return db.update(TABLE_REMOTES, rinfo, KEY_REMOTE_ID + " = ?",
                    new String[] { String.valueOf(id)});
        } catch (Exception e){
            Log.d("DB", "Error updating current remote in db");
            e.printStackTrace();
        }

        return -1;
    }





    // DEBUG & TESTING METHODS

    protected ArrayList<Code> dumpAllCodes(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Code> codes = new ArrayList<>();
        final String DUMP_CODES = "Select * FROM " + TABLE_CODES;
        Cursor cursor = db.rawQuery(DUMP_CODES, null);
        try {
            if(cursor.moveToFirst()){
                do {
                    codes.add(new Code(cursor.getInt(cursor.getColumnIndex(KEY_CODE_ID)),
                            cursor.getInt(cursor.getColumnIndex(KEY_CODE_REMOTE_ID_FK)),
                            cursor.getString(cursor.getColumnIndex(KEY_CODE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_CODE_BUTTON)),
                            cursor.getString(cursor.getColumnIndex(KEY_CODE_BUTTON_NAME))));
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d("DB", "Error dumping all codes");
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return (codes.size() != 0) ? codes : null;
    }

    protected void purgeDB(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDORS);
        onCreate(db);
	    Log.d("DB", "~ Purged DB ~");
    }
}



