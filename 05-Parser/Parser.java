import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    /* FORMATTAZIONE OUTPUT SU PROMPT */
    public static String ANSI_BOLD = " \u001b[1m ";
    public static String ANSI_RED = " \u001B[31m ";
    public static String ANSI_GREEN = " \u001b[32m ";
    public static String ANSI_CYAN = " \u001b[36m ";
    public static String ANSI_RESET_ERROR = " \u001B[0m " + ANSI_RED;
    public static String ANSI_RESET = " \u001B[0m ";

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println(ANSI_CYAN + "token = " + look + ANSI_RESET_ERROR);
    }

    void error(String s) {
        throw new Error("\n\tnear line " + lex.line + ": " + ANSI_RED + s + ANSI_RESET);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else
            error("syntax error");
    }

    public void start() {
        if (look.tag == '(' || look.tag == Tag.NUM) { // mi aspetto un numero o una parentesi tonda aperta
            expr(); // inizio l'analisi partendo dall'espressione
            match(Tag.EOF);
        } else {
            error(ANSI_BOLD + "(" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "Number" + ANSI_RESET_ERROR + " expected.");
        }
    }

    private void expr() {
        if (look.tag == '(' || look.tag == Tag.NUM) { // mi aspetto un numero o una parentesi tonda
            // GUIDA(E -> TE')
            term();
            exprp();
        } else {
            error(ANSI_BOLD + "(" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "Number" + ANSI_RESET_ERROR + " expected.");
        }
    }

    private void exprp() {
        switch (look.tag) {

            // mi aspetto uno tra '+', '-' => per continuare la ricorsione
            case '+':
                // GUIDA(E' -> +TE')
                match('+');
                term();
                exprp();
                break;

            case '-':
                // GUIDA(E' -> -TE')
                match('-');
                term();
                exprp();
                break;

            // FOLLOW(exprp) = {')', EOF}
            case ')':
            case Tag.EOF:
                break;

            default:
                error(ANSI_BOLD + "+" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "-" + ANSI_RESET_ERROR + " or "
                        + ANSI_BOLD + ")" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "EOF" + ANSI_RESET_ERROR
                        + " expected.");
        }
    }

    private void term() {
        if (look.tag == '(' || look.tag == Tag.NUM) { // mi aspetto un numero o una parentesi tonda
            // GUIDA(T -> FT')
            fact();
            termp();
        } else {
            error(ANSI_BOLD + "(" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "Number" + ANSI_RESET_ERROR + " expected.");
        }
    }

    private void termp() {
        switch (look.tag) {
            case '*':
                // GUIDA(T' -> *FT')
                match('*');
                fact();
                termp();
                break;

            case '/':
                // GUIDA(T' -> /FT')
                match('/');
                fact();
                termp();
                break;

            // FOLLOW(termp) = {'+', '-', ')', EOF}
            case '+':
            case '-':
            case ')':
            case Tag.EOF:
                break;

            default:
                error(ANSI_BOLD + "*" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "/" + ANSI_RESET_ERROR + " or "
                        + ANSI_BOLD + "+" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "-" + ANSI_RESET_ERROR + " or "
                        + ANSI_BOLD + ")" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "EOF" + ANSI_RESET_ERROR
                        + " expected.");
        }
    }

    private void fact() {
        switch (look.tag) {
            case '(':
                // GUIDA(F -> (E))
                match('(');
                expr();
                match(')');
                break;

            case Tag.NUM:
                // GUIDA(F -> a)
                match(Tag.NUM);
                break;

            default:
                error(ANSI_BOLD + "(" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "Number" + ANSI_RESET_ERROR
                        + " expected.");
        }
    }

    public static void main(String[] args) {
        System.out.println(
                "\n!! Se il testo su stdout non dovesse essere formattato correttamente,\n    si inizializzino tutti i colori ANSI, dichiarati all'inizio del file \"Parser.java\", con stringa vuota !!\n\n");
        Lexer lex = new Lexer();
        String path = "./parser.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println(ANSI_GREEN + "Input OK" + ANSI_RESET);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}