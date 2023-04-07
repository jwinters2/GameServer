var userid = null;
var matchid = null;
var game = null;
var stompClient = null;

function chat_init(_uid, _game, _matchid) {
    
    userid = _uid;
    game = _game;
    matchid = _matchid;
    
    console.log(`connecting to ${userid} for ${game} match ${matchid}`);
    var socket = new SockJS("/server");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected", frame);
        stompClient.subscribe(`/websocket/chat/${game}/${matchid}/${userid}`, function(message) {
            chat_handleUpdate(JSON.parse(message.body));
        });
        heartbeat();
    });
}

// heartbeat stuff
function heartbeat() {
    if(stompClient !== null) {
        stompClient.send(`/to-server/heart/${userid}`, {}, "beat");
    }
}

setInterval(heartbeat, 10000);


function chat_sendMessage() {
    
    let message = document.getElementById("messageText").value;
    document.getElementById("messageText").value = "";
    
    const request = new XMLHttpRequest();  
    request.open('POST', `/game/${game}/${matchid}/chat`);
    request.arguments
    request.onload = function() {
        console.log(request.response);
    };
    request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    request.send(`message=${encodeURIComponent(message)}`);
}

function chat_handleUpdate(messages) {
    
    let chatLog = document.getElementById("chatLog");
    chatLog.innerHTML = "";
    
    messages.sort((a, b) => {
        return a.timestamp - b.timestamp;
    });
    
    messages.forEach(message => {
        
        let row = document.createElement("div");
        row.classList.add("row", "gy-1");
        chatLog.appendChild(row);
        
        let flex = document.createElement("div");
        flex.classList.add("d-sm-inline-flex", "p-2", "border", "border-dark",
            "text-width", "text-break");
        row.appendChild(flex);
        
        let p = document.createElement("p");
        p.classList.add("text-break");
        p.innerHTML = message.message;
        flex.appendChild(p);
        
        if(message.uid === userid) {
            row.classList.add("justify-content-end");
            flex.classList.add("text-right", "chat-me");
        }
        else
        {
            row.classList.add("justify-content-start");
            flex.classList.add("text-left", "chat-other");
        }
    });
    
    chatLog.scrollTop = chatLog.scrollHeight;
}