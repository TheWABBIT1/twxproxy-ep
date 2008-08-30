var attackPtn = / (.*)'s (.*) \(([0-9]+)-([0-9]+)\).*/;

function maybeAttack(prompt) {
    game.setTextTrigger("maybeAttack", "[N]?", attack);
    game.pause();
    game.killTextTrigger("maybeAttack");
}
function attack(prompt) {
    match = prompt.match(attackPtn);
    if (match != null) {
        println("Try to attack "+match[1]+" with "+match[4]+" using "+match[3]+" fighters");
        player.send("*\u001b[30mDon't be a dick*");
        game.setCapturingTextLineTrigger("no", "No", function(txt){println("txt:"+txt);});
        game.send("n");
        game.pause();
    } else {
        println("Can't match :"+prompt+":");
    }

    game.pause();
}

game.setTextTrigger("attack", "Attack", maybeAttack);
game.pause();

println("Attack script done");
