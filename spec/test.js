game.setTextTrigger("login","ENTER");
var id = game.pause();

game.send("don\r\n");
game.setTextTrigger("menu", "menu):");
game.pause();
game.send("a");
game.setTextTrigger("attack", "<Attack>");
game.killTextTrigger("menu");
game.killTextTrigger("login");
game.pause();
println("last line:"+game.currentLine);

game.send("n");
player.send("\r\n\u001b[30mDon't be a dick\r\n");
println("matchline:"+game.matchedLine);

println("hello world and bob");
