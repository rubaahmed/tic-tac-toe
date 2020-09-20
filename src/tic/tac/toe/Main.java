package tic.tac.toe;


import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Scanner;
import static tic.tac.toe.Main.TicTacToe.Model.init;


public class Main {

    public static void main(String[] args) {
        init("_________");
    }

    static class TicTacToe {

        static class Model {
            public final static int X_WINS = 'X';
            public final static int O_WINS = 'O';
            public final static int DRAW = 'D';
            public final static int NOT_FINISHED = '#';

            private static char[][] stat;
            static int numX;
            static int numO;
            static boolean isXTurn;

            public static void init(String text) {
                numO = 0;
                numX = 0;
                stat = new char[3][3];
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        numX += text.charAt(0) == 'X' ? 1 : 0;
                        numO += text.charAt(0) == 'Y' ? 1 : 0;
                        stat[x][y] = (text.charAt(0) == '_') ? ' ' : text.charAt(0);
                        text = text.substring(1);
                    }
                }
                isXTurn = isXTurn();
            }

            public static void init() {
                init("_________");
            }

            private static boolean isXTurn() {
                return numX <= numO;
            }

            private static void move(int x, int y) {
                int[] coordinates = decode(x, y);
                if (isXTurn) {
                    stat[coordinates[0]][coordinates[1]] = 'X';
                    numX++;
                } else {
                    stat[coordinates[0]][coordinates[1]] = 'O';
                    numO++;
                }
                isXTurn = !isXTurn;
            }

            public static int[] encode(int x, int y) {
                return new int[]{y + 1, 3 - x};
            }

            public static boolean isEmpty(int x, int y) {
                return get(x, y) == ' ';
            }

            public static char get(int x, int y) {
                int[] coordinates = decode(x, y);
                return stat[coordinates[0]][coordinates[1]];
            }

            private static int[] decode(int x, int y) {
                return new int[]{3 - y, x - 1};
            }

            public static char checkForWin() {
                for (int i = 0; i < 3; i++) {
                    if (stat[i][0] == 'O' && stat[i][1] == 'O' && stat[i][2] == 'O') {
                        return O_WINS;
                    }
                    if (stat[0][i] == 'O' && stat[1][i] == 'O' && stat[2][i] == 'O') {
                        return O_WINS;
                    }
                    if (stat[i][0] == 'X' && stat[i][1] == 'X' && stat[i][2] == 'X') {
                        return X_WINS;
                    }
                    if (stat[0][i] == 'X' && stat[1][i] == 'X' && stat[2][i] == 'X') {
                        return X_WINS;
                    }
                }
                if (stat[0][0] == 'X' && stat[1][1] == 'X' && stat[2][2] == 'X') {
                    return X_WINS;
                }
                if (stat[2][0] == 'X' && stat[1][1] == 'X' && stat[0][2] == 'X') {
                    return X_WINS;
                }
                if (stat[0][0] == 'O' && stat[1][1] == 'O' && stat[2][2] == 'O') {
                    return O_WINS;
                }
                if (stat[2][0] == 'O' && stat[1][1] == 'O' && stat[0][2] == 'O') {
                    return O_WINS;
                }
                if (numX + numO == 9) {
                    return DRAW;
                }
                return NOT_FINISHED;
            }

            public static int[] hasAWinMove(char player) {
                int[] rowSpaceCoordinates = new int[2];
                int[] colSpaceCoordinates = new int[2];
                for (int i = 0; i < 3; i++) {
                    int numRow = 0;
                    boolean clearRow = true;
                    int numCol = 0;
                    boolean clearCol = true;
                    for (int j = 0; j < 3; j++) {
                        if (stat[i][j] != ' ' && stat[i][j] != player)
                            clearRow = false;
                        else if (stat[i][j] == player)
                            numRow++;
                        else {
                            rowSpaceCoordinates[0] = i;
                            rowSpaceCoordinates[1] = j;
                        }
                        if (stat[j][i] != ' ' && stat[j][i] != player)
                            clearRow = false;
                        else if (stat[j][i] == player)
                            numRow++;
                        else {
                            colSpaceCoordinates[1] = i;
                            colSpaceCoordinates[0] = j;
                        }
                    }
                    if (clearRow && numRow == 2)
                        return encode(rowSpaceCoordinates[0], rowSpaceCoordinates[1]);
                    if (clearCol && numCol == 2)
                        return encode(colSpaceCoordinates[0], colSpaceCoordinates[1]);
                }
                return null;
            }

            public static void undo(int... position) {
                position = decode(position[0], position[1]);
                stat[position[0]][position[1]] = ' ';
                if (isXTurn) {
                    numO--;
                } else {
                    numX--;
                }
                isXTurn = !isXTurn;
            }

            public static int getEmptyNum() {
                return 9 - numX - numO;
            }

            public static int[] getEmptyFields() {
                int max = 2 * getEmptyNum();
                int[] empty = new int[max];
                int count = 0;
                for (int i = 1; i < 4; i++) {
                    for (int j = 1; j < 4; j++) {
                        if (get(i, j) == ' ') {
                            empty[count] = i;
                            empty[++count] = j;
                            count++;
                            if (count >= max) {
                                return empty;
                            }
                        }
                    }
                }
                System.out.println("Some thing went wrong!!!");
                return empty;
            }

        }

        static class Controller {

            final static int USER = 0;
            final static int EASY = 1;
            final static int MEDIUM = 2;
            final static int HARD = 3;

            static Player x;
            static Player o;

            public static void init(String stat) {
                int[] players = readCommand();
                if (players == null) return;
                createUser(players[0], 'X');
                createUser(players[1], 'O');
                Model.init(stat);
                View.printStat();
                readMove();
                init("_________");
            }

            private static void readMove() {
                x.select('X');
                char winner = Model.checkForWin();
                if (winner == Model.NOT_FINISHED)
                    o.select('O');
                else {
                    View.printFinish(winner);
                    return;
                }
                if (Model.checkForWin() == Model.NOT_FINISHED)
                    readMove();
                else View.printFinish(winner);
            }

            private static int[] readCommand() {
                try {
                    System.out.print("Input command: ");
                    Scanner in = new Scanner(System.in);
                    String command = in.nextLine();
                    switch (command) {
                        case "exit":
                            return null;
                        default:
                    }
                    in = new Scanner(command);
                    command = in.next();
                    if (command.equals("start")) {
                        String X = in.next();
                        String O = in.next();
                        int x = parseUser(X);
                        int o = parseUser(O);
                        if (x != -1 && o != -1) {
                            return new int[]{x, o};
                        }
                    }
                } catch (NoSuchElementException e) {
                }
                System.out.println("Bad parameters!");
                return readCommand();
            }

            private static int parseUser(String u) {
                switch (u) {
                    case "user":
                        return USER;
                    case "easy":
                        return EASY;
                    case "medium":
                        return MEDIUM;
                    case "hard":
                        return HARD;
                    default:
                        return -1;
                }
            }

            private static void createUser(int num, char xo) {
                Player temp = null;
                switch (num) {
                    case USER:
                        temp = new User();
                        break;
                    case EASY:
                        temp = new Easy();
                        break;
                    case MEDIUM:
                        temp = new Medium();
                        break;
                    case HARD:
                        temp = new Hard();
                    default:
                        break;
                }
                switch (xo) {
                    case 'X':
                        Controller.x = temp;
                        break;
                    case 'O':
                        Controller.o = temp;
                        break;
                }
            }
        }

        static class View {
            public final static int NAN = 0;
            public final static int OUT_RANGE = 1;
            public final static int OCCUPIED = 2;

            private static void printStat() {
                System.out.println("---------");
                for (int i = 1; i < 4; i++) {
                    System.out.print("| ");
                    for (int j = 1; j < 4; j++) {
                        System.out.print(Model.get(i, j) + " ");
                    }
                    System.out.println("|");
                }
                System.out.println("---------");
            }

            private static void printWarning(int i) {
                switch (i) {
                    case NAN:
                        System.out.println("You should enter numbers!");
                        break;
                    case OUT_RANGE:
                        System.out.println("Coordinates should be from 1 to 3!");
                        break;
                    case OCCUPIED:
                        System.out.println("This cell is occupied! Choose another one!");
                        break;
                }
            }

            public static void printFinish(char winner) {
                System.out.println(winner == Model.DRAW ? "Draw" : winner == Model.X_WINS ? "X wins" : "O wins");
            }
        }

        static class Easy implements Player {
            @Override
            public void select(char type) {
                int[] coordinates = select();
                Model.move(coordinates[0], coordinates[1]);
                System.out.println("Making move level \"easy\"");
                View.printStat();
            }

            public static int[] select() {
                SecureRandom random = new SecureRandom();
                int coordinates[] = new int[2];
                int x = 0, y = 0;
                x = random.nextInt(3);
                y = random.nextInt(3);
                coordinates = Model.encode(x, y);
                if (Model.isEmpty(coordinates[0], coordinates[1]))
                    return coordinates;
                return select();
            }
        }

        static class Medium implements Player {

            @Override
            public void select(char type) {
                int win[] = Model.hasAWinMove(type);
                if (win == null) {
                    win = Model.hasAWinMove(type == 'X' ? 'O' : 'X');
                    if (win == null) {
                        win = Easy.select();
                    }
                }
                Model.move(win[0], win[1]);
                System.out.println("Making move level \"medium\"");
                View.printStat();
            }
        }

        static class Hard implements Player {
            @Override
            public void select(char type) {
                Move move = minmax();
                Model.move(move.x, move.y);
                System.out.println("Making move level \"medium\"");
                View.printStat();
            }

            public static Move minmax() {
                Move[] moves = new Move[Model.getEmptyNum()];
                int[] positions = Model.getEmptyFields();
                for (int i = 0; i < moves.length; i++) {
                    moves[i] = new Move(positions[2 * i], positions[2 * i + 1]);
                }
                for (Move move : moves) {
                    int val = move.generateVal();
                    if (val == 1) {
                        return move;
                    }
                }
                return moves[0];
            }

            static class Move {
                int x;
                int y;
                int val;

                public Move(int... position) {
                    x = position[0];
                    y = position[1];
                }

                public int generateVal() {
                    Model.move(x, y);
                    if (Model.checkForWin() == Model.DRAW) {
                        Model.undo(x, y);
                        return 0;
                    }
                    if (Model.checkForWin() == Model.NOT_FINISHED) {
                        int val = minmax().val;
                        Model.undo(x, y);
                        return -val;
                    }
                    Model.undo(x, y);
                    return 1;
                }
            }
        }

        static class User implements Player {

            @Override
            public void select(char type) {
                boolean check = false;
                int x = 0, o = 0;
                String text = "";
                try {
                    System.out.print("Enter the Coordinates: ");
                    text = new Scanner(System.in).nextLine();
                } catch (NoSuchElementException e) {
                    System.out.println("\nGame not finished");
                    System.exit(1);

                }
                Scanner scanner = new Scanner(text);
                String moveX = "", moveY = "";
                try {
                    moveX = scanner.next();
                    moveY = scanner.next();
                    x = Integer.parseInt(moveX);
                    o = Integer.parseInt(moveY);

                } catch (NoSuchElementException | NumberFormatException e) {
                    View.printWarning(View.NAN);
                    select(type);
                    return;
                }
                if (x > 3 || o > 3 || x < 1 || o < 1) {
                    View.printWarning(View.OUT_RANGE);
                    select(type);
                    return;
                }
                if (!Model.isEmpty(x, o)) {
                    View.printWarning(View.OCCUPIED);
                    select(type);
                    return;
                }
                Model.move(Integer.parseInt(moveX), Integer.parseInt(moveY));
                View.printStat();
            }
        }

        static interface Player {
            public void select(char type);
        }

    }
}