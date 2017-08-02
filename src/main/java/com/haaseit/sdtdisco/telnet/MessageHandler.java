package com.haaseit.sdtdisco.telnet;

import sx.blah.discord.api.IDiscordClient;

public class MessageHandler {
    private IDiscordClient discordClient;
    private TelnetHandler telnetHandler;

    public MessageHandler(IDiscordClient discordClient, TelnetHandler telnetHandler) {
        this.discordClient = discordClient;
        this.telnetHandler = telnetHandler;
    }

    public void handleMessageFromTelnet(String line) {

    }

    public void handleMessageFromChannel(String line) {
        telnetHandler.write("say \"" + line + "\"");
    }

    public void handleMessageFromAdminChannel(String line) {

    }
}
