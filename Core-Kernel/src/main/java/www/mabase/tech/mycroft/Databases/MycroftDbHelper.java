package www.mabase.tech.mycroft.Databases;

/**
 * Created by Chris on 10/25/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The design of this database is supposed to be a simplistic recreation of Adapts database in the standard Python Mycroft. Adapt itself will load all of the details for parsing from its internal database, rather than trying to load them from skills at runtime.
 */

public class MycroftDbHelper extends SQLiteOpenHelper {

    private static final String ENTITIES_CREATE_ENTRIES =
            "CREATE TABLE " + MycroftContract.Entities.TABLE_NAME + " (" +
                    MycroftContract.Entities._ID + " INTEGER PRIMARY KEY," +
                    MycroftContract.Entities.COLUMN_NAME_STRING + " TEXT," +
                    MycroftContract.Entities.COLUMN_NAME_TYPE + " TEXT," +
                    MycroftContract.Entities.COLUMN_NAME_TAG + " TEXT," +
                    MycroftContract.Entities.COLUMN_NAME_SKILL + " TEXT)";
    private static final String INTENT_CREATE_ENTRIES =
            "CREATE TABLE " + MycroftContract.Intents.TABLE_NAME + " (" +
                    MycroftContract.Intents._ID + " INTEGER PRIMARY KEY," +
                    MycroftContract.Intents.COLUMN_NAME_SKILL + " TEXT," +
                    MycroftContract.Intents.COLUMN_NAME_INTENT + " TEXT," +
                    MycroftContract.Intents.COLUMN_NAME_VALUE + " TEXT," +
                    //increases, based on order. Simple counter
                    //0 = yes, 1 = optional, 2 = other, 3 = etc
                    MycroftContract.Intents.COLUMN_NAME_TAG + " TEXT)";
    private static final String MODULE_CREATE_ENTRIES =
            "CREATE TABLE " + MycroftContract.Module.TABLE_NAME + " (" +
                    MycroftContract.Module._ID + " INTEGER PRIMARY KEY," +
                    MycroftContract.Module.COLUMN_NAME_PACKAGE + " TEXT," +
                    MycroftContract.Module.COLUMN_NAME_SKILL + " TEXT," +
                    MycroftContract.Module.COLUMN_NAME_TYPE + " TEXT)";

    private static final String ENTITIES_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MycroftContract.Entities.TABLE_NAME;
    private static final String INTENT_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MycroftContract.Intents.TABLE_NAME;
    private static final String MODULE_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MycroftContract.Module.TABLE_NAME;

        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        // This initializes the database
        public MycroftDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ENTITIES_CREATE_ENTRIES);
            db.execSQL(INTENT_CREATE_ENTRIES);
            db.execSQL(MODULE_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(ENTITIES_DELETE_ENTRIES);
            db.execSQL(INTENT_DELETE_ENTRIES);
            db.execSQL(MODULE_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}
