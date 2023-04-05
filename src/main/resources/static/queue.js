var stompClient = null;

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
    console.log(`connecting to ${uid}`);
    var socket = new SockJS("/server");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected", frame);
        stompClient.subscribe(`/websocket/queue-challenge/${uid}`, function(message) {
            updateQueue(JSON.parse(message.body));
        });
    });
}

function updateQueue(body) {
    const users = body.userList;
    
    // clear table
    const tbody = document.getElementById("userlist");
    tbody.innerHTML = "";
    const tr = document.createElement("tr");
    tbody.appendChild(tr);
    /*
     *  <td class="user-list-entry align-middle"><i>No users waiting to play</i></td>
        <td class="user-list-entry" th:if="${#lists.size(users) > 0}">
            <a class="btn btn-primary border-0" th:attr="onclick=|challenge('${user.uid}')|">Send Invite</a>
        </td>
     */
    
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
        button.innerHTML = "Send Invite";
        
        playCell.appendChild(button);
        
        tr.appendChild(usernameCell);
        tr.appendChild(playCell);

    });
}