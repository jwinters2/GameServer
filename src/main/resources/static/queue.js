var stompClient = null;
var userid = null;

function challenge(uid) {
    const request = new XMLHttpRequest();  
    request.open('GET', `/queue/challenge/${uid}`);
    request.onload = function() {
        console.log(request.response);
    };
    request.send();
}

function accept(inviteId) {
    const request = new XMLHttpRequest();  
    request.open('GET', `/queue/accept/${inviteId}`);
    request.onload = function() {
        console.log(request.response);
    };
    request.send();
}

function cancel(inviteId) {
    const request = new XMLHttpRequest();  
    request.open('GET', `/queue/cancel/${inviteId}`);
    request.onload = function() {
        console.log(request.response);
    };
    request.send();
}

function decline(inviteId) {
    const request = new XMLHttpRequest();  
    request.open('GET', `/queue/decline/${inviteId}`);
    request.onload = function() {
        console.log(request.response);
    };
    request.send();
}

function init(uid) {
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
    console.log(body);
    const users = body.userList;
    
    // clear table
    const tbody = document.getElementById("userlist");
    tbody.innerHTML = "";
    const tr = document.createElement("tr");
    tbody.appendChild(tr);
    
    // update user list
    
    users.forEach(u => {
        let usernameCell = document.createElement("td");
        usernameCell.classList.add('user-list-entry');
        usernameCell.classList.add('align-middle');
        usernameCell.innerHTML = u.username;
        
        tr.appendChild(usernameCell);

        let playCell = document.createElement("td");
        playCell.classList.add('user-list-entry');
        
        let button = generateUserButton(userid, u.uid, body.invites);
        button.classList.add("float-right");
        
        playCell.appendChild(button);

        tr.appendChild(playCell);

    });
    
    if(users.length === 0) {
 
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

function generateUserButton(userUid, listUid, invites) {
    
    let outgoingInvite = false;
    let incomingInvite = false;
    let timestamp = -1;
    
    Object.values(invites).forEach((invite) => {
        if(invite.fromUid === userUid) {
            outgoingInvite = true;
            timestamp = invite.timestamp;
        }
        if(invite.toUid === userUid) {
            incomingInvite = true;
            timestamp = invite.timestamp;
        }
    });
    
    let button = document.createElement("a");
    button.classList.add("btn");
    button.classList.add("btn-primary");
    button.classList.add("border-0");
        
    if(outgoingInvite) {
       
        button.onclick = function() {cancel(timestamp);};
        button.innerHTML = "Cancel";   
        
        let label = document.createElement("a");
        label.classList.add("btn");
        label.classList.add("btn-disabled");
        label.classList.add("border-0");
        label.style.fontStyle = "italic";
        label.innerHTML = "Invitation sent";
        
        let buttonGroup = document.createElement("div");
        buttonGroup.appendChild(label);
        buttonGroup.appendChild(button);
        return buttonGroup;
        
    } else if (incomingInvite) {
        button.onclick = function() {accept(timestamp);};
        button.innerHTML = "Accept";
        
        let decline = document.createElement("a");
        decline.classList.add("btn");
        decline.classList.add("btn-primary");
        decline.classList.add("border-0");
        decline.onclick = function() {decline(timestamp);};
        decline.innerHTML = "Decline";
        
        let buttonGroup = document.createElement("div");
        decline.classList.add("btn-group");
        buttonGroup.appendChild(button);
        buttonGroup.appendChild(decline);
        
        return buttonGroup;
    } else {
        button.onclick = function() {challenge(listUid);};
        button.innerHTML = "Send Invite";
        return button;
    }
}

