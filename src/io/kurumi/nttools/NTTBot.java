package io.kurumi.nttools;

import io.kurumi.nttools.fragments.MainFragment;
import io.kurumi.nttools.model.Callback;
import io.kurumi.nttools.model.Msg;
import io.kurumi.nttools.twitter.TwitterDataParser;
import io.kurumi.nttools.twitter.TwitterUI;
import io.kurumi.nttools.utils.UserData;
import java.io.File;
import io.kurumi.nttools.spam.SpamUI;
import io.kurumi.nttools.model.request.Send;
import io.kurumi.nttools.spam.VoteUI;
import io.kurumi.nttools.twitter.TwitterFunc;

public class NTTBot extends MainFragment {

    public NTTBot(File dataDir) {

        super(dataDir);

        fragments.add(TwitterUI.INSTANCE);
        fragments.add(SpamUI.INSTANCE);
        fragments.add(VoteUI.INSTANCE);
        fragments.add(TwitterDataParser.INSTANCE);

        timer.tasks.add(VoteUI.INSTANCE);

        fragments.add(TwitterFunc.INSTANCE);

    }

    @Override
    public boolean processPrivateMessage(UserData user, Msg msg) {

        if (!user.isAdmin && true) {
            
            msg.send("bot正在紧急维护 请过 18:30 再试试 （￣～￣）").exec();
            return true;
            
        }


        if (msg.isCommand()) {

            switch (msg.commandName()) {

                    case "start" : 

                    if (msg.commandParms().length != 0) {

                        return SpamUI.INSTANCE.processPrivateMessage(user, msg);

                    } else help(user, msg); break;

                    case "help" : 

                    help(user, msg);


                    return true;


                    case "admin" :

                    admin(user, msg);

                    return true;


            }

        }

        return false;

    }

    @Override
    public boolean processCallbackQuery(UserData user, Callback callback) {

        if (!user.isAdmin && true) {

            callback.alert("bot 正在紧急维护 请过 18:30 再来 （￣～￣）");

            return true;

        } else return false;

    }

    public void admin(UserData user, Msg msg) {

        if (user.isAdmin && msg.commandParms().length == 2) {

            String targetStr =  msg.commandParms()[0];

            if (targetStr.startsWith("@"))  {

                targetStr = targetStr.substring(1);

            }

            UserData target = findUserData(targetStr);

            boolean action = Boolean.parseBoolean(msg.commandParms()[1]);

            if (target != null) {

                target.isAdmin = action;

                target.save();

                msg.send(target.name + " (@" + target.userName + ") 已被设置了管理员权限 : " + action).send();

                if (action) {

                    new Send(this, target.id, "您已被 " + user.name + " (@" + user.userName + ") 设为管理员 (◦˙▽˙◦)").exec();

                }

            } else {

                msg.send("本地没有此用户的数据 : @" + targetStr).exec();

            }

        }

    }

    public void help(UserData user, Msg msg) {

        String[] helpMsg = new String[] {

            "这里是奈间家的BOT (◦˙▽˙◦)","",

            TwitterUI.help,
            SpamUI.help,

            "",
            "",

            TwitterDataParser.help


        };

        msg.send(helpMsg).exec();

    }

}
