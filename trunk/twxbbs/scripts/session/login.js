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
function twMenu(prompt) {
    game.send("T*");
}

function todaysLog(prompt) {
    game.send("n");
}

function passwd(prompt) {
    game.send("bob*");
}

game.setTextTrigger("pause", "[Pause]", gamePause);
game.setTextTrigger("login","ENTER", login);
game.pause();
game.setTextTrigger("menu", "menu):", menu);
game.pause();
game.setTextTrigger("twmenu", "Enter your choice:", twMenu);
game.pause();
game.setTextTrigger("todaysLog", "Show today's log?", todaysLog);
game.pause();
game.setTextTrigger("passwd", "Password?", passwd);
game.pause();
game.setTextTrigger("Command", "Command [", function(txt){});
game.pause();