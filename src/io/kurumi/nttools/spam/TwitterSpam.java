package io.kurumi.nttools.spam;

import cn.hutool.core.util.ArrayUtil;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import io.kurumi.nttools.fragments.Fragment;
import io.kurumi.nttools.model.Msg;
import io.kurumi.nttools.model.request.ButtonMarkup;
import io.kurumi.nttools.model.request.Send;
import io.kurumi.nttools.twitter.TwiAccount;
import io.kurumi.nttools.utils.Markdown;
import io.kurumi.nttools.utils.UserData;
import java.util.LinkedList;
import java.util.Iterator;
import com.pengrad.telegrambot.response.BaseResponse;

public class TwitterSpam {

    public static final String PUBLIC_CHANNEL = "NTTSpamPublic";
    public static final String VOTE_CHANNEL = "NTTSpamVote";
    public static final String DISCUSS_GROUP = "NTTDiscuss";

    private Fragment fragment;

    public TwitterSpam(Fragment fragment) {

        this.fragment = fragment;

    }

    public String formatSpam(SpamVote vote) {

        StringBuilder str = new StringBuilder();

        if (!vote.agree.isEmpty()) {

            str.append("同意 : \n\n");

            Iterator<Long> i = vote.agree.iterator();

            while(i.hasNext()) {
               
                UserData user = fragment.main.getUserData(i.next());

                if (!user.twitterAccounts.isEmpty()) {
                
                str.append(user.twitterAccounts.getFirst().getFormatedNameMarkdown());
                str.append("\n");
                
                } else {
                    
                    i.remove();
                    
                    vote.save();
                    
                }

            }

            str.append("\n");

        }

        if (!vote.disagree.isEmpty()) {

            str.append("反对 : \n\n");

            Iterator<Long> i = vote.disagree.iterator();

            while(i.hasNext()) {

                UserData user = fragment.main.getUserData(i.next());

                if (!user.twitterAccounts.isEmpty()) {

                    str.append(user.twitterAccounts.getFirst().getFormatedNameMarkdown());
                    str.append("\n");

                } else {

                    i.remove();

                    vote.save();

                }

            }

            str.append("\n");

        }

        str.append("\n");

        return str.toString();

    }

    public void votePassed(final SpamVote vote) {

        SpamList list = fragment.main.getSpamList(vote.listId);

        UserSpam spam = new UserSpam(list);

        spam.origin = vote.origin;

        spam.twitterAccountId = vote.twitterAccountId;
        spam.twitterScreenName = vote.twitterScreenName;
        spam.twitterDisplyName = vote.twitterDisplyName;

        spam.spamCause = vote.spamCause;

        list.spamUsers.add(spam);

        String[] passMsg = new String[] {

            "投票通过了将 [「" + Markdown.encode(spam.twitterDisplyName) + "」](https://twitter.com/" + spam.twitterScreenName + ")",
            "",
            "添加到公共列表 「 " + spam.belongTo.name + " 」 的决定"

        };

        final Msg pubMsg = new Send(fragment, "@" + PUBLIC_CHANNEL, passMsg)
            .buttons(new ButtonMarkup() {{

                    newUrlButtonLine("投票地址", "https://t.me/" + VOTE_CHANNEL + "/" + vote.vote_message_id);

                }}).markdown().disableLinkPreview().send();

        spam.public_message_id = pubMsg.messageId();

        fragment.bot.execute(new EditMessageReplyMarkup("@" + VOTE_CHANNEL, vote.vote_message_id)
                             .replyMarkup(new ButtonMarkup() {{

                                                  newUrlButtonLine("结果 : 已通过", "https://t.me/" + PUBLIC_CHANNEL + "/" + pubMsg.messageId());

                                              }}.markup()));

        fragment.main.deleteSpamVote(vote.id);

        list.save();

    }

    public void adminPassed(UserData user, final SpamVote vote, String cause) {

        SpamList list = fragment.main.getSpamList(vote.listId);

        UserSpam spam = new UserSpam(list);

        spam.origin = vote.origin;

        spam.twitterAccountId = vote.twitterAccountId;
        spam.twitterScreenName = vote.twitterScreenName;
        spam.twitterDisplyName = vote.twitterDisplyName;

        spam.spamCause = vote.spamCause;

        list.spamUsers.add(spam);

        String[] passMsg = new String[] {

            "管理员通过了将 [「" + Markdown.encode(spam.twitterDisplyName) + "」](https://twitter.com/" + spam.twitterScreenName + ")",
            "",
            "添加到公共列表 「 " + spam.belongTo.name + " 」 的决定",
            "",
            "(" + cause + ")",
            "",
            "操作人 : " + user.markdownName()

        };

        final Msg pubMsg = new Send(fragment, "@" + PUBLIC_CHANNEL, passMsg)
            .buttons(new ButtonMarkup() {{

                    newUrlButtonLine("投票地址", "https://t.me/" + VOTE_CHANNEL + "/" + vote.vote_message_id);

                }}).markdown().disableLinkPreview().send();

        spam.public_message_id = pubMsg.messageId();

        fragment.bot.execute(new EditMessageReplyMarkup("@" + VOTE_CHANNEL, vote.vote_message_id)
                                                 .replyMarkup(new ButtonMarkup() {{

                                                  newUrlButtonLine("结果 : 管理员通过", "https://t.me/" + PUBLIC_CHANNEL + "/" + pubMsg.messageId());

                                              }}.markup()));

        fragment.main.deleteSpamVote(vote.id);

        list.save();

    }

    public void adminRejected(UserData user, final SpamVote vote, String cause) {

        String[] passMsg = new String[] {

            "管理员否决了将 [「" + Markdown.encode(vote.twitterDisplyName) + "」](https://twitter.com/" + vote.twitterScreenName + ")","","添加到公共列表 「 " + fragment.main.getSpamList(vote.listId).name + " 」 的决定",
            "",
            "(" + cause + ")",
            "",
            "操作人 : " + user.markdownName()

        };

        final Msg pubMsg = new Send(fragment, "@" + PUBLIC_CHANNEL, passMsg)
            .buttons(new ButtonMarkup() {{

                    newUrlButtonLine("投票地址", "https://t.me/" + VOTE_CHANNEL + "/" + vote.vote_message_id);

                }}).markdown().disableLinkPreview().send();

        fragment.bot.execute(new EditMessageReplyMarkup("@" + VOTE_CHANNEL, vote.vote_message_id)
                             .replyMarkup(new ButtonMarkup() {{

                                                  newUrlButtonLine("结果 : 管理员否决", "https://t.me/" + PUBLIC_CHANNEL + "/" + pubMsg.messageId());

                                              }}.markup()));

        fragment.main.deleteSpamVote(vote.id);

    }

    public void voteRejected(final SpamVote vote) {

        String[] passMsg = new String[] {

            "投票否决了将 [「" + Markdown.encode(vote.twitterDisplyName) + "」](https://twitter.com/" + vote.twitterScreenName + ")","","添加到公共列表 「 " + fragment.main.getSpamList(vote.listId).name + " 」 的决定",

        };

        final Msg pubMsg = new Send(fragment, "@" + PUBLIC_CHANNEL, passMsg)
            .buttons(new ButtonMarkup() {{

                    newUrlButtonLine("投票地址", "https://t.me/" + VOTE_CHANNEL + "/" + vote.vote_message_id);

                }}).markdown().disableLinkPreview().send();

        fragment.bot.execute(new EditMessageReplyMarkup("@" + VOTE_CHANNEL, vote.vote_message_id)
                             .replyMarkup(new ButtonMarkup() {{

                                                  newUrlButtonLine("结果 : 已否决", "https://t.me/" + PUBLIC_CHANNEL + "/" + pubMsg.messageId());

                                              }}.markup()));

        fragment.main.deleteSpamVote(vote.id);

    }
    

    public String newSpam(SpamList list, UserSpam spam) {

        UserData origin = fragment.main.getUserData(spam.origin);

        String[] newSpamMsg = new String[] {

            "Twitter 用户 [" + Markdown.encode(spam.twitterDisplyName) + "](https://twitter.com/" + spam.twitterScreenName + ")",
            "",
            "已被添加到 公共分类 「 " + spam.belongTo.name + " 」","",
            "原因 : " + Markdown.encode(spam.spamCause),
            "",
            "操作人 : [" + Markdown.encode(origin.name) + "](https://t.me/" + origin.userName + ")"

        };


        Msg pubMsg = new Send(fragment, "@" + PUBLIC_CHANNEL, newSpamMsg).markdown().disableLinkPreview().send();

        spam.public_message_id = pubMsg.messageId();

        list.spamUsers.add(spam);

        list.save();

        return "https://t.me/" + PUBLIC_CHANNEL + "/" + pubMsg.messageId();
        
        
    }

    public void remSpam(UserData user,UserSpam spam,String cause) {
        
        String[] newSpamMsg = new String[] {

            "[" + Markdown.encode(spam.twitterDisplyName) + "](https://twitter.com/" + spam.twitterScreenName + ")",
            "",
            "已从 公共分类 「 " + spam.belongTo.name + " 」 被移出",
            "",
            "原因 : " + cause,
            "",
            "操作人 : [" + Markdown.encode(user.name) + "](https://t.me/" + user.userName + ")"

        };


        new Send(fragment, "@" + PUBLIC_CHANNEL, newSpamMsg).markdown().disableLinkPreview().exec();

    }

}
