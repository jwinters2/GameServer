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
        flex.classList.add("d-flex");
        row.appendChild(flex);
        
        let span = document.createElement("span");
        span.classList.add("my-1", "p-2", "border", "border-dark", "text-wrap", "text-break");
        span.innerHTML = message.message;
        flex.appendChild(span);
        
        if(message.uid === userid) {
            row.classList.add("justify-content-end");
            flex.classList.add("flex-row-reverse");
            span.classList.add("text-end", "chat-me");
        }
        else
        {
            row.classList.add("justify-content-start");
            flex.classList.add("flex-row");
            span.classList.add("text-start", "chat-other");
        }
    });
    
    chatLog.scrollTop = chatLog.scrollHeight;
}