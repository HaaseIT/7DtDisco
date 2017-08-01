package com.haaseit.sdtdisco.discord;


import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class EventListener {
    private String channel;

    public EventListener(String channel) {
        this.channel = channel;
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        System.out.println("Ready.");
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        System.out.println(event.getMessage() + " " + event.getChannel().getStringID() + " " + event.getGuild());

        // event.getGuild() == null -> direct message

        if (event.getChannel().getStringID().equals(channel)) {
            System.out.println("hooray");
        }
    }
}
