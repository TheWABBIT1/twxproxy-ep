# Introduction #

At the core of TWX BBS is a scripting engine that allows you to script both game and player text streams to give you full control over the game.  The primary scripting language is the popular JavaScript language, and the general design should be familiar to TWX Proxy players.

In TWX Proxy, you pass labels to the triggers, which instructs TWX Proxy to send the script to the code beneath the passed label when it is triggered.  In TWX BBS, you pass the callback function, which will be called with the matched text when the trigger is fired.  For example, here is a simple text line trigger that calls a function that prints out the matched line:
```
function sector(txt) {
    println("text: "+txt);
}
game.setTextLineTrigger("sector", "Sector  :", sector);
game.pause();
```

# Reference #

Each text stream, the one coming from the game TradeWars and the one coming from the player, get its own API in the variables "game" and "player", respectively.  Therefore, if you want to match text coming from the game, set a text trigger using the "game" variable, or if you wanted to capture input from the player, set a capturing text trigger using the "player" variable.

## Stream functions ##
|setTextTrigger(id, text, callback)| Sets a text trigger that calls the "callback" function of a match, passing the matched line |
|:---------------------------------|:--------------------------------------------------------------------------------------------|
|setTextLineTrigger(id, text, callback)|Sets a text trigger that doesn't fire until the full line containing the trigger text is matched.  The callback function is then executed and the matched line is its only parameter.|
|setCapturingTextTrigger(id, text, callback)| Sets a text trigger that captures the text matching the trigger.  Pass an empty string, "", as the trigger text if you want to match any character.|
|setCapturingTextLineTrigger(id, text, callback)|Sets a text line trigger that captures the text line matching the trigger.  Passing an empty string, "", also works to capture the whole next line.|
|killTrigger(id)|Kills a text trigger.  Unnecessary for capturing text triggers, which are automatically killed after one firing|
|send(text)| Sends text downstream.  The star character is translated into an "\r\n".|
|pause()|Pauses the script, allowing the triggers to fire.|

Capturing text triggers differ from normal text triggers in several ways:
  * They only fire once, then are automatically removed
  * They remove their matching text from the stream, so if you want to send it along, call "send(text)".
  * When the trigger is set, any text in the back buffer is cleared to ensure you don't match text that happened before the trigger was set.  Since these triggers are generally used for collecting user input, you generally only want text typed after the trigger is set.

## Context ##

There are two general purpose context maps that can be used to store data per session or per application, with the former only available for session scripts:
  * **application** - a context shared between application and session scripts
  * **session** - a context shared for all scripts in a given player's session

These context objects are maps, so the following methods are available:
|put(key, value) | Puts a value into a map, indexed by a string key|
|:---------------|:------------------------------------------------|
|get(key)| Gets a value from a map|
|clear()| Clears all values in the map|

## Examples ##

There are several example session scripts that come with TWX BBS.  You can browse them at:
> http://code.google.com/p/twxproxy-ep/source/browse/#svn/trunk/twxbbs/scripts/session