class Game {
	userid = null;
	matchid = null;
	game = null;
	stompClient = null;
	canvas = null;
	context = null;

	init(_uid, _game, _matchid) {

		this.userid = _uid;
		this.game = _game;
		this.matchid = _matchid;

		console.log(`connecting to game uid=${this.userid} for ${this.game} match ${this.matchid}`);


		var socket = new SockJS(`${contextRoot}/server`);
		var stompClient = Stomp.over(socket);
		var handleUpdate = this.handleUpdate;
		var showPopup = this.showPopup;
		var socketPath = `${contextRoot}/websocket/game/${this.game}/${this.matchid}/${this.userid}`;
		var userid = this.userid;
		var game = this.game;
		var matchid = this.matchid;
		var gameObj = this;

		stompClient.connect({}, function (frame) {
			console.log(`connected to ${contextRoot}/websocket/game`, frame);
			stompClient.subscribe(socketPath, function(message) {
				let updateData = JSON.parse(message.body);
				if(updateData.type === "gameEnd") {
					showPopup(updateData.reason);
				} else {
					handleUpdate(updateData, gameObj);
				}
			});

			const request = new XMLHttpRequest();  
			request.open('GET', `${contextRoot}/game/${game}/${matchid}/state`);
			request.onload = function() {
				console.log(request.response);
			};
			request.send();
		});
		
		this.stompClient = stompClient;
	}

	sendMove(data) {

		const request = new XMLHttpRequest();  
		request.open('POST', `${contextRoot}/game/${this.game}/${this.matchid}/move`);
		request.chessObj = this;
		request.onload = function (response) {
			console.log(response);
			if(response.srcElement.status !== 200) {
				response.srcElement.chessObj.showPromotionMenu = false;
				response.srcElement.chessObj.draw();
			}
		};
		request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
		request.setRequestHeader(getCsrfTokenHeader(), getCsrfToken());
		request.send(data);
	} 
	
	showPopup(reason) {
		let popupBody = document.getElementById("popup-body");
		popupBody.innerHTML = reason;
		let modal = new bootstrap.Modal(document.getElementById("popup"));
		modal.show();
	}
	
	handleResult(status, winner) {
		if(status !== "INCOMPLETE") {
			let result = null;
			if(status === "WINNER_DECIDED") {
				result = (winner === this.userid ? "You won" : "You lost");
			} else if (status === "DRAW") {
				result = "Game is a draw";
			} else {
				result = "Game has ended: " + status.toLowerCase();
			}

			this.showPopup(result);
		}
	}

}