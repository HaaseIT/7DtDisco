package com.haaseit.sdtdisco;

import com.haaseit.sdtdisco.discord.BaseBot;
import com.haaseit.sdtdisco.discord.InterfaceListener;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;


public class Main {
    public static boolean exit;
    public static BaseBot discordBot;

    public static void main(String[] args) {
        exit = false;

        IDiscordClient discordClient = createClient(args[0], true);
        EventDispatcher discordDispatcher = discordClient.getDispatcher();
        discordDispatcher.registerListener(new InterfaceListener());



        discordBot = new BaseBot(discordClient);


    }

    public static IDiscordClient createClient(String token, boolean login) {
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
