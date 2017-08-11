package com.haaseit.sdtdisco.telnet;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.RequestBuffer;

public class SdtdMessageParser {
    private IChannel channel;
    private TelnetHandler telnetHandler;

    public SdtdMessageParser(IChannel channel, TelnetHandler telnetHandler) {
        this.channel = channel;
        this.telnetHandler = telnetHandler;
    }

    public void parseDiscordMessageFromChannel(String author, String line) {
//        String foo = line.substring(0, 1);
        if (line.substring(0, 1).equals("/")) {
            if (line.equals("/version")) {
                telnetHandler.write("version");
            } else if (line.equals("/time")) {
                telnetHandler.write("gettime");
            } else if (line.equals("/help")) {
                final String message = "Available commands: /time /version /help";
                RequestBuffer.request(() -> {
                    channel.sendMessage(message);
                });
            }
        } else {
            String message = author + ": " + line;
            telnetHandler.write("say \"" + message.replace("\"", "'").replace("&", "") + "\"");
        }
    }

    public void parseTelnetMessageForChannel(String line) {
        if (
                line.length() > 20
                        && line.substring(4,5).equals("-")
                        && line.substring(7,8).equals("-")
                        && line.substring(10,11).equals("T")
                        && line.substring(13,14).equals(":")
                        && line.substring(16,17).equals(":")
                        && line.substring(19,20).equals(" ")
                ) {
            int endoftimestamp = line.indexOf((int) ' ', 20);

            if (endoftimestamp > 21) {
                if (line.length() >= endoftimestamp + 17) {
                    if (
                            line.substring(endoftimestamp + 5, endoftimestamp + 11).equals("Chat: ")
                                    && !line.substring(endoftimestamp + 11, endoftimestamp + 20).equals("'Server':")
                            ) {
                        // 2017-08-10T16:52:19 4356.184 INF Chat: 'Lahme Wade': Yeah!
                        final String message = line.substring(39);
                        RequestBuffer.request(() -> {
                            channel.sendMessage(message);
                        });
                    } else if (
                            line.substring(endoftimestamp + 5, endoftimestamp + 17).equals("GMSG: Player")
                                && (
                                    line.substring(line.length() - 4).equals("died")
                                            || line.substring(line.length() - 15).equals("joined the game")
                                            || line.substring(line.length() - 13).equals("left the game")
                                    )
                            ) {
                        /* Death
                        2017-08-06T12:17:01 21060.664 INF GMSG: Player 'Halp' died
                        */
                        /* Logon
                        017-08-06T12:15:02 20941.150 INF [NET] PlayerConnected EntityID=-1, PlayerID='', OwnerID='', PlayerName=''
                        2017-08-06T12:15:02 20941.307 INF PlayerLogin: Halp/Alpha 16.2
                        2017-08-06T12:15:02 20941.307 INF Client IP: 10.0.7.123
                        2017-08-06T12:15:02 20941.307 INF [Steamworks.NET] Auth.AuthenticateUser()
                        2017-08-06T12:15:02 20941.308 INF [Steamworks.NET] Authenticating player: Halp SteamId: 76561197976155858 TicketLen: 1024 Result: k_EBeginAuthSessionResultOK
                        2017-08-06T12:15:02 20941.657 INF [Steamworks.NET] Authentication callback. ID: 76561197976155858, owner: 76561197976155858, result: k_EAuthSessionResponseOK
                        2017-08-06T12:15:02 20941.657 INF Steam authentication successful, allowing user: EntityID=-1, PlayerID='76561197976155858', OwnerID='76561197976155858', PlayerName='Halp'
                        2017-08-06T12:15:02 20941.657 INF Allowing player with id 76561197976155858
                        2017-08-06T12:15:03 20942.457 INF RequestToEnterGame: 76561197976155858/Halp
                        2017-08-06T12:15:11 20950.007 INF RequestToSpawnPlayer: 171, Halp, 8
                        2017-08-06T12:15:11 20950.016 INF Created player with id=171
                        2017-08-06T12:15:24 20963.531 INF GMSG: Player 'Halp' joined the game
                        2017-08-06T12:15:24 20963.531 INF PlayerSpawnedInWorld (reason: JoinMultiplayer, position: -3121, 91, 1078): EntityID=171, PlayerID='76561197976155858', OwnerID='76561197976155858', PlayerName='Halp'
                        */
                        // 2017-08-10T16:42:12 3748.634 INF GMSG: Player 'Ja ne, is klar!' left the game
                        final String message = line.substring(endoftimestamp + 11);
                        RequestBuffer.request(() -> {
                            channel.sendMessage(message);
                        });
                    }
                }
            }
        } else if (line.substring(0, 3).equals("Day") || line.substring(0, 13).equals("Game version:")) {
            /* Result from gettime
            2017-08-06T12:18:54 21173.339 INF Executing command 'gettime' by Telnet from 10.0.7.123:32776
            Day 128, 11:24
            */
            // Game version: Alpha 16.2 (b7) Compatibility Version: Alpha 16.2
            final String message = line;
            RequestBuffer.request(() -> {
                channel.sendMessage(message);
            });
        }
    }

/*
2017-08-10T16:49:31 4187.359 INF Executing command 'listplayers' by Telnet from 10.0.7.123:39330
0. id=8806, BohnenTon, pos=(-1359.6, 50.1, -412.8), rot=(-18.3, 506.3, 0.0), remote=True, health=52, deaths=151, zombies=201, players=6, score=1, level=50, steamid=76561198511061403, ip=79.226.4.214, ping=39
1. id=338, LahmeWade, pos=(-1269.1, 44.1, -348.5), rot=(-18.0, -3022.3, 0.0), remote=True, health=72, deaths=210, zombies=795, players=11, score=42, level=100, steamid=76561198254602367, ip=79.226.4.214, ping=33
2. id=40040, Ja ne, is klar!, pos=(2031.3, 117.0, 482.2), rot=(0.0, 293.9, 0.0), remote=True, health=70, deaths=5, zombies=0, players=0, score=0, level=1, steamid=76561197976476063, ip=10.0.7.123, ping=1
Total of 3 in the game
*/

/*
*** Generic Console Help ***
To get further help on a specific topic or command type (without the brackets)
    help <topic / command>

Generic notation of command parameters:
   <param name>              Required parameter
   <entityId / player name>  Possible types of parameter values
   [param name]              Optional parameter

*** List of Help Topics ***
None yet

*** List of Commands ***
 admin => Manage user permission levels
 aiddebug => Toggles AIDirector debug output.
 audio => Watch audio stats
 ban => Manage ban entries
 BiomeParticles => Debug
 buff => Applies a buff to the local player
 buffplayer => Apply a buff to a player
 chunkcache cc => shows all loaded chunks in cache
 commandpermission cp => Manage command permission levels
 creativemenu cm => enables/disables the creativemenu
 DeathScreen => Display DeathScreen
 debuff => Removes a buff from the local player
 debuffplayer => Remove a buff from a player
 debugmenu dm => enables/disables the debugmenu
 debugweather => Dumps internal weather state to the console.
 dof => Control DOF
 enablescope es => toggle debug scope
 exhausted => Makes the player exhausted.
 exportitemicons => Exports all ItemIcons
 gamestage => usage: gamestage - displays the gamestage of the local player.
 getgamepref gg => Gets game preferences
 getgamestat ggs => Gets game stats
 gettime gt => Get the current game time
 givequest => usage: givequest questname
 giveself => usage: giveself itemName qualityLevel (default quality is 600)
 giveselfskillxp => usage: giveselfskillxp Mining Tools 10000
 giveselfxp => usage: giveselfxp 10000
 help => Help on console and specific commands
 kick => Kicks user with optional reason. "kick playername reason"
 kickall => Kicks all users with optional reason. "kickall reason"
 kill => Kill a given entity
 killall => Kill all players
 lgo listgameobjects => List all active game objects
 lights => Debug views to optimize lights
 listents le => lists all entities
 listplayerids lpi => Lists all players with their IDs for ingame commands
 listplayers lp => lists all players
 listthreads lt => lists all threads
 loggamestate lgs => Log the current state of the game
 loglevel => Telnet/Web only: Select which types of log messages are shown
 mem => Prints memory information and calls garbage collector
 memcl => Prints memory information on client and calls garbage collector
 pplist => Lists all PersistentPlayer data
 prefab =>
 profilenetwork => Writes network profiling information
 profiling => Enable Unity profiling for 5 seconds
 removequest => usage: removequest questname
 repairchunkdensity rcd => check and optionally fix densities of a chunk
 saveworld sa => Saves the world manually.
 say => Sends a message to all connected clients
 setgamepref sg => sets a game pref
 setgamestat sgs => sets a game stat
 settargetfps => Set the target FPS the game should run at (upper limit)
 settempunit stu => Set the current temperature units.
 settime st => Set the current game time
 showalbedo albedo => enables/disables display of albedo in gBuffer
 showchunkdata sc => shows some date of the current chunk
 showClouds => Artist command to show one layer of clouds.
 showhits => Show hit entity locations
 shownexthordetime => Displays the wandering horde time
 shownormals norms => enables/disables display of normal maps in gBuffer
 showspecular spec => enables/disables display of specular values in gBuffer
 shutdown => shuts down the game
 sounddebug => Toggles SoundManager debug output.
 spawnairdrop => Spawns an air drop
 spawnentity se => spawns an entity
 spawnentityat sea => Spawns an entity at a give position
 spawnscouts => Spawns zombie scouts
 SpawnScreen => Display SpawnScreen
 spawnsupplycrate => Spawns a supply crate where the player is
 spawnwanderinghorde => Spawns a wandering horde of zombies
 spectrum => Force a particular lighting spectrum.
 stab => stability
 starve => Makes the player starve.
 staticmap => Switches Navazgane map between static and dynamic
 switchview sv => Switch between fpv and tpv
 SystemInfo => List SystemInfo
 teleport tp => Teleport the local player
 teleportplayer tele => Teleport a given player
 thirsty => Makes the player thirsty.
 traderarea => ...
 updatelighton => Commands for UpdateLightOnAllMaterials and UpdateLightOnPlayers
 version => Get the currently running version of the game and loaded mods
 visitmap => Visit an given area of the map. Optionally run the density check on each visited chunk.
 water => Control water settings
 weather => Control weather settings
 weathersurvival => Enables/disables weather survival
 whitelist => Manage whitelist entries
 zip => Control zipline settings
 */
}
