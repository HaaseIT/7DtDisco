# sdtdisco

A bot written in Java that connects a *7 Days to Die* dedicated server to Discord.

It will connect to a Discord channel and optionally to a second channel, the admin channel.

Text typed into the first channel will be transmitted into game chat, text typed into the adminchannel will be transmitted into the telnet console, so make sure, the adminchannel is only allowed for admins!

![Chat example](https://www.oldworld.de/_img/7dtdbotchat.png "Chat example")

## Required command line arguments
* token - The Discord bot token 
* channel - The channel ID of the playerchannel
* 7dtdhost - The IP or hostname of the 7dtd telnet server
* 7dtdport - The port of the 7dtd telnet server
* 7dtdpwd - The password of the 7dtd telnet server

## Optional command line arguments
* adminchannel

## Example commandline
* java -jar 7DtDisco.jar -token 1234567 -channel 2345678 -adminchannel 3456789 -7dtdhost 127.0.0.1 -7dtdport 8081 -7dtdpws mypassword


## Discord Setup

### Creating the bot account
1. Log in to the [Discord developers section](https://discordapp.com/developers/applications/me) in a browser and click "New App".
2. Name the bot anything you'd like and click "Create App".
3. Click "Create a Bot User".
4. Next to "Token:" click the link that says "click to reveal". This is the password for your bot's account. You'll need this and the "Client ID" number later. Copy them both somewhere safe or keep the tab open for later.
5. Paste the URL listed below into your browser and replace "CLIENT_ID" with your client ID number. Hit enter and select the desired Discord server. Once this is done, the bot will show up in your server!

`https://discordapp.com/oauth2/authorize?client_id=CLIENT_ID&scope=bot`

### Configuring the bot
1. On your server's system, navigate to the game's directory and edit your the config xml file.
2. Find "TelnetEnabled" and make sure it is set to "true". Set a telnet password and save the file.
3. Now navigate back to the bot's folder. Edit config.json.
4. Find the line containing `"password": "changeme",` and replace "changeme" with your server's telnet password.
5. If running the bot on a different network from the server, change "localhost" to your server's external IP. (If using an external IP to connect the bot, forwarding the telnet port may be required)
6. Replace "your_token_here" with the Discord bot token from earlier.  Keep the file open for the next section.

### Setting up the channel
1. Open Discord in a browser.
2. Open your user settings (gear in the bottom left) and go to "Appearance".
3. Scroll down to "Advanced" and turn Developer Mode on.
4. Now go to your Discord server. Create a text channel or choose an existing one for your server's chat.
5. Right-click the text channel you want to use and click "Copy ID". This will copy the channel's number ID to your clipboard.
6. Go back to your server's config.json file and enter the ID in place of "channelid".
7. Save and close the file.
