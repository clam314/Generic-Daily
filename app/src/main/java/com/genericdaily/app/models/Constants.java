package com.genericdaily.app.models;


public final class Constants {

    private Constants(){

    }

    public static final class Url{
        //获取开启画面图片
        public static final String START_IMAGE= "http://news-at.zhihu.com/api/4/start-image/480*728";
        //获取主题日报大概信息
        public static final String THEMES_DAILY = "http://news-at.zhihu.com/api/4/themes";
        //获取今日热闻
        public static final String HOT_NEWS = "http://news-at.zhihu.com/api/4/news/latest";
        //获取消息内容
        public static final String NEWS_CONTENT = "http://news-at.zhihu.com/api/4/news/";
        //获取以前的新闻列表
        public static final String OLD_NEWS = "http://news.at.zhihu.com/api/4/news/before/";
        //获取新闻额外信息
        public static final String EXTRA_INFORMATION = "http://news-at.zhihu.com/api/4/story-extra/";
    }


}