package net.wintersjames.gameserver.Games.Shogi;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Bishop;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Piece;
import net.wintersjames.gameserver.Games.GameState;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Gold;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.King;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Knight;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Lance;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.JumpMove;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.LineMove;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.LionMove;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.MoveType;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Pawn;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Rook;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Silver;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.VariantPiece;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 *
 * @author james
 */
public class ShogiState extends GameState implements Serializable {
	
	Logger logger = LoggerFactory.getLogger(ShogiState.class);
	
	final public int boardWidth;
	final public int promotionWidth;
	final protected String configFile;
	
	private List<Piece> pieces;
	private Map<String, Integer> whiteHand;
	private Map<String, Integer> blackHand;
	private boolean whiteToMove;
	
	private List<Integer> squaresToHighlight;
	
	// the piece we're waiting on whether or not to promote, if there is one
	private Piece pendingPromotion;
	private Piece pendingSecondMove;
	private int[] lionOriginalPos;
	private Piece lastCapturedPiece;
	
	public ShogiState() {
		super("shogiState");
		this.pieces = new ArrayList<>();
		this.whiteHand = new HashMap<>();
		this.blackHand = new HashMap<>();
		
		this.boardWidth = 9;
		this.promotionWidth = 3;
		this.configFile = "shogi.json";
	
		this.whiteToMove = true;
		this.pendingPromotion = null;
		this.pendingSecondMove = null;
		this.lionOriginalPos = null;
		this.lastCapturedPiece = null;
		this.squaresToHighlight = new ArrayList<>();
		
		this.setupPieces();
	}
	
	// for subclasses to call
	public ShogiState(int width, int promotionWidth, String config) {
		super("shogiState");
		this.pieces = new ArrayList<>();
		this.whiteHand = new HashMap<>();
		this.blackHand = new HashMap<>();
		
		this.boardWidth = width;
		this.promotionWidth = promotionWidth;
		this.configFile = config;
	
		this.whiteToMove = true;
		this.pendingPromotion = null;
		this.pendingSecondMove = null;
		this.lionOriginalPos = null;
		this.lastCapturedPiece = null;
		this.squaresToHighlight = new ArrayList<>();
		
		this.setupPieces();
	}
	
	public ShogiState(ShogiState other) {
		super("shogiState");
		this.pieces = this.getDeepCopy(other.pieces);
		this.whiteHand = new HashMap<>(other.whiteHand);
		this.blackHand = new HashMap<>(other.blackHand);
	
		this.whiteToMove = other.whiteToMove;
		this.pendingPromotion = other.pendingPromotion;
		this.pendingSecondMove = other.pendingSecondMove;
		
		if(other.lionOriginalPos != null) {
			this.lionOriginalPos = Arrays.copyOf(other.lionOriginalPos, other.lionOriginalPos.length);
		} else {
			this.lionOriginalPos = null;
		}
		
		this.lastCapturedPiece = other.lastCapturedPiece;
		this.squaresToHighlight = other.squaresToHighlight;
		
		this.boardWidth = other.boardWidth;
		this.promotionWidth = other.promotionWidth;
		this.configFile = other.configFile;
	}
	
	protected void setupPieces() {
		try {
			logger.info("reading config file {}", this.configFile);
			File shogiFile = ResourceUtils.getFile("classpath:static/gameres/shogi/" + this.configFile);
			Scanner scanner = new Scanner(shogiFile);
			
			String contents = "";
			while(scanner.hasNextLine()) {
				contents += scanner.nextLine();
			}
			
			scanner.close();
			JSONObject shogiJson = new JSONObject(contents);
			
			logger.info("shogiJson");
			logger.info(shogiJson.toString(2));
			
			JSONObject setup = shogiJson.getJSONObject("setup");
			for(String key: setup.keySet()) {
				
				int y = Integer.parseInt(key);
				List<Object> piecesInRow = setup.getJSONArray(key).toList();
				for(int x = 0; x < piecesInRow.size(); x++) {
					if(piecesInRow.get(x) != null && piecesInRow.get(x) instanceof String) {
						this.pieces.add(
							setupNewPiece(
								(String)piecesInRow.get(x), 
								x, 
								y, 
								Piece.Color.WHITE,
								shogiJson.getJSONObject("pieces"))
						);
						this.pieces.add(
							setupNewPiece(
								(String)piecesInRow.get(x), 
								this.boardWidth - 1 - x,
								this.boardWidth - 1 - y, 
								Piece.Color.BLACK,
								shogiJson.getJSONObject("pieces"))
						);
					}
				}
			}
			
		} catch (IOException e) {
			logger.info("{} failed to parse", this.configFile);
			logger.info(e.getMessage());
		}
	}
	
