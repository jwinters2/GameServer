class Go extends Game {
	
	stones = null;
	territory = null;
	whiteToMove = null;
	lastPlacedStone = null;
	
	scoreInfo = {
		white: {
			prisoners: 0,
			territory: 0,
			komi: 0
		},
		black: {
			prisoners: 0,
			territory: 0,
			komi: 6.5
		}
	};
	
	playerIsWhite = null;
	
	lightBg = "#dfad68";
	darkBg = "#000000";
	
	whitePieceColor = "#ffffff";
	blackPieceColor = "#221100";
	pieceBorderColor = "#000000";
	neutralColor = "#908880"
	
	boardStyle = {
		x: 100, 
		y: 150, 
		width: 50, 
		height: 50,
		pieceRadius: 24,
		lineWidth: 2,
		scoreTextOffset: 10,
		passButtonRect: {x: 10, y: 10, w: 100, h: 50}
	};
	
	boardWidth = 0;
	boardHeight = 0;
	boardAspectRatio = 1;
	
	drawLiberties = false;
	drawTerritory = true;
	
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
		
		document.getElementById("optionLiberties").onclick = function () {
			let libertiesButton = document.getElementById("optionLiberties");
			if(libertiesButton.classList.contains("activeOption")) {
				libertiesButton.classList.remove("activeOption");
				libertiesButton.innerHTML = "Liberties Off";
				go.setDrawLiberties(false);
			} else {
				libertiesButton.classList.add("activeOption");
				libertiesButton.innerHTML = "Liberties On";
				go.setDrawLiberties(true);
			}
			document.getElementById("optionList").style.display = "none";
		};
		
		document.getElementById("optionTerritory").onclick = function () {
			let territoryButton = document.getElementById("optionTerritory");
			if(territoryButton.classList.contains("activeOption")) {
				territoryButton.classList.remove("activeOption");
				territoryButton.innerHTML = "Territory Off";
				go.setDrawTerritory(false);
			} else {
				territoryButton.classList.add("activeOption");
				territoryButton.innerHTML = "Territory On";
				go.setDrawTerritory(true);
			}
			document.getElementById("optionList").style.display = "none";
		};
	}
	
	resetTransform() {
		this.context.setTransform(this.canvas.width/this.boardWidth, 0, 0, this.canvas.height/this.boardHeight, 0, 0);
	}
	
	calculateBoardDimensions() {
		this.boardWidth  = (2 * this.boardStyle.x) + ((19-1) * this.boardStyle.width);
		this.boardHeight = (2 * this.boardStyle.y) + ((19-1) * this.boardStyle.height);
		this.boardAspectRatio = this.boardHeight/this.boardWidth;
	}
	
	getStoneAt(x, y, colorFilter) {
		if(this.stones) {
			for(var i=0; i<this.stones.length; i++) {
				if(this.stones[i].x === x && this.stones[i].y === y) {
					if(!colorFilter || this.stones[i].color === colorFilter) {
						// if there's no filter, or if the stone is the same color as the filter, return this stone
						return this.stones[i];
					} else {
						// otherwise return null
						return null;
					}
				}
			}
		}
		return null;
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
		this.context.fillStyle = pieceColor;
		
		// draw territory
		if(this.drawTerritory && this.territory && this.stones.length > 1) {
			
			this.context.globalAlpha = 0.25;
			
			const boundaryXs = [-1, 0, 1, 1, 1, 0, -1, -1, -1];
			const boundaryYs = [1, 1, 1, 0, -1, -1, -1, 0, 1];
			
			// which segment under the piece to draw territory for
			//   0 1
			// 7     2
			// 6     3
			//   5 4
			let drawSegments = [false, false, false, false, false, false, false, false];
			
			for(let i=0; i < 8; i++) {
				let bx = stone.x + boundaryXs[i];
				let by = stone.y + boundaryYs[i];
				
				if(bx >= 0 && bx < 19 && by >= 0 && by < 19
				&& this.territory[bx][by] === stone.color.toLowerCase()[0]) {
					if(i === 0) {
						drawSegments[0] = true;
						drawSegments[7] = true;
					} else {
						drawSegments[i] = true;
						drawSegments[i-1] = true;
					}
				}
			}
			
			const tx = (this.playerIsWhite ? 1: -1);
			const ty = (this.playerIsWhite ? -1: 1);
			
			// check for board edge left and right
			if(this.getStoneAt(stone.x - 1, stone.y, stone.color) || stone.x === 0) {
				if(drawSegments[0]) {
					drawSegments[7] = true;
				}
				if(drawSegments[5]) {
					drawSegments[6] = true;
				}
			}
			if(this.getStoneAt(stone.x + 1, stone.y, stone.color) || stone.x === 19 - 1) {
				if(drawSegments[1]) {
					drawSegments[2] = true;
				}
				if(drawSegments[4]) {
					drawSegments[3] = true;
				}
			}
			
			// check for board edge above and below
			if(this.getStoneAt(stone.x, stone.y - 1, stone.color) || stone.y === 0) {
				if(drawSegments[3]) {
					drawSegments[4] = true;
				}
				if(drawSegments[6]) {
					drawSegments[5] = true;
				}
			}
			if(this.getStoneAt(stone.x, stone.y + 1, stone.color) || stone.y === 19 - 1) {
				if(drawSegments[7]) {
					drawSegments[0] = true;
				}
				if(drawSegments[2]) {
					drawSegments[1] = true;
				}
			}
			

			for(let i=0; i < 8; i++) {
				if(drawSegments[i]) {
					this.context.beginPath();
					this.context.moveTo(0, 0);
					this.context.lineTo(boundaryXs[i]   * this.boardStyle.width/2 * tx, boundaryYs[i]   * this.boardStyle.height/2 * ty);
					this.context.lineTo(boundaryXs[i+1] * this.boardStyle.width/2 * tx, boundaryYs[i+1] * this.boardStyle.height/2 * ty);
					this.context.fill();
				}
			}
			this.context.globalAlpha = 1;
		}
		
		// fill color
		this.context.beginPath();
		this.context.arc(0, 0, this.boardStyle.pieceRadius, 0, 2 * Math.PI);
		this.context.fill();
		
		// draw outline
		this.context.strokeStyle = this.pieceBorderColor;
		this.context.beginPath();
		this.context.arc(0, 0, this.boardStyle.pieceRadius, 0, 2 * Math.PI);
		this.context.stroke();
		
		// draw liberty count
		if(this.drawLiberties) {
			this.context.font = "24px sans-serif";
			this.context.fillStyle = this.neutralColor;
			this.context.textBaseline = "middle";
			this.context.textAlign = "center";
			this.context.fillText(stone.liberties, 0, 0);
		}
		
		// draw marker if this is the last placed stone
		if(this.lastPlacedStone && this.lastPlacedStone.x === stone.x && this.lastPlacedStone.y === stone.y) {

			this.context.strokeStyle = (stone.color === "WHITE") ? this.blackPieceColor : this.whitePieceColor;	
			this.context.beginPath();
			this.context.arc(0, 0, this.boardStyle.pieceRadius/2, 0, 2 * Math.PI);
			this.context.stroke();
		}
		
		this.resetTransform();
	}
	
	drawScoreInfo(color, info) {
		
		this.context.font = "24px sans-serif";
		this.context.textAlign = "start";
		
		let x = this.boardStyle.x + this.boardStyle.scoreTextOffset;
		let y = this.boardStyle.y - this.boardStyle.scoreTextOffset;
		if((color === "white") === this.playerIsWhite) {
			// player's score: draw on the botton
			y = this.boardStyle.y + (this.boardStyle.height * (19-1)) + this.boardStyle.scoreTextOffset;;
			this.context.textBaseline = "top";
		} else {
			// opponen't score: draw on the top	
			this.context.textBaseline = "bottom";
		}
		
		let scoreText = `Score: ${info.prisoners + info.territory + info.komi}`;
		let prisonerText = `${info.prisoners} prisoner${info.prisoners === 1 ? "" : "s"}`;
		let territoryText = `${info.territory} territory`;
		
		this.context.fillStyle = (color === "white" ? this.whitePieceColor : this.blackPieceColor);
		this.context.fillText(`${scoreText} (${prisonerText}, ${territoryText})`, x, y);
	}
	
	draw() {

		this.context.setTransform(this.canvas.width/this.boardWidth, 0, 0, this.canvas.height/this.boardHeight, 0, 0);
		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.context.fillStyle = this.lightBg;
		this.context.fillRect(0,0,this.boardWidth, this.boardHeight);
		
		this.context.lineWidth = this.boardStyle.lineWidth;
		this.context.lineCap = "round";
		
		
		// draw territory
		if(this.drawTerritory && this.territory && this.stones.length > 1)  {
			this.context.globalAlpha = 0.25;
			for(var x=0; x<19; x++) {
				for(var y=0; y<19; y++) {
					if(this.territory[x][y] === 'b' || this.territory[x][y] === 'w') {
						const bx = (this.playerIsWhite ? x : (19 - 1 - x));
						const by = (this.playerIsWhite ? (19 - 1 - y) : y);
						
						this.context.fillStyle = (this.territory[x][y] === 'w' ? this.whitePieceColor : this.blackPieceColor);
						this.context.fillRect(
							this.boardStyle.x + (this.boardStyle.width * (bx - 0.5)),
							this.boardStyle.y + (this.boardStyle.height * (by - 0.5)),
							this.boardStyle.width,
							this.boardStyle.height
						);
					}
				}
			}
			this.context.globalAlpha = 1;
		}

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
		
		// draw score info
		this.drawScoreInfo("white", this.scoreInfo.white);
		this.drawScoreInfo("black", this.scoreInfo.black);
		
		// draw pass button
		this.context.fillStyle = this.lightBg;
		this.context.strokeStyle = this.darkBg;
		this.context.fillRect(
			this.boardStyle.x + (this.boardStyle.width * (19 - 1)) - this.boardStyle.passButtonRect.w -  this.boardStyle.passButtonRect.x,
			this.boardStyle.y + (this.boardStyle.height * (19 - 1)) + this.boardStyle.passButtonRect.y,
			this.boardStyle.passButtonRect.w,
			this.boardStyle.passButtonRect.h
		);
		this.context.strokeRect(
			this.boardStyle.x + (this.boardStyle.width * (19 - 1)) - this.boardStyle.passButtonRect.w -  this.boardStyle.passButtonRect.x,
			this.boardStyle.y + (this.boardStyle.height * (19 - 1)) + this.boardStyle.passButtonRect.y,
			this.boardStyle.passButtonRect.w,
			this.boardStyle.passButtonRect.h
		);

		this.context.textBaseline = "middle";
		this.context.textAlign = "center";
		this.context.fillStyle = this.darkBg;
		this.context.fillText(
			"Pass",
			this.boardStyle.x + (this.boardStyle.width * (19 - 1)) - (this.boardStyle.passButtonRect.w/2) -  this.boardStyle.passButtonRect.x,
			this.boardStyle.y + (this.boardStyle.height * (19 - 1)) + (this.boardStyle.passButtonRect.h/2) + this.boardStyle.passButtonRect.y,
		);
	}
	
	handleUpdate(update, chessObj) {
		
		console.log("update", update);
		
		// check if this is the format we're expecting
		if(update.type === "goState") {
			chessObj.stones = update.stones;
			chessObj.territory = update.territory;
			chessObj.lastPlacedStone = update.lastPlacedStone;
			
			chessObj.scoreInfo.white.prisoners = update.whitePrisoners;
			chessObj.scoreInfo.white.territory = update.whiteTerritoryScore;
			chessObj.scoreInfo.black.prisoners = update.blackPrisoners;
			chessObj.scoreInfo.black.territory = update.blackTerritoryScore;
			
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
		
		// check for click pass button
		let passX = chessObj.boardStyle.x + (chessObj.boardStyle.width * (19 - 1)) - chessObj.boardStyle.passButtonRect.w -  chessObj.boardStyle.passButtonRect.x;
		let passY = chessObj.boardStyle.y + (chessObj.boardStyle.height * (19 - 1)) + chessObj.boardStyle.passButtonRect.y;
		let passW = chessObj.boardStyle.passButtonRect.w;
		let passH = chessObj.boardStyle.passButtonRect.h;
		
		if(x >= passX && x <= passX + passW && y >= passY && y <= passY + passH) {
			
			chessObj.sendMove(null, null, "pass");
			
		} else {
			x = (x - chessObj.boardStyle.x)/chessObj.boardStyle.width;
			y = (y - chessObj.boardStyle.y)/chessObj.boardStyle.height;
				

			x = Math.round(x);
			y = Math.round(y);

			console.log(`place piece at ${chessObj.xToFile(x)},${chessObj.yToRank(y)}`);	

			chessObj.sendMove(x, y);
		}
		
		chessObj.draw();
	};
	
	sendMove(toX, toY, extra) {

		const dict = [];
		
		// destination
		if(toX !== null && toY !== null) {
			dict.push(`to=${19 - this.xToFile(toX)},${19 - this.yToRank(toY)}`);
		}
		if(extra === "pass") {
			dict.push("pass=true");
		}
		
		const data = dict.join('&');

		super.sendMove(data);
	}
	
	setDrawLiberties(v) {
		this.drawLiberties = !!v;
	}
	
	setDrawTerritory(v) {
		this.drawTerritory = !!v;
	}
}

const go = new Go();

document.title = "Go";