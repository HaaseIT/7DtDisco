package com.haaseit.sdtdisco.telnet;

import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.handle.obj.IChannel;

import java.util.Date;


public class AdminChannelBuffer {
    private volatile String buffer = "";
    private volatile boolean isRunning = false;
    private IChannel adminChannel = null;
    private volatile long lastWrite = 0;

    public AdminChannelBuffer(IChannel adminChannel) {
        this.adminChannel = adminChannel;
    }

    public Thread startBuffer() {
        isRunning = true;

        return new Thread() {
            @Override
            public void run()
            {
                try
                {
                    while (isRunning)
                    {
                        // todo: zeit seit letztem writeToBuffer messen, wenn länger als 1 sekunde und buffer != null flush()
                        if (buffer.length() >= 1000) {
                            flush();
                        }
                        if (lastWrite != 0 && !buffer.isEmpty()) {
                            long now = new Date().getTime();
                            if (now - lastWrite > 1) {
                                flush();
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
    }

    public void stop() {
        isRunning = false;
    }

    public void writeToBuffer(String line) {
        buffer += line + "\n";
        lastWrite = new Date().getTime();
    }

    public void flush() {
        final String message = buffer;
        RequestBuffer.request(() -> {
            adminChannel.sendMessage(message);
        });
        buffer = "";
        lastWrite = 0;
    }
}
