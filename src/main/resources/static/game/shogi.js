class Shogi extends Game {
	
	pieces = null;
	piecesInHand = null;
	whiteToMove = null;
	
	playerIsWhite = null;
	
	lightBg = "#dfad68";
	darkBg = "#000000";
	
	pieceColor = "#ffeaa3";
	promotedColor = "#cc0000";
	
	boardStyle = {
		x: 50, 
		y: 200, 
		width: 90, 
		height: 100,
		lineWidth: 4,
		textFont: "24px times new roman",
		fileRankOffset: 25,
		fontSize: 24,
		pieceFont: "times new roman",
		pieceFontSize: 50,
		pieceYOffset: 0,
		pieceTextYOffset: 5,
		handOffset: 50
	};
	
	boardWidth = 0;
	boardHeight = 0;
	boardAspectRatio = 1;
	
	holdingPiece = null;
	
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
		"gold":		{chars: [["金", "将"]],				abbr: ["金"],		symbols: ["☉"],			size: 30/32},
		"silver":	{chars: [["銀", "将"], ["成", "銀"]],	abbr: ["銀", "全"],	symbols: ["☽", "☽"],	size: 30/32},
		"knight":	{chars: [["桂", "馬"], ["成", "桂"]],	abbr: ["桂", "圭"],	symbols: ["♞", "♞"],	size: 29/32},
		"lance":	{chars: [["香", "車"], ["成", "香"]],	abbr: ["香", "杏"],	symbols: ["↟", "↟"],	size: 28/32}
	};
	pieceOutline = [[0, -1], [0.75, -0.783], [1, 1], [-1, 1], [-0.75, -0.783]];
	pieceOutlineScale = 0.4;
	
	japaneseNumerals = ["零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"];
	
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
		
		// example data
		this.handleUpdate(exampleUpdate, this);
		
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
		
		const x = this.playerIsWhite ? piece.x : (8 - piece.x);
		//const y = this.playerIsWhite ? piece.y : (8 - piece.y);
		const y = this.playerIsWhite ? (8 - piece.y) : piece.y;
		
		this.resetTransform();
		this.context.translate(
			this.boardStyle.x + (this.boardStyle.width  * (x + 0.5)),
			this.boardStyle.y + (this.boardStyle.height * (y + 0.5)) + this.boardStyle.pieceYOffset
		);
		if((piece.color.toLowerCase() === "white") !== this.playerIsWhite) {
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
			charIndex = (piece.color.toLowerCase() === "white" ? 0 : 1);
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
		
		this.resetTransform();
	}
	
	drawHand(side, piecesInHand) {
		// draw box
		this.resetTransform();
		
		this.context.fillStyle = this.darkBg;
		this.context.lineWidth = this.boardStyle.lineWidth;
		this.context.lineCap = "round";
		
		const ourHand = ((side === "white") === !!this.playerIsWhite);

		if(ourHand) {
			// our hand
			this.context.beginPath();
			this.context.moveTo(
				this.boardStyle.x + (this.boardStyle.width), 
				this.boardStyle.y + (this.boardStyle.height * 11) - this.boardStyle.handOffset
			);
			this.context.lineTo(
				this.boardStyle.x + (this.boardStyle.width * 8), 
				this.boardStyle.y + (this.boardStyle.height * 11) - this.boardStyle.handOffset
			);
			this.context.lineTo(
				this.boardStyle.x + (this.boardStyle.width * 8), 
				this.boardStyle.y + (this.boardStyle.height * 10) - this.boardStyle.handOffset
			);
			this.context.lineTo(
				this.boardStyle.x + (this.boardStyle.width), 
				this.boardStyle.y + (this.boardStyle.height * 10) - this.boardStyle.handOffset
			);
			this.context.lineTo(
				this.boardStyle.x + (this.boardStyle.width), 
				this.boardStyle.y + (this.boardStyle.height * 11) - this.boardStyle.handOffset
			);
			this.context.stroke();
		} else {
			// opponent's hand
			this.context.beginPath();
			this.context.moveTo(
				this.boardStyle.x + (this.boardStyle.width), 
				this.boardStyle.y + (this.boardStyle.height * -2) + this.boardStyle.handOffset
			);
			this.context.lineTo(
				this.boardStyle.x + (this.boardStyle.width * 8), 
				this.boardStyle.y + (this.boardStyle.height * -2) + this.boardStyle.handOffset
			);
			this.context.lineTo(
				this.boardStyle.x + (this.boardStyle.width * 8), 
				this.boardStyle.y + (this.boardStyle.height * -1) + this.boardStyle.handOffset
			);
			this.context.lineTo(
				this.boardStyle.x + (this.boardStyle.width), 
				this.boardStyle.y + (this.boardStyle.height * -1) + this.boardStyle.handOffset
			);
			this.context.lineTo(
				this.boardStyle.x + (this.boardStyle.width), 
				this.boardStyle.y + (this.boardStyle.height * -2) + this.boardStyle.handOffset
			);
			this.context.stroke();
		}
		
		if(piecesInHand) {
			
			for(var i=0; i<piecesInHand.length; i++) {
				var handPieceX = side === "white" ? i+1 : 7-i;
				var handPieceY = side === "white"
					? -2 + this.boardStyle.handOffset/this.boardStyle.height
					: 10 - this.boardStyle.handOffset/this.boardStyle.height;
				this.drawPiece({type: piecesInHand[i][0], x: handPieceX, y: handPieceY, color: side});

				var quantity = piecesInHand[i][1];
				if(quantity > 1) {

					// the pieces adjust for whose side it is on their own, so for the numbers
					// use their unadjusted values
					handPieceX = i+1;
					handPieceY = 10 - this.boardStyle.handOffset/this.boardStyle.height;

					var quantityX = this.boardStyle.x + (this.boardStyle.width * (handPieceX + 0.8)); 
					var quantityY = this.boardStyle.y + (this.boardStyle.height * (handPieceY + 0.8));

					if(!ourHand) {
						quantityX = this.boardStyle.x + (this.boardStyle.width * ((8-handPieceX) + 0.8)); 
						quantityY = this.boardStyle.y + (this.boardStyle.height * ((8-handPieceY) + 0.2));
					}

					// draw the circle around the number
					this.context.fillStyle = this.pieceColor;
					this.context.beginPath();
					this.context.arc(
						quantityX,
						quantityY,
						this.boardStyle.fontSize * 0.75,
						0, 2 * Math.PI
					);
					this.context.fill();

					this.context.fillStyle = this.darkBg;
					this.context.beginPath();
					this.context.arc(
						quantityX,
						quantityY,
						this.boardStyle.fontSize * 0.75,
						0, 2 * Math.PI
					);
					this.context.stroke();

					// prepare for writing text
					this.context.textBaseline = "middle";
					this.context.textAlign = "center";
					this.context.font = this.boardStyle.textFont;

					//quantityY += ourHand ? -this.boardStyle.handOffset : this.boardStyle.handOffset;

					this.context.fillText(quantity, quantityX, quantityY);
				}
			}
		}
	}
	
	draw() {

		this.context.setTransform(this.canvas.width/this.boardWidth, 0, 0, this.canvas.height/this.boardHeight, 0, 0);
		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.context.fillStyle = this.lightBg;
		this.context.fillRect(0,0,this.boardWidth, this.boardHeight);
		
		const offset = this.boardStyle.lineWidth/2;
		
		this.context.fillStyle = this.darkBg;
		this.context.lineWidth = this.boardStyle.lineWidth;
		this.context.lineCap = "round";
		
		// draw vertical lines
		for(var x=0; x<=9; x++) {
			this.context.beginPath();
			this.context.moveTo(this.boardStyle.x + (x * this.boardStyle.width), this.boardStyle.y);
			this.context.lineTo(this.boardStyle.x + (x * this.boardStyle.width), this.boardStyle.y + (9 * this.boardStyle.height));
			this.context.stroke();
		}
		
		// draw horizontal lines
		for(var y=0; y<=9; y++) {
			this.context.beginPath();
			this.context.moveTo(this.boardStyle.x, this.boardStyle.y + (y * this.boardStyle.height));
			this.context.lineTo(this.boardStyle.x + (9 * this.boardStyle.width), this.boardStyle.y + (y * this.boardStyle.height));
			this.context.stroke();
		}
		
		// prepare for writing text
		this.context.textBaseline = "middle";
		this.context.textAlign = "center";
		this.context.font = this.boardStyle.textFont;
		
		// draw the file labels
		for(var i=0; i<9; i++) {
			this.context.fillText(
				this.playerIsWhite ? (8-i)+1 : i+1, 
				this.boardStyle.x + ((i+0.5) * this.boardStyle.width), 
				this.boardStyle.y - this.boardStyle.fileRankOffset
			);
			this.context.fillText(
				this.playerIsWhite ? (8-i)+1 : i+1, 
				this.boardStyle.x + ((i+0.5) * this.boardStyle.width), 
				this.boardStyle.y + (this.boardStyle.height * 9) + this.boardStyle.fileRankOffset
			);
		}
		
		// draw the rank labels
		for(var i=0; i<9; i++) {
			this.context.fillText(
				this.japaneseNumerals[this.playerIsWhite ? i+1 : (8-i)+1], 
				this.boardStyle.x - this.boardStyle.fileRankOffset,
				this.boardStyle.y + ((i+0.5) * this.boardStyle.height)
			);
			this.context.fillText(
				this.japaneseNumerals[this.playerIsWhite ? i+1 : (8-i)+1], 
				this.boardStyle.x + (this.boardStyle.width * 9) + this.boardStyle.fileRankOffset,
				this.boardStyle.y + ((i+0.5) * this.boardStyle.height)
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
		if(Array.isArray(this.pieces)) {
			for(var i = 0; i < this.pieces.length; i++) {
				this.drawPiece(this.pieces[i]);
			}	
		}

		if(this.piecesInHand) {
			this.drawHand("white", this.piecesInHand.white);
			this.drawHand("black", this.piecesInHand.black);		
		} else {
			this.drawHand("white");
			this.drawHand("black");
		}

	}
	
	handleUpdate(update, chessObj) {
		
		console.log("update", update);
		
		// check if this is the format we're expecting
		if(update.type === "shogiState") {
			chessObj.pieces = update.pieces;
			chessObj.piecesInHand = {white: [], black: []};

			// sort hands
			var whiteKeys = Object.keys(update.whiteHand);
			for(var i=0; i<whiteKeys.length; i++) {
				chessObj.piecesInHand.white.push(
					[whiteKeys[i], update.whiteHand[whiteKeys[i]]
				]);
			}

			var blackKeys = Object.keys(update.blackHand);
			for(var i=0; i<blackKeys.length; i++) {
				chessObj.piecesInHand.black.push(
					[blackKeys[i], update.blackHand[blackKeys[i]]
				]);
			}

			const sortFunction = function (a, b) {
				const order = ["king", "rook", "bishop", "gold", "silver", "knight", "lance", "pawn"];
				return order.indexOf(a[0]) - order.indexOf(b[0]);
			};
			chessObj.piecesInHand.white.sort(sortFunction);
			chessObj.piecesInHand.black.sort(sortFunction);
		}	
	}
	
	pieceToCanvasXCoord(x) {
		return this.playerIsWhite ? x : (8 - x);
	}
	
	pieceToCanvasYCoord(y) {
		return this.playerIsWhite ? (8 - y) : y;
	}
	
	xToFile(x) {
		return this.playerIsWhite ? (9-x) : (x+1);
	}
	
	yToRank(y) {
		return this.playerIsWhite ? this.japaneseNumerals[y+1] : this.japaneseNumerals[9-y];
	}
	
	yToRankWestern(y) {
		return this.playerIsWhite ? (y+1) : (9-y);
	}
	
	onClick(event) {
		if ((event instanceof MouseEvent)) {
			event.preventDefault();
		}

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
		
		// offset for player's hand
		var clickOnHand = false;
		if(y > 9 + chessObj.boardStyle.handOffset/chessObj.boardStyle.height) {
			clickOnHand = true;
		}

		var xOffset = x - Math.floor(x);
		var yOffset = y - Math.floor(y);

		x = Math.floor(x);
		y = Math.floor(y);

		var type = null;
		chessObj.pieces.forEach(piece => {
			if(x === chessObj.pieceToCanvasXCoord(piece.x) && 
			   y === chessObj.pieceToCanvasYCoord(piece.y)) {
				type = piece.type;
			}
		});
		
		var fromHand = false;
		var hand = chessObj.playerIsWhite ? chessObj.piecesInHand.white : chessObj.piecesInHand.black;
		// bounding boxes for player's hand
		if(clickOnHand && x > 0 && x < hand.length + 1) {
			type = hand[x-1][0];
			fromHand = true;
		}

		if(chessObj.holdingPiece === null) {
			chessObj.holdingPiece = {
				x: x,
				y: y,
				xOffset: xOffset,
				yOffset: yOffset,
				mouseX: mouseX,
				mouseY: mouseY,
				destX: 0,
				destY: 0,
				type: type,
				fromHand: fromHand
			};
		}
		
		console.log("click on piece: ", chessObj.holdingPiece);

		chessObj.draw();
	}
	
	onClickMove (event) {
			
		if ((event instanceof MouseEvent)) {
			event.preventDefault();
		}

		var chessObj = event.currentTarget.chessObj;
		if(chessObj.holdingPiece !== null) {

			var x = (event instanceof MouseEvent) ? event.clientX : 
				(event instanceof TouchEvent) ? event.changedTouches[0].clientX : 
				-1;
			var y = (event instanceof MouseEvent) ? event.clientY : 
				(event instanceof TouchEvent) ? event.changedTouches[0].clientY : 
				-1;

			var rect = chessObj.canvas.getBoundingClientRect();
			chessObj.holdingPiece.mouseX = (x - rect.left) * chessObj.boardWidth/chessObj.canvas.width;;
			chessObj.holdingPiece.mouseY = (y - rect.top) * chessObj.boardHeight/chessObj.canvas.height;
			chessObj.draw();
		}
	};
		
	onClickRelease (event) {
			
		if ((event instanceof MouseEvent)) {
			event.preventDefault();
		}

		var chessObj = event.currentTarget.chessObj;

		if(chessObj.holdingPiece === null && !chessObj.showPromotionMenu) {
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

		if(chessObj.showPromotionMenu) {
			console.log("show promotion menu");
			x = (x - chessObj.boardWidth/2 + chessObj.menuWidth/2)/chessObj.boardStyle.width;
			y = (y - chessObj.boardHeight/2 + chessObj.menuHeight/2 - chessObj.boardStyle.y)/chessObj.boardStyle.height;

			x = Math.floor(x);
			y = Math.floor(y);

			console.log(`click release (x,y) = (${x},${y})`);

			if(y === 0 && x >= 0 && x <= chessObj.promotionPieces.length) {
				//chessObj.sendMove(null, null, null, null, chessObj.promotionPieces[x]);
			}
		} else {					
			x = (x - chessObj.boardStyle.x)/chessObj.boardStyle.width;
			y = (y - chessObj.boardStyle.y)/chessObj.boardStyle.height;

			x = Math.floor(x);
			y = Math.floor(y);

			chessObj.holdingPiece.destX = x;
			chessObj.holdingPiece.destY = y;

			
	
			if(chessObj.holdingPiece.fromHand) {
				console.log(`drop ${chessObj.holdingPiece.type} to ${chessObj.xToFile(chessObj.holdingPiece.destX)}${chessObj.yToRank(chessObj.holdingPiece.destY)}`);
				
				chessObj.sendMove(
					chessObj.holdingPiece.destX, 
					chessObj.holdingPiece.destY,
					true,
					{}
				);
			} else {
				console.log(`move ${chessObj.holdingPiece.type} ${chessObj.xToFile(chessObj.holdingPiece.x)}${chessObj.yToRank(chessObj.holdingPiece.y)} to ${chessObj.xToFile(chessObj.holdingPiece.destX)}${chessObj.yToRank(chessObj.holdingPiece.destY)}`);	
				
				chessObj.sendMove(
					chessObj.holdingPiece.destX,
					chessObj.holdingPiece.destY,
					false,
					{
						fromX: chessObj.holdingPiece.x,
						fromY: chessObj.holdingPiece.y
					}
				);
			}
			
			chessObj.holdingPiece = null;
		}
		chessObj.draw();
	};
	
	sendMove(toX, toY, fromHand, extra) {

		const dict = [];
		
		// destination
		if(toX !== null && toY !== null) {
			dict.push(`to=${9 - this.xToFile(toX)},${9 - this.yToRankWestern(toY)}`);
		}
		
		// whether or not we're dropping it (from hand) or moving it
		dict.push(`drop=${!!fromHand}`);
		if(!fromHand) {
			// if we're not dropping it, we need the from coordinates
			dict.push(`from=${9 - this.xToFile(extra.fromX)},${9 - this.yToRankWestern(extra.fromY)}`);
		}
		
		if(extra.promotion) {
			dict.push(`promotion=${!!extra.promotion}`);
		}
		
		const data = dict.join('&');

		super.sendMove(data);
	} 
}

const shogi = new Shogi();

document.title = "Shogi";

const exampleUpdate = {
	pieces: [
		{type: "lance", x: 0, y: 0, side: "black", isPromoted: true},
		{type: "knight", x: 1, y: 0, side: "black", isPromoted: true},
		{type: "silver", x: 2, y: 0, side: "black", isPromoted: true},
		{type: "gold", x: 3, y: 0, side: "black", isPromoted: false},
		{type: "king", x: 4, y: 0, side: "black", isPromoted: false},
		{type: "gold", x: 5, y: 0, side: "black", isPromoted: false},
		{type: "silver", x: 6, y: 0, side: "black", isPromoted: true},
		{type: "knight", x: 7, y: 0, side: "black", isPromoted: true},
		{type: "lance", x: 8, y: 0, side: "black", isPromoted: true},

		{type: "rook", x: 1, y: 1, side: "black", isPromoted: true},
		{type: "bishop", x: 7, y: 1, side: "black", isPromoted: true},


		{type: "pawn", x: 0, y: 2, side: "black", isPromoted: true},
		{type: "pawn", x: 1, y: 2, side: "black", isPromoted: true},
		{type: "pawn", x: 2, y: 2, side: "black", isPromoted: true},
		{type: "pawn", x: 3, y: 2, side: "black", isPromoted: true},
		{type: "pawn", x: 4, y: 2, side: "black", isPromoted: true},
		{type: "pawn", x: 5, y: 2, side: "black", isPromoted: true},
		{type: "pawn", x: 6, y: 2, side: "black", isPromoted: true},
		{type: "pawn", x: 7, y: 2, side: "black", isPromoted: true},
		{type: "pawn", x: 8, y: 2, side: "black", isPromoted: true},

		{type: "pawn", x: 0, y: 6, side: "white"},
		{type: "pawn", x: 1, y: 6, side: "white"},
		{type: "pawn", x: 2, y: 6, side: "white"},
		{type: "pawn", x: 3, y: 6, side: "white"},
		{type: "pawn", x: 4, y: 6, side: "white"},
		{type: "pawn", x: 5, y: 6, side: "white"},
		{type: "pawn", x: 6, y: 6, side: "white"},
		{type: "pawn", x: 7, y: 6, side: "white"},
		{type: "pawn", x: 8, y: 6, side: "white"},

		{type: "lance", x: 0, y: 8, side: "white"},
		{type: "knight", x: 1, y: 8, side: "white"},
		{type: "silver", x: 2, y: 8, side: "white"},
		{type: "gold", x: 3, y: 8, side: "white"},
		{type: "king", x: 4, y: 8, side: "white"},
		{type: "gold", x: 5, y: 8, side: "white"},
		{type: "silver", x: 6, y: 8, side: "white"},
		{type: "knight", x: 7, y: 8, side: "white"},
		{type: "lance", x: 8, y: 8, side: "white"},

		{type: "bishop", x: 1, y: 7, side: "white"},
		{type: "rook", x: 7, y: 7, side: "white"}
	],
	hands: {
		white: {rook: 2, pawn: 4, knight: 1}, 
		black: {bishop: 1, pawn: 18}
	}
};