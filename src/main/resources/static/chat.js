class Chat {
	userid = null;
	matchid = null;
	game = null;
	stompClient = null;

	init(_uid, _game, _matchid) {

		this.userid = _uid;
		this.game = _game;
		this.matchid = _matchid;

		console.log(`connecting to chat uid=${this.userid} for ${this.game} match ${this.matchid}`);

		var socket = new SockJS("/server");
		var stompClient = Stomp.over(socket);
		var heartbeat = this.heartbeat;
		var handleUpdate = this.handleUpdate;
		var socketPath = `/websocket/chat/${this.game}/${this.matchid}/${this.userid}`;
		var userid = this.userid;

		stompClient.connect({}, function (frame) {
			console.log("connected to chat", frame);
			stompClient.subscribe(socketPath, function(message) {
				console.log("received chat update", message.body);
				handleUpdate(JSON.parse(message.body), userid);
			});
			heartbeat(stompClient, userid);
			setInterval(heartbeat, 10000, stompClient, userid);
		});
		
		this.stompClient = stompClient;
	}

	heartbeat(stompClient, userid) {
		if(stompClient !== null) {
			stompClient.send(`/to-server/heart/${userid}`, {}, "beat");
		}
	}

	sendMessage() {

		let message = document.getElementById("messageText").value;
		document.getElementById("messageText").value = "";

		const request = new XMLHttpRequest();  
		request.open('POST', `/game/${this.game}/${this.matchid}/chat`);
		request.onload = function() {
			console.log(request.response);
		};
		request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
		request.setRequestHeader(getCsrfTokenHeader(), getCsrfToken());
		request.send(`message=${encodeURIComponent(message)}`);
	}



	handleUpdate(messages, userid) {

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
}

const chat = new Chat();

function getCsrfTokenHeader() {
	return document.querySelector("meta[name='_csrf_header']").content;
}

function getCsrfToken() {
	return document.querySelector("meta[name='_csrf']").content;
}