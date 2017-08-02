package com.haaseit.sdtdisco;

import com.haaseit.sdtdisco.discord.EventListener;
import org.apache.commons.cli.*;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;


public class Main {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("token", true, "The discord auth token for the bot.");
//        options.addOption("guild", true, "The discord guild to listen to.");
        options.addOption("channel", true, "The discord channel to listen to.");
        options.addOption("adminchannel", true, "The discord admin channel to listen to.");

        CommandLineParser parser = new DefaultParser();

        String token = null;
        String channel = null;
        String adminchannel = null;
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("token")) {
                token = line.getOptionValue("token");
            } else {
                throw new ParseException("The token is missing.");
            }
            if (line.hasOption("channel")) {
                channel = line.getOptionValue("channel");
            } else {
                throw new ParseException("The channel is missing.");
            }
            if (line.hasOption("channel")) {
                adminchannel = line.getOptionValue("adminchannel");
            }
        } catch (ParseException e) {
            System.err.println( "Parsing CLI arguments failed.  Reason: " + e.getMessage() );
            System.exit(1);
        }



        IDiscordClient discordClient = createClient(token, true);
        EventDispatcher discordDispatcher = discordClient.getDispatcher();
        discordDispatcher.registerListener(new EventListener(channel, adminchannel));


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
