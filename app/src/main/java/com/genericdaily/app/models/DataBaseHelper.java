package com.genericdaily.app.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 */
public class DataBaseHelper extends SQLiteOpenHelper{

            /**创建主题日报列表的table*/
    public static final String CREATE_THEME_DAILY = "create table Theme ("
                    + "key_id integer primary key autoincrement, "
                    + "thumbnail text, "
                    + "description text, "
                    + "id text, "
                    + "name text)";

            /**创建最新消息列表的表格*/
    public static final String CREATE_HOT_NEWS_LIST = "create table HotNews ("
                    + "key_id integer primary key autoincrement, "
                    + "date text, "
                    + "title text, "
                    + "image text, "
                    + "id text, "
                    + "multipic text)";

             /**创建顶部内容的表格*/
    public static final String CREATE_TOP_NEWS_LIST = "create table TopNews ("
                     + "key_id integer primary key autoincrement, "
                     + "date text, "
                     + "title text, "
                     + "image text, "
                     + "id text)";

            /**创建已读消息的表格*/
    public static final String CREATE_READED_NEWS_LIST = "create table Read ("
                    + "key_id integer primary key autoincrement, "
                    + "date text, "
                    + "id text)";


    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_THEME_DAILY);
      //  db.execSQL(CREATE_HOT_NEWS_LIST);
       // db.execSQL(CREATE_TOP_NEWS_LIST);
        db.execSQL(CREATE_READED_NEWS_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
