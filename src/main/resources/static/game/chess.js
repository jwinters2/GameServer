class Chess {
	userid = null;
	matchid = null;
	game = null;
	stompClient = null;
	canvas = null;
	context = null;
	
	pieces = null;
	whiteToMove = null;
	
	playerIsWhite = null;
	
	lightBg = "#dfad68";
	darkBg = "#432618";
	
	lightSquare = "#dfad68";
	darkSquare = "#a87124";
	
	boardStyle = {
		x: 50, 
		y: 50, 
		width: 100, 
		height: 100,
		lineWidth: 6,
		fontSize: 24,
		pieceFont: "100px times new roman",
		pieceYOffset: 6
	};
	
	boardWidth = 900;
	boardHeight = 900;
	
	pieceInHand = null;
	
	// two characters each, white then black
	pieceChars = {
		"pawn": "\u2659\u265F",
		"king": "\u2654\u265A",
		"queen": "\u2655\u265B",
		"rook": "\u2656\u265C",
		"bishop": "\u2657\u265D",
		"knight": "\u2658\u265E"
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
		var chessObj = this;

		stompClient.connect({}, function (frame) {
			console.log("connected to /websocket/game", frame);
			stompClient.subscribe(socketPath, function(message) {
				handleUpdate(JSON.parse(message.body), chessObj);
			});

			const request = new XMLHttpRequest();  
			request.open('GET', `/game/${game}/${matchid}/state`);
			request.onload = function() {
				console.log(request.response);
			};
			request.send();
		});
		
		this.stompClient = stompClient;
		
		// determine if we're white or not
		this.playerIsWhite = document.querySelector("meta[name='playerColor']").content === "white";
		console.log(this.playerIsWhite ? "we're white" : "we're black");
		
		// setup canvas
		this.canvas = document.getElementById("chessCanvas");
		var dim = Math.min(this.canvas.parentElement.offsetWidth, this.canvas.parentElement.offsetHeight);
		this.canvas.width = dim;
		this.canvas.height = dim;
		this.context = this.canvas.getContext("2d");
		
		this.draw();
		
		// handle resize
		setInterval((chessObj) => {
			var dim = Math.min(chessObj.canvas.parentElement.offsetWidth, chessObj.canvas.parentElement.offsetHeight);
			if(chessObj.canvas.width !== dim || chessObj.canvas.height !== dim) {
				chessObj.canvas.width = dim;
				chessObj.canvas.height = dim;
				chessObj.draw();	
			}

		}, 500, this);
		
		var onClick = function(event) {
			var chessObj = event.currentTarget.chessObj;
			
			var x = (event instanceof MouseEvent) ? event.clientX : 
					(event instanceof TouchEvent) ? event.changedTouches[0].clientX : 
					-1;
			var y = (event instanceof MouseEvent) ? event.clientY : 
					(event instanceof TouchEvent) ? event.changedTouches[0].clientY : 
					-1;
			
			var rect = chessObj.canvas.getBoundingClientRect();
			
			var mouseX = (x - rect.left) * chessObj.boardWidth/chessObj.canvas.width;;
			var mouseY = (y - rect.top) * chessObj.boardHeight/chessObj.canvas.height;
			
			x = (x - rect.left) * chessObj.boardWidth/chessObj.canvas.width;
			x = (x - chessObj.boardStyle.x)/chessObj.boardStyle.width;
			y = (y - rect.top) * chessObj.boardHeight/chessObj.canvas.height;
			y = (y - chessObj.boardStyle.y)/chessObj.boardStyle.height;
			
			var xOffset = x - Math.floor(x);
			var yOffset = y - Math.floor(y);
			
			x = Math.floor(x);
			y = Math.floor(y);
			
			chessObj.pieceInHand = {
				x: x,
				y: y,
				xOffset: xOffset,
				yOffset: yOffset,
				mouseX: mouseX,
				mouseY: mouseY
			};
			
			console.log(chessObj.pieceInHand);
			chessObj.draw();
		};
		
		var onClickMove = function(event) {
			var chessObj = event.currentTarget.chessObj;
			if(chessObj.pieceInHand !== null) {
				
				var x = (event instanceof MouseEvent) ? event.clientX : 
					(event instanceof TouchEvent) ? event.changedTouches[0].clientX : 
					-1;
				var y = (event instanceof MouseEvent) ? event.clientY : 
					(event instanceof TouchEvent) ? event.changedTouches[0].clientY : 
					-1;
				
				var rect = chessObj.canvas.getBoundingClientRect();
				chessObj.pieceInHand.mouseX = (x - rect.left) * chessObj.boardWidth/chessObj.canvas.width;;
				chessObj.pieceInHand.mouseY = (y - rect.top) * chessObj.boardHeight/chessObj.canvas.height;
				chessObj.draw();
			}
		};
		
		var onClickRelease = function(event) {
			var chessObj = event.currentTarget.chessObj;
			
			var x = (event instanceof MouseEvent) ? event.clientX : 
					(event instanceof TouchEvent) ? event.changedTouches[0].clientX : 
					-1;
			var y = (event instanceof MouseEvent) ? event.clientY : 
					(event instanceof TouchEvent) ? event.changedTouches[0].clientY : 
					-1;
			
			var rect = chessObj.canvas.getBoundingClientRect();
			
			x = (x - rect.left) * chessObj.boardWidth/chessObj.canvas.width;
			x = (x - chessObj.boardStyle.x)/chessObj.boardStyle.width;
			y = (y - rect.top) * chessObj.boardHeight/chessObj.canvas.height;
			y = (y - chessObj.boardStyle.y)/chessObj.boardStyle.height;
			
			x = Math.floor(x);
			y = Math.floor(y);
			
			chessObj.sendMove(chessObj.pieceInHand.x, chessObj.pieceInHand.y, x, y);
			
			chessObj.pieceInHand = null;
			chessObj.draw();
		};
		
		this.canvas.chessObj = this;
		this.canvas.addEventListener('mousedown', onClick);
		this.canvas.addEventListener('touchstart', onClick);
		this.canvas.addEventListener('mousemove', onClickMove);
		this.canvas.addEventListener('touchmode', onClickMove);
		this.canvas.addEventListener('mouseup', onClickRelease);
		this.canvas.addEventListener('touchend', onClickRelease);
		

	}

	sendMove(fromX, fromY, toX, toY) {

		const dict = [];
		dict.push(`from=${this.xToFile(fromX)}${this.yToRank(fromY)}`);
		dict.push(`to=${this.xToFile(toX)}${this.yToRank(toY)}`);
		const data = dict.join('&');

		const request = new XMLHttpRequest();  
		request.open('POST', `/game/${this.game}/${this.matchid}/move`);
		request.onload = function() {
			console.log(request.response);
		};
		request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
		request.setRequestHeader(getCsrfTokenHeader(), getCsrfToken());
		request.send(data);
	}

	handleUpdate(update, chessObj) {
		chessObj.setPieces(update.pieces);
		chessObj.setWhiteToMove(update.whiteToMove);
		chessObj.draw();
	}
	
	setPieces(pieces) {
		this.pieces = pieces;
	}
	
	setWhiteToMove(whiteToMove) {
		this.whiteToMove = whiteToMove;
	}
	
	xToFile(x) {
		return this.playerIsWhite 
		? String.fromCharCode('a'.charCodeAt(0) + x)
		: String.fromCharCode('h'.charCodeAt(0) - x);
	}
	
	yToRank(y) {
		return this.playerIsWhite ? (8-y) : (y+1);
	}
	
	pieceToCanvasXCoord(x) {
		return this.playerIsWhite ? x : (7 - x);
	}
	
	pieceToCanvasYCoord(y) {
		return this.playerIsWhite ? (7 - y) : y;
	}
	
	draw() {
		
		this.boardWidth  = (2 * this.boardStyle.x) + (8 * this.boardStyle.width);
		this.boardHeight = (2 * this.boardStyle.y) + (8 * this.boardStyle.height);

		this.context.setTransform(this.canvas.width/this.boardWidth, 0, 0, this.canvas.height/this.boardHeight, 0, 0);
		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.context.fillStyle = this.lightBg;
		this.context.fillRect(0,0,this.boardWidth, this.boardHeight);
		
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
		
		// draw file letters
		this.context.textBaseline = "middle";
		this.context.textAlign = "center";
		this.context.font = `${this.boardStyle.fontSize}px sans-serif`;
		
		for(var x=0; x<8; x++) {
			this.context.fillText(
				this.xToFile(x),
				this.boardStyle.x + ((x+0.5) * this.boardStyle.width),
				this.boardStyle.y - this.boardStyle.lineWidth - this.boardStyle.fontSize
			);
			this.context.fillText(
				this.xToFile(x),
				this.boardStyle.x + ((x+0.5) * this.boardStyle.width),
				this.boardStyle.y + (8*this.boardStyle.height) + this.boardStyle.lineWidth + this.boardStyle.fontSize
			);
		}
				
		// draw rank numbers
		for(var y=0; y<8; y++) {
			this.context.fillText(
				this.yToRank(y),
				this.boardStyle.x - this.boardStyle.lineWidth - this.boardStyle.fontSize,
				this.boardStyle.y + ((y+0.5) * this.boardStyle.height)

			);
			this.context.fillText(
				this.yToRank(y),
				this.boardStyle.x + (8*this.boardStyle.width) + this.boardStyle.lineWidth + this.boardStyle.fontSize,
				this.boardStyle.y + ((y+0.5) * this.boardStyle.height)
			);
		}
		
		// draw pieces
		try {
			this.context.font = this.boardStyle.pieceFont;
			this.pieces.forEach(piece => {
				if(this.pieceInHand !== null 
						&& this.pieceInHand.x === this.pieceToCanvasXCoord(piece.x)
						&& this.pieceInHand.y === this.pieceToCanvasYCoord(piece.y)) {
					
					// draw transparent piece on board
					this.context.fillStyle = this.darkBg + "40";
					this.context.fillText(
						this.pieceChars[piece.type].charAt(piece.color === "WHITE" ? 0 : 1),
						this.boardStyle.x + (this.boardStyle.width  * (this.pieceToCanvasXCoord(piece.x) + 0.5)),
						this.boardStyle.y + (this.boardStyle.height * (this.pieceToCanvasYCoord(piece.y) + 0.5)) + this.boardStyle.pieceYOffset
					);
			
					// draw solid piece in hand
					this.context.fillStyle = this.darkBg;
					this.context.fillText(
						this.pieceChars[piece.type].charAt(piece.color === "WHITE" ? 0 : 1),
						this.pieceInHand.mouseX,
						this.pieceInHand.mouseY
					);
			
				} else {
					this.context.fillStyle = this.darkBg;
					this.context.fillText(
						this.pieceChars[piece.type].charAt(piece.color === "WHITE" ? 0 : 1),
						this.boardStyle.x + (this.boardStyle.width  * (this.pieceToCanvasXCoord(piece.x) + 0.5)),
						this.boardStyle.y + (this.boardStyle.height * (this.pieceToCanvasYCoord(piece.y) + 0.5)) + this.boardStyle.pieceYOffset
					);
				}

			});
		} catch (e) {
			console.log(e);
		}
	}
}

const chess = new Chess();