package com.haaseit.sdtdisco.discord;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class InterfaceListener implements IListener<ReadyEvent> {
    @Override
    public void handle(ReadyEvent event) {
        System.out.println("Ready");
    }
}

