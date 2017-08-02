package com.haaseit.sdtdisco.discord;


import com.haaseit.sdtdisco.Helper;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class EventListener {
    private String channel;
    private String adminchannel;

    public EventListener(String channel, String adminchannel) {
        this.channel = channel;
        this.adminchannel = adminchannel;
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        System.out.println("Ready.");
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        if (event.getChannel().getStringID().equals(channel)) {
            System.out.println(Helper.getCurrentLocalDateTimeFormatted() + " Channel: " + event.getAuthor().getName() + "> " + event.getMessage());
            // todo: regular channel should send everything except whitelisted commands (eg. /time) to server by "Say".
        } else if (adminchannel != null && event.getChannel().getStringID().equals(adminchannel)) {
            System.out.println(Helper.getCurrentLocalDateTimeFormatted() + " Admin: " + event.getAuthor().getName() + "> " + event.getMessage());
            // todo: admin channel posts directly to server console, so you can execute commands
        } else if (event.getChannel().isPrivate()) {
            System.out.println(Helper.getCurrentLocalDateTimeFormatted() + " Direct: " + event.getAuthor().getName() + "> " + event.getMessage());
            // todo: direct messages should only listen to whitelisted commands
        }

    }
}
