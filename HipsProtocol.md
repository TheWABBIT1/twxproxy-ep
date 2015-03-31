# Introduction #

In order to retrieve meaningful data from a telnet application in an automated way, the client need to parse the server's textual output made for humans.  Unfortunately, this approach, known as screen scraping, is highly brittle and time consuming for the client.  The "Hidden-in-Plain-Sight" or HIPS protocol seeks to define a way for a telnet server application to "hide" structured data within its human-readable textual output, and likewise, a telnet client application can use HIPS to send structured instructions or requests.

# Details #

HIPS works by using the [ASCII escape codes](http://en.wikipedia.org/wiki/ANSI_color) 8 and 28, which turn on and off text concealing, respectively.  Within these boundries, a specific text-based format for message encoding is used to store the structured data.  The message format contains a message name and structured body, using the [JSON](http://json.org) format for brevity.

The HIPS format is as follows:
```
Esc[8mMESSAGE_NAME=JSON_BODYEsc[28m
```
Where:
  * "Esc" is the ASCII escape code
  * MESSAGE\_NAME can be any alphanumeric character and any of the following special characters: '-','_', or '.'.  The message name can optionally be name spaced using the NAMESPACE.MESSAGE\_NAME convention.
  * JSON\_BODY is any valid JSON data structure with optional substructures_

For example, say you wanted to encode a login message.  This is what it would look like:
```
Esc[8mlogin={"name":"John Doe"}Esc[28m
```
Which creates a message object in a hash map format with a single entry, "name", set to "John Doe".

HIPS messages should be not be displayed in the user's terminal, because HIPS interprets the conceal and reveal ANSI codes to mean nothing is displayed to the user.  Some terminals may show a blank space for every character, which will probably break the visual display.

```
If the interpretation of the conceal and reveal codes is not feasible, a later version of this protocol may adopt a new set of ASCII escape codes out of the ones currently unused.
```

## Telnet negotation ##

Not all terminals support the conceal commands, so we need a way for the server to know if it should use them to embed the structured data.  The [telnet options negotiation](http://www.iana.org/assignments/telnet-options) is a proven way for the client and server to determine if certain features are supported, so HIPS negotiation will use the unassigned option code '76'.

The proper negotiation process will go:
  1. Server sends: IAC WILL HIPS
  1. Client responds: IAC [DO|DONT] HIPS

If the server gets no response, it is assumed HIPS is not supported.

# Known Namespaces #
These are namespaces that are known to use this protocol:
## tw ##
The 'tw' namespace is for common messages in the game TradeWars 2002.  Messages will contain data structures representing the information that is being displayed on the screen.  The protocol doesn't take into account whether the client has already encountered the information or not.

The following are TradeWars screens and example messages.

### Prompts ###
#### Command Prompt ####
```
tw.commandPrompt={
    "sector" : 33
}
```
#### Computer Prompt ####
```
tw.computerPrompt={
    "sector" : 33
}
```
#### Sector Display ####
Each line in the sector display gets its own message that represents their data, with the exception
of ships, traders, and planets, which cover multiple lines.
##### Constellation #####
```
tw.sector={
    "id"            : 2
    "constellation" : "Fedspace"
}
```
##### Beacon #####
```
tw.sector={
    "id"            : 2
    "beacon"        : "Stay away"
}
```
##### Port #####
```
tw.port={
    "id"            : 2
    "dead"          : false,
    "class"         : 2,
    "buildTime"     : 0,
    "name"          : "Some Port Name"
}
```
##### Port Build Time #####
```
tw.port={
    "id"            : 2
    "buildTime"     : 3
}
```
##### Trader #####
```
tw.trader={
    "sector"        : 2
    "name"          : "Bob Trader",
    "shipType"      : "SomeIndustry Merchant Ship",
    "figs"          : 500
}
```
##### Ship #####
```
tw.ship={
    "sector"        : 2
    "name"          : "The Fast Ship",
    "owner"         : "Bob Trader",
    "shipType"      : "SomeIndustry Merchant Ship",
    "figs"          : 500
}
```
##### Planet #####
```
tw.planet={
    "sector"        : 2
    "name"          : "My Planet"
}
```
##### NavHaz #####
```
tw.sector={
    "id"            : 2
    "navHaz"        : 23
}
```
##### Limpet Mines #####
```
tw.limpetMines={
    "sector"        : 2
    "quantity"      : 4,
    "owner"         : "Bob Trader
}
```
##### Armid Mines #####
```
tw.armidMines={
    "sector"        : 2
    "quantity"      : 4,
    "owner"         : "Bob Trader
}
```
##### Fighters #####
```
tw.fighters={
    "sector"        : 2
    "quantity"      : 4,
    "owner"         : "Bob Trader,
    "figType"       : "toll"
}
```
##### Warps #####
```
tw.sector={
    "id"            : 2
    "warps"         : [1,5,243]
}
```
#### CIM ####
These messages are fired per line in a CIM report
##### Warps #####
```
tw.sector={
    "id"            : 2
    "warps"         : [1,5,243]
}
```
##### Port #####
```
tw.port={
    "id"             : 2
    "productAmounts" : {
        "fuelOre"       : 2302,
        "organics"      : 201,
        "equipment"     : 1044
        },
    "productPercents": {
        "fuelOre"       : 100,
        "organics"      : 13,
        "equipment"     : 60
        },
    "class"         : 2
}
```
#### Port Docking ####
These messages fire when docking at a port
##### Commerce Report #####
```
tw.port={
    "id"             : 2,
    "name"           : "My Port",
    "productAmounts" : {
        "fuelOre"       : 2302,
        "organics"      : 201,
        "equipment"     : 1044
        },
    "productPercents": {
        "fuelOre"       : 100,
        "organics"      : 13,
        "equipment"     : 60
        },
    "class"         : 2
}
```
#### Miscellaneous ####
Everything else...
##### Warp Calculation #####
```
tw.warp={
    "from"          : 2
    "to"            : 4
}
```
##### No Deployed Fighters #####
```
tw.noDeployedFighters={}
```
##### StarDock in Game Stats #####
```
tw.sector={
    "id"            : 23
    "beacon"        : "FedSpace, FedLaw Enforced",
    "constellation" : "The Federation"
}
```
```
tw.port={
    "id"            : 23
    "dead"          : false,
    "class"         : 9,
    "buildTime"     : 0,
    "name"          : "Stargate Alpha I"
}
```
##### Density Scan #####
```
tw.sector={
    "id"            : 23
    "density"       : 600,
    "anomoly"       : false,
    "numWarps"      : 3
}
```
# Implementations #

  * TWXProxy 2.0.5+
  * TWGS (wishful thinking)