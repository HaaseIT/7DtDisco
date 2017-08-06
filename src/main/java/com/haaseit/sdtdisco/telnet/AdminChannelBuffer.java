package com.haaseit.sdtdisco.telnet;

import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.handle.obj.IChannel;

import java.util.Date;


public class AdminChannelBuffer {
    private String buffer = "";
    private boolean isRunning = false;
    private IChannel adminChannel = null;
    private long lastWrite = 0;

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
                        buffer = getBuffer();
                        if (getLastWrite() != 0 && !buffer.isEmpty()) {
                            long now = new Date().getTime();
                            if (now - getLastWrite() > 1) {
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

    public String getBuffer() {
        return buffer;
    }

    public long getLastWrite() {
        return lastWrite;
    }

    public void writeToBuffer(String line) {
        buffer += line + "\n";
        lastWrite = new Date().getTime();
        if (buffer.length() >= 1000) {
            flush();
        }
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
