package com.haaseit.sdtdisco.telnet;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.RequestBuffer;

class SdtdMessageParser {
    private IChannel channel;
    private TelnetHandler telnetHandler;

    SdtdMessageParser(IChannel channel, TelnetHandler telnetHandler) {
        this.channel = channel;
        this.telnetHandler = telnetHandler;
    }

    void parseDiscordMessageFromChannel(String author, String line) {
        if (line.substring(0, 1).equals("/")) {
            if (line.equals("/version")) {
                telnetHandler.write("version");
            } else if (line.equals("/time")) {
                telnetHandler.write("gettime");
            } else if (line.equals("/players")) {
                telnetHandler.write("listplayers");
            } else if (line.equals("/info")) {
                final String message = "**Info:** This bot relays chat messages to and from a 7 Days To Die server as well as running commands to get information from the server.\n"
                        + "**Source code, instructions and the compiled executable:** https://github.com/HaaseIT/7DtDisco\n"
                        + "**Available commands:** /time, /version, /players, /info";
                RequestBuffer.request(() -> {
                    channel.sendMessage(message);
                });
            }
        } else {
            String message = author + ": " + line;
            telnetHandler.write("say \"" + message.replace("\"", "'").replace("&", "") + "\"");
        }
    }

    void parseTelnetMessageForChannel(String line) {
        // first if checks, if this is a iso time
        if (
                line.length() > 20
                        && line.substring(4, 5).equals("-")
                        && line.substring(7, 8).equals("-")
                        && line.substring(10, 11).equals("T")
                        && line.substring(13, 14).equals(":")
                        && line.substring(16, 17).equals(":")
                        && line.substring(19, 20).equals(" ")
                ) {
            // the number after the first space is the server uptime in seconds, this varies in length, so we have to
            // check where it ends.
            int endoftimestamp = line.indexOf((int) ' ', 20);

            if (endoftimestamp > 21) {
                // any relevant message will have at least 17 chars after the end of timestamp
                if (line.length() >= endoftimestamp + 17) {
                    boolean messagepresent = false;

                    // we filter out chat messages that originated by ourselves (Chat: 'Server':)
                    if (
                            line.substring(endoftimestamp + 5, endoftimestamp + 11).equals("Chat: ")
                                    && (
                                            line.length() > endoftimestamp + 20
                                                && !line.substring(endoftimestamp + 11, endoftimestamp + 20).equals("'Server':")
                            )
                            ) {
                        // 2017-08-10T16:52:19 4356.184 INF Chat: 'Lahme Wade': Yeah!
                        messagepresent = true;
                    } else if (
                            line.substring(endoftimestamp + 5, endoftimestamp + 17).equals("GMSG: Player")
                                    && (
                                    line.substring(line.length() - 4).equals("died")
                                            || line.substring(line.length() - 15).equals("joined the game")
                                            || line.substring(line.length() - 13).equals("left the game")
                            )
                            ) {
                        /* Sample messages:
                        2017-08-06T12:17:01 21060.664 INF GMSG: Player 'Halp' died
                        2017-08-06T12:15:24 20963.531 INF GMSG: Player 'Halp' joined the game
                        2017-08-10T16:42:12 3748.634 INF GMSG: Player 'Ja ne, is klar!' left the game
                        */
                        messagepresent = true;
                    }

                    if (messagepresent) {
                        final String message = line.substring(endoftimestamp + 11);
                        RequestBuffer.request(() -> {
                            channel.sendMessage(message);
                        });
                    }
                }
            }
        } else if (
                line.substring(0, 3).equals("Day")
                        || line.substring(0, 13).equals("Game version:")
                        || line.substring(0, 9).equals("Total of ")
                ) {
            /* Sample messages:
            Day 128, 11:24
            Game version: Alpha 16.2 (b7) Compatibility Version: Alpha 16.2
            */
            if (line.substring(0, 3).equals("Day")) {
                String[] timepieces = line.split(" ");
                String day = timepieces[1];
                day = day.replace(",", "");
                int dayint = Integer.parseInt(day);
                int daystohorde = ((dayint / 7) + 1) * 7 - dayint;
                if (daystohorde == 7) {
                    daystohorde = 0;
                }

                boolean daysmessageshownalready = false;
                if (daystohorde == 0 || daystohorde == 6) {
                    String[] daytimepieces = timepieces[2].split(":");
                    int hour = Integer.parseInt(daytimepieces[0]);
//                    int minute = Integer.parseInt(daytimepieces[1]);

                    if (daystohorde == 0 && hour < 22) {
                        line = line + "\nThe horde comes tonight!";
                        daysmessageshownalready = true;
                    } else if ((daystohorde == 0 && hour >= 22) || (daystohorde == 6 && hour < 4)) {
                        daysmessageshownalready = true;
                        line = line + "\nThe horde is rampaging now!";
                    }

                }
                if (!daysmessageshownalready) {
                    line = line + "\n" + daystohorde + " days to next horde.";
                }
            }

            final String message = line;
            RequestBuffer.request(() -> {
                channel.sendMessage(message);
            });
        }
    }
}
