import java.io.*;
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    private String word = "";

    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n')
                line++;
            readch(br);
        }

        switch (peek) {
        // ... gestire i casi di ( ) { } + - * / ; , ... //

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

        // ... gestire i casi di || < > <= >= == <> ... //

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
                System.err.println("Erroneous character" + " after & : " + peek);
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
            if (Character.isLetter(peek)) {

                if (peek == 'a') {
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
                                    }
                                }
                            }
                        }
                    }
                } else if (peek == 't') {
                    readch(br);
                    if (peek == 'o') {
                        peek = ' ';
                        return Word.to;
                    }
                } else if (peek == 'i') {
                    readch(br);
                    if (peek == 'f') {
                        peek = ' ';
                        return Word.iftok;
                    }
                } else if (peek == 'e') {
                    readch(br);
                    if (peek == 'l') {
                        readch(br);
                        if (peek == 's') {
                            readch(br);
                            if (peek == 'e') {
                                peek = ' ';
                                return Word.elsetok;
                            }
                        }
                    } else if (peek == 'n') {
                        readch(br);
                        if (peek == 'd') {
                            peek = ' ';
                            return Word.end;
                        }
                    }
                } else if (peek == 'w') {
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
                            }
                        }
                    }
                } else if (peek == 'b') {
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
                                }
                            }
                        }
                    }
                } else if (peek == 'p') {
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
                                }
                            }
                        }
                    }
                } else if (peek == 'r') {
                    readch(br);
                    if (peek == 'e') {
                        readch(br);
                        if (peek == 'a') {
                            readch(br);
                            if (peek == 'd') {
                                peek = ' ';
                                return Word.read;
                            }
                        }
                    }
                }

                // ... gestire il caso degli identificatori e delle parole chiave //
                return null;

            } else if (Character.isDigit(peek)) {

                // ... gestire il caso dei numeri ... //

                return new NumberTok((int) peek);

            } else {
                System.err.println("Erroneous character: " + peek);
                return null;
            }
        }
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
