import java.io.*;
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';

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

            case '/':
                peek = ' ';
                return Token.div;

            case ';':
                peek = ' ';
                return Token.semicolon;

            case ',':
                peek = ' ';
                return Token.comma;

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character" + " after & : " + peek);
                    return null;
                }

            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character" + " after | : " + peek);
                    return null;
                }

            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Erroneous character" + " after = : " + peek);
                    return null;
                }

            case '<':
                readch(br);
                switch (peek) {
                    case '=':
                        peek = ' ';
                        return Word.le;
                    case '>':
                        peek = ' ';
                        return Word.ne;
                    default:
                        peek = ' ';
                        return Word.lt;
                }

            case '>':
                readch(br);
                switch (peek) {
                    case '=':
                        peek = ' ';
                        return Word.ge;
                    default:
                        peek = ' ';
                        return Word.gt;
                }

            case (char) -1:
                return new Token(Tag.EOF);

            default:
                // IDENTIFICATORI
                if (Character.isLetter(peek)) {
                    switch (peek) {
                        case 'a': // assign
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
                                                peek = ' ';
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
                                peek = ' ';
                                return Word.to;
                            } else
                                return mkIdentifier("t", br);

                        case 'i': // if
                            readch(br);
                            if (peek == 'f') {
                                peek = ' ';
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
                                            peek = ' ';
                                            return Word.elsetok;
                                        } else
                                            return mkIdentifier("els", br);
                                    } else
                                        return mkIdentifier("el", br);

                                case 'n': // end
                                    readch(br);
                                    if (peek == 'd') {
                                        peek = ' ';
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
                                            peek = ' ';
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
                                            peek = ' ';
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
                                            peek = ' ';
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
                                        peek = ' ';
                                        return Word.read;
                                    } else
                                        return mkIdentifier("rea", br);
                                } else
                                    return mkIdentifier("re", br);
                            } else
                                return mkIdentifier("r", br);

                        // Identificatore base
                        default:
                            return mkIdentifier("", br);
                    }

                } else if (Character.isDigit(peek)) {

                    // NUMERI
                    if (peek == '0') {
                        peek = ' ';
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
                    System.err.println("Erroneous character: " + peek);
                    return null;
                }
        }
    }

    public Token mkIdentifier(String t, BufferedReader br) {
        do {
            t += peek;
            readch(br);
        } while (('a' <= peek && peek <= 'z') || ('A' <= peek && peek <= 'Z') || ('0' <= peek && peek <= '9'));
        return new Word(Tag.ID, t);
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "./lexer.txt"; // il percorso del file da leggere
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
