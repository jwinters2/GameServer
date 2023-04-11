class Chess {
	userid = null;
	matchid = null;
	game = null;
	stompClient = null;
	canvas = null;
	context = null;
	
	lightBg = "#ffffff"; //"#dfad68";
	darkBg = "#432618";
	
	lightSquare = "#dfad68";
	darkSquare = "#a87124";
	
	boardStyle = {
		x: 50, 
		y: 50, 
		width: 100, 
		height: 100,
		lineWidth: 6
	};

	init(_uid, _game, _matchid) {

		this.userid = _uid;
		this.game = _game;
		this.matchid = _matchid;

		console.log(`connecting to game uid=${this.userid} for ${this.game} match ${this.matchid}`);


		var socket = new SockJS("/server");
		var stompClient = Stomp.over(socket);
		var handleUpdate = this.handleUpdate;
		var socketPath = `/websocket/game/${this.game}/${this.matchid}/${this.userid}`;
		var userid = this.userid;
		var game = this.game;
		var matchid = this.matchid;

		stompClient.connect({}, function (frame) {
			console.log("connected to /websocket/game", frame);
			stompClient.subscribe(socketPath, function(message) {
				handleUpdate(JSON.parse(message.body));
			});

			const request = new XMLHttpRequest();  
			request.open('GET', `/game/${game}/${matchid}/state`);
			request.onload = function() {
				console.log(request.response);
			};
			request.send();
		});
		
		this.stompClient = stompClient;
		
		// setup canvas
		this.canvas = document.getElementById("chessCanvas");
		var dim = Math.min(this.canvas.parentElement.offsetWidth, this.canvas.parentElement.offsetHeight);
		this.canvas.width = dim;
		this.canvas.height = dim;
		this.context = this.canvas.getContext("2d");
		this.context.fillStyle = this.lightBg;
		this.context.fillRect(0,0,this.canvas.width, this.canvas.height);
		
		this.draw();
	}

	sendMessage() {

		const request = new XMLHttpRequest();  
		request.open('POST', `/game/${game}/${matchid}/move`);
		request.onload = function() {
			console.log(request.response);
		};
		request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
		request.setRequestHeader(getCsrfTokenHeader(), getCsrfToken());
		//request.send(`message=${encodeURIComponent(message)}`);
	}

	handleUpdate(update) {
		console.log("game update", update);
	}
	
	draw() {
		
		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.context.fillStyle = this.lightBg;
		this.context.fillRect(0,0,this.canvas.width, this.canvas.height);
		
		const offset = this.boardStyle.lineWidth/2;
		
		// draw checkerboard grid
		for(var x=0; x<8; x++) {
			for(var y=0; y<8; y++) {
				
				this.context.fillStyle = ((x+y)%2 === 0 ? this.lightSquare : this.darkSquare);
				
				this.context.fillRect(
				this.boardStyle.x + (x * this.boardStyle.width),
				this.boardStyle.y + (y * this.boardStyle.height), 
				this.boardStyle.width,
				this.boardStyle.height
				);
			}
		}
		
		this.context.fillStyle = this.darkBg;
		// draw verical lines
		for(var x=0; x<=8; x++) {
			this.context.fillRect(
				this.boardStyle.x + (x * this.boardStyle.width) - offset, 
				this.boardStyle.y - offset, 
				this.boardStyle.lineWidth,
				this.boardStyle.lineWidth + (8 * this.boardStyle.height)
			);
		}
		// draw horizontal lines
		for(var y=0; y<=8; y++) {
			this.context.fillRect(
				this.boardStyle.x - offset,
				this.boardStyle.y + (y * this.boardStyle.height) - offset, 
				this.boardStyle.lineWidth + (8 * this.boardStyle.width),
				this.boardStyle.lineWidth
			);
		}
		
	}
}

const chess = new Chess();