package paronomasia.audioir;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Braden - 8/31/17.
 */

public class RemotesDBHelper extends SQLiteOpenHelper {

    // Extensive help from here: github.com/codepath/android_guides/wiki/Local-Databases-with-SQLiteOpenHelper

    // DB Info
    private static final String DBNAME = "remoteDatabase";
    private static final int DBVERSION = 1;

    // Table names
    private static final String TABLE_REMOTES = "remotes";
    private static final String TABLE_VENDORS = "vendor";
    private static final String TABLE_CODES = "codes";

    // Remote Columns ?
    private static final String KEY_REMOTE_ID = "id"; //this needs to auto-increment
    private static final String KEY_REMOTE_NAME = "name";
    private static final String KEY_REMOTE_TYPE = "type";
    private static final String KEY_REMOTE_VENDOR = "vendorID";

    // Code Columns ?
    private static final String KEY_CODE = "code";
    private static final String KEY_BUTTON = "button";
    private static final String KEY_CODE_REMOTE_ID_FK = "remoteID"; //join this on remotes.id

    // Vendor Columns ?
    private static final String KEY_VENDOR_ID = "id";
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
                    KEY_BUTTON + " INTEGER " +
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
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMOTES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDORS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CODES);
            onCreate(db);
        }
    }

    // return all remotes
    public List<Remote> getRemotes(){
        // Query the database ?
        List<Remote> remotes = new ArrayList<>();

        // SELECT * FROM REMOTES

        String BASIC_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_REMOTES);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(BASIC_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Remote remote = new Remote();
                    remote.setName(cursor.getString(cursor.getColumnIndex(KEY_REMOTE_NAME)));
                    remote.setID(cursor.getInt(cursor.getColumnIndex(KEY_REMOTE_ID)));
                    remotes.add(remote);
                    // That's all for now.

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("DB", "Error retrieving remote from db.");
            Log.d("e", e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return remotes;
    }


    // returns true if it worked, false if nah
    public boolean addRemote(Remote remote){

        // Do the thing
        SQLiteDatabase db = getWritableDatabase();

        long remoteID = -1;


        db.beginTransaction();
        try {
            ContentValues rinfo = new ContentValues();
            rinfo.put(KEY_REMOTE_NAME, remote.name);
            rinfo.put(KEY_REMOTE_TYPE, remote.type.toString());
            rinfo.put(KEY_REMOTE_VENDOR, remote.vendor);

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
        return true;
    }

    public void purgeDB(){
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, 0, 1);
        Log.d("DB", "~ Purged DB ~");
    }
}



