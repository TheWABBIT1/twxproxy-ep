function init() {
    var jclient =document.getElementById("jclient");
    jclient.addHipsMessageListener("tw", "handleTW");
    //alert("Initialized: "+jclient.getHipsListenerCount());
}

function handleTW(name, json) {
    var msg = eval('('+json+')');
    switch(name) {
        case "commandPrompt" : $("#sector-status").html(msg.sector);
    }
    //alert("message "+name+" message:"+msg);
}
$(document).ready(init);