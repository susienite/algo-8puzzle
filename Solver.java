
import edu.princeton.cs.algs4.In;       //TODO - for testing ONLY 
import edu.princeton.cs.algs4.StdOut;

import edu.princeton.cs.algs4.MinPQ;
import java.util.Comparator;
import java.util.Iterator;

public class Solver {

    private final int dimension; 
    private boolean canSolve;
    private SearchNode finalNode;

    private class SearchNode {

        final Board board;
        int moves; 
        int priority;
        SearchNode prev;

        public SearchNode(Board board, int moves) {
            this.board = board;
            this.moves = moves; 
            int man = board.manhattan(); 
            priority = man + moves;
            prev = null;
        }
    }

    // Custom comparator class implementing Comparator<SearchNode>
    private static class SearchNodeComparator implements Comparator<SearchNode> {

        @Override
        public int compare(SearchNode o1, SearchNode o2) {
            // Compare on priority 
            int o1Priority = o1.priority;
            int o2Priority = o2.priority;
            return Integer.compare(o1Priority, o2Priority);
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("Initial Board cannot be null");
        }

        dimension = initial.dimension();
        // check solvability 
        canSolve = isSolvable(initial);
        if (canSolve) { 

        // create two priority queues that compares SearchNodes 
        MinPQ<SearchNode> minPQ = new MinPQ<>(new SearchNodeComparator());
        //MinPQ<SearchNode> minPQ2 = new MinPQ<>(new SearchNodeComparator());

        // insert a SearchNode into minPQ (not a board)
        SearchNode root = new SearchNode(initial, 0);
        minPQ.insert(root);

        /* 
        // insert twin into minPQ 2
        Board twin = initial.twin();
        SearchNode root2 = new SearchNode(twin, moves);
        minPQ2.insert(root2);
        SearchNode minNode2 = minPQ2.delMin(); */
        SearchNode minNode = root; 
        while (!minPQ.isEmpty()/*&& (!minNode2.board.isGoal())*/) {
            //insert neighbors into PQ based on priority and remove the neighbor w min Priority 
            minNode = minPQ.delMin();
            if (minNode.board.isGoal()) {
                finalNode = minNode; 
                break; 
            }

            for (Board board : minNode.board.neighbors()) {
                // Optimize by seeing if neighbor is same as board of previous search node
                if (minNode.prev != null && board.equals(minNode.prev.board)) {
                    continue;
                } else {
                    //fix error with moves 
                    SearchNode newNode = new SearchNode(board, minNode.moves +1);
                    newNode.prev = minNode;
                    minPQ.insert(newNode);
                }

            }
            /* 
            for (Board board2 : minNode2.board.neighbors()) {
                // Optimize by seeing if neighbor is same as board of previous search node
                if (minNode2.prev != null && board2.equals(minNode2.prev.board)) {
                    continue;
                } else {
                    SearchNode newNode2 = new SearchNode(board2, moves);
                    newNode2.prev = minNode2;
                    minPQ2.insert(newNode2);
                }

            } */
            //minNode = minPQ.delMin();
            //minNode2 = minPQ2.delMin();
        }
        }
        /* 
        if (minNode.board.isGoal()) {
            finalNode = minNode;
        } else {
            canSolve = false;
        } */

    }

    private static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int[][] parseBoardString(Board initial) {
        String boardString = initial.toString();
        String[] lines = boardString.split("\n");
        int[] flat = new int[dimension*dimension];
        int size = lines.length; 
        int iter = 0; 

        for (int i = 1; i < size; i++) {
            // ignore first line with dimension 
            String[] oneLine = lines[i].split(" "); 

            for (String word: oneLine) {
                if (isInteger(word)){
                    flat[iter++] = Integer.parseInt(word.trim());
                }
            }
        }

        int[][] initialBoard = new int[dimension][dimension];
        int iterAgain = 0; 
        for (int i = 0; i < dimension; i++){
            for (int j = 0; j < dimension; j++) {
                initialBoard[i][j] = flat[iterAgain++]; 
            }
        }
        return initialBoard; 
    }

    
    // flatten the 2d board into 1d
    private int[] flatBoard(int[][] initialBoard) {
        int flattensize = dimension * dimension - 1; //exclude the 0 
        int[] flattenBoard = new int[flattensize];

        // flatten board
        int k = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (initialBoard[i][j] != 0) {
                    flattenBoard[k++] = initialBoard[i][j];
                }
            }
        }
        return flattenBoard;
    }

    // count number of inversions 
    //inversion = when a higher-numbered tile precedes a lower-numbered tile in the 1D array.
    private int inversions(int[] flattenBoard) {
        int numInversions = 0;
        for (int i = 0; i < flattenBoard.length - 1; i++) {
            for (int j = i + 1; j < flattenBoard.length; j++) {
                if (flattenBoard[i] > flattenBoard[j]) {
                    numInversions++;
                }
            }
        }
        return numInversions;
    }

    private int findZeroRow(int[][] board) {
        // find where the 0 is at
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] == 0) {
                    return i; 
                }
            }
        }
        // catch all 
        return -1;      
    }

    // helper function for isSolvable without parameters 
    private boolean isSolvable(Board initial) {
        int n = dimension; 
        int[][] initialBoard = parseBoardString(initial);
        int[] flattenBoard = flatBoard(initialBoard);
        int numInversions = inversions(flattenBoard);
        // find zero's row
        int zeroInRow = findZeroRow(initialBoard);

        // even dimension requirements 
        if (n % 2 == 0) {
            //Solvable if 0 on an even row counting from the bottom and the number of inversions is odd.
            int rowFromBottom = n - zeroInRow;

            if (rowFromBottom % 2 == 0) {
                if (numInversions % 2 != 0) {
                    return true;
                } else {
                    return false;
                }
            }
            // Solvable if 0 on an odd row counting from the bottom and the number of inversions is even.
            if (rowFromBottom % 2 != 0) {
                if (numInversions % 2 == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        } else {
            // For odd n, solvable if the number of inversions is even.
            return numInversions % 2 == 0;
        }

        // catch all
        return false;
    }


    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        //return finalNode != null; 
        return canSolve;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!canSolve) {
            return -1;
        }
        //System.out.println("determined solvability");
        SearchNode current = finalNode;
        int finalNumMoves = 0;
        while (current != null) {
            current = current.prev;
            finalNumMoves++;
        }
        return finalNumMoves - 1;
    }

    //deprecate? If we add this function into the constuctor 
    private static SearchNode reverseList(SearchNode last) {
        SearchNode next = null;
        SearchNode current = last;
        while (current != null) {
            SearchNode prev = current.prev;
            current.prev = next;
            next = current;
            current = prev;
        }
        return next;
    }

    // Inner class implementing Iterable<Item>
    private class BoardIterable implements Iterable<Board> {

        @Override
        public Iterator<Board> iterator() {
            return new BoardIterator();
        }

        // Iterator class implementing Iterator<Item>
        private class BoardIterator implements Iterator<Board> {

            private SearchNode node;

            public BoardIterator() {
                node = reverseList(finalNode);
            }

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public Board next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                Board nextBoard = node.board;
                node = node.prev;
                return nextBoard;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!canSolve) {
            return null;
        }
        return new BoardIterable();

    }

    // test client (see below) 
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }

        Board initial = new Board(tiles);
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            /*  for (Board board : solver.solution())
                StdOut.println(board); */
        }
    }

}
