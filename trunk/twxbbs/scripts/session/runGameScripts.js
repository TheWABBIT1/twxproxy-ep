var lastGameThreads;
function gameSelectionPrompt(prompt) {
    player.setCapturingTextTrigger("gameSelection", "", gameSelection);
    player.pause();
}

function gameSelection(input) {
    if (lastGameThreads != null) {
        sessionScriptRunner.stopScripts(lastGameThreads);
    }
    if (input.match(/[A-Pa-p]/) != null) {
        lastGameThreads = sessionScriptRunner.runAllInDirectory("game-"+input.toUpperCase());
    }
    game.send(input);
    game.pause();
}

game.setTextTrigger("gameSelectionPrompt", "Selection (? for menu):", gameSelectionPrompt);
game.pause();