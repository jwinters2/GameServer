const contextRoot = document.querySelector("meta[name='contextRoot']").content;

var stompClient = null;
var userid = null;

function challenge(uid, continueMatchid = null) {
    const request = new XMLHttpRequest();  
	
	var continueArg = "";
    if(continueMatchid !== null) {
		continueArg = `?continue=${continueMatchid}`;
	}
	
	request.open('GET', `${contextRoot}/queue/challenge/${uid}${continueArg}`);
    request.onload = function() {
        console.log(request.response);
    };
    request.send();
}

function handleInvite(command, inviteId) {
    const request = new XMLHttpRequest();  
    request.open('GET', `${contextRoot}/queue/${command}/${inviteId}`);
    request.onload = function() {
        console.log(request.response);
    };
    request.send();
}

function accept(inviteId) {
    handleInvite("accept", inviteId);
}

function cancel(inviteId) {
    handleInvite("cancel", inviteId);
}

function init(uid) {
    userid = uid;
    console.log(`connecting to ${uid}`);
    var socket = new SockJS(`${contextRoot}/server`);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected", frame);
        stompClient.subscribe(`${contextRoot}/websocket/queue/${uid}`, function(message) {
            handleUpdate(JSON.parse(message.body));
        });
        heartbeat();
    });
	
	let userList = JSON.parse(document.querySelector("meta[name='userList']").content);
	let body = new Object();
	body.userList = userList;
	updateQueue(body);
}

// heartbeat stuff
function heartbeat() {
    if(stompClient !== null) {
        stompClient.send(`${contextRoot}/to-server/heart/${userid}`, {}, "beat");
    }
}

setInterval(heartbeat, 10000);

function handleUpdate(body) {
    if(body.messageType === "queueUpdate") {
        updateQueue(body);
    } else if (body.messageType === "gameInvite") {
        goToGame(body);
    }
}

function updateQueue(body) {
    console.log("updateQueue body=", body);
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
        button.classList.add("float-end");
        
        playCell.appendChild(button);

        tr.appendChild(playCell);

    });
    
    if(users.length === 0) {
 
        let italics = document.createElement("i");
        italics.innerHTML = "No users waiting to play";
        
        let usernameCell = document.createElement("td");
        usernameCell.classList.add('user-list-entry');
        usernameCell.classList.add('text-center');
        usernameCell.appendChild(italics);
        usernameCell.colSpan = 2;
        
        tr.appendChild(usernameCell);
    }
}

function generateUserButton(userUid, listUid, invites) {
    
    let outgoingInvite = false;
    let incomingInvite = false;
    let timestamp = -1;
    
	if(typeof invites === "object") {
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
	}

    
    let button = document.createElement("a");
    button.classList.add("btn");
    button.classList.add("btn-primary");
    button.classList.add("border-0");
	
	const pendingGame = findPendingGame(listUid);
        
    if(outgoingInvite) {
       
        let label = document.createElement("a");
        label.classList.add("btn");
        label.classList.add("btn-disabled");
        label.classList.add("border-0");
        label.style.fontStyle = "italic";
        label.innerHTML = "Invitation sent";
		
		button.onclick = function() {cancel(timestamp);};
        button.innerHTML = "Cancel";
        
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
		decline.onclick = function() {cancel(timestamp);};
        decline.innerHTML = "Decline";

		let buttonGroup = document.createElement("div");

		// if we need to, let the player know if this is resuming a game or starting a new one
		if(pendingGame !== null) {
			let label = document.createElement("a");
			label.classList.add("btn");
			label.classList.add("btn-disabled");
			label.classList.add("border-0");
			label.style.fontStyle = "italic";
			label.innerHTML = (timestamp == pendingGame ? "Continue" : "Start New Game");
			
			buttonGroup.appendChild(label);
		}
    
        buttonGroup.appendChild(button);
        buttonGroup.appendChild(decline);
        
        return buttonGroup;
		
    } else {
		
		if(pendingGame !== null) {
			button.onclick = function() {challenge(listUid, pendingGame);};
			button.innerHTML = "Invite to Continue";

			let decline = document.createElement("a");
			decline.classList.add("btn");
			decline.classList.add("btn-primary");
			decline.classList.add("border-0");
			decline.onclick = function() {challenge(listUid);};
			decline.innerHTML = "Invite to New Game";

			let buttonGroup = document.createElement("div");
			buttonGroup.classList.add("btn-group");
			buttonGroup.appendChild(button);
			buttonGroup.appendChild(decline);

			return buttonGroup;
		} else {
			button.onclick = function() {challenge(listUid);};
			button.innerHTML = "Send Invite";
			return button;
		}
    }
}

function findPendingGame(uid) {
	let matches = JSON.parse(document.querySelector("meta[name='pendingMatches']").content);
	
	if(matches === null) {
		return null;
	}
	
	let keys = Object.keys(matches);
	for(var i=0; i<keys.length; i++) {
		let match = keys[i];
		if(matches[match].includes(uid)) {
			return match;
		}
	}
	
	return null;
}

function goToGame(body) {
    window.location.href = (`${contextRoot}/game/${body.gameStr.replace(" ","")}/${body.timestamp}`);
}