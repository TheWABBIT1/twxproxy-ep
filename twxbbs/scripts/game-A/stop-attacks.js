var attackPtn = / (.*)'s (.*) \(([0-9]+)-([0-9]+)\).*/;

function maybeAttack(prompt) {
    game.setTextTrigger("maybeAttack", "[N]?", attack);
    pause();
    game.killTextTrigger("maybeAttack");
}
function attack(prompt) {
    match = prompt.match(attackPtn);
    if (match != null) {
        println("Try to attack "+match[1]+" with "+match[4]+" using "+match[3]+" fighters");
        player.send("*#[bold;fgRed]Let's just be friends#[normal]*");
        game.setCapturingTextLineTrigger("no", "No", function(txt){});
        game.send("n");

        // Gets the game database and updates the attackAttempts stats
        var db = session.get("db");
        var stats = db.getTable("stats");
        var attackAttemptsList = stats.get("attackAttempts");
        attackAttemptsList = (attackAttemptsList == null ? new Array() : attackAttemptsList);
        attackAttemptsList.push({
            "targetTrader" : match[1],
            "targetShip" : match[2],
            "targetFigs" : match[4],
            "attackerFigs" : match[3]
        });
        stats.put("attackAttempts", attackAttemptsList);

        // debugging
        println("Attack attempts:");
        for (x in attackAttemptsList) {
            println("\tAttacked: "+attackAttemptsList[x].targetTrader);
        }

        pause();
    } else {
        println("Can't match :"+prompt+":");
    }

    pause();
}

game.setTextTrigger("attack", "Attack", maybeAttack);
pause();