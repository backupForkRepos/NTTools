package io.kurumi.nttools.fragments;

import com.pengrad.telegrambot.model.InlineQuery;
import io.kurumi.nttools.model.Callback;
import io.kurumi.nttools.model.Msg;
import io.kurumi.nttools.twitter.TwiAccount;
import io.kurumi.nttools.utils.CData;
import io.kurumi.nttools.utils.UserData;
import io.kurumi.nttools.model.request.AbstractSend;

public abstract class FragmentBase {

    public boolean processPrivateMessage(UserData user, Msg msg) { return false; }
    public boolean processGroupMessage(UserData user, Msg msg) { return false; }
    public boolean processChannelPost(UserData user, Msg msg) { return false; }
    public boolean processCallbackQuery(UserData user, Callback callback) { return false; }
    public boolean processInlineQuery(UserData user, InlineQuery inlineQuery) { return false; }
    public boolean processChosenInlineQueryResult(UserData user, InlineQuery inlineQuery) { return false; }

    public CData cdata(String point) {

        CData data = new CData();

        data.setPoint(point);

        return data;

    }

    public CData cdata(String point, String index) {

        CData data = cdata(point);

        data.setindex(index);

        return data;

    }

    public CData cdata(String point, UserData userData, TwiAccount account) {

        CData data = cdata(point);

        data.setUser(userData, account);

        return data;

    }

    public CData cdata(String point, String index, UserData userData, TwiAccount account) {

        CData data = cdata(point, index);

        data.setUser(userData, account);

        return data;

    }

    public AbstractSend sendOrEdit(Msg msg, boolean edit, String... contnent) {

        if (!edit)return msg.send(contnent);
        else return msg.edit(contnent);

    }

}
