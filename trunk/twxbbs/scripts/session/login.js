var attackPtn = / (.*)'s (.*) \(([0-9]+)-([0-9]+)\).*/;

function login(prompt) {
    game.send("don*");
}
function menu(prompt) {
    game.send("a");
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

println("Login done");
