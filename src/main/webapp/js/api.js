(function(global) {"use strict";

    function getRootUri() {
        return "ws://" + (document.location.hostname === "" ? "localhost" : document.location.hostname) + ":" +
            (document.location.port === "" ? "8080" : document.location.port);
    }

    var wsConfigBet = getRootUri() + "/bets";
    var wsConfigGames = getRootUri() + "/games";

    var ws1 = new WebSocket(wsConfigBet);

    ws1.onopen = function() {
        console.log("SOCKET:open");
    }

    ws1.onmessage = function(evt) {
        var data = evt.data;
        var splits = data.split("#");
        var type = splits[0];
        var dataBody = splits[1];

        if (type == "bet_success") {
            alert("BET PUT SUCCESS!");
        }
    }

    var ws2 = new WebSocket(wsConfigGames);

    return {
        makeBet: function() {
            debugger;
            var form = document.getElementById("makeBetForm");
            var ammountField = form.querySelector("input");

            var msg_body = "bet_put" + "#" + "alex" + ":" + "1" + ":" + ammountField.value;

            ws1.send(msg_body);
        }
    }
})(window)