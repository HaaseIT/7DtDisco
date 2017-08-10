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
            //System.out.println(Helper.getCurrentLocalDateTimeFormatted() + " Channel: " + event.getAuthor().getName() + "> " + event.getMessage().getContent());
            // todo: regular channel should send everything except whitelisted commands (eg. /time) to server by "Say".
            messageHandler.handleMessageFromChannel(event.getAuthor().getName(), event.getMessage().getContent());
        } else if (adminchannel != null && event.getChannel().getStringID().equals(adminchannel)) {
            //System.out.println(Helper.getCurrentLocalDateTimeFormatted() + " Admin: " + event.getAuthor().getName() + "> " + event.getMessage().getContent());
            messageHandler.handleMessageFromAdminChannel(event.getMessage().getContent());
        } else if (event.getChannel().isPrivate()) {
            System.out.println(Helper.getCurrentLocalDateTimeFormatted() + " Direct: " + event.getAuthor().getName() + "> " + event.getMessage().getContent());
            // todo: direct messages should only listen to whitelisted commands
        }

    }
}
