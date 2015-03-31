# Introduction #

In order to make TradeWars more approachable, the game should be made accessible via the web in a more controlled manner where everyone has access to the same scripts and user interface.  The TWX BBS project is for a slimmed down TradeWars helper that uses TWX Proxy on the server-side for data collection and script execution.  The main interface for TWX BBS will be a series of web pages.

# Details #

The TWX BBS will consist of the following logical modules:
  * Web interface
  * TWX Proxy
  * TWX Proxy dispatcher
  * Game client

## Web Interface ##

The web interface will be the public face of the TWX BBS.  It will consist minimally of the following screens:
  1. Game list
  1. Game detail
  1. Login/Signup
  1. Game playing screen

The data source for these screens will come from TWGS itself, via scripting of TEdit.

## TWX Proxy ##

This is the TWX Proxy application with scripts.

## TWX Proxy Dispatcher ##

Manages the TWX Proxy instances and dispatches client connects to the correct TWX Proxy.

## Game Client ##

The game client is a web page consisting of HTML and CSS for the layout, JavaScript for buttons and UI operations, and a minimal applet for the telnet client.  HIPS will be the message bus to handle communication between TWX Proxy on the server and the JavaScript in the client.  Hips will be used to send data from TWX Proxy to the JavaScript and the JavaScript will use HIPS to send commands to TWX Proxy like script execution or data retrieval.

This are very early mockups of a possible game client layout:

![http://twxproxy-ep.googlecode.com/svn/trunk/twxbbs/spec/tw-cockpit-mockup.png](http://twxproxy-ep.googlecode.com/svn/trunk/twxbbs/spec/tw-cockpit-mockup.png) ![http://twxproxy-ep.googlecode.com/svn/trunk/twxbbs/spec/twx-bbs-play.png](http://twxproxy-ep.googlecode.com/svn/trunk/twxbbs/spec/twx-bbs-play.png)

Notice the bulk of the interface is not the telnet applet, but rather helper status readouts, contextual and global command buttons, visual sector display, and the chat pane.  HTML, CSS, and JavaScript are used to make the client very skinnable and approachable by non-Java developers.

## Interaction diagram ##
![http://twxproxy-ep.googlecode.com/svn/trunk/twxbbs/spec/interactions.png](http://twxproxy-ep.googlecode.com/svn/trunk/twxbbs/spec/interactions.png)

Generated via http://www.websequencediagrams.com using the following code

```
Browser->TWXBBS: Visit BBS web site
TWXBBS->Browser: List of available games
Browser->TWXBBS: Game A selected
TWXBBS->Browser: Displays game details
Browser->TWXBBS: Clicks play
TWXBBS->Browser: Present login form
Browser->TWXBBS: Submit user name and password
note right of TWXBBS: TWXBBS verifies info via TEdit, generates session token
TWXBBS->Browser: Web page containing Applet configured with session token
Browser->TWXBBS: Telnet with first text being "SESSION_TOKEN\\r\\n"
note right of TWXBBS: Session token verified against local registry
note right of TWXBBS: TWXBBS instantiates TWX Proxy instance for game A
TWXBBS->TWXProxy: Sends connect command
TWXProxy->TWGS: Connects to the server and runs the login script
TWXBBS->Browser: Game presented to the user already started
note right of Browser: Player plays game
Browser->TWXBBS: Player quits game exiting to main TWGS menu
note right of TWXBBS: TWXBBS detects the exit and shuts down
TWXBBS->TWXProxy: Sends quit command
note right of TWXBBS: Session token thrown away
TWXBBS->Browser: Player returned to game list
```