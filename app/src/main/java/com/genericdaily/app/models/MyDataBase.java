package com.genericdaily.app.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MyDataBase {

    public static final String DB_name = "news_DB";//数据库名

    public static final int VERSION = 1; //版本名

    private  static MyDataBase myDataBase;
    private SQLiteDatabase db;

    private MyDataBase(Context context){
        DataBaseHelper dbHelper = new DataBaseHelper(context,DB_name,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static MyDataBase getMyDataBase(Context context){
        if(myDataBase == null){
            myDataBase = new MyDataBase(context);
        }
        return myDataBase;
    }

    /**
     *将主题日报列表存储到数据库
     */
    public void saveThemeDailyList(JSONObject jsonObject){
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("others");
                if (jsonArray!= null){
                    db.delete("Theme",null,null);
                for (int i = 0; i < jsonArray.length();i++){
                    ContentValues values = new ContentValues();
                    jsonObject = jsonArray.getJSONObject(i);
                    values.put("thumbnail",jsonObject.getString("thumbnail"));
                    values.put("description",jsonObject.getString("description"));
                    values.put("id",jsonObject.getInt("id"));
                    values.put("name",jsonObject.getString("name"));
                    db.insert("Theme",null,values);
                }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

    }

    /**
     *查询一列数据
     */
    public List<String> loadData (String tableName,String colunmName){
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query(tableName,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                String set = cursor.getString(cursor.getColumnIndex(colunmName));
                list.add(set);
            }while (cursor.moveToNext());
        }
        if (cursor!=null){
            cursor.close();
        }
        return list;
    }

    public void saveWeatheridRead(String id){
        db.execSQL("INSERT INTO Read (id) VALUES ("+id+")");
    }
}
