# Introduction #

TWX BBS is a server-side proxy for TW 2002, specifically, TWGS.  It allows a game sysop to customize their games in ways that is not possible with TEdit and Gold configurations alone.  A sysop now has the ability to completely control what text the player sends to TWGS and what gets returned, using a JavaScript-based scripting environment with commands similar to TWX Proxy.

# Getting Started #

To run TWX BBS, you will need:
  * Java 6, available at http://java.com

Once you've downloaded the TWX BBS release, unzip it into a directory, of your choice.  We will use C:\games\twxbbs in the examples.  The layout of the directory should be:

```
twxbbs-VERSION.jar
README.txt
scripts\
           session\
                       \login.js
                       \stop-attacks.js
           application\
```

The scripts in the "session" directory will executed for every connection, while the ones in the "application" directory are executed when the server starts.  There will be a few example scripts in the "session" directory, which you will want to remove when you prepare to start using TWX BBS for your games.

To start TWX BBS, navigate to the installation directory and execute:
```
java -jar twxbbs-VERSION.jar
```
or if using Windows, you can double-click on the `twxbbs-VERSION.jar`.  Replace the "VERSION" token with the version you installed.

When running TWX BBS for the first time, open your web browser and visit the configuration page:
> http://localhost:8080
You should see a page like this:

![http://twxproxy-ep.googlecode.com/svn/trunk/twxbbs/spec/twxbbs-admin.png](http://twxproxy-ep.googlecode.com/svn/trunk/twxbbs/spec/twxbbs-admin.png)

Change the TWGS server name and port accordingly, and possibly change what port the proxy will listen on.  Be sure the web port is unaccessible to the world to prevent anyone from reconfiguring TWX BBS.

You are now running TWX BBS!

See the [TwxBbsScriptingGuide](TwxBbsScriptingGuide.md) for information about how to install and write your own scripts.