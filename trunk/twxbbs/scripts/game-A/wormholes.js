function sectorStart(txt) {
    game.setTextTrigger("warps", "Warps to Sector");
    pause();
    game.killTextTrigger("warps");
    game.setCapturingTextTrigger("insertText", ":", insertWarp);
    pause();
}

function insertWarp(txt) {

    game.killTextTrigger("insertText");

    player.send(txt+" #[bold;blinkSlow;fgRed;bgWhite]<<Wormhole>>#[normal]");
    pause();
    
}

game.setTextLineTrigger("sectorStart", "#[bold;fgGreen]Sector  #[fgYellow]: #[fgCyan]666", sectorStart);
pause();