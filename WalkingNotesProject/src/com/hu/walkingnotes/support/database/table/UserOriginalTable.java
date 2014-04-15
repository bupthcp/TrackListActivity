package com.hu.walkingnotes.support.database.table;

public class UserOriginalTable {
    public static final String TABLE_NAME = "user_original_table";
    //support multi user,so primary key can't be message id
    public static final String ID = "_id";
    //support mulit user
    public static final String ACCOUNTID = "accountid";

    public static final String TIMELINEDATA = "timelinedata";


    public static class UserOriginalDataTable {

        public static final String TABLE_NAME = "user_original_data_table";
        //support multi user,so primary key can't be message id
        public static final String ID = "_id";
        //support mulit user
        public static final String ACCOUNTID = "accountid";
        //message id
        public static final String MBLOGID = "mblogid";

        public static final String JSONDATA = "json";

    }
}
