package theappsnamegoeshere.apptest.testapp.camtag;

/**
 * Created by Ben on 2/20/2017.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;


public class DBhandler extends SQLiteOpenHelper{
    private static final int database_version = 1;
    private static final String database_name = "CamTag.db";

    private static final String TAG_TABLE = "Tags";
    private static final String IMAGE_TABLE = "Images";
    private static final String LINK_TABLE = "Links";

    private static final String TAG_ID = "TAG_ID";
    private static final String IMAGE_ID = "IMAGE_ID";
    private static final String LINK_ID = "LINK_ID";

    private static final String TAG_NAME = "TAG_NAME";

    private static final String IMAGE_FILENAME = "IMAGE_FILENAME";
    private static final String IMAGE_SIZE = "IMAGE_SIZE";
    private static final String IMAGE_DATE = "IMAGE_DATE";




    // handles database logic - connects class objects and their functions to tables and their Insert, Update, Delete commands
    public DBhandler(Context context) {
        super(context, database_name, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       // Tags table
        String CREATE_TAGS_TABLE = "CREATE TABLE " + TAG_TABLE + "("
                + TAG_ID + " INTEGER PRIMARY KEY," + TAG_NAME + " TEXT)";
        db.execSQL(CREATE_TAGS_TABLE);
       //Images table - need to add 2 columns for width and height
        String CREATE_IMAGES_TABLE = "CREATE TABLE " + IMAGE_TABLE + "("
                + IMAGE_ID + " INTEGER PRIMARY KEY," + IMAGE_FILENAME + " TEXT)";
        db.execSQL(CREATE_IMAGES_TABLE);
        // Links table
        String CREATE_LINKS_TABLE = "CREATE TABLE " + LINK_TABLE + "("
                + LINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + IMAGE_ID + " TEXT, " + TAG_ID + " TEXT)";
        db.execSQL(CREATE_LINKS_TABLE);
    }

    // Upgrading database version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LINK_TABLE);
        // Create tables again
        onCreate(db);
    }
    // INSERT Commands

    public void addTag(Tags t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TAG_NAME, t.get_tagName());
        db.insert(TAG_TABLE, null, values);

    }
    // need to add date, width and height
    public void addImage(String imagefname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_FILENAME, imagefname);
        db.insert(IMAGE_TABLE, null, values);

    }
    public void addLink(String imageID, String tagID ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.clear();

        values.put(IMAGE_ID, imageID);
        values.put(TAG_ID,tagID);
        db.insert(LINK_TABLE, null, values);

    }
    public void addLinks(ImageTags t){
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> tags = t.listoftags();
        String fname = t.getimagename();
        if (fname == "nullfname"){
            ContentValues cv = new ContentValues();
            cv.put(IMAGE_ID,"fail");
            db.insert(LINK_TABLE,null,cv);
            return;
        }
        String imageid;
        Cursor c = db.rawQuery("SELECT IMAGE_ID FROM Images WHERE IMAGE_FILENAME = '" + fname.trim() +"'",null);
        if(c != null && c.moveToFirst())
        {
            imageid = c.getString(c.getColumnIndex("IMAGE_ID"));
            String temp = "";
            for (String j : tags){
                ContentValues cv = new ContentValues();
                Cursor xx = db.rawQuery("SELECT TAG_ID FROM Tags WHERE TAG_NAME = '"+ j + "'",null);
                if (xx != null && xx.moveToFirst())
                {
                    temp = xx.getString(xx.getColumnIndex(TAG_ID));
                    cv.put(IMAGE_ID,imageid);
                    cv.put(TAG_ID,temp);
                    db.insert(LINK_TABLE,null,cv);
                }
            }

            return;
        }
        else {

            return;

        }

    }

    public boolean containsTag(String tagname){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Tags WHERE TAG_NAME = '" + tagname.trim() +"'",null);
        if (c.getCount()<=0)
        {
            c.close();
            db.close();
            return false;
        }
        else
        {
            return true;
        }
    }
    public void clearTable(String TableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TableName,null,null);

    }
    public Tags getTag(String tagname) {

        return null;
    }
    public List<String> getTagsList()
    {
        List<String> tl = new ArrayList<String>();

        String select = "SELECT * FROM " + TAG_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(select, null);

        if(c.moveToFirst())
        {
            do{
                tl.add(c.getString(1));
            }while (c.moveToNext());
        }
        return tl;
    }
    public List<String> getCustomTagsList(String typed){
        SQLiteDatabase db = this.getReadableDatabase();
        String select;
        if (typed.contains(" "))
        {
            select = "SELECT "+ TAG_NAME + " FROM "+ TAG_TABLE+" WHERE "+TAG_NAME+ " LIKE '"+typed+"%'";
        }
        else
        {
            select = "SELECT "+ TAG_NAME + " FROM "+ TAG_TABLE+" WHERE "+TAG_NAME+ " LIKE '%"+typed+"%'";
        }

        Cursor c = db.rawQuery(select, null);
        List<String> tl = new ArrayList<String>();
        if(c.moveToFirst())
        {
            do{
                tl.add(c.getString(0));
            }while (c.moveToNext());
        }
        return tl;
    }
    public List<String> getContainsImageList(List<String> ls) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select;
        if (ls.isEmpty()) {
            return new ArrayList<String>();
        } else if (ls.size() == 1) {
            select = "SELECT " + IMAGE_TABLE + "." + IMAGE_FILENAME + " FROM " +
                    IMAGE_TABLE + " JOIN " + LINK_TABLE + " ON " + LINK_TABLE + "." + IMAGE_ID + " = " + IMAGE_TABLE + "." + IMAGE_ID +
                    " JOIN " + TAG_TABLE + " ON " + TAG_TABLE + "." + TAG_ID + " = " + LINK_TABLE + "." + TAG_ID + " WHERE " + TAG_TABLE + "." + TAG_NAME + " IN ('" + ls.get(0) + "') " +
                    "GROUP BY " + IMAGE_TABLE + "." + IMAGE_ID;

            try {
                Cursor c = db.rawQuery(select, null);
                List<String> tl = new ArrayList<String>();
                if (c.moveToFirst()) {
                    do {
                        tl.add(c.getString(0));
                    } while (c.moveToNext());
                }
                return tl;
            } catch (Exception e) {
                return new ArrayList<String>();
            }
        }
        else{
            select = "SELECT " + IMAGE_TABLE + "." + IMAGE_FILENAME + " FROM " +
                    IMAGE_TABLE + " JOIN " + LINK_TABLE + " ON " + LINK_TABLE + "." + IMAGE_ID + " = " + IMAGE_TABLE + "." + IMAGE_ID +
                    " JOIN " + TAG_TABLE + " ON " + TAG_TABLE + "." + TAG_ID + " = " + LINK_TABLE + "." + TAG_ID + " WHERE " + TAG_TABLE + "." + TAG_NAME + " IN (";
            for (String j : ls)
            {
                select += "'"+j+"'";
                if (ls.get(ls.size()-1) != j){
                    select +=",";
                }
            }
            String add = ") " +
                    "GROUP BY " + IMAGE_TABLE + "." + IMAGE_ID+" HAVING COUNT(DISTINCT "+TAG_TABLE+"."+TAG_NAME+") >=" + Integer.toString(ls.size());
            select += add;

            try {
                Cursor c = db.rawQuery(select, null);
                List<String> tl = new ArrayList<String>();
                if (c.moveToFirst()) {
                    do {
                        tl.add(c.getString(0));
                    } while (c.moveToNext());
                }
                return tl;
            } catch (Exception e) {
                return new ArrayList<String>();
            }
        }
    }
public List<String> getImageList(){
    SQLiteDatabase db = this.getReadableDatabase();
    String select = "SELECT "+ IMAGE_FILENAME + " FROM "+ IMAGE_TABLE;
    Cursor c = db.rawQuery(select, null);
    List<String> tl = new ArrayList<String>();
    if(c.moveToFirst())
    {
        do{
            tl.add(c.getString(0));
        }while (c.moveToNext());
    }
    return tl;
}

    public void deleteTag(String s){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TAG_TABLE +" WHERE TAG_NAME = '" + s.trim() +"'");

    }
}

