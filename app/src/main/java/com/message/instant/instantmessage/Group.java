package com.message.instant.instantmessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by fujiaoyang1 on 11/11/16.
 */

public class Group {
    private String groupName;
    private String hostName;
    private Set<String> userList;
    private Map<String, Map<String, String>> message;

    Group(String groupName, String hostName) {
        this.groupName = groupName;
        this.hostName = hostName;
        userList = new HashSet<>();
        message = new HashMap<String, Map<String, String>>();
    }

    String getGroupName () {return groupName;}
    String getHostName () {return hostName;}
    Set<String> getUserList () {return userList;}
}
