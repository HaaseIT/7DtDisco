package com.haaseit.sdtdisco.telnet;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.RequestBuffer;

public class MessageHandler {
    private IDiscordClient discordClient;
    private TelnetHandler telnetHandler;
    private IChannel channel = null;
    private IChannel adminchannel = null;

    public MessageHandler(IDiscordClient discordClient, TelnetHandler telnetHandler) {
        this.discordClient = discordClient;
        this.telnetHandler = telnetHandler;
    }

    public void setChannel(String channel) {
        this.channel = discordClient.getChannelByID(Long.parseLong(channel));
    }

    public void setAdminChannel(String channel) {
        this.adminchannel = discordClient.getChannelByID(Long.parseLong(channel));
    }

    public void handleMessageFromTelnet(String line) {
        line = line.trim();
        if (line.equals("")) {
            return;
        }

        if (adminchannel != null) {
            final String message = line;
            RequestBuffer.request(() -> {
                adminchannel.sendMessage(message);
            });
        }
        if (channel != null) {
            // 2017-08-03T18:17:16 803568.139 INF Chat:
            if (line.length() > 50 && line.substring(35, 41).equals("Chat: ") && !line.substring(41, 50).equals("'Server':")) {
                final String message = line.substring(41);
                RequestBuffer.request(() -> {
                    channel.sendMessage(message);
                });
            }
        }
        // todo: send wanted messages (eg. chat messages) to normal channel
    }

    public void handleMessageFromChannel(String line) {
        telnetHandler.write("say \"" + line.replace("\"", "'").replace("&", "") + "\"");
    }

    public void handleMessageFromAdminChannel(String line) {
        telnetHandler.write(line);
    }
}
