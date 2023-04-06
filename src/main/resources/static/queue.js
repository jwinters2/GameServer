var stompClient = null;
var userid = null;

function challenge(uid) {
    //stompClient.send(`/to-server/challenge/${uid}`, {}, JSON.stringify("foo"));
    const request = new XMLHttpRequest();  
    request.open('GET', `/to-server/challenge/${uid}`);
    request.onload = function() {
        console.log(request.response);
    };
    request.send();
}

function connect(uid) {
    userid = uid;
    console.log(`connecting to ${uid}`);
    var socket = new SockJS("/server");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected", frame);
        stompClient.subscribe(`/websocket/queue/${uid}`, function(message) {
            updateQueue(JSON.parse(message.body));
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

function updateQueue(body) {
    const users = body.userList;
    
    // clear table
    const tbody = document.getElementById("userlist");
    tbody.innerHTML = "";
    const tr = document.createElement("tr");
    tbody.appendChild(tr);
    
    users.forEach(u => {
        let usernameCell = document.createElement("td");
        usernameCell.classList.add('user-list-entry');
        usernameCell.classList.add('align-middle');
        usernameCell.innerHTML = u.username;

        let playCell = document.createElement("td");
        playCell.classList.add('user-list-entry');
        
        let button = document.createElement("a");
        button.classList.add("btn");
        button.classList.add("btn-primary");
        button.classList.add("border-0");
        button.onclick = function() {challenge(u.uid);};
        button.innerHTML = "Play";
        
        playCell.appendChild(button);
        
        tr.appendChild(usernameCell);
        tr.appendChild(playCell);

    });
    
    if(users.length === 0) {
        /*
         * <td class="user-list-entry align-middle"><i>No users waiting to play</i></td>
                        <td class="user-list-entry">
                        </td>
         */
        let italics = document.createElement("i");
        italics.innerHTML = "No users waiting to play";
        
        let usernameCell = document.createElement("td");
        usernameCell.classList.add('user-list-entry');
        usernameCell.classList.add('align-middle');
        usernameCell.appendChild(italics);
        
        tr.appendChild(usernameCell);
        
        let playCell = document.createElement("td");
        playCell.classList.add('user-list-entry');
        
        tr.appendChild(playCell);
    }
}

updateQueue({"userList": []});