function Api(lexer) {

    var triggerCallbacks = {};

    this.send = function(txt) {
        lexer.send(txt);
    };
    this.getCurrentLine = function() {
        lexer.getCurrentLine();
    };
    this.pause = function() {
        var id = lexer.pause();
        if (id != null) {
            triggerCallbacks[id](lexer.getMatchedLine());
        }
    }
    this.killTextTrigger = function(id) {
        lexer.killTextTrigger(id);
    }
    this.setTextTrigger = function(id, text, callback) {
        lexer.setTextTrigger(id, text);
        triggerCallbacks[id] = callback;
    }
    this.setTextLineTrigger = function(id, text, callback) {
        lexer.setTextLineTrigger(id, text);
        triggerCallbacks[id] = callback;
    }
    this.setCapturingTextTrigger = function(id, text, callback) {
        lexer.setCapturingTextTrigger(id, text);
        triggerCallbacks[id] = callback;
    }
    this.setCapturingTextLineTrigger = function(id, text, callback) {
        lexer.setCapturingTextLineTrigger(id, text);
        triggerCallbacks[id] = callback;
    }
}

var game = new Api(gameApi);
var player = new Api(playerApi);