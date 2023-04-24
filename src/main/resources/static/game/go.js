class Go extends Game {
	
	stones = null;
	whiteToMove = null;
	
	playerIsWhite = null;
	
	lightBg = "#dfad68";
	darkBg = "#000000";
	
	whitePieceColor = "#ffffff";
	blackPieceColor = "#221100";
	pieceBorderColor = "#000000";
	
	boardStyle = {
		x: 100, 
		y: 100, 
		width: 50, 
		height: 50,
		pieceRadius: 24,
		lineWidth: 2
	};
	
	boardWidth = 0;
	boardHeight = 0;
	boardAspectRatio = 1;
	
	lastMoved = null;
	
	init(_uid, _game, _matchid) {

		super.init(_uid, _game, _matchid);
		
		// determine if we're white or not
		this.playerIsWhite = document.querySelector("meta[name='playerColor']").content === "white";
		console.log(this.playerIsWhite ? "we're white" : "we're black");
		
		this.calculateBoardDimensions();
		
		// setup canvas
		this.canvas = document.getElementById("chessCanvas");
		var dim = Math.min(this.canvas.parentElement.offsetWidth, this.canvas.parentElement.offsetHeight / this.boardAspectRatio);
		this.canvas.width = dim;
		this.canvas.height = dim * this.boardAspectRatio;;
		this.context = this.canvas.getContext("2d");
		
		this.draw();
		
		// handle resize
		setInterval((chessObj) => {
			var dim = Math.min(chessObj.canvas.parentElement.offsetWidth, chessObj.canvas.parentElement.offsetHeight / this.boardAspectRatio);
			if(chessObj.canvas.width !== dim || chessObj.canvas.height !== dim) {
				chessObj.canvas.width = dim;
				chessObj.canvas.height = dim * this.boardAspectRatio;
				chessObj.draw();	
			}

		}, 500, this);
		
		// click events
		this.canvas.chessObj = this;
		this.canvas.addEventListener('mousedown', this.onClick);
		this.canvas.addEventListener('touchstart', this.onClick);
		this.canvas.addEventListener('mousemove', this.onClickMove);
		this.canvas.addEventListener('touchmove', this.onClickMove);
		this.canvas.addEventListener('mouseup', this.onClickRelease);
		this.canvas.addEventListener('touchend', this.onClickRelease);
	}
	
	resetTransform() {
		this.context.setTransform(this.canvas.width/this.boardWidth, 0, 0, this.canvas.height/this.boardHeight, 0, 0);
	}
	
	calculateBoardDimensions() {
		this.boardWidth  = (2 * this.boardStyle.x) + ((19-1) * this.boardStyle.width);
		this.boardHeight = (2 * this.boardStyle.y) + ((19-1) * this.boardStyle.height);
		this.boardAspectRatio = this.boardHeight/this.boardWidth;
	}
	
	drawStone(stone, xOffset = 0, yOffset = 0) {
		
		this.context.lineWidth = this.boardStyle.lineWidth;
		
		const x = (this.playerIsWhite ? stone.x : (19 - 1 - stone.x)) + xOffset;
		const y = (this.playerIsWhite ? (19 - 1 - stone.y) : stone.y) + yOffset;
		
		this.resetTransform();
		this.context.translate(
			this.boardStyle.x + (this.boardStyle.width  * x),
			this.boardStyle.y + (this.boardStyle.height * y)
		);
		
		const pieceColor = (stone.color === "WHITE") ? this.whitePieceColor : this.blackPieceColor;
		
		// fill color
		this.context.fillStyle = pieceColor;
		this.context.beginPath();
		this.context.arc(0, 0, this.boardStyle.pieceRadius, 0, 2 * Math.PI);
		this.context.fill();
		
		// draw outline
		this.context.strokeStyle = this.pieceBorderColor;
		this.context.beginPath();
		this.context.arc(0, 0, this.boardStyle.pieceRadius, 0, 2 * Math.PI);
		this.context.stroke();
		
		this.resetTransform();
	}
	
	draw() {

		this.context.setTransform(this.canvas.width/this.boardWidth, 0, 0, this.canvas.height/this.boardHeight, 0, 0);
		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.context.fillStyle = this.lightBg;
		this.context.fillRect(0,0,this.boardWidth, this.boardHeight);
		
		this.context.lineWidth = this.boardStyle.lineWidth;
		this.context.lineCap = "round";
		
		this.context.fillStyle = this.darkBg;
		this.context.strokeStyle = this.darkBg;
		
		// draw vertical lines
		for(var x=0; x<19; x++) {
			this.context.beginPath();
			this.context.moveTo(this.boardStyle.x + (x * this.boardStyle.width), this.boardStyle.y);
			this.context.lineTo(this.boardStyle.x + (x * this.boardStyle.width), this.boardStyle.y + ((19-1) * this.boardStyle.height));
			this.context.stroke();
		}
		
		// draw horizontal lines
		for(var y=0; y<19; y++) {
			this.context.beginPath();
			this.context.moveTo(this.boardStyle.x, this.boardStyle.y + (y * this.boardStyle.height));
			this.context.lineTo(this.boardStyle.x + ((19-1) * this.boardStyle.width), this.boardStyle.y + (y * this.boardStyle.height));
			this.context.stroke();
		}
		
		// draw the dots
		for(var x = 3; x <= 19; x+=6) {
			for(var y = 3; y <= 19; y+=6) {
				this.context.beginPath();
				this.context.arc(
					this.boardStyle.x + (x * this.boardStyle.width),
					this.boardStyle.y + (y * this.boardStyle.height),
					this.boardStyle.lineWidth * 2,
					0, 2 * Math.PI
				);
				this.context.fill();
			}
		}
		
		// draw stones
		if(Array.isArray(this.stones)) {
			for(var i = 0; i < this.stones.length; i++) {
				this.drawStone(this.stones[i]);
			}	
		}
	}
	
	handleUpdate(update, chessObj) {
		
		console.log("update", update);
		
		// check if this is the format we're expecting
		if(update.type === "goState") {
			chessObj.stones = update.stones;
			chessObj.handleResult(update.status, update.winner);
			chessObj.draw();
		}	
	}
	
	pieceToCanvasXCoord(x) {
		return this.playerIsWhite ? x : (19 - 1 - x);
	}
	
	pieceToCanvasYCoord(y) {
		return this.playerIsWhite ? (19 - 1  - y) : y;
	}
	
	xToFile(x) {
		return this.playerIsWhite ? (19-x) : (x+1);
	}
	
	yToRank(y) {
		return this.playerIsWhite ? (y+1) : (19-y);
	}
	
	isOurStone(stone) {
		return (stone.color === "WHITE") === this.playerIsWhite;
	}
	
	onClick(event) {
		if ((event instanceof MouseEvent)) {
			event.preventDefault();
		}

		var chessObj = event.currentTarget.chessObj;
	}
	
	onClickMove (event) {
			
		if ((event instanceof MouseEvent)) {
			event.preventDefault();
		}

		var chessObj = event.currentTarget.chessObj;
	};
		
	onClickRelease (event) {
			
		if ((event instanceof MouseEvent)) {
			event.preventDefault();
		}

		var chessObj = event.currentTarget.chessObj;

		if(chessObj.holdingPiece === null && !chessObj.pieceNeedsPromotion) {
			return;
		}

		var x = (event instanceof MouseEvent) ? event.clientX : 
				(event instanceof TouchEvent) ? event.changedTouches[0].clientX : 
				-1;
		var y = (event instanceof MouseEvent) ? event.clientY : 
				(event instanceof TouchEvent) ? event.changedTouches[0].clientY : 
				-1;

		var rect = chessObj.canvas.getBoundingClientRect();

		x = (x - rect.left) * chessObj.boardWidth/chessObj.canvas.width;
		y = (y - rect.top) * chessObj.boardHeight/chessObj.canvas.height;	
		x = (x - chessObj.boardStyle.x)/chessObj.boardStyle.width;
		y = (y - chessObj.boardStyle.y)/chessObj.boardStyle.height;
				

		x = Math.round(x);
		y = Math.round(y);

		console.log(`place piece at ${chessObj.xToFile(x)},${chessObj.yToRank(y)}`);	

		chessObj.sendMove(x, y);
		
		chessObj.draw();
	};
	
	sendMove(toX, toY, extra) {

		const dict = [];
		
		// destination
		if(toX !== null && toY !== null) {
			dict.push(`to=${19 - this.xToFile(toX)},${19 - this.yToRank(toY)}`);
		}
		
		const data = dict.join('&');

		super.sendMove(data);
	}
	
	setDrawPieceGuides(v) {
		this.drawPieceGuides = !!v;
	}
}

const go = new Go();

document.title = "Go";