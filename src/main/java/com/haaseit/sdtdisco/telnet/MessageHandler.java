package com.haaseit.sdtdisco.telnet;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

public class MessageHandler {
    private IDiscordClient discordClient;
    private TelnetHandler telnetHandler;
    private IChannel channel = null;
    private IChannel adminchannel = null;

    private AdminChannelBuffer adminChannelBuffer = null;

    private SdtdMessageParser messageParser = null;


    public MessageHandler(IDiscordClient discordClient, TelnetHandler telnetHandler) {
        this.discordClient = discordClient;
        this.telnetHandler = telnetHandler;
    }

    public void setChannel(String channel) {
        this.channel = discordClient.getChannelByID(Long.parseLong(channel));
        this.messageParser = new SdtdMessageParser(this.channel, telnetHandler);
        discordClient.changePlayingText("Type /info for help.");
    }

    public void setAdminChannel(String channel) {
        this.adminchannel = discordClient.getChannelByID(Long.parseLong(channel));
        adminChannelBuffer = new AdminChannelBuffer(this.adminchannel);

        // the admin channel can send loads of text, so we have to buffer its output. we could of course send it line by
        // line, but the discord buffer would then send it to the channel very slowly (around 1 line per 2 seconds or
        // so), so we collect some text and hand it over to the discord buffer
        Thread adminChannelBufferThread;
        adminChannelBufferThread = adminChannelBuffer.startBuffer();
        adminChannelBufferThread.run();
    }

    void handleMessageFromTelnet(String line) {
        if (line == null) {
            return;
        }
        line = line.trim();
        if (line.equals("")) {
            return;
        }

        if (adminchannel != null) {
            adminChannelBuffer.writeToBuffer(line);
            // if we get a message with a timestamp at the beginning, we flush the buffer
            if (
                    line.substring(4, 5).equals("-")
                            && line.substring(7, 8).equals("-")
                            && line.substring(10, 11).equals("T")
                    ) {
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
