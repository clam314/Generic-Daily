package com.genericdaily.app.utils;

import android.util.Log;

import com.genericdaily.app.models.MyDataBase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by clam314 on 2016/1/30.
 */
public class Utils {

    /**
     * 解析和处理服务器返回的主题日报列表
     */
    public synchronized static List<String> handleThemeResponse(MyDataBase myDataBase,JSONObject jsonObject){
        if(jsonObject != null) {
            myDataBase.saveThemeDailyList(jsonObject);
        }
            return myDataBase.loadData("Theme","name");
    }

    public static List<NewsInformation> handleHotNewsResponse(JSONObject jsonObject){
        List<NewsInformation> list = new ArrayList<>();
        if(jsonObject != null){
            NewsInformation information = null;
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("top_stories");
                for (int i = 0;i < jsonArray.length(); i++){
                    information = new NewsInformation();
                    jsonObject = jsonArray.getJSONObject(i);
                    information.title = jsonObject.getString("title");
                    information.id = jsonObject.getString("id");
                    information.images = jsonObject.getString("image");
                    list.add(information);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<NewsInformation> handleNewsResponse(JSONObject jsonObject,boolean today){
        List<NewsInformation> list = new ArrayList<>();
        if(jsonObject != null){
            JSONObject jsonObject1 = null;
            JSONArray jsonArray1 = null;
            NewsInformation information = null;
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("stories");
                for (int i = 0;i < jsonArray.length(); i++){
                    information = new NewsInformation();
                    jsonObject1 = jsonArray.getJSONObject(i);

                    jsonArray1 = jsonObject1.getJSONArray("images");
                    information.images = jsonArray1.getString(0);
                    if(i==0) {
                        information.date = dateformat(jsonObject.getString("date"));

                    }
                    if(!today){
                        information.dateFormat = dateformat(jsonObject.getString("date"));
                    }else {
                        information.dateFormat = "今日热闻";
                    }
                    information.title = jsonObject1.getString("title");
                    information.id = jsonObject1.getString("id");
                    if(jsonObject1.has("multipic")) {
                        information.multipic = jsonObject1.getBoolean("multipic");
                    }
                    list.add(information);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return list;
    }

    public static NewsContent handleNewsContent(JSONObject jsonObject){
        NewsContent newsContent = null;
        if (jsonObject != null){
            newsContent = new NewsContent();
            try {
                newsContent.body = jsonObject.getString("body");
                newsContent.image = jsonObject.getString("image");
                newsContent.image_source = jsonObject.getString("image_source");
                newsContent.title = jsonObject.getString("title");
                newsContent.id = jsonObject.getString("id");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return newsContent;
    }


    public static StoryExtra handleStoryExtra(JSONObject jsonObject){
        StoryExtra storyExtra = null;
        if (jsonObject != null){
            storyExtra = new StoryExtra();
            try {
                storyExtra.comments = jsonObject.getString("comments");
                storyExtra.popularity = intiNum( jsonObject.getInt("popularity"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return storyExtra;
    }

    public static String  dateformat (Calendar calendar){
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyyMMdd");
        return  dateFormat.format(calendar.getTime());
    }

    public static String dateformat(String ddate){
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = dateFormat.parse(ddate);
            dateFormat = new SimpleDateFormat("MM月dd日 E");
            return dateFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getIdArrayList(List<NewsInformation> list){
        ArrayList<String> arrayList = new ArrayList<>();
        for (NewsInformation information : list){
            arrayList.add(information.id);
        }
        return arrayList;
    }

    /**格式化为千为单位，保留百位做小数*/
    private static String intiNum(int i){
        if(i<1000){
            return String.valueOf(i);
        }else {
            int qian = i/1000;
            int bai = i%1000/100;
            return qian+"."+bai+"k";
        }
    }
}
