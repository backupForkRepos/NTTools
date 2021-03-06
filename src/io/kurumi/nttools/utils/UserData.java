package io.kurumi.nttools.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import io.kurumi.nttools.fragments.Fragment;
import io.kurumi.nttools.fragments.MainFragment;
import io.kurumi.nttools.twitter.TwiAccount;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UserData extends JSONObject {

    public final Long id;
    
    public File userDataFile;

    public UserData(Fragment fragment, long id) {

        this.id = id;

        userDataFile = new File(fragment.main.dataDir, "users/" + id + ".json");

        refresh();

    }

    public String markdownName() {
       
        return "[" + name + "](https://t.me/" + userName + ")";
    }
    
    public void update(User user) {

        setName(user.firstName(),user.lastName());
        
        userName = user.username();
        
        isBot = user.isBot();
        
        isAdmin =  isAdmin || "HiedaNaKan".equals(userName);
        
        save();
        
    }

    public String name;
    
    public String userName;
    
    public boolean isAdmin;
    
    public boolean isBanned;
    
    
    public boolean isBot;
    
    public CData point;
    
    public LinkedList<TwiAccount> twitterAccounts = new LinkedList<>();

    public void setName(String first, String last) {

        if (last != null && !"".equals(last)) {

            first =  last + " " + first;

        }

        name = first;

    }

    public void refresh() {

        try {

            JSONObject userData = new JSONObject(FileUtil.readUtf8String(userDataFile));

            clear();

            putAll(userData);

        } catch (Exception e) {}
        
        name = getStr("name");
        
        userName = getStr("user_name");
        
        isBot = getBool("is_bot",false);

        isAdmin = "HiedaNaKan".equals(userName) || getBool("is_admin", false);
        
        isBanned = !"HiedaNaKan".equals(userName) && getBool("is_banned",false);
        
        JSONObject pointObj = getJSONObject("point");
        
        if (pointObj == null) point = null;
        else point = new CData(pointObj);
        
        twitterAccounts.clear();
        
        List<JSONObject> twitterAccountList = (List<JSONObject>)(Object)getJSONArray("twitter_accounts");

        if (twitterAccountList != null) {

            for (JSONObject obj : twitterAccountList) {

                twitterAccounts.add(new TwiAccount(obj));

            }

        }

    }
    
    public void save() {

        put("name",name);
        
        put("user_name",userName);
        
        put("is_bot",isBot);
        
        put("is_admin",isAdmin);
        
        put("is_banned",isBanned);
        
        put("point",point);
        
        JSONArray twitterAccountList = new JSONArray();

        for (TwiAccount account : twitterAccounts) {

            twitterAccountList.add(account.save());

        }
        
        put("twitter_accounts", twitterAccountList);

        FileUtil.writeUtf8String(toStringPretty(), userDataFile);

    }
    
    public TwiAccount findUser(String screenName) {

        for (TwiAccount acc : twitterAccounts) {

            if (screenName.equals(acc.screenName)) {

                return acc;

            }

        }

        return null;

    }

    public TwiAccount findUser(long accountId) {

        for (TwiAccount acc : twitterAccounts) {

            if (acc.accountId == accountId) {

                return acc;

            }

        }

        return null;

    }
    
    public void delete() {

        FileUtil.del(userDataFile);

    }
 
    @Override
    public boolean equals(Object obj) {

        return super.equals(obj) || (obj instanceof UserData && id.equals(((UserData)obj).id));

    }

}

