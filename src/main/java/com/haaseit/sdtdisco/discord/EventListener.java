package com.haaseit.sdtdisco.discord;

import com.haaseit.sdtdisco.Helper;
import com.haaseit.sdtdisco.telnet.MessageHandler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class EventListener {
    private String channel;
    private String adminchannel;
    private MessageHandler messageHandler;

    public EventListener(String channel, String adminchannel, MessageHandler messageHandler) {
        this.channel = channel;
        this.adminchannel = adminchannel;
        this.messageHandler = messageHandler;
    }

    // almost everything within discord can only take place once the ready event is triggered
    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        messageHandler.setChannel(channel);
        System.out.println("Discord connection ready.");
        if (adminchannel != null) {
            messageHandler.setAdminChannel(adminchannel);
        }
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        if (event.getChannel().getStringID().equals(channel)) {
            // regular channel will send everything except whitelisted commands (eg. /time) to server by "Say".
            messageHandler.handleMessageFromChannel(event.getAuthor().getName(), event.getMessage().getContent());

        } else if (adminchannel != null && event.getChannel().getStringID().equals(adminchannel)) {
            // admin channel will send everything directly to telnet console
            messageHandler.handleMessageFromAdminChannel(event.getMessage().getContent());

        } else if (event.getChannel().isPrivate()) {
            // handling of direct messages is not implemented, will only be output to console
            System.out.println(Helper.getCurrentLocalDateTimeFormatted() + " Direct: " + event.getAuthor().getName() + "> " + event.getMessage().getContent());
        }
    }
}
