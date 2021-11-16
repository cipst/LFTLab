import java.io.*;
import java.util.*;

public class Lexer3 {

    public static int line = 1;
    private char peek = ' ';
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        String s = "";
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n')
                line++;
            readch(br);
        }

        switch (peek) {

        case '_': // Potrebbe essere un identificatore che inizia con una sequenza di '_'
            String tmp = "_";
            readch(br);
            while (peek == '_') {
                tmp += peek;
                readch(br);
            }
            if (Character.isLetter(peek) || Character.isDigit(peek)) {
                return mkIdentifier(tmp, br);
            } else {
                System.err.println(ANSI_RED + "> Erroneous identifier. '_' without characters" + ANSI_RESET);
                return null;
            }

        case '!':
            peek = ' ';
            return Token.not;

        case '(':
            peek = ' ';
            return Token.lpt;

        case ')':
            peek = ' ';
            return Token.rpt;

        case '{':
            peek = ' ';
            return Token.lpg;

        case '}':
            peek = ' ';
            return Token.rpg;

        case '+':
            peek = ' ';
            return Token.plus;

        case '-':
            peek = ' ';
            return Token.minus;

        case '*':
            peek = ' ';
            return Token.mult;

        case ';':
            peek = ' ';
            return Token.semicolon;

        case ',':
            peek = ' ';
            return Token.comma;

        case '/':
            readch(br);
            if (peek == '/') { // Caso del commento su una linea //
                readch(br);
                while (peek != '\n' && peek != (char) -1)
                    readch(br);
                return lexical_scan(br);
            } else if (peek == '*') { // Caso del commento su più linee /* */
                boolean close = false;
                boolean trovatoStar = false;
                while (close == false && peek != (char) -1) {
                    readch(br);
                    if (peek == '*') {
                        trovatoStar = true;
                    } else if (peek == '/' && trovatoStar == true) {
                        close = true;
                    } else
                        trovatoStar = false;
                }
                if (peek == (char) -1) {
                    System.err.println(ANSI_RED + "> Error! Comment /* not closed." + ANSI_RESET);
                    return null;
                }
                peek = ' ';
                return lexical_scan(br);
            } else {
                return Token.div;
            }

        case '&':
            readch(br);
            if (peek == '&') {
                peek = ' ';
                return Word.and;
            } else {
                System.err.println(ANSI_RED + "> Erroneous character" + " after & : " + peek + ANSI_RESET);
                return null;
            }

        case '=':
            readch(br);
            if (peek == '=') {
                peek = ' ';
                return Word.eq;
            }

        case '|':
            readch(br);
            if (peek == '|') {
                peek = ' ';
                return Word.or;
            } else {
                System.err.println(ANSI_RED + "> Erroneous character" + " after | : " + peek + ANSI_RESET);
                return null;
            }

        case '<':
            readch(br);
            if (peek == '=') {
                peek = ' ';
                return Word.le;
            } else if (peek == '>') {
                peek = ' ';
                return Word.ne;
            } else {
                return Word.lt;
            }

        case '>':
            readch(br);
            if (peek == '=') {
                peek = ' ';
                return Word.ge;
            } else {
                return Word.gt;
            }

        case (char) -1:
            return new Token(Tag.EOF);

        default:
            if (Character.isLetter(peek)) {
                // ... gestire il caso degli identificatori e delle parole chiave //

                switch (peek) {
                case 'a':
                    readch(br);
                    if (peek == 's') {
                        readch(br);
                        if (peek == 's') {
                            readch(br);
                            if (peek == 'i') {
                                readch(br);
                                if (peek == 'g') {
                                    readch(br);
                                    if (peek == 'n') {
                                        readch(br);
                                        if (Character.isLetter(peek) || Character.isDigit(peek))
                                            return mkIdentifier("assign", br);
                                        // peek = ' ';
                                        return Word.assign;
                                    } else
                                        return mkIdentifier("assig", br);
                                } else
                                    return mkIdentifier("assi", br);
                            } else
                                return mkIdentifier("ass", br);
                        } else
                            return mkIdentifier("as", br);
                    } else
                        return mkIdentifier("a", br);

                case 't': // to
                    readch(br);
                    if (peek == 'o') {
                        readch(br);
                        if (Character.isLetter(peek) || Character.isDigit(peek))
                            return mkIdentifier("to", br);
                        // peek = ' ';
                        return Word.to;
                    } else
                        return mkIdentifier("t", br);

                case 'i': // if
                    readch(br);
                    if (peek == 'f') {
                        readch(br);
                        if (Character.isLetter(peek) || Character.isDigit(peek))
                            return mkIdentifier("if", br);
                        // peek = ' ';
                        return Word.iftok;
                    } else
                        return mkIdentifier("i", br);

                case 'e': // else || end
                    readch(br);
                    switch (peek) {
                    case 'l': // else
                        readch(br);
                        if (peek == 's') {
                            readch(br);
                            if (peek == 'e') {
                                readch(br);
                                if (Character.isLetter(peek) || Character.isDigit(peek))
                                    return mkIdentifier("else", br);
                                // peek = ' ';
                                return Word.elsetok;
                            } else
                                return mkIdentifier("els", br);
                        } else
                            return mkIdentifier("el", br);

                    case 'n': // end
                        readch(br);
                        if (peek == 'd') {
                            readch(br);
                            if (Character.isLetter(peek) || Character.isDigit(peek))
                                return mkIdentifier("end", br);
                            // peek = ' ';
                            return Word.end;
                        } else
                            return mkIdentifier("en", br);

                    default:
                        return mkIdentifier("e", br);
                    }

                case 'w': // while
                    readch(br);
                    if (peek == 'h') {
                        readch(br);
                        if (peek == 'i') {
                            readch(br);
                            if (peek == 'l') {
                                readch(br);
                                if (peek == 'e') {
                                    readch(br);
                                    if (Character.isLetter(peek) || Character.isDigit(peek))
                                        return mkIdentifier("while", br);
                                    // peek = ' ';
                                    return Word.whiletok;
                                }
                            } else
                                return mkIdentifier("whil", br);
                        } else
                            return mkIdentifier("wh", br);
                    } else
                        return mkIdentifier("w", br);

                case 'b': // begin
                    readch(br);
                    if (peek == 'e') {
                        readch(br);
                        if (peek == 'g') {
                            readch(br);
                            if (peek == 'i') {
                                readch(br);
                                if (peek == 'n') {
                                    readch(br);
                                    if (Character.isLetter(peek) || Character.isDigit(peek))
                                        return mkIdentifier("begin", br);
                                    // peek = ' ';
                                    return Word.begin;
                                } else
                                    return mkIdentifier("begi", br);
                            } else
                                return mkIdentifier("beg", br);
                        } else
                            return mkIdentifier("be", br);
                    } else
                        return mkIdentifier("b", br);

                case 'p': // print
                    readch(br);
                    if (peek == 'r') {
                        readch(br);
                        if (peek == 'i') {
                            readch(br);
                            if (peek == 'n') {
                                readch(br);
                                if (peek == 't') {
                                    readch(br);
                                    if (Character.isLetter(peek) || Character.isDigit(peek))
                                        return mkIdentifier("print", br);
                                    // peek = ' ';
                                    return Word.print;
                                } else
                                    return mkIdentifier("prin", br);
                            } else
                                return mkIdentifier("pri", br);
                        } else
                            return mkIdentifier("pr", br);
                    } else
                        return mkIdentifier("p", br);

                case 'r': // read
                    readch(br);
                    if (peek == 'e') {
                        readch(br);
                        if (peek == 'a') {
                            readch(br);
                            if (peek == 'd') {
                                readch(br);
                                if (Character.isLetter(peek) || Character.isDigit(peek))
                                    return mkIdentifier("read", br);
                                // peek = ' ';
                                return Word.read;
                            } else
                                return mkIdentifier("rea", br);
                        } else
                            return mkIdentifier("re", br);
                    } else
                        return mkIdentifier("r", br);

                    /* Sono un identificatore */
                default:
                    return mkIdentifier("", br);
                }

            } else if (Character.isDigit(peek)) {
                if (peek == '0') {
                    readch(br);
                    if ('0' <= peek && peek <= '9') {
                        System.err.println(ANSI_RED + "> Number cannot start with 0" + ANSI_RESET);
                        return null;
                    } else
                        s = "0";
                } else {
                    if ('1' <= peek && peek <= '9') {
                        while ('0' <= peek && peek <= '9') {
                            s += peek;
                            readch(br);
                        }
                    }
                }
                return new NumberTok(Integer.parseInt(s));
            } else {
                System.err.println(ANSI_RED + "> Erroneous character: " + peek + ANSI_RESET);
                return null;
            }
        }
    }

    public Token mkIdentifier(String t, BufferedReader br) {
        while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
            t += peek;
            readch(br);
        }
        return new Word(Tag.ID, t);
    }

    public static void main(String[] args) {
        Lexer3 lex = new Lexer3();
        String path = "./lexer.txt"; // Il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}