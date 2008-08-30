function login(prompt) {
    game.send("don*");
}
function menu(prompt) {
    game.send("a");
}
function attack(prompt) {
    player.send("*\u001b[30mDon't be a dick*");
    game.setCapturingTextLineTrigger("no", "No", function(txt){println("txt:"+txt);});
    game.send("n");
    game.pause();
}
function gamePause(prompt) {
    game.send(" ");
    game.pause();
}

game.setTextTrigger("pause", "[Pause]", gamePause);
game.setTextTrigger("login","ENTER", login);
game.pause();
game.setTextTrigger("menu", "menu):", menu);
game.pause();
game.setTextTrigger("attack", "<Attack>", attack);
game.killTextTrigger("menu");
game.killTextTrigger("login");
game.pause();

println("Script done");
