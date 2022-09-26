package chess

class Board(private val firstPlayer: String, private val secondPlayer: String) {

    // creating a variable to store current position when special move(em-passant) is open.
    private var emPassant = " "

    // Initialize default board
    private val chessboard = mutableListOf(
        mutableListOf("  +---+---+---+---+---+---+---+---+"),
        mutableListOf("8 |", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|"),
        mutableListOf("  +---+---+---+---+---+---+---+---+"),
        mutableListOf("7 |", "B", "|", "B", "|", "B", "|", "B", "|", "B", "|", "B", "|", "B", "|", "B", "|"),
        mutableListOf("  +---+---+---+---+---+---+---+---+"),
        mutableListOf("6 |", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|"),
        mutableListOf("  +---+---+---+---+---+---+---+---+"),
        mutableListOf("5 |", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|"),
        mutableListOf("  +---+---+---+---+---+---+---+---+"),
        mutableListOf("4 |", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|"),
        mutableListOf("  +---+---+---+---+---+---+---+---+"),
        mutableListOf("3 |", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|"),
        mutableListOf("  +---+---+---+---+---+---+---+---+"),
        mutableListOf("2 |", "W", "|", "W", "|", "W", "|", "W", "|", "W", "|", "W", "|", "W", "|", "W", "|"),
        mutableListOf("  +---+---+---+---+---+---+---+---+"),
        mutableListOf("1 |", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|", " ", "|"),
        mutableListOf("  +---+---+---+---+---+---+---+---+"),
        mutableListOf("    a   b   c   d   e   f   g   h")
    )

    // initialize keys to map move input from user
    private val cMap = mapOf(
        '1' to 15,
        '2' to 13,
        '3' to 11,
        '4' to 9,
        '5' to 7,
        '6' to 5,
        '7' to 3,
        '8' to 1,
        'a' to 1,
        'b' to 3,
        'c' to 5,
        'd' to 7,
        'e' to 9,
        'f' to 11,
        'g' to 13,
        'h' to 15,
    )

    fun printBoard(): String {
        var output = ""
        for (i in chessboard.indices) {
            for (j in chessboard[i].indices) {
                output += chessboard[i][j] + " "
            }
            output += "\n"
        }
        println(output)
        return output
    }


    private fun executeMove(move: String) {
        chessboard[cMap[move[3]]!!][cMap[move[2]]!!] = chessboard[cMap[move[1]]!!][cMap[move[0]]!!]
        chessboard[cMap[move[1]]!!][cMap[move[0]]!!] = " "
    }

    // check the board to know if the current move is valid, then executes or return invalid
    fun moveOnBoard(move: String, currentPlayer: String): Boolean {
        val initialPosition = chessboard[cMap[move[1]]!!][cMap[move[0]]!!]
        val finalPosition = chessboard[cMap[move[3]]!!][cMap[move[2]]!!]
        val pawn: String
        val color: String
        val isNotMovingForward: Boolean
        val wrongAdvance: Boolean
        val wrongDiagonal: Boolean
        val valid2Step: Boolean

        if (currentPlayer == firstPlayer) {
            pawn = "W"
            color = "white"
            isNotMovingForward = move[3] <= move[1]
            wrongAdvance = move[1] == '2' && move[3] - move[1] > 2 || move[1] != '2' && move[3] - move[1] > 1
            wrongDiagonal = move[2] != move[0] && finalPosition != "B"
            valid2Step = move[1] == '2' && move[3] - move[1] == 2
        } else {
            pawn = "B"
            color = "black"
            isNotMovingForward = move[3] >= move[1]
            wrongAdvance = move[1] == '7' && move[1] - move[3] > 2 || move[1] != '7' && move[1] - move[3] > 1
            wrongDiagonal = move[2] != move[0] && finalPosition != "W"
            valid2Step = move[1] == '7' && move[1] - move[3] == 2
        }

        return when {
            // when initial position does not contain owner's pawn (no pawn prompt)
            initialPosition != pawn -> {
                println("No $color pawn at ${move.substring(0, 2)}")
                println("$currentPlayer's turn:")
                false
            }

            // when final position contains owners pawn (invalid)
            finalPosition == pawn -> {
                invalidPrompt()
                playPrompt(currentPlayer)
                false
            }

            // when moving straight and final position is not empty (invalid)
            move[2] == move[0] && finalPosition != " " -> {
                invalidPrompt()
                playPrompt(currentPlayer)
                false
            }

            // when moving diagonally & it's beyond 1 step (invalid)
            move[2] != move[0] && move[2] - move[0] > 1 || move[2] - move[0] < -1 -> {
                invalidPrompt()
                playPrompt(currentPlayer)// when moving diagonally & it's beyond 1 step (invalid)
                false
            }

            // when moving in wrong direction or beyond valid steps
            isNotMovingForward || wrongAdvance -> {
                invalidPrompt()
                playPrompt(currentPlayer)
                false
            }

            // checking for em-passant opportunity
            isEmPassantCapture(move, currentPlayer) -> {
                executeMove(move)
                chessboard[cMap[emPassant[1]]!!][cMap[emPassant[0]]!!] = " "
                printBoard()
                true
            }

            // when moving diagonally & it is not a capture move (invalid)
            wrongDiagonal -> {
                invalidPrompt()
                false
            }

            // when on pawn home rank & moving 2 steps (capture final position as en-passant)
            valid2Step -> {
                emPassant = move.substring(2)
                executeMove(move)
                printBoard()
                true
            }
            else -> {
                emPassant = " "
                executeMove(move)
                printBoard()
                true
            }
        }
    }

    private fun isEmPassantCapture(move: String, currentPlayer: String): Boolean {
        val rightSide = "${move[0] + 1}${move[1]}" // for checking "em-passant"
        val leftSide = "${move[0] - 1}${move[1]}" // for checking "em-passant"
        val aToG = 'a'..'g' // to ensure no out-of-bounds error while checking "em-passant"
        val bToH = 'b'..'h' // to ensure no out-of-bounds error while checking "em-passant"

        val rank: Char = if (currentPlayer == firstPlayer) {
            '5'
        } else {
            '4'
        }

        // when on capture rank & in specified file & previous move is sideways & moving towards there (boolean)
        return move[1] == rank && move[0] in aToG && rightSide == emPassant && move[2] == move[0] + 1 ||
                move[1] == rank && move[0] in bToH && leftSide == emPassant && move[2] == move[0] - 1
    }

    fun winCondition(move: String, currentPlayer: String): String {
        val finalRank = move[3]
        var output = ""
        val thisPlayer: String
        val winRank: Char
        if (currentPlayer == firstPlayer) {
            thisPlayer = firstPlayer
            winRank = '8'
        } else {
            thisPlayer = secondPlayer
            winRank = '1'
        }

        if (currentPlayer == thisPlayer) {
            if (finalRank == winRank || noMoreOpponentPawn(thisPlayer)) {
                output = thisPlayer
            } else if (isStaleMate(currentPlayer)) {
                output = "stalemate"
            }
        }
        return output
    }

    private fun noMoreOpponentPawn(thisPlayer: String): Boolean {
        var isEmpty = true
        val opponentPawn: String = if (thisPlayer == firstPlayer) {
            "B"
        } else {
            "W"
        }
        for (rank in chessboard) if (rank.contains(opponentPawn)) isEmpty = false
        return isEmpty
    }

    private fun isStaleMate(currentPlayer: String): Boolean {
        var output = true
        val opponentPawn: String
        val myPawn: String
        if (currentPlayer == firstPlayer) {
            myPawn = "W"
            opponentPawn = "B"
        } else {
            myPawn = "B"
            opponentPawn = "W"
        }
        for (i in chessboard.indices) {
            val aToG = 0..chessboard[i].size - 3
            val bToH = 3..chessboard[i].size - 2
            var forwardCondition: Boolean
            var diagonalCondition: Boolean

            for (j in aToG) {
                if (chessboard[i][j] == opponentPawn) {

                    if (currentPlayer == firstPlayer) {
                        forwardCondition = chessboard[i + 2][j] == " "
                        diagonalCondition = chessboard[i + 2][j + 2] == myPawn
                    } else {
                        forwardCondition = chessboard[i - 2][j] == " "
                        diagonalCondition = chessboard[i - 2][j + 2] == myPawn
                    }

                    if (forwardCondition || diagonalCondition) {
                        output = false
                        continue
                    }
                }
            }

            for (j in bToH) {
                if (chessboard[i][j] == opponentPawn) {

                    if (currentPlayer == firstPlayer) {
                        forwardCondition = chessboard[i + 2][j] == " "
                        diagonalCondition = chessboard[i + 2][j - 2] == myPawn
                    } else {
                        forwardCondition = chessboard[i - 2][j] == " "
                        diagonalCondition = chessboard[i - 2][j - 2] == myPawn
                    }

                    if (forwardCondition || diagonalCondition) {
                        output = false
                        continue
                    }
                }
            }
        }
        return output
    }
}

//----------------------------------------------------------------------------------------------

class Move(private val move: String) {
    private val moveRegex = Regex("[a-h][1-8][a-h][1-8]")

    fun isMoveValid(): Boolean {
        return !(move == "exit" || !moveRegex.matches(move) || move[2] > move[0] + 1 || move[2] < move[0] - 1)
    }
}

//----------------------------------------------------------------------------------------------

fun playPrompt(currentPlayer: String) {
    println("$currentPlayer's turn:")
}

fun invalidPrompt() {
    println("Invalid Input")
}


//------------------------------------------------------------------------------------------------------------


fun main() {
    println("Pawns-Only Chess")
    println("First Player's name:")
    val firstPlayer = readln()
    println("Second Player's name:")
    val secondPlayer = readln()
    var currentPlayer = firstPlayer

    val pChessBoard = Board(firstPlayer, secondPlayer)
    pChessBoard.printBoard()
    playPrompt(currentPlayer)

    do {
        val move = readln()
        val currentMove = Move(move)
        if (!currentMove.isMoveValid()) {
            if (move == "exit") {
                break // break do while loop when input is "exit"
            } else {
                invalidPrompt()
                playPrompt(currentPlayer)
            }
        } else {
            if (pChessBoard.moveOnBoard(move, currentPlayer)) {
                when (pChessBoard.winCondition(move, currentPlayer)) {
                    firstPlayer -> {
                        println("White Wins!")
                        break
                    }
                    secondPlayer -> {
                        println("Black Wins!")
                        break
                    }
                    "stalemate" -> {
                        println("Stalemate!")
                        break
                    }
                }
                currentPlayer = if (currentPlayer == firstPlayer) secondPlayer
                else firstPlayer
            }
            playPrompt(currentPlayer)
        }
    } while (true)
    println("Bye!")


}