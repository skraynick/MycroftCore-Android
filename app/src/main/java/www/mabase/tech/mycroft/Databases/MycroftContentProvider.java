package www.mabase.tech.mycroft.Databases;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MycroftContentProvider extends ContentProvider {
    public MycroftContentProvider() {
    }

    private static final int ENTITY = 100;
    private static final int INTENT = 200;
    private static final int MODULE = 300;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MycroftDbHelper mDbHelper;

    //Builds a UriMatcher that is used to determine which table is being requested
    public static UriMatcher buildUriMatcher(){

        String content = MycroftContract.CONTENT_AUTHORITY;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(content, MycroftContract.PATH_ENTITY, ENTITY);
        matcher.addURI(content, MycroftContract.PATH_INTENT, INTENT);
        matcher.addURI(content, MycroftContract.PATH_MODULE, MODULE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MycroftDbHelper(getContext());

        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long _id = 0;
        //Uri returnUri;

        switch(sUriMatcher.match(uri)) {
            case MODULE:
                _id = db.insert(MycroftContract.Module.TABLE_NAME, null, values);
                //Use this to return the row of the entry. It will allow a quick lookup later when
                //checking for updates
                /*if(_id > 0){
                    returnUri =  MycroftContract.Module.buildGenreUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
                */
                Log.e("Mycroft","Putting information in the MODULE table");
                break;
            default:
                Log.e("Mycroft","You suck at making Uris");
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //This is for skills and modules to offload all of their information in Mycroft as needed
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        switch(sUriMatcher.match(uri)){
            case ENTITY:
                Log.i("ContentProvider", "Entity bulkInsert works");
                break;
            case INTENT:
                Log.i("ContentProvider", "Intent bulkInsert works");
                break;
            case MODULE:
                Log.i("ContentProvider", "Module bulkInsert works");
                break;
        }
        return 1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
