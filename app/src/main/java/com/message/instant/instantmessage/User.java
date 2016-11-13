package com.message.instant.instantmessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fujiaoyang1 on 11/11/16.
 */

public class User {
    private String userName;
    private String passWord;
    private List<String> groupList;

    User() {}

    User(String name, String pwd) {
        userName = name;
        passWord = pwd;
        groupList = new ArrayList<>();
    }

    public String getUserName () {return userName;}
    public String getPassword () {return passWord;}

    public List<String> getGroupList () {return groupList;}

    public void addNewGroup (String groupName) {
        groupList.add(groupName);
    }
    /*
    public void addGroupList(Map<String, Boolean> groups) {
        groupList.(groups);
    }
    */
    public void delteOneGroup (String groupName) {
        groupList.remove(groupName);
    }

}
