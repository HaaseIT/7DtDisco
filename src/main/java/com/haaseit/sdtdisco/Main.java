package com.haaseit.sdtdisco;

import com.haaseit.sdtdisco.discord.EventListener;
import com.haaseit.sdtdisco.telnet.MessageHandler;
import com.haaseit.sdtdisco.telnet.TelnetHandler;
import org.apache.commons.cli.*;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("token", true, "The discord auth token for the bot.");
        options.addOption("channel", true, "The discord channel to listen to.");
        options.addOption("adminchannel", true, "The discord admin channel to listen to.");
        options.addOption("7dtdhost", true, "The 7dtd Server host/ip.");
        options.addOption("7dtdport", true, "The 7dtd Server telnet port.");
        options.addOption("7dtdpwd", true, "The 7dtd Server telnet password");

        CommandLineParser parser = new DefaultParser();

        String token = null;
        String channel = null;
        String adminchannel = null;
        String sdtdhost = null;
        String sdtdport = null;
        String sdtdpwd = null;
        String[] requiredoptions = {
                "token",
                "channel",
                "7dtdhost",
                "7dtdport",
                "7dtdpwd",
        };
        try {
            // get cli arguments
            CommandLine line = parser.parse(options, args);
            for (int i = 0; i < requiredoptions.length; i++) {
                if (!line.hasOption(requiredoptions[i])) {
                    throw new ParseException("Argument missing:" + requiredoptions[i]);
                }
            }
            token = line.getOptionValue("token");
            channel = line.getOptionValue("channel");
            sdtdhost = line.getOptionValue("7dtdhost");
            sdtdport = line.getOptionValue("7dtdport");
            sdtdpwd = line.getOptionValue("7dtdpwd");

            if (line.hasOption("adminchannel")) {
                adminchannel = line.getOptionValue("adminchannel");
            }
        } catch (ParseException e) {
            System.err.println("Parsing CLI arguments failed.  Reason: " + e.getMessage());
            System.exit(1);
        }

        try {
            IDiscordClient discordClient = createClient(token, true);
            EventDispatcher discordDispatcher = discordClient.getDispatcher();

            TelnetHandler th = new TelnetHandler(sdtdhost, sdtdport, sdtdpwd);

            MessageHandler messageHandler = new MessageHandler(discordClient, th);

            th.setMessageHandler(messageHandler);

            discordDispatcher.registerListener(new EventListener(channel, adminchannel, messageHandler));

            Thread reader = th.startReader();
            reader.run();

            th.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
        }

    }

    private static IDiscordClient createClient(String token, boolean login) {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);

        try {
            if (login) {
                return clientBuilder.login();
            } else {
                return clientBuilder.build();
            }
        } catch (DiscordException e) {
            e.printStackTrace();
            return null;
        }
    }
}
