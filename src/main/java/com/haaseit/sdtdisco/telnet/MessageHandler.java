package com.haaseit.sdtdisco.telnet;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.RequestBuffer;

public class MessageHandler {
    private IDiscordClient discordClient;
    private TelnetHandler telnetHandler;
    private IChannel channel = null;
    private IChannel adminchannel = null;

    private AdminChannelBuffer adminChannelBuffer = null;
    private Thread adminChannelBufferThread = null;

    private SdtdMessageParser messageParser = null;


    public MessageHandler(IDiscordClient discordClient, TelnetHandler telnetHandler) {
        this.discordClient = discordClient;
        this.telnetHandler = telnetHandler;
    }

    public void setChannel(String channel) {
        this.channel = discordClient.getChannelByID(Long.parseLong(channel));
        this.messageParser = new SdtdMessageParser(this.channel, telnetHandler);
    }

    public void setAdminChannel(String channel) {
        this.adminchannel = discordClient.getChannelByID(Long.parseLong(channel));
        adminChannelBuffer = new AdminChannelBuffer(this.adminchannel);
        adminChannelBufferThread = adminChannelBuffer.startBuffer();
        adminChannelBufferThread.run();
    }

    public void handleMessageFromTelnet(String line) {
        if (line == null) {
            return;
        }
        line = line.trim();
        if (line.equals("")) {
            return;
        }

        if (adminchannel != null) {
            adminChannelBuffer.writeToBuffer(line);
            // todo: wenn servernachricht mit timestamp am anfang eintrifft, buffer flushen
            if (line.substring(4, 5).equals("-") && line.substring(7, 8).equals("-") && line.substring(10, 11).equals("T")) {
                adminChannelBuffer.flush();
            }
            // 2017-08-06T09:07:29 9688.240 INF Executing command 'listplayers' by Telnet from 10.0.7.123:56674
        }
        if (channel != null) {
            messageParser.parseTelnetMessageForChannel(line);
        }
    }

    public void handleMessageFromChannel(String author, String line) {
        messageParser.parseDiscordMessageFromChannel(author, line);
    }

    public void handleMessageFromAdminChannel(String line) {
        telnetHandler.write(line);
    }
}