	protected Piece setupNewPiece(String piece, int x, int y, Piece.Color color, JSONObject pieceConfig) {
		
		Piece retval = switch(piece.toLowerCase()) {
			case "king"		-> new King(x, y, color);
			//case "gold"		-> new Gold(x, y, color);
			//case "silver"	-> new Silver(x, y, color);
			//case "knight"	-> new Knight(x, y, color);
			//case "lance"	-> new Lance(x, y, color);
			//case "rook"		-> new Rook(x, y, color);
			//case "bishop"	-> new Bishop(x, y, color);
			//case "pawn"		-> new Pawn(x, y, color);
			default -> new VariantPiece(x, y, color, piece);
		};
		
		if(retval instanceof VariantPiece vp) {
			JSONObject pieceInfo = pieceConfig.getJSONObject(piece);
			
			parsePieceMove(pieceInfo.getJSONObject("moves"), vp, false);
			
			vp.setIsRoyal(pieceInfo.optBoolean("isKing", false));
			vp.setSubstantial(pieceInfo.optBoolean("substantial", true));
			
			vp.setTradeDisabled(pieceInfo.optBoolean("noTrades", false));
			vp.setTradeDisabledOnPromote(pieceInfo.optBoolean("noTradesOnPromote", false));
			
			vp.setCanPromote(pieceInfo.optString("promotesTo", null) != null);
			vp.setCanPromoteOnFinalRank(pieceInfo.optBoolean("canPromoteOnFinalRank", false));
			
			if(vp.getCanPromote()) {
				
				vp.setPromotesTo(pieceInfo.getString("promotesTo"));
				
				JSONObject promotionPieceInfo = pieceConfig.getJSONObject(pieceInfo.getString("promotesTo"));
			
				parsePieceMove(promotionPieceInfo.getJSONObject("moves"), vp, true);
				
				vp.setPromotesToRoyal(promotionPieceInfo.optBoolean("isKing", false));
			}
		}
		
		if(piece.equalsIgnoreCase("drunk elephant")) {
			logger.info("drunk elephant");
			logger.info(retval.toString());
		}
		
		return retval;
	}
	
	private void parsePieceMove(JSONObject moves, VariantPiece vp, boolean isPromotion) {
		List<MoveType> parsedMoves = parsePieceMoves(moves);
		if(isPromotion) {
			vp.addPromotedMoves(parsedMoves);
		} else {
			vp.addMoves(parsedMoves);
		}
		
		JSONArray lionMoves = moves.optJSONArray("lion");
		if(lionMoves != null) {
			for(int i = 0; i < lionMoves.length(); i++) {
				
				JSONObject firstMoves = lionMoves.getJSONObject(i).getJSONObject("first");
				JSONObject secondMoves = lionMoves.getJSONObject(i).getJSONObject("second");
				
				List<MoveType> parsedFirstMoves = parsePieceMoves(firstMoves);
				List<MoveType> parsedSecondMoves = parsePieceMoves(secondMoves);
				
				List<MoveType> parsedLionMoves = new ArrayList<>();
				for(MoveType firstMove: parsedFirstMoves) {
					parsedLionMoves.add(new LionMove(firstMove, parsedSecondMoves));
				}
				
				// get the set of moves
				if(isPromotion) {
					vp.addPromotedMoves(parsedLionMoves);
				} else {
					vp.addMoves(parsedLionMoves);
				}
			}
		}
	}
	
