package kivaaz.com.firebasedownloadupload.DB;

/**
 * Created by Muguntan on 11/26/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import kivaaz.com.firebasedownloadupload.Adapter.Files;

/**
 * Created by Muguntan on 11/17/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "FileManager";

    // Contacts table name
    private static final String TABLE_FILES = "Files";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LOCAL_URL = "local_url";
    private static final String KEY_URL = "url";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DOWNLOADED = "downloaded";
    private static final String KEY_USER_EMAIL = "email";


    public DatabaseHandler(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FILES + "("
                + KEY_ID + " TEXT," + KEY_NAME + " TEXT PRIMARY KEY,"
                + KEY_URL + " TEXT,"+ KEY_LOCAL_URL + " TEXT," + KEY_TYPE + " TEXT," + KEY_DOWNLOADED + " TEXT," + KEY_USER_EMAIL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);

        // Create tables again
        onCreate(db);
    }

    // Adding new files
    public void addFiles(Files files) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, files.getName());
        values.put(KEY_URL, files.getUrl());
        values.put(KEY_LOCAL_URL, files.getUrl());
        values.put(KEY_TYPE, files.getType());
        values.put(KEY_DOWNLOADED, files.getDownloaded());
        values.put(KEY_USER_EMAIL, files.getUserEmail());

        // Inserting Row
        db.insert(TABLE_FILES, null, values);
        db.close(); // Closing database connection

    } // Adding new files
    public void addFilesIf(Files files) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, files.getName());
        values.put(KEY_URL, files.getUrl());
        values.put(KEY_LOCAL_URL, files.getLocal_url());
        values.put(KEY_TYPE, files.getType());
        values.put(KEY_DOWNLOADED, files.getDownloaded());
        values.put(KEY_USER_EMAIL, files.getUserEmail());

        // Inserting Row
        db.insertWithOnConflict(TABLE_FILES,null,values,SQLiteDatabase.CONFLICT_IGNORE);
        db.close(); // Closing database connection

    }


    public boolean isExists(String fieldValue) {

        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_FILES + " where " + KEY_NAME + " = " + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    // Getting single files
    public Files getFiles(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FILES, new String[] { KEY_ID,
                        KEY_NAME, KEY_URL,KEY_LOCAL_URL,KEY_TYPE, KEY_DOWNLOADED, KEY_USER_EMAIL }, KEY_NAME + "=?",
                new String[] { String.valueOf(name) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Files files = new Files(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getString(5), cursor.getString(6));
        // return files
        return files;

    }

    // Getting All files
    public List<Files> getAllFiles() {

        List<Files> filestList = new ArrayList<Files>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FILES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Files files = new Files();
                files.setId(cursor.getString(0));
                files.setName(cursor.getString(1));
                files.setUrl(cursor.getString(2));
                files.setLocal_url(cursor.getString(3));
                files.setType(cursor.getString(4));
                files.setDownloaded(cursor.getString(5));
                files.setUserEmail(cursor.getString(6));
                // Adding contact to list
                filestList.add(files);
            } while (cursor.moveToNext());
        }

        // return files list
        return filestList;

    }

    // Getting files Count
    public int getFilesCount() {

        String countQuery = "SELECT  * FROM " + TABLE_FILES ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;

    }
    // Getting files Count
    public int getCurrentFilesCount(String email) {

        String countQuery = "SELECT  * FROM " + TABLE_FILES + " WHERE " + KEY_USER_EMAIL + " =? " + new String[] { String.valueOf(email) };
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FILES, new String[] { KEY_ID,
                        KEY_NAME, KEY_URL,KEY_LOCAL_URL,KEY_TYPE, KEY_DOWNLOADED, KEY_USER_EMAIL }, KEY_USER_EMAIL + "=?",
                new String[] { String.valueOf(email) }, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;

    }
    // Updating single files
    public int updateFiles(Files files) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, files.getName());
        values.put(KEY_URL, files.getUrl());
        values.put(KEY_LOCAL_URL, files.getLocal_url());
        values.put(KEY_TYPE, files.getType());
        values.put(KEY_DOWNLOADED, files.getDownloaded());
        values.put(KEY_USER_EMAIL, files.getUserEmail());

        // updating row
        return db.update(TABLE_FILES, values, KEY_NAME + " = ?",
                new String[] { String.valueOf(files.getName()) });
    }

    // Deleting single files
    public boolean deleteFiles(Files files) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_FILES, KEY_NAME + " = ?",
                new String[] { String.valueOf(files.getName()) });
        db.close();
        return rows > 0;
    }

}
