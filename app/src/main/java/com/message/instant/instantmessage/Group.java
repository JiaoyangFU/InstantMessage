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
    private Map<String, String> messages;
    Group () {
        if (userList == null) {
            userList = new LinkedList<>();
            userList.add(hostName);
        }
        if (messages == null) {
            messages = new HashMap<String, String>();
        }
    }
    Group(String groupName, String hostName) {
        this.groupName = groupName;
        this.hostName = hostName;
        userList = new LinkedList<>();
        userList.add(hostName);
        messages = new HashMap<String, String>();
    }

    public void addNewUser(String userName) {
        userList.add(userName);
    }

    public String getGroupName () {return groupName;}
    public String getHostName () {return hostName;}
    public List<String>  getUserList () {return userList;}
    public Map<String, String>  getMsg () {return messages;}
}
