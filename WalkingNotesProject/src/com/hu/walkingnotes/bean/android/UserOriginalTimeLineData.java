package com.hu.walkingnotes.bean.android;

import com.hu.walkingnotes.bean.MessageListBean;


public class UserOriginalTimeLineData {
    public MessageListBean msgList;
    public TimeLinePosition position;

    public UserOriginalTimeLineData(MessageListBean msgList, TimeLinePosition position) {
        this.msgList = msgList;
        this.position = position;
    }
}
