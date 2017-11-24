package com.haaseit.sdtdisco.telnet;

import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.handle.obj.IChannel;

import java.util.Date;


public class AdminChannelBuffer implements Runnable {
    private volatile String buffer = "";
    private boolean isRunning = false;
    private IChannel adminChannel = null;
    private volatile long lastWrite = 0;
    private Thread t;
    private String threadName;

    AdminChannelBuffer(IChannel adminChannel, String threadName) {
        this.adminChannel = adminChannel;
        this.threadName = threadName;
    }

    public void run() {
        try {
            isRunning = true;
            while (isRunning) {
                // check time since last writeToBuffer, if longer than 1 second and the buffer != null, flush()
                if (buffer.length() >= 1000) {
                    flush();
                }
                if (lastWrite != 0 && !buffer.isEmpty()) {
                    long now = new Date().getTime();
                    if (now - lastWrite > 1) {
                        flush();
                    }
                }
                Thread.sleep(250);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    void writeToBuffer(String line) {
        buffer += line + "\n";
        lastWrite = new Date().getTime();
    }

    void flush() {
        final String message = buffer;
        if (message != null && !message.isEmpty()) {
            RequestBuffer.request(() -> {
                adminChannel.sendMessage(message);
            });
        }
        buffer = "";
        lastWrite = 0;
    }
}
