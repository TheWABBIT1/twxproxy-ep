var triggerCallbacks = {};
function Api(lexer) {
    var replaceAnsiTokens = function(txt) {
        var finalTxt = [];
        var token = null;
        var hashHit = false;
        for (var x = 0; x < txt.length; x++) {
            var c = txt.charAt(x);
            if (token == null) {
                if (c == '#') {
                    hashHit = true;
                    finalTxt.push("\u001b")
                } else {
                    if (hashHit && c == '[') {
                        token = [];
                    }
                    finalTxt.push(c);
                    hashHit = false;
                }
            } else {
                if (c == ';' || c == ']') {
                    var param = token.join("");
                    if (isNaN(parseInt(param))) {
                        param = ansiCode[param];
                    }
                    finalTxt.push(param);
                    finalTxt.push((c == ']' ? 'm' : c));
                    if (c == ';') {
                        token = [];
                    } else {
                        token = null;
                    }
                } else {
                    token.push(c);
                }
            }
        }
        //println("turned "+txt+" into "+finalTxt.join(""));
        return finalTxt.join("");
    };
    this.send = function(txt) {
        lexer.send(replaceAnsiTokens(txt));
    };
    this.getCurrentLine = function() {
        lexer.getCurrentLine();
    };
    this.killTextTrigger = function(id) {
        lexer.killTextTrigger(id);
    }
    this.setTextTrigger = function(id, text, callback) {
        lexer.setTextTrigger(id, replaceAnsiTokens(text));
        triggerCallbacks[id] = callback;
    }
    this.setTextLineTrigger = function(id, text, callback) {
        lexer.setTextLineTrigger(id, replaceAnsiTokens(text));
        triggerCallbacks[id] = callback;
    }
    this.setCapturingTextTrigger = function(id, text, callback) {
        lexer.setCapturingTextTrigger(id, replaceAnsiTokens(text));
        triggerCallbacks[id] = callback;
    }
    this.setCapturingTextLineTrigger = function(id, text, callback) {
        lexer.setCapturingTextLineTrigger(id, replaceAnsiTokens(text));
        triggerCallbacks[id] = callback;
    }

}

var game = new Api(gameApi);
var player = new Api(playerApi);

function pause() {
    var id = gameApi.pause();
    if (id != null) {
        if (triggerCallbacks[id])
            triggerCallbacks[id](stripAnsi(gameApi.getMatchedLine()));
    }
}

function stripAnsi(txt) {
    return new String(gameApi.stripAnsi(txt));
}

var ansiCode = new function() {
    this.normal = "0";
    this.bold = "1";
    this.faint = "2";
    this.italic = "3";
    this.underlineSingle = "4";
    this.blinkSlow = "5";
    this.blinkRapid = "6";
    this.inverse = "7";
    this.strike = "9";
    this.underlineDouble = "21";
    this.boldOff = "22";
    this.italicsOff = "23";
    this.underlineOff = "24";
    this.inverseOff = "27";
    this.strikeOff = "29";
    this.fgBlack = "30";
    this.fgRed = "31";
    this.fgGreen = "32";
    this.fgYellow = "33";
    this.fgBlue = "34";
    this.fgPurple = "35";
    this.fgCyan = "36";
    this.fgWhite = "37";
    this.fgDefault = "39";
    this.bgBlack = "40";
    this.bgRed = "41";
    this.bgGreen = "42";
    this.bgYellow = "43";
    this.bgBlue = "44";
    this.bgPurple = "45";
    this.bgCyan = "46";
    this.bgWhite = "47";
    this.bgDefault = "49";
};