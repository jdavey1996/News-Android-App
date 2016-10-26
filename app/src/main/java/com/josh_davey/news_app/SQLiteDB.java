package com.josh_davey.news_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class SQLiteDB extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "Articles";

    // Contacts table name
    private static final String TABLE_ALL = "all_articles";
    private static final String TABLE_LOCAL = "local_articles";
    private static final String TABLE_TOP = "top_articles";

    // Contacts Table Columns names
    private static final String KEY_NUM = "number";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESC = "desc";

    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALL_TABLE = "CREATE TABLE " + TABLE_ALL + "("
                + KEY_NUM + " TEXT PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_DESC + " TEXT" + ")";
        db.execSQL(CREATE_ALL_TABLE);

        String CREATE_LOCAL_TABLE = "CREATE TABLE " + TABLE_LOCAL + "("
                + KEY_NUM + " TEXT PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_DESC + " TEXT" + ")";
        db.execSQL(CREATE_LOCAL_TABLE);

        String CREATE_TOP_TABLE = "CREATE TABLE " + TABLE_TOP + "("
                + KEY_NUM + " TEXT PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_DESC + " TEXT" + ")";
        db.execSQL(CREATE_TOP_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP);

        // Create tables again
        onCreate(db);
    }


    public void addArticle(ArticleConstructor article,String table) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_NUM, article.getArticleNum());
        values.put(KEY_TITLE, article.getArticleTitle());
        values.put(KEY_DESC, article.getArticleDesc());

        db.insert(table, null, values);
        db.close();
    }

    public ArrayList<ArticleConstructor> getArticles(String table) {
        ArrayList<ArticleConstructor> list = new ArrayList<ArticleConstructor>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + table;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ArticleConstructor article = new ArticleConstructor(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)

                );
                // Adding contact to list
                list.add(article);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public void deleteAll(String table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+table+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                db.execSQL("DELETE FROM " + table);
            }
            cursor.close();
        }
    }
}