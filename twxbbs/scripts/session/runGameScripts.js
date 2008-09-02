var lastGameThreads;
function SessionDatabase(db) {
    this.close = function() {
        db.close();
    }

    this.getTable = function(name) {
        return new SessionDatabaseTable(db.getMap(name));
    }
}

// Simple wrapper around the database table map to automatically handle json encoding/decoding
function SessionDatabaseTable(map) {
        this.get = function(key) {
            var ret = map.get(key);
            if (ret != null) {
                if (ret.length() > 0) {
                    var firstChar = ret.substring(0, 1);
                    if (firstChar == '[' || firstChar == '{') {
                        ret = JSON.parse(new String(ret));
                    }
                }
            }
            return ret;
        };
        this.put = function(key, value) {
            if (typeof(value) == 'object' || typeof(value) == 'array') {
                value = JSON.stringify(value);
            } 
            map.put(key, value);
        }
        this.remove = function(key) {
            map.remove(new String(key));
        }
        this.clear = function() {
            map.clear();
        }
    }

function gameSelectionPrompt(prompt) {

    // Cleanup any old database in the session
    var oldDb = session.get("db");
    if (oldDb != null) {
        oldDb.close();
    }

    // Stop any old game scripts left running
    if (lastGameThreads != null) {
        sessionScriptRunner.stopScripts(lastGameThreads);
    }
    player.setCapturingTextTrigger("gameSelection", "", gameSelection);
    pause();
}

function gameSelection(input) {

    if (input.match(/[A-Pa-p]/) != null) {
        lastGameThreads = sessionScriptRunner.runAllInDirectory("game-"+input.toUpperCase());
    }
    session.put("game", input);

    // Create a game db and put it in the session
    session.put("db", new SessionDatabase(dbManager.createDatabase("game-"+input)));
    game.send(input);
    pause();
}

game.setTextTrigger("gameSelectionPrompt", "Selection (? for menu):", gameSelectionPrompt);
pause();