package model;

import chess.ChessGame;

public record GameData(Integer GameID, String whiteUsername, String blackUsername,
                       String gameName, ChessGame game) {
}
