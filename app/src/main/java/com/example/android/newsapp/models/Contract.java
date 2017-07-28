package com.example.android.newsapp.models;

import android.provider.BaseColumns;

/**
 * Created by Danny on 7/27/2017.
 */

//added a contract to establish a database
public class Contract {

    public static class TABLE_ARTICLES implements BaseColumns {
        public static final String TABLE_NAME = "news";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DESC = "desc";
        public static final String COLUMN_NAME_IMGURL = "imgUrl";
        public static final String COLUMN_NAME_URL = "url";
    }
}