	private List<MoveType> parsePieceMoves(JSONObject moves) {
		
		List<MoveType> retval = new ArrayList<>();
		
		JSONArray jumpMoves = moves.optJSONArray("jump");
		if(jumpMoves != null) {
			for(int i = 0; i < jumpMoves.length(); i++) {
				retval.add(new JumpMove(
					jumpMoves.getJSONArray(i).getInt(0),
					jumpMoves.getJSONArray(i).getInt(1)
				));
			}
		}


		JSONArray lineMoves = moves.optJSONArray("line");
		if(lineMoves != null) {
			for(int i = 0; i < lineMoves.length(); i++) {
				retval.add(new LineMove(
					lineMoves.getJSONArray(i).getInt(0),
					lineMoves.getJSONArray(i).getInt(1)
				));
			}
		}
		
		return retval;
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public Map<String, Integer> getWhiteHand() {
		return whiteHand;
	}

	public Map<String, Integer> getBlackHand() {
		return blackHand;
	}
	
	public boolean isWhiteToMove() {
		return whiteToMove;
	}
	
	public Piece getPieceAt(int x, int y) {
		for(Piece piece: pieces) {
			if(piece.getX() == x && piece.getY() == y) {
				return piece;
			}
		}
		return null;
	}
	
	public void captureAt(int x, int y) {
		
		Piece toRemove = getPieceAt(x, y);
		if(toRemove != null) {
			
			logger.info("removing {}", toRemove);
			
			// removefrom board
			pieces.remove(toRemove);
			
			// add to hand
			toRemove.setInHand(true);
			toRemove.toggleColor();
			
			Map<String, Integer> hand = toRemove.getColor() == Piece.Color.WHITE ? this.whiteHand : this.blackHand;

			if(hand.containsKey(toRemove.getType())) {
				int count = hand.get(toRemove.getType());
				hand.put(toRemove.getType(), count + 1);
			} else {
				hand.put(toRemove.getType(), 1);
			}
		}
	}
	
	public boolean canMove(int fromX, int fromY, int toX, int toY, boolean isWhite) {
		
		// if we're expecting a second move we have to move that piece
		// according to its second moveset
		if(this.pendingSecondMove != null) {
			Piece toMove = getPieceAt(fromX, fromY);
			if(toMove != this.pendingSecondMove) {
				return false;
			}
		}
		
		// check if the position is in bounds
		if(fromX < 0 || fromX >= boardWidth || fromY < 0 || fromY >= boardWidth) {
			logger.info("piece is out of bounds");
			return false;
		}
		
		Piece pieceToMove = getPieceAt(fromX, fromY);
		if(pieceToMove != null) {
			logger.info("piece: {}, color: {}", pieceToMove.getType(), pieceToMove.getColor());
		} else {		
			logger.info("piece to move is null");
		}

		
		Piece.Color color = isWhite ? Piece.Color.WHITE : Piece.Color.BLACK;
		
		// a piece can't exist here, can't be the other person's color,
		// and it can't be the opponent's turn
		if(pieceToMove == null 
			|| pieceToMove.getColor() != color
			|| (color == Piece.Color.WHITE) != this.whiteToMove) {
			return false;
		}
		
		// check if the destination is in bounds
		if(toX < 0 || toX >= boardWidth || toY < 0 || toY >= boardWidth) {
			logger.info("piece destination out of bounds");
			return false;
		}

		Piece pieceToCapture = getPieceAt(toX, toY);
		// if there's a piece in the square we're moving to, and it's the same
		// color as the moving piece, it's an invalid move
		if(pieceToCapture != null && pieceToCapture.getColor() == color && pieceToCapture != pieceToMove) {
			logger.info("cannot capture our own pieces");
			return false;
		}
		
		// check for no-trade pieces (i.e. lion) trading
		if(!pieceToMove.canTrade(this, pieceToCapture)) {
			return false;
		}
		
		// if we just lost our non-tradable piece, we can't take the same piece
		if(pieceToCapture != null && pieceToCapture.isTradeDisabled() 
		&& lastCapturedPiece != null && lastCapturedPiece.getColor() != pieceToCapture.getColor()) {
			String pieceToCaptureType = pieceToCapture.getIsPromoted() ? pieceToCapture.getPromotesTo() : pieceToCapture.getType();
			String lastCapturedPieceType = lastCapturedPiece.getIsPromoted() ? lastCapturedPiece.getPromotesTo() : lastCapturedPiece.getType();
			if(lastCapturedPieceType.equals(pieceToCaptureType)) {
				return false;
			}
		}

		// we're not allowed to move into check
		ShogiState nextState = new ShogiState(this);
		nextState.move(fromX, fromY, toX, toY);
		if(nextState.isInCheck(color)) {
			logger.info("(move) king is in check");
			return false;
		}

		// if this is the second move of a lion move, use the special moveset for it instead of the regular one
		if(this.pendingSecondMove != null) {
			return pieceToMove.canLionMove(toX, toY, this);
		}
		
		return pieceToMove.canMove(toX, toY, this);
	}
	
	public boolean canDrop(int toX, int toY, String pieceType, boolean isWhite) {
		
		// if we're expecting a second move we can't drop
		if(this.pendingSecondMove != null) {
			return false;
		}
		
		// check if the position is in bounds
		if(toX < 0 || toX >= boardWidth || toY < 0 || toY >= boardWidth) {
			logger.info("piece is out of bounds");
			return false;
		}
		
		// we can't drop on another piece
		if(getPieceAt(toX, toY) != null) {
			return false;
		}
		
		Piece.Color color = isWhite ? Piece.Color.WHITE : Piece.Color.BLACK;
		
		// it can't be the opponent's turn
		if((color == Piece.Color.WHITE) != this.whiteToMove) {
			return false;
		}
		
		Map<String, Integer> hand = color == Piece.Color.WHITE ? this.whiteHand : this.blackHand;
		if(!hand.containsKey(pieceType)) {
			// we can't drop a piece we don't have
			return false;
		}
		
		int lastRank = (color == Piece.Color.WHITE ? boardWidth-1 : 0);
		int penultimateRank = (color == Piece.Color.WHITE ? boardWidth-2 : 1);
		
		// pawns, knights and lances can't drop on the last rank
		if(toY == lastRank && (pieceType.equals("pawn") || pieceType.equals("knight") || pieceType.equals("lance"))) {
			return false;
		}
		
		// knights also can't drop on the second to last rank
		if(toY == penultimateRank && pieceType.equals("knight")) {
			return false;
		}
		
		// pawns can't drop on a file that already has a pawn
		if(pieceType.equals("pawn")) {
			for(int y = 0; y < boardWidth; y++) {
				Piece p = getPieceAt(toX, y);
				if(p instanceof Pawn && !p.getIsPromoted() && p.getColor() == color) {
					return false;
				}
			}
		}
		
		// pawns can't checkmate
		if(pieceType.equals("pawn")) {
			ShogiState nextState = new ShogiState(this);
			nextState.drop(toX, toY, pieceType, isWhite);
			nextState.nextMove();
			if(!nextState.hasLegalMove()) {
				logger.info("illegal checkmate by pawn drop");
				return false;
			}
		}
		
		// we're not allowed to drop a piece that leaves us in check
		ShogiState nextState = new ShogiState(this);
		nextState.drop(toX, toY, pieceType, isWhite);
		if(nextState.isInCheck(color)) {
			logger.info("(drop) king is in check");
			return false;
		}


		return true;
	}
	
	public boolean isSquareUnderAttack(int x, int y, Piece.Color attackingColor) {
		
		for(Piece piece: this.pieces) {
			if(piece.getColor() == attackingColor && piece.canMove(x, y, this)) {
				logger.info("attacking piece {}", piece);
				return true;
			}
		}
		return false;
	}
	
	public boolean isInCheck(Piece.Color colorInCheck) {
		
		// if we have more than one king, we're not in check
		if(numRoyal(colorInCheck) > 1) {
			return false;
		}
	
		Piece.Color attackingColor = (colorInCheck == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE);
		for(Piece piece: pieces) {
			if(piece.isRoyal() && piece.getColor() == colorInCheck) {
				logger.info("king piece: {}", piece);
				// in some variants a player might have more than one king-like piece
				// so if any of them are safe then we're good
				if(!isSquareUnderAttack(piece.getX(), piece.getY(), attackingColor)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public int numRoyal(Piece.Color color) {
		
		int retval = 0;
		
		for(Piece piece: pieces) {
			if(piece.isRoyal() && piece.getColor() == color) {
				retval++;
			}
		}
		
		logger.info("numRoyal = {}", retval);
		return retval;
	}
	
	public void move(int fromX, int fromY, int toX, int toY) {
		logger.info("moving ({},{}) to ({},{})", fromX, fromY, toX, toY);
		if(fromX != toX || fromY != toY) {
			this.lastCapturedPiece = getPieceAt(toX, toY);
			this.captureAt(toX, toY);
		}
		Piece toMove = getPieceAt(fromX, fromY);
		toMove.move(toX, toY, this);
		
		if(isPromotionMandatory(toX, toY, toMove.getType(), toMove.getColor())) {
			toMove.promote();
		}
		
		if(toMove.getSecondLionMoves() != null) {
			this.lionOriginalPos = new int[]{fromX, fromY};
			this.pendingSecondMove = toMove;
		} else {
			this.lionOriginalPos = null;
			this.pendingSecondMove = null;
		}
	}
	
	public void drop(int toX, int toY, String pieceType, boolean isWhite) {
		
		Piece.Color color = isWhite ? Piece.Color.WHITE : Piece.Color.BLACK;
		
		Piece droppedPiece = switch (pieceType) {
			case "pawn" -> new Pawn(toX, toY, color);
			case "rook" -> new Rook(toX, toY, color);
			case "bishop" -> new Bishop(toX, toY, color);
			case "lance" -> new Lance(toX, toY, color);
			case "knight" -> new Knight(toX, toY, color);
			case "silver" -> new Silver(toX, toY, color);
			case "gold" -> new Gold(toX, toY, color);
			default -> null;
		};
		
		this.pieces.add(droppedPiece);
		Map<String, Integer> hand = (color == Piece.Color.WHITE ? this.whiteHand : this.blackHand);
		
		int count = hand.get(pieceType) - 1;
		if(count == 0) {
			hand.remove(pieceType);
		} else {
			hand.put(pieceType, count);
		}
		
		this.pendingSecondMove = null;
	}
	
	public boolean isPromotionOptional(int fromX, int fromY, int toX, int toY) {
		return isPromotionOptional(fromX, fromY, toX, toY, null);
	}
	
	public boolean isPromotionOptional(int fromX, int fromY, int toX, int toY, Piece toCapture) {
		
		Piece toPromote = getPieceAt(toX, toY);
		
		// null pieces, already-promoted pieces, gold generals and kings can't promote
		if(toPromote == null 
			|| toPromote.getIsPromoted() 
			|| !toPromote.getCanPromote()) {
			logger.info("promotion not allowed here, isPromoted={}, type={}",
				toPromote == null ? null : toPromote.getIsPromoted(),
				toPromote == null ? null : toPromote.getType()
			);
			return false;
		}
		
		// piece must either start or end in their promotion zone
		if(toPromote.getColor() == Piece.Color.WHITE) {
			if(fromY < boardWidth - promotionWidth && toY < boardWidth - promotionWidth) {
				logger.info("y ({} & {}) >= 6", fromY, toY);
				return false;
			}
		} else {
			if(fromY >= promotionWidth && toY >= promotionWidth) {
				logger.info("y ({} & {}) <= 2", fromY, toY);
				return false;
			}
		}
		
		if(isPromotionMandatory(toX, toY, toPromote.getType(), toPromote.getColor())) {
			logger.info("promotion is mandatory");
			return false;
		}
		
		return true;
	}
	public boolean isPromotionMandatory(int toX, int toY, Piece piece, Piece.Color color) {
		return isPromotionMandatory(toX, toY, piece.getType(), color);
	}
	
	public boolean isPromotionMandatory(int toX, int toY, String pieceType, Piece.Color color) {
		
		int lastRank = (color == Piece.Color.WHITE ? boardWidth-1 : 0);
		
		// pawns, knights and lances can't drop on the last rank
		if(toY == lastRank && (pieceType.equals("pawn") || pieceType.equals("knight") || pieceType.equals("lance"))) {
			return true;
		}
		
		int penultimateRank = (color == Piece.Color.WHITE ? boardWidth-2 : 1);
		
		// knights also can't drop on the second to last rank
		if(toY == penultimateRank && pieceType.equals("knight")) {
			return true;
		}
		
		return false;
	}
	
	public boolean hasLegalMove() {
		logger.info("checking for legal move");
		
		Piece.Color colorToMove = this.whiteToMove ? Piece.Color.WHITE : Piece.Color.BLACK;
		
		// check for moving pieces
		for(Piece piece: this.pieces) {
			if(piece.getColor() == colorToMove && piece.hasLegalMove(this)) {
				logger.info("piece {} has legal move", piece);
				return true;
			}
		}
		
		// check for drops
		Map<String, Integer> hand = (colorToMove == Piece.Color.WHITE ? this.whiteHand : this.blackHand);
		for(String pieceType: hand.keySet()) {
			for(int x = 0; x < boardWidth; x++) {
				for(int y = 0; y < boardWidth; y++) {
					if(canDrop(x, y, pieceType, colorToMove == Piece.Color.WHITE)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public void setPendingPromotion(int x, int y) {
		this.pendingPromotion = getPieceAt(x, y);
	}
	
	public void resetPendingPromotion() {
		this.pendingPromotion = null;
	}
	
	public void setSquaresToHighlight(int...coords) {
		this.squaresToHighlight.clear();
		for(int coord: coords) {
			this.squaresToHighlight.add(coord);
		}
	}
	
	public void addSquaresToHighlight(int...coords) {
		
		boolean alreadyPresent = false;
		
		for(int i = 0; i < coords.length - 1; i += 2) {
			for(int j = 0; j < squaresToHighlight.size() - 1; j += 2) {
				if(coords[i] == squaresToHighlight.get(j) && coords[i + 1] == squaresToHighlight.get(j + 1)) {
					alreadyPresent = true;
				}
			}
		}
		
		if(!alreadyPresent) {
			for(int coord: coords) {
				this.squaresToHighlight.add(coord);
			}
		}
	}

	public List<Integer> getSquaresToHighlight() {
		return squaresToHighlight;
	}

	public Piece getPendingPromotion() {
		return pendingPromotion;
	}

	public Piece getPendingSecondMove() {
		return pendingSecondMove;
	}

	public Piece getLastCapturedPiece() {
		return lastCapturedPiece;
	}

	public int[] getLionOriginalPos() {
		return lionOriginalPos;
	}
	
	public void nextMove() {
		this.whiteToMove = !this.whiteToMove;
	}	

	private List<Piece> getDeepCopy(List<Piece> piecesToCopy) {
		ArrayList<Piece> retval = new ArrayList<>();
		for(Piece p: piecesToCopy) {
			if(p != null) {
				retval.add(p.deepCopy());
			}
		}
		return retval;
	}
}
