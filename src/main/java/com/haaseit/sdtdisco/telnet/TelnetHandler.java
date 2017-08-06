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

    public String send(String command) throws Exception {
        try {
            write(command);
            readUntil("\n"); // read past echo
            String result = readUntil("\n");
            // drop trailing '\n'
            return result.substring(0, result.length() - 1);
        } catch (IOException e) {
            throw new Exception(e);
        }
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
//                    return str.substring(0, str.length() - pattern.length());
                }
            }
        }

        return null;
    }

    public Thread startReader() {
        return new Thread() {
            @Override
            public void run()
            {
                String line;

                try
                {
                    while (true)
                    {
                        line = readUntil("\r\n");
                        messageHandler.handleMessageFromTelnet(line);
                        System.out.print(line);
                    }
                }
                catch (IOException e)
                {
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