var attackPtn = / (.*)'s (.*) \(([0-9]+)-([0-9]+)\).*/;

function maybeAttack(prompt) {
    game.setTextTrigger("maybeAttack", "[N]?", attack);
    game.pause();
    game.killTextTrigger("maybeAttack");
}
function attack(prompt) {
    match = prompt.match(attackPtn);
    if (match != null) {
        println("Try to attack "+match[1]+" with "+match[4]+" using "+match[3]+" fighters in game "+currentGame);
        player.send("*\u001b[31m\u001b[1mLet's just be friends\u001b[22m*");
        game.setCapturingTextLineTrigger("no", "No", function(txt){});
        game.send("n");
        game.pause();
    } else {
        println("Can't match :"+prompt+":");
    }

    game.pause();
}

function gameSelection(prompt) {
    player.setCapturingTextTrigger("sel", "", function(txt) {currentGame=txt; game.send(txt); game.pause()});
    player.pause();
}

var currentGame;
game.setTextTrigger("gameSelection", "(? for menu):", gameSelection);
game.setTextTrigger("attack", "Attack", maybeAttack);
game.pause();

println("Attack script done");
