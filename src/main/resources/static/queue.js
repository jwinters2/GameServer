var stompClient = null;

function challenge(uid) {
    const request = new XMLHttpRequest();
    request.open('GET', `challenge?uid=${uid}`);
    request.send();
}

function connect() {
    var socket = new SockJS("/queue");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log("connected" + frame);
        stompClient.subscribe("/queue/challenge", function(message) {
            console.log("received message " + message);
        });
    });
}