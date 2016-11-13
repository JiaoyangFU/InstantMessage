package com.message.instant.instantmessage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by fujiaoyang1 on 11/11/16.
 */

public class Group {
    private String groupName;
    private String hostName;
    private List<String> userList;
    private Map<String, Map<String, String>> message;

    Group(String groupName, String hostName) {
        this.groupName = groupName;
        this.hostName = hostName;
        userList = new LinkedList<>();
        userList.add(hostName);
        message = new HashMap<String, Map<String, String>>();
    }

    String getGroupName () {return groupName;}
    String getHostName () {return hostName;}
    List<String>  getUserList () {return userList;}
}
