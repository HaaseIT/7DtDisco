package com.haaseit.sdtdisco.telnet;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class TelnetHandler {
    private InputStream in;
    private PrintStream out;
    private TelnetClient tc;
    private MessageHandler messageHandler;

    public TelnetHandler(InputStream in, PrintStream out, TelnetClient tc) {
        this.in = in;
        this.out = out;
        this.tc = tc;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Closes the connection.
     */
    public void close() throws Exception {
        try {
            tc.disconnect();
        } catch (IOException ioe) {
            throw new Exception(ioe);
        }
    }

    /**
     * Reads input stream until the given pattern is reached. The
     * pattern is discarded and what was read up until the pattern is
     * returned.
     */
    public String readUntil(String pattern) throws IOException {
        char lastChar = pattern.charAt(pattern.length() - 1);
        StringBuilder sb = new StringBuilder();
        int c;

        while((c = in.read()) != -1) {
            char ch = (char) c;
            char skip = 0x0000;
            if (ch == skip) {
                continue;
            }
//            System.out.print(ch);
            sb.append(ch);
            if(ch == lastChar) {
                String str = sb.toString();
                if(str.endsWith(pattern)) {
                    return str;
                }
            }
        }

        return null;
    }

    public Thread startReader(String sdtdhost, String sdtdport) {
        return new Thread() {
            @Override
            public void run()
            {
                String line;

                try
                {
                    while (true) {
                        line = readUntil("\r\n");
                        messageHandler.handleMessageFromTelnet(line);
                        System.out.print(line);
                        if (!tc.isConnected()) {
                            sleep(5000);
                            tc.connect(sdtdhost, Integer.parseInt(sdtdport));
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Writes the value to the output stream.
     */
    public void write(String value) {
        out.println(value);
        out.flush();
//        System.out.println(value);
    }
}
// '\u0000' 0