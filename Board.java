
import edu.princeton.cs.algs4.In;       //for testing ONLY 
import java.util.Iterator;
import java.util.Random;

public class Board {

    private final int[][] board;
    private final int dimension;
    private int zeroInRow; //do i need this in the constructor?
    private int zeroInCol;
    private int numNeighbors; //do I need this?

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("tiles cannot be null");
        }
       
        this.board = tiles;
        this.dimension = tiles.length;
       
        if (dimension <= 1) {
            throw new IllegalArgumentException("puzzle is 1 x 1 which is solved");
        }

        // find where the 0 is at
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (tiles[i][j] == 0) {
                    zeroInRow = i;
                    zeroInCol = j;
                    break; 
                }
            }
        }

        // determine the number of neighbors
        int i = 0;
        if (zeroInRow - 1 >= 0) {
            i++;
        }
        if (zeroInRow + 1 < dimension) {
            i++;
        }
        if (zeroInCol - 1 >= 0) {
            i++;
        }
        if (zeroInCol + 1 < dimension) {
            i++;
        }
        numNeighbors = i;
    }

    // string representation of this board
    public String toString() {
        String firstLine = dimension + "\n";
        String boardLine = "";
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j <= dimension; j++) {
                if (j == dimension) {
                    boardLine += "\n";
                    break;
                }
                boardLine += " " + board[i][j];
            }
        }
        String rep = firstLine + boardLine;
        return rep;
    }

    // board dimension n
    public int dimension() {
        return this.dimension;
    }

    // goal of the tile ar (row, col)
    private int goal(int i, int j) {
        int goal = (dimension * i) + (j + 1);
        return goal;
    }

    // number of tiles out of place
    public int hamming() {
        int outOfPlaceCounter = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] != goal(i, j) && board[i][j] != 0) {    //can I optimize?
                    outOfPlaceCounter += 1;
                }
            }
        }
        return outOfPlaceCounter;
    }

    // distance from goal for a tile 
    private int distance(int i, int j, int item) {
        int row = (item-1) / dimension;
        int col = (item-1) % dimension; 
        int dis = Math.abs(row - i) + Math.abs(col - j);
        return dis;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int sum = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                int item = board[i][j];
                int goal = goal(i, j);
                if (item != goal) {
                    //ignore empty tile 
                    if (item != 0) {
                        sum += distance(i, j, item);
                    }
                }
            }
        }
        return sum;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return manhattan() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        // referencing same object
        if (this == y) {
            return true;
        }
        // do we have same class?
        if (y == null || this.getClass() != y.getClass()) {
            return false;
        }

        // if so, check that board is equal to other board 
        Board newBoard = (Board) y;
        int[][] newBoardArray = newBoard.board;
        // check for same size first
        if (this.dimension != newBoardArray.length) {
            return false;
        }
        // same size, same tiles?
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] != newBoardArray[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Create a neighbor board by replicating old board and swapping 0 with item at (rol,col)
    private Board neighborBoard(int rol, int col) {
        int[][] newTiles = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                newTiles[i][j] = this.board[i][j];
            }
        }
        newTiles[zeroInRow][zeroInCol] = newTiles[rol][col];
        newTiles[rol][col] = 0;
        return new Board(newTiles);
    }

    // Array of all neighboring boards
    private Board[] getNeighbors() {
        // Should findiing the zero be here?!
        int i = 0;
        int prevRow = zeroInRow - 1;
        int afterRow = zeroInRow + 1;
        int prevCol = zeroInCol - 1;
        int afterCol = zeroInCol + 1;
        Board[] boards = new Board[numNeighbors];
        if (prevRow >= 0) {
            boards[i++] = neighborBoard(prevRow, zeroInCol);
        }
        if (afterRow < dimension) {
            boards[i++] = neighborBoard(afterRow, zeroInCol);
        }
        if (prevCol >= 0) {
            boards[i++] = neighborBoard(zeroInRow, prevCol);
        }
        if (afterCol < dimension) {
            boards[i++] = neighborBoard(zeroInRow, afterCol);
        }
        return boards;
    }

    // Inner class implementing Iterable<Item>
    private class BoardIterable implements Iterable<Board> {

        @Override
        public Iterator<Board> iterator() {
            return new BoardIterator();
        }

        // Iterator class implementing Iterator<Item>
        private class BoardIterator implements Iterator<Board> {

            private int index;
            private Board[] boards;

            public BoardIterator() {
                this.index = 0;
                this.boards = getNeighbors();
            }

            @Override
            public boolean hasNext() {
                return index < numNeighbors;
            }

            @Override
            public Board next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                return boards[index++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    // Iterable for all neighboring boards
    public Iterable<Board> neighbors() {
        return new BoardIterable();
    }

    // flatten the n by n board to a n^2 array 
    private int[] flatBoard() {
        int flattensize = dimension * dimension ; 
        int[] flattenBoard = new int[flattensize];

        // flatten board
        int k = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                flattenBoard[k++] = board[i][j];
            }
        }
        return flattenBoard;
    }
 
    // an inverted pair does not contain 0 
    private int[] invertPair(int[] flattenBoard) {
        int[] inverted = new int[2];
        for (int i = 0; i < flattenBoard.length - 1; i++) {
            for (int j = i + 1; j < flattenBoard.length; j++) {
                // do not compare the zero 
                if (flattenBoard[i] == 0 || flattenBoard[j] == 0) {
                    continue; 
                }
                if (flattenBoard[i] > flattenBoard[j]) {
                    inverted[0] = flattenBoard[i]; 
                    inverted[1] = flattenBoard[j]; 
                    return inverted; 
                }
            }
        }
        // no inverted numbers
        inverted[0] = -1; 
        inverted[1] = -1;
        return inverted;
    }

    // find the coordinates given a value
    private int[] findNumCoord(int num) {
        int[] point = new int[2];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] == num) {
                    point[0] = i;
                    point[1] = j; 
                    return point; 
                }
            }
        } 
        // cannot find point 
        point[0] = -1; 
        point[1] = -1;
        return point; 
    }
 
    // if no inverted pair, find a pair of tiles (no 0) to swap
    private int[] randomTiles() {

        int size = dimension*dimension;
        int[] random1dPair = new int[2];
        int zeroIn1d = goal(zeroInRow, zeroInCol);
        int pos = zeroIn1d -1; 
        // System.out.println("what is zero? " + pos);
        // next 2 after 0 
        if (pos-1 <= 0 && pos+2 < size) {
            //System.out.println("is closer left");
            random1dPair[0] = zeroIn1d+1;
            random1dPair[1] = zeroIn1d+2; 
        } 
        // first 2 before 0 
        if (pos-1 > 0 && pos+2 >= size) {
           // System.out.println("is closer right");
            random1dPair[0] = zeroIn1d-1;
            random1dPair[1] = zeroIn1d-2; 
        } 
        return random1dPair; 
        
    }

    private int[] convert1dTo2d(int item){
        int row = (item-1) / dimension;
        int col = (item-1) % dimension;
        int[] point = new int[2];
        point[0] = row;
        point[1] = col; 
        return point; 
    }

    
    // a board that is obtained by exchanging any pair of tiles (not include 0)
    // flatten board, find a pair of inversions, search for coordinates, swap
    public Board twin() {
        int[] board1d = flatBoard(); 
        int[] pair = invertPair(board1d); 
        int[] point1 = null; 
        int[] point2 = null;
        if (pair[0] == -1) {
            // if no inverted pair, do a random pair 
          //  System.out.println("is random tile");
            pair = randomTiles();
            point1 = convert1dTo2d(pair[0]); 
            point2 = convert1dTo2d(pair[1]); 
        } else{
          //  System.out.println("is inverted pair");
            point1 = findNumCoord(pair[0]); 
            point2 = findNumCoord(pair[1]); 
        }
        
       // System.out.println("point1 is " + point1[0] + "," + point1[1]);
      //  System.out.println("point2 is " + point2[0] + "," + point2[1]);

        // create a replica of original board
        int[][] newTiles = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                newTiles[i][j] = this.board[i][j];
            }
        }
        // swap the tiles at the 2 points
        int temp = newTiles[point1[0]][point1[1]];
        newTiles[point1[0]][point1[1]] = newTiles[point2[0]][point2[1]];
        newTiles[point2[0]][point2[1]] = temp;
        return new Board(newTiles);
    } 

    // get board for isSolvable() in Solver.java
    private int[][] getBoard() {
        return this.board;
    }

    // get zero for isSolvable() in Solver.java
    private int[] getZero() {
        int[] zeroPosition = new int[2];
        zeroPosition[0] = zeroInRow;
        zeroPosition[1] = zeroInCol;
        return zeroPosition;
    }

    // unit testing (not graded)
    public static void main(String[] args) {

        // read in the board specified in the filename
        In in = new In(args[0]);
        System.out.println(args[0] + "");
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }

        // solve the slider puzzle
        Board initial = new Board(tiles);

        System.out.println(initial.toString());
        System.out.println("dimension = " + initial.dimension());
        System.out.println("hamming = " + initial.hamming());
        System.out.println("manhattan = " + initial.manhattan());
        System.out.println("goal = " + initial.isGoal());
        System.out.println("equals = " + initial.equals(null));
        System.out.println("equals = " + initial.equals("1 2 3 0"));
        System.out.println("equals = " + initial.equals(initial));

        Board twin = initial.twin();
        System.out.println(twin.toString());

        /* 
        In in2 = new In(args[1]);
        System.out.println(args[1] + "");
        int n2 = in2.readInt();
        int[][] tiles2 = new int[n2][n2];
        for (int i = 0; i < n2; i++) {
            for (int j = 0; j < n2; j++) {
                tiles2[i][j] = in2.readInt();
            }
        }
        Board board2 = new Board(tiles2);
        System.out.println("equals" + initial.equals(board2)); */

         
        for (Board aboard : initial.neighbors()) {
            System.out.println(aboard.toString());
            System.out.println("equals neighbor? " + initial.equals(aboard));
        }
        System.out.println("number of neighbors = " + initial.numNeighbors);
        

    }

}
