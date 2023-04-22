class Shogi extends Game {
	
	pieces = null;
	piecesInHand = null;
	whiteToMove = null;
	highlightedSquares = [];
	pieceNeedsPromotion = null;
	
	playerIsWhite = null;
	
	lightBg = "#dfad68";
	darkBg = "#000000";
	
	pieceColor = "#ffeaa3";
	promotedColor = "#cc0000";
	highlightColor = "#f5d3a3";
	
	boardStyle = {
		x: 50, 
		y: 200, 
		width: 90, 
		height: 100,
		lineWidth: 4,
		textFont: "24px yuji mai, georgia",
		fileRankOffset: 25,
		fontSize: 24,
		pieceFont: "yuji mai, georgia",
		pieceFontSize: 50,
		pieceYOffset: 0,
		pieceTextYOffset: 1,
		handOffset: 50,
		guideWidth: 30,
		guideColor: "#aa000040"
	};
	
	boardWidth = 0;
	boardHeight = 0;
	boardAspectRatio = 1;
	
	holdingPiece = null;
	hoverOverX = null;
	hoverOverY = null;
	
	lastMoved = null;
	
	drawPieceGuides = true;
	
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
		
		// click events
		this.canvas.chessObj = this;
		this.canvas.addEventListener('mousedown', this.onClick);
		this.canvas.addEventListener('touchstart', this.onClick);
		this.canvas.addEventListener('mousemove', this.onClickMove);
		this.canvas.addEventListener('touchmove', this.onClickMove);
		this.canvas.addEventListener('mouseup', this.onClickRelease);
		this.canvas.addEventListener('touchend', this.onClickRelease);
		
		// setup options
		let span = document.getElementById("extraButtonContainer");
		let options = document.getElementById("optionsDropdown");
		span.appendChild(options);

		document.getElementById("optionButton").onclick = function () {
			let list = document.getElementById("optionList");
			if(list.style.display === "block") {
				list.style.display = "none";
			} else {
				list.style.display = "block";
			}
		};

		document.getElementById("optionTraditional").onclick = function () {
			console.log("setting to traditional");
			shogi.displayStyle = "traditional";
			
			document.getElementById("optionList").style.display = "none";
			document.getElementById("optionTraditional").classList.add("activeOption");
			document.getElementById("optionAbbreviated").classList.remove("activeOption");
			document.getElementById("optionSymbols").classList.remove("activeOption");
		};

		document.getElementById("optionAbbreviated").onclick = function () {
			console.log("setting to traditional");
			shogi.displayStyle = "abbreviated";
			
			document.getElementById("optionList").style.display = "none";
			document.getElementById("optionTraditional").classList.remove("activeOption");
			document.getElementById("optionAbbreviated").classList.add("activeOption");
			document.getElementById("optionSymbols").classList.remove("activeOption");
		};

		document.getElementById("optionSymbols").onclick = function () {
			console.log("setting to traditional");
			shogi.displayStyle = "symbols";
			
			document.getElementById("optionList").style.display = "none";
			document.getElementById("optionTraditional").classList.remove("activeOption");
			document.getElementById("optionAbbreviated").classList.remove("activeOption");
			document.getElementById("optionSymbols").classList.add("activeOption");
		};
		
		document.getElementById("optionGuide").onclick = function () {
			let guideButton = document.getElementById("optionGuide");
			if(guideButton.classList.contains("activeOption")) {
				guideButton.classList.remove("activeOption");
				guideButton.innerHTML = "Piece Guides Off";
				shogi.setDrawPieceGuides(false);
			} else {
				guideButton.classList.add("activeOption");
				guideButton.innerHTML = "Piece Guides On";
				shogi.setDrawPieceGuides(true);
			}
			document.getElementById("optionList").style.display = "none";
		};
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
	
	drawPiece(piece, xOffset = 0, yOffset = 0) {
		const fontSize = this.boardStyle.pieceFontSize * this.pieceChars[piece.type].size;
		
		this.context.textBaseline = "middle";
		this.context.textAlign = "center";
		this.context.lineWidth = this.boardStyle.lineWidth/2;
		
		this.context.font = `${fontSize}px ${this.boardStyle.pieceFont}`;
		
		const x = (this.playerIsWhite ? piece.x : (8 - piece.x)) + xOffset;
		const y = (this.playerIsWhite ? (8 - piece.y) : piece.y) + yOffset;
		
		this.resetTransform();
		this.context.translate(
			this.boardStyle.x + (this.boardStyle.width  * (x + 0.5)),
			this.boardStyle.y + (this.boardStyle.height * (y + 0.5)) + this.boardStyle.pieceYOffset
		);
		if((piece.color.toLowerCase() === "white") !== this.playerIsWhite) {
			this.context.rotate(Math.PI);
		}
		
		var isHoldingPiece = (this.holdingPiece !== null && this.holdingPiece.x === x && this.holdingPiece.y === y);
		if(this.holdingPiece !== null && this.holdingPiece.fromHand) {
			isHoldingPiece = (piece.inHand && this.holdingPiece.x === x);
		}
		
		if(isHoldingPiece) {
			this.context.globalAlpha = 0.5;
		}
		
		// fill color
		this.context.fillStyle = this.pieceColor;
		this.context.beginPath();
		this.tracePieceOutline(piece);
		this.context.fill();
		
		// draw outline
		this.context.strokeStyle = this.darkBg;
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
		this.context.globalAlpha = 1;
	}
	
	drawHoldingPiece(piece) {
		
		const x = this.playerIsWhite ? piece.x : (8 - piece.x);
		const y = this.playerIsWhite ? (8 - piece.y) : piece.y;
		
		var isHoldingPiece = (this.holdingPiece !== null && this.holdingPiece.x === x && this.holdingPiece.y === y);
		if(this.holdingPiece !== null && this.holdingPiece.fromHand) {
			isHoldingPiece = (piece.inHand && this.holdingPiece.x === x);
		}
		if(!isHoldingPiece) {
			return;
		}
		
		this.context.textBaseline = "middle";
		this.context.textAlign = "center";
		this.context.lineWidth = this.boardStyle.lineWidth/2;
		
		const fontSize = this.boardStyle.pieceFontSize * this.pieceChars[piece.type].size;
		// piece on mouse coordinates
		
		this.context.translate(this.holdingPiece.mouseX, this.holdingPiece.mouseY);
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
			this.context.strokeRect(
				this.boardStyle.x + (this.boardStyle.width),
				this.boardStyle.y + (this.boardStyle.height * 10) - this.boardStyle.handOffset,
				this.boardStyle.width * 7,
				this.boardStyle.height
			);
		} else {
			// opponent's hand
			this.context.strokeRect(
				this.boardStyle.x + (this.boardStyle.width),
				this.boardStyle.y + (this.boardStyle.height * -1) - this.boardStyle.handOffset,
				this.boardStyle.width * 7,
				this.boardStyle.height
			);
		}
		
		if(piecesInHand) {
			
			for(var i=0; i<piecesInHand.length; i++) {
				var handPieceX = side === "white" ? i+1 : 7-i;
				var handPieceY = side === "white"
					? -2 + this.boardStyle.handOffset/this.boardStyle.height
					: 10 - this.boardStyle.handOffset/this.boardStyle.height;
				this.drawPiece({type: piecesInHand[i][0], x: handPieceX, y: handPieceY, color: side, inHand: true});

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
	
	drawPromotionMenu(piece) {
		// draw box
		this.resetTransform();
		
		this.context.fillStyle = this.highlightColor;
		this.context.lineWidth = this.boardStyle.lineWidth;
		this.context.lineCap = "round";
		
		const x = this.playerIsWhite ? piece.x : (8 - piece.x);
		const y = this.playerIsWhite ? (8 - piece.y) : piece.y;
		
		this.context.fillRect(
			this.boardStyle.x + ((x - 0.5) * this.boardStyle.width),
			this.boardStyle.y + (y * this.boardStyle.height),
			this.boardStyle.width * 2,
			this.boardStyle.height
		);

		this.context.strokeStyle = this.darkBg;
		this.context.strokeRect(
			this.boardStyle.x + ((x - 0.5) * this.boardStyle.width),
			this.boardStyle.y + (y * this.boardStyle.height),
			this.boardStyle.width * 2,
			this.boardStyle.height
		);

		this.drawPiece(piece, -0.5);
		piece.isPromoted = true;
		this.drawPiece(piece, 0.5);
		piece.isPromoted = false;
	}
	
	drawGuideCircle(x, y) {
		if(x >= 0 && x < 9 && y >= 0 && y < 9) {
			this.context.fillStyle = this.boardStyle.guideColor;
			this.context.beginPath();
			this.context.arc(
				this.boardStyle.x + ((x+0.5) * this.boardStyle.width), 
				this.boardStyle.y + ((y+0.5) * this.boardStyle.height),
				this.boardStyle.guideWidth/2, 0, 2 * Math.PI);
			this.context.fill();
		}
	}
	
	drawGuideLine(x, y, toX, toY) {
		if(x >= 0 && x <= 8 && y >= 0 && y <= 8 
		&& toX >= 0 && toX <= 8  && toY >= 0 && toY <= 8 ) {
			this.context.strokeStyle = this.boardStyle.guideColor;
			this.context.lineWidth = this.boardStyle.guideWidth;
			this.context.beginPath();
			this.context.moveTo(
				this.boardStyle.x + ((x+0.5) * this.boardStyle.width), 
				this.boardStyle.y + ((y+0.5) * this.boardStyle.height));
			this.context.lineTo(
				this.boardStyle.x + ((toX+0.5) * this.boardStyle.width), 
				this.boardStyle.y + ((toY+0.5) * this.boardStyle.height));
			this.context.stroke();
			this.context.strokeStyle = this.darkBg;
		}
	}
	
	drawPieceGuide(x, y, type, color, isPromoted) {
		this.resetTransform();
		var direction = ((color === "WHITE") === this.playerIsWhite) ? -1 : 1;
		
		if(isPromoted) {
			switch (type) {
				case "silver":
				case "knight":
				case "pawn":
				case "lance":
					this.drawGuideCircle(x - 1, y + direction);
					this.drawGuideCircle(x    , y + direction);
					this.drawGuideCircle(x + 1, y + direction);
					
					this.drawGuideCircle(x - 1, y);
					this.drawGuideCircle(x + 1, y);
					
					this.drawGuideCircle(x    , y - direction);
					break;
				case "rook":
					this.drawGuideLine(x - 0.5, y, 0, y);
					this.drawGuideLine(x + 0.5, y, 8, y);
					this.drawGuideLine(x, y - 0.5, x, 0);
					this.drawGuideLine(x, y + 0.5, x, 8);
					
					this.drawGuideCircle(x - 1, y + direction);
					this.drawGuideCircle(x + 1, y + direction);
					this.drawGuideCircle(x - 1, y - direction);
					this.drawGuideCircle(x + 1, y - direction);
					break;
				case "bishop":
					this.drawGuideLine(x - 0.5, y - 0.5, x - Math.min(x, y), y - Math.min(x, y));
					this.drawGuideLine(x + 0.5, y - 0.5, x + Math.min(8-x, y), y - Math.min(8-x, y));
					this.drawGuideLine(x - 0.5, y + 0.5, x - Math.min(x, 8-y), y + Math.min(x, 8-y));
					this.drawGuideLine(x + 0.5, y + 0.5, x + Math.min(8-x, 8-y), y + Math.min(8-x, 8-y));
					
					this.drawGuideCircle(x    , y + direction);
					this.drawGuideCircle(x - 1, y);
					this.drawGuideCircle(x + 1, y);
					this.drawGuideCircle(x    , y - direction);
					break;
			}
		} else {
			switch (type) {
				case "king":
					this.drawGuideCircle(x - 1, y + direction);
					this.drawGuideCircle(x    , y + direction);
					this.drawGuideCircle(x + 1, y + direction);
					
					this.drawGuideCircle(x - 1, y);
					this.drawGuideCircle(x + 1, y);
					
					this.drawGuideCircle(x - 1, y - direction);
					this.drawGuideCircle(x    , y - direction);
					this.drawGuideCircle(x + 1, y - direction);
					break;
				case "gold":
					this.drawGuideCircle(x - 1, y + direction);
					this.drawGuideCircle(x    , y + direction);
					this.drawGuideCircle(x + 1, y + direction);
					
					this.drawGuideCircle(x - 1, y);
					this.drawGuideCircle(x + 1, y);
					
					this.drawGuideCircle(x    , y - direction);
					break;
				case "silver":
					this.drawGuideCircle(x - 1, y + direction);
					this.drawGuideCircle(x    , y + direction);
					this.drawGuideCircle(x + 1, y + direction);
					
					this.drawGuideCircle(x - 1, y - direction);
					this.drawGuideCircle(x + 1, y - direction);
					break;
				case "knight":
					this.drawGuideCircle(x - 1, y + (2*direction));
					this.drawGuideCircle(x + 1, y + (2*direction));
					break;
				case "pawn":
					this.drawGuideCircle(x, y + direction);
					break;
					
				case "rook":
					this.drawGuideLine(x - 0.5, y, 0, y);
					this.drawGuideLine(x + 0.5, y, 8, y);
					this.drawGuideLine(x, y - 0.5, x, 0);
					this.drawGuideLine(x, y + 0.5, x, 8);
					break;
				case "lance":
					this.drawGuideLine(x, y + (direction > 0 ? 0.5 : -0.5), x, (direction > 0 ? 8 : 0));
					break;
					
				case "bishop":
					this.drawGuideLine(x - 0.5, y - 0.5, x - Math.min(x, y), y - Math.min(x, y));
					this.drawGuideLine(x + 0.5, y - 0.5, x + Math.min(8-x, y), y - Math.min(8-x, y));
					this.drawGuideLine(x - 0.5, y + 0.5, x - Math.min(x, 8-y), y + Math.min(x, 8-y));
					this.drawGuideLine(x + 0.5, y + 0.5, x + Math.min(8-x, 8-y), y + Math.min(8-x, 8-y));
					break;
			}	
		}

	}
	
	draw() {

		this.context.setTransform(this.canvas.width/this.boardWidth, 0, 0, this.canvas.height/this.boardHeight, 0, 0);
		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.context.fillStyle = this.lightBg;
		this.context.fillRect(0,0,this.boardWidth, this.boardHeight);
		
		const offset = this.boardStyle.lineWidth/2;
		
		this.context.lineWidth = this.boardStyle.lineWidth;
		this.context.lineCap = "round";
		
		// draw highlighted squares
		this.context.fillStyle = this.highlightColor;
		for(var i=0; i<this.highlightedSquares.length-1; i+=2) {
			
			var highlightX = this.playerIsWhite ? this.highlightedSquares[i] : 8-this.highlightedSquares[i];
			var highlightY = this.playerIsWhite ? 8-this.highlightedSquares[i+1] : this.highlightedSquares[i+1];
			
			this.context.fillRect(
				this.boardStyle.x + (highlightX * this.boardStyle.width), 
				this.boardStyle.y + (highlightY * this.boardStyle.height),
				this.boardStyle.width,
				this.boardStyle.height
			);
		}
		
		this.context.fillStyle = this.darkBg;
		this.context.strokeStyle = this.darkBg;
		
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
		
		// draw guides
		if(this.drawPieceGuides) {
			if(this.holdingPiece) {
				this.drawPieceGuide(
					this.holdingPiece.x, 
					this.holdingPiece.y,
					this.holdingPiece.type,
					this.holdingPiece.color,
					this.holdingPiece.isPromoted
				);

			} else if (this.hoverOverX !== null && this.hoverOverY !== null) {

				if(Array.isArray(this.pieces)) {
					for(var i = 0; i < this.pieces.length; i++) {
						if(this.pieceToCanvasXCoord(this.pieces[i].x) === this.hoverOverX 
						&& this.pieceToCanvasYCoord(this.pieces[i].y) === this.hoverOverY) {
							this.drawPieceGuide(
								this.hoverOverX, 
								this.hoverOverY,
								this.pieces[i].type,
								this.pieces[i].color,
								this.pieces[i].isPromoted,
							);

							this.drawPiece(this.pieces[i]);
						}
					}	
				}
			}
		}
		
		// draw piece being held
		if(Array.isArray(this.pieces)) {
			for(var i = 0; i < this.pieces.length; i++) {
				this.drawHoldingPiece(this.pieces[i]);
			}	
		}
		
		// draw hand piece being held
		if(this.piecesInHand) {
			if(this.playerIsWhite && Array.isArray(this.piecesInHand.white)) {
				for(var i=0; i<this.piecesInHand.white.length; i++) {
					var type = this.piecesInHand.white[i][0];
					var handPieceX = i+1;
					var handPieceY = -2 + this.boardStyle.handOffset/this.boardStyle.height;
					this.drawHoldingPiece({type: type, x: handPieceX, y: handPieceY, color: "white", inHand: true});
				}
			}

			if(!this.playerIsWhite && Array.isArray(this.piecesInHand.black)) {
				for(var i=0; i<this.piecesInHand.black.length; i++) {
					var type = this.piecesInHand.black[i][0];
					var handPieceX = 7-i;
					var handPieceY = 10 - this.boardStyle.handOffset/this.boardStyle.height;
					this.drawHoldingPiece({type: type, x: handPieceX, y: handPieceY, color: "black", inHand: true});
				}
			}
		}
		
		// draw promotion menu
		if(this.pieceNeedsPromotion && this.isOurPiece(this.pieceNeedsPromotion)) {
			this.drawPromotionMenu(this.pieceNeedsPromotion);
		}
	}
	
	handleUpdate(update, chessObj) {
		
		console.log("update", update);
		
		// check if this is the format we're expecting
		if(update.type === "shogiState") {
			chessObj.pieces = update.pieces;
			chessObj.piecesInHand = {white: [], black: []};
			chessObj.highlightedSquares = update.squaresToHighlight;
			chessObj.pieceNeedsPromotion = update.pendingPromotion;

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
			
			chessObj.handleResult(update.status, update.winner);
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
	
	isOurPiece(piece) {
		return (piece.color === "WHITE") === this.playerIsWhite;
	}
	
	onClick(event) {
		if ((event instanceof MouseEvent)) {
			event.preventDefault();
		}

		var chessObj = event.currentTarget.chessObj;
		
		if(chessObj.pieceNeedsPromotion) {
			return;
		}

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
		var isPromoted = null;
		var color = null;
		chessObj.pieces.forEach(piece => {
			if(x === chessObj.pieceToCanvasXCoord(piece.x) && 
			   y === chessObj.pieceToCanvasYCoord(piece.y)) {
				type = piece.type;
				isPromoted = piece.isPromoted;
				color = piece.color;
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
				isPromoted: isPromoted,
				color: color,
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

		var x = (event instanceof MouseEvent) ? event.clientX : 
			(event instanceof TouchEvent) ? event.changedTouches[0].clientX : 
			-1;
		var y = (event instanceof MouseEvent) ? event.clientY : 
			(event instanceof TouchEvent) ? event.changedTouches[0].clientY : 
			-1;

		var rect = chessObj.canvas.getBoundingClientRect();
		if(chessObj.holdingPiece !== null) {
			chessObj.holdingPiece.mouseX = (x - rect.left) * chessObj.boardWidth/chessObj.canvas.width;;
			chessObj.holdingPiece.mouseY = (y - rect.top) * chessObj.boardHeight/chessObj.canvas.height;
		}

		x = (x - rect.left) * chessObj.boardWidth/chessObj.canvas.width;
		x = (x - chessObj.boardStyle.x)/chessObj.boardStyle.width;
		y = (y - rect.top) * chessObj.boardHeight/chessObj.canvas.height;
		y = (y - chessObj.boardStyle.y)/chessObj.boardStyle.height;

		x = Math.floor(x);
		y = Math.floor(y);

		if(x >= 0 && x < 9 && y >= 0 && y < 9) {
			chessObj.hoverOverX = x;
			chessObj.hoverOverY = y;
		} else {
			chessObj.hoverOverX = null;
			chessObj.hoverOverY = null;
		}

		chessObj.draw();
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

		if(chessObj.pieceNeedsPromotion) {
			console.log("show promotion menu");
			// offset for promotion menu
			x += 0.5;
			let offset = chessObj.playerIsWhite ? 1 : -1;

			x = chessObj.pieceToCanvasXCoord(Math.floor(x));
			y = chessObj.pieceToCanvasYCoord(Math.floor(y));
			
			console.log(`click release (x,y) = (${x},${y}), piece= (${chessObj.pieceNeedsPromotion.x},${chessObj.pieceNeedsPromotion.y})`);

			if(y === chessObj.pieceNeedsPromotion.y && x === chessObj.pieceNeedsPromotion.x) {
				chessObj.sendMove(null, null, null, {promotion: false});
			}
			if(y === chessObj.pieceNeedsPromotion.y && x === chessObj.pieceNeedsPromotion.x + offset) {
				chessObj.sendMove(null, null, null, {promotion: true});
			}
		} else {					

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
					{
						type: chessObj.holdingPiece.type
					}
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
		if(fromHand) {
			// if we're dropping it, we need to include what piece it is
			dict.push(`type=${extra.type}`);
		} else if (extra && extra.fromX !== null && extra.fromY !== null) {
			// if we're not dropping it, we need the from coordinates
			dict.push(`from=${9 - this.xToFile(extra.fromX)},${9 - this.yToRankWestern(extra.fromY)}`);
		}
		
		if(extra && extra.promotion !== null) {
			dict.push(`promotion=${!!extra.promotion}`);
		}
		
		const data = dict.join('&');

		super.sendMove(data);
	}
	
	setDrawPieceGuides(v) {
		this.drawPieceGuides = !!v;
	}
}

const shogi = new Shogi();

document.title = "Shogi";