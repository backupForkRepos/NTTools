package io.kurumi.nttools.utils;

import org.apache.commons.cli.*;

import io.kurumi.nttools.model.Msg;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import io.kurumi.nttools.fragments.FragmentBase;

public abstract class CommandUI extends FragmentBase {

    protected abstract  String cmdLineSyntax();

    protected abstract void applyOptions(UserData user, Options options);

    protected abstract void onCommand(UserData userData, Msg msg, CommandLine cmd);

    private CommandLineParser parser = new DefaultParser();

    public void process(final UserData user, Msg msg) {

        if (!msg.isCommand() || !cmdLineSyntax().equals(msg.commandName())) return;

        if (msg.commandParms().length == 0) {
            
            msg.send(help(user));
            return;
            
        }
        
        Options options = new Options() {{ applyOptions(user, this); }};

        try {

            CommandLine cmd = parser.parse(options, msg.commandParms());

            onCommand(user, msg, cmd);

        } catch (ParseException e) {

            msg.send(help(user));

        }

    }

    public String help(final UserData user) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        HelpFormatter formatter = new HelpFormatter();

        PrintWriter printer = new PrintWriter(out);

        Options options = new Options() {{ applyOptions(user, this); }};

        formatter.printHelp(printer, "/" + cmdLineSyntax(), options);
        
        return out.toString();

    }


}
