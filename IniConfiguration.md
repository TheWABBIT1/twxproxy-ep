# Introduction #

TWXProxy can optionally run without a GUI if you pass the "/nogui" flag on the command line, however, in such an embedded mode, there is still a need to configure TWXProxy and optionally create game databases.  TWX Proxy supports an INI file format that can be passed to TWX Proxy for configuration.
```
This feature is available in 2.0.5 or later
```
# Details #

The INI file follows the established INI specification.  Each section maps internally to different areas of TWX Proxy.  The following are the sections and the configuration lines they support.

## TWXServer ##

| **Name** | **Description** | **Format** | **Default** |
|:---------|:----------------|:-----------|:------------|
| Port | The port TWX Proxy will listen on for connections | Any valid TCP port | 23 |
| AcceptExternal | Whether TWX Proxy will allow connections from external IP addresses | 1 for true, 0 for false | 0 |

## TWXClient ##
| **Name** | **Description** | **Format** | **Default** |
|:---------|:----------------|:-----------|:------------|
| Reconnect | Whether to automatically reconnect after being disconnected or not |  1 for true, 0 for false | 1 |

## TWXDatabase ##
This section supports the creation of a database if no existing database file can be found.

| **Name** | **Description** | **Format** | **Default** |
|:---------|:----------------|:-----------|:------------|
| Description | The game description, used for the database name |  Any valid file name | A |
| Address | The URL for the TWGS server |  Any valid Internet host | localhost |
| Port | The port for the TWGS server |  Any valid TCP port | 2003 |
| Sectors | The number of sectors in the game | 100 - 20000 | 5000 |
| UseLogin | Whether to use an autologin script or not |  1 for true, 0 for false | 0 |

The following values are only read if the 'UseLogin' value is set to true:
| **Name** | **Description** | **Format** | **Default** |
|:---------|:----------------|:-----------|:------------|
| LoginName | The login name of the player |  Any valid TradeWars player name | TestTrader |
| Password | The password of the player |  Any valid password | TestTrader |
| Game | The TradeWars game character |  A-Z | A |
| LoginScript | The script to use for the automatic login |  The file path to the script | scripts\1\_Login.ts |