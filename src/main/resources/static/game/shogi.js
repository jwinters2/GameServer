class Shogi extends Game {
	
	pieces = null;
	whiteToMove = null;
	
	playerIsWhite = true; //null;
	
	lightBg = "#dfad68";
	darkBg = "#000000";
	
	pieceColor = "#ffeaa3";
	promotedColor = "#cc0000";
	
	boardStyle = {
		x: 50, 
		y: 50, 
		width: 90, 
		height: 100,
		lineWidth: 4,
		textFont: "24px sans-serif",
		fontSize: 24,
		pieceFont: "times new roman",
		pieceFontSize: 50,
		pieceYOffset: 0,
		pieceTextYOffset: 5
	};
	
	boardWidth = 0;
	boardHeight = 0;
	boardAspectRatio = 1;
	
	pieceInHand = null;
	
	lastMoved = null;
	
	// traditional = 2 kanji per piece
	// abbreviated = 1 kanji per piece
	// symbols = western chess pieces
	displayStyle = "traditional";
	pieceChars = {
		"pawn":		{chars: [["歩", "兵"], ["と", "金"]],	abbr: ["歩", "と"],	symbols: ["♟︎", "♟︎"],	size: 27/32},
		"king":		{chars: [["王", "将"], ["玉", "将"]],	abbr: ["王", "玉"],	symbols: ["♔", "♚"],	size: 32/32},
		"rook":		{chars: [["飛", "車"], ["龍", "王"]],	abbr: ["飛", "龍"],	symbols: ["♜", "♜"],	size: 31/32},
		"bishop":	{chars: [["角", "行"], ["龍", "馬"]],	abbr: ["角", "馬"],	symbols: ["♝", "♝"],	size: 31/32},
		"gold":		{chars: [["金", "将"]],				abbr: ["金"],		symbols: ["☉"],		size: 30/32},
		"silver":	{chars: [["銀", "将"], ["成", "銀"]],	abbr: ["銀", "全"],	symbols: ["☽", "☽"],	size: 30/32},
		"knight":	{chars: [["桂", "馬"], ["成", "桂"]],	abbr: ["桂", "圭"],	symbols: ["♞", "♞"],	size: 29/32},
		"lance":	{chars: [["香", "車"], ["成", "香"]],	abbr: ["香", "杏"],	symbols: ["↟", "↟"],	size: 28/32}
	};
	pieceOutline = [[0, -1], [0.75, -0.783], [1, 1], [-1, 1], [-0.75, -0.783]];
	pieceOutlineScale = 0.4;
	
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
	}
	
	resetTransform() {
		this.context.setTransform(this.canvas.width/this.boardWidth, 0, 0, this.canvas.height/this.boardHeight, 0, 0);
	}
	
	calculateBoardDimensions() {
		this.boardWidth  = (2 * this.boardStyle.x) + (9 * this.boardStyle.width);
		this.boardHeight = (2 * this.boardStyle.y) + (9 * this.boardStyle.height);
		this.boardAspectRatio = this.boardHeight/this.boardWidth;
	}
	
	tracePieceOutline(piece) {
		this.context.moveTo(
			this.pieceOutline[4][0] * this.pieceChars[piece.type].size * this.boardStyle.width * this.pieceOutlineScale,
			this.pieceOutline[4][1] * this.pieceChars[piece.type].size * this.boardStyle.height * this.pieceOutlineScale
		);
		for(var i = 0; i<this.pieceOutline.length; i++) {
			this.context.lineTo(
				this.pieceOutline[i][0] * this.pieceChars[piece.type].size * this.boardStyle.width * this.pieceOutlineScale,
				this.pieceOutline[i][1] * this.pieceChars[piece.type].size * this.boardStyle.height * this.pieceOutlineScale
			);
		}
	}
	
	drawPiece(piece) {
		const fontSize = this.boardStyle.pieceFontSize * this.pieceChars[piece.type].size;
		
		this.context.textBaseline = "middle";
		this.context.textAlign = "center";
		this.context.lineWidth = this.boardStyle.lineWidth/2;
		
		this.context.font = `${fontSize}px ${this.boardStyle.pieceFont}`;
		
		this.resetTransform();
		this.context.translate(
			this.boardStyle.x + (this.boardStyle.width  * (piece.x + 0.5)),
			this.boardStyle.y + (this.boardStyle.height * (piece.y + 0.5)) + this.boardStyle.pieceYOffset
		);
		if((piece.side === "white") === this.playerIsWhite) {
			this.context.rotate(Math.PI);
		}

		// fill color
		this.context.fillStyle = this.pieceColor;
		this.context.beginPath();
		this.tracePieceOutline(piece);
		this.context.fill();
		
		// draw outline
		this.context.beginPath();
		this.tracePieceOutline(piece);
		this.context.stroke();
		
		// which character(s) to draw
		var charIndex = 0;
		if(piece.type === "king") {
			charIndex = (piece.side === "white" ? 0 : 1);
		} else if (piece.type !== "gold") {
			charIndex = (piece.isPromoted ? 1 : 0);
		}
		
		// draw text
		this.context.fillStyle = piece.isPromoted ? this.promotedColor : this.darkBg;
		if ( this.displayStyle === "traditional" ) {
			this.context.font = `${Math.floor(fontSize * 0.7)}px ${this.boardStyle.pieceFont}`;
			this.context.textBaseline = "bottom";
			this.context.fillText(this.pieceChars[piece.type].chars[charIndex][0], 0, this.boardStyle.pieceTextYOffset);
			this.context.textBaseline = "top";
			this.context.fillText(this.pieceChars[piece.type].chars[charIndex][1], 0, this.boardStyle.pieceTextYOffset);
		} else if (this.displayStyle === "symbols" ) {
			this.context.font = `${fontSize}px ${this.boardStyle.pieceFont}`;
			this.context.textBaseline = "middle";
			this.context.fillText(this.pieceChars[piece.type].symbols[charIndex], 0, this.boardStyle.pieceTextYOffset);
		} else {
			this.context.font = `${fontSize}px ${this.boardStyle.pieceFont}`;
			this.context.textBaseline = "middle";
			this.context.fillText(this.pieceChars[piece.type].abbr[charIndex], 0, this.boardStyle.pieceTextYOffset);
		}
		
		

	}
	
	draw() {

		this.context.setTransform(this.canvas.width/this.boardWidth, 0, 0, this.canvas.height/this.boardHeight, 0, 0);
		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.context.fillStyle = this.lightBg;
		this.context.fillRect(0,0,this.boardWidth, this.boardHeight);
		
		const offset = this.boardStyle.lineWidth/2;
		
		this.context.fillStyle = this.darkBg;
		// draw verical lines
		for(var x=0; x<=9; x++) {
			this.context.fillRect(
				this.boardStyle.x + (x * this.boardStyle.width) - offset, 
				this.boardStyle.y - offset, 
				this.boardStyle.lineWidth,
				this.boardStyle.lineWidth + (9 * this.boardStyle.height)
			);
		}
		// draw horizontal lines
		for(var y=0; y<=9; y++) {
			this.context.fillRect(
				this.boardStyle.x - offset,
				this.boardStyle.y + (y * this.boardStyle.height) - offset, 
				this.boardStyle.lineWidth + (9 * this.boardStyle.width),
				this.boardStyle.lineWidth
			);
		}
		// draw the dots
		for(var x = 0; x <= 1; x++) {
			for(var y = 0; y <= 1; y++) {
				this.context.beginPath();
				this.context.arc(
					this.boardStyle.x + (3 * (x+1) * this.boardStyle.width),
					this.boardStyle.y + (3 * (y+1) * this.boardStyle.height),
					this.boardStyle.lineWidth * 1.5,
					0, 2 * Math.PI
				);
				this.context.fill();
			}
		}
		
		// draw pieces
		this.drawPiece({type: "lance", x: 0, y: 0, side: "black", isPromoted: true});
		this.drawPiece({type: "knight", x: 1, y: 0, side: "black", isPromoted: true});
		this.drawPiece({type: "silver", x: 2, y: 0, side: "black", isPromoted: true});
		this.drawPiece({type: "gold", x: 3, y: 0, side: "black", isPromoted: false});
		this.drawPiece({type: "king", x: 4, y: 0, side: "black", isPromoted: false});
		this.drawPiece({type: "gold", x: 5, y: 0, side: "black", isPromoted: false});
		this.drawPiece({type: "silver", x: 6, y: 0, side: "black", isPromoted: true});
		this.drawPiece({type: "knight", x: 7, y: 0, side: "black", isPromoted: true});
		this.drawPiece({type: "lance", x: 8, y: 0, side: "black", isPromoted: true});
		
		this.drawPiece({type: "rook", x: 1, y: 1, side: "black", isPromoted: true});
		this.drawPiece({type: "bishop", x: 7, y: 1, side: "black", isPromoted: true});
		
		for(var i=0; i<9; i++) {
			this.drawPiece({type: "pawn", x: i, y: 2, side: "black", isPromoted: true});
			this.drawPiece({type: "pawn", x: i, y: 6, side: "white"});
		}
		
		this.drawPiece({type: "lance", x: 0, y: 8, side: "white"});
		this.drawPiece({type: "knight", x: 1, y: 8, side: "white"});
		this.drawPiece({type: "silver", x: 2, y: 8, side: "white"});
		this.drawPiece({type: "gold", x: 3, y: 8, side: "white"});
		this.drawPiece({type: "king", x: 4, y: 8, side: "white"});
		this.drawPiece({type: "gold", x: 5, y: 8, side: "white"});
		this.drawPiece({type: "silver", x: 6, y: 8, side: "white"});
		this.drawPiece({type: "knight", x: 7, y: 8, side: "white"});
		this.drawPiece({type: "lance", x: 8, y: 8, side: "white"});
		
		this.drawPiece({type: "bishop", x: 1, y: 7, side: "white"});
		this.drawPiece({type: "rook", x: 7, y: 7, side: "white"});


	}
}

const shogi = new Shogi();

document.title = "Shogi";