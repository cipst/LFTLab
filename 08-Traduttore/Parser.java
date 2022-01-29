import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    /* FORMATTAZIONE OUTPUT SU PROMPT */
    public static String ANSI_BOLD = "\u001b[1m";
    public static String ANSI_RED = "\u001B[31m";
    public static String ANSI_GREEN = "\u001b[32m";
    public static String ANSI_CYAN = "\u001b[36m";
    public static String ANSI_RESET_ERROR = "\u001B[0m" + ANSI_RED;
    public static String ANSI_RESET = "\u001B[0m";

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

    public void prog() {
        // GUIDA(<prog> -> <statlist> EOF)
        if (look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.WHILE
                || look.tag == Tag.IF || look.tag == '{') {
            statlist();
            match(Tag.EOF);
        } else {
            error("Error in prog() method. Expected " + ANSI_BOLD + "assign" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                    + "print" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "read" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                    + "while" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "if" + ANSI_RESET_ERROR + " or " + ANSI_BOLD
                    + "'{'" + ANSI_RESET_ERROR);
        }
    }

    public void statlist() {
        // GUIDA(<statlist> -> <stat><statlistP>)
        if (look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.WHILE
                || look.tag == Tag.IF || look.tag == '{') {
            stat();
            statlistP();
        } else {
            error("Error in statlist() method. Expected " + ANSI_BOLD + "assign" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                    + "print" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "read" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                    + "while" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "if" + ANSI_RESET_ERROR + " or " + ANSI_BOLD
                    + "'{'" + ANSI_RESET_ERROR);
        }
    }

    public void statlistP() {
        switch (look.tag) {
            // GUIDA(<statlistP> -> ;<stat><statlistP>)
            case ';':
                match(';');
                stat();
                statlistP();
                break;

            // GUIDA(<statlistP> -> eps)
            // FOLLOW(<statlistP>) = {'}', EOF}
            case '}':
            case Tag.EOF:
                break;

            default:
                error("Error in statlistP() method. Expected " + ANSI_BOLD + ";" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                        + "}" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "EOF" + ANSI_RESET_ERROR);
        }
    }

    public void stat() {
        switch (look.tag) {

            // GUIDA(<stat> -> assign <expr> to <idlist>)
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                if (look.tag == Tag.TO)
                    match(Tag.TO);
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'to'" + ANSI_RESET_ERROR
                            + " after expression!");
                idlist();
                break;

            // GUIDA(<stat> -> print(<exprlist>))
            case Tag.PRINT:
                match(Tag.PRINT);
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR
                            + " after print command!");
                exprlist();
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after list expression!");
                break;

            // GUIDA(<stat> -> read(<idlist>))
            case Tag.READ:
                match(Tag.READ);
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR
                            + " after read command!");
                idlist();
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after list identifier!");
                break;

            // GUIDA(<stat> -> while(<bexpr>) <stat>)
            case Tag.WHILE:
                match(Tag.WHILE);
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR
                            + " after while command!");
                bexpr();
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after boolean expression!");
                stat();
                break;

            // GUIDA(<stat> -> if(<bexpr>)<stat><statP>)
            case Tag.IF:
                match(Tag.IF);
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR
                            + " after if command!");
                bexpr();
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after boolean expression!");
                stat();
                statp();
                break;

            // GUIDA(<stat> -> {<statlist>})
            case '{':
                match('{');
                statlist();
                if (look.tag == '}')
                    match('}');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'}'" + ANSI_RESET_ERROR
                            + " after a list of statements!");
                break;

            default:
                error("Error in stat() method. Expected " + ANSI_BOLD + "assign" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                        + "print" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "read" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                        + "while" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "if" + ANSI_RESET_ERROR + " or " + ANSI_BOLD
                        + "'{'" + ANSI_RESET_ERROR);
        }
    }

    public void statp() {
        switch (look.tag) {

            // GUIDA(<statP> -> else <stat> end)
            case Tag.ELSE:
                match(Tag.ELSE);
                stat();
                if (look.tag == Tag.END)
                    match(Tag.END);
                else
                    error("Error in statp() method. Expected " + ANSI_BOLD + "end" + ANSI_RESET_ERROR
                            + " after a statement!");
                break;

            // GUIDA(<statP> -> end)
            case Tag.END:
                match(Tag.END);
                break;
        }
    }

    public void idlist() {
        // GUIDA(<idlist> -> ID <idlistP>)
        if (look.tag == Tag.ID) {
            match(Tag.ID);
            idlistP();
        } else {
            error("Error in idlist() method. Expected " + ANSI_BOLD + "ID" + ANSI_RESET_ERROR + "!");
        }
    }

    public void idlistP() {
        switch (look.tag) {
            // GUIDA(<idlistP> -> , ID <idlistP>)
            case ',':
                match(',');
                if (look.tag == Tag.ID)
                    match(Tag.ID);
                else
                    error("Error in idlistP() method. Expected " + ANSI_BOLD + "ID" + ANSI_RESET_ERROR + " after ','!");
                idlistP();
                break;

            // GUIDA(<idlistP> -> eps)
            // FOLLOW(<idlistP>) = {')', ';', EOF}
            case ')':
            case ';':
            case Tag.EOF:
                break;

            default:
                error("Error in idlistP() method. Expected " + ANSI_BOLD + "','" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                        + "';'" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + ")" + ANSI_RESET_ERROR + " or " + ANSI_BOLD
                        + "EOF" + ANSI_RESET_ERROR);
        }
    }

    public void bexpr() {
        // GUIDA(<bexpr> -> RELOP <expr> <expr>)
        if (look.tag == Tag.RELOP) {
            match(Tag.RELOP);
            expr();
            expr();
        } else {
            error("Error in bexpr() method. Expected {" + ANSI_BOLD + "<, >, <=, >=, ==, <>" + ANSI_RESET_ERROR + "}!");
        }
    }

    public void expr() {
        switch (look.tag) {
            // GUIDA(<expr> -> + ( <exprlist> ))
            case '+':
                match('+');
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in expr() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR + " after '+'!");
                exprlist();
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in expr() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after list expression!");
                break;

            // GUIDA(<expr> -> * ( <exprlist> ))
            case '*':
                match('*');
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in expr() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR + " after '*'!");
                exprlist();
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in expr() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after list expression!");
                break;

            // GUIDA(<expr> -> - <expr> <expr>)
            case '-':
                match('-');
                expr();
                expr();
                break;

            // GUIDA(<expr> -> / <expr> <expr>)
            case '/':
                match('/');
                expr();
                expr();
                break;

            // GUIDA(<expr> -> NUM)
            case Tag.NUM:
                match(Tag.NUM);
                break;

            // GUIDA(<expr> -> ID)
            case Tag.ID:
                match(Tag.ID);
                break;

            default:
                error("Error in expr() method. Expected " + ANSI_BOLD + "+" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "-"
                        + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "*" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "/"
                        + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "NUM" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "ID");
        }
    }

    public void exprlist() {
        // GUIDA(<exprlist> -> <expr> <exprlistP>)
        if (look.tag == '+' || look.tag == '*' || look.tag == '-' || look.tag == '/' || look.tag == Tag.NUM
                || look.tag == Tag.ID) {
            expr();
            exprlistP();
        } else {
            error("Error in exprlist() method. Expected " + ANSI_BOLD + "+" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "-"
                    + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "*" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "/"
                    + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "NUM" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "ID");
        }
    }

    public void exprlistP() {
        switch (look.tag) {
            // GUIDA(<exprlistP> -> , <expr> <exprlistP>)
            case ',':
                match(',');
                expr();
                exprlistP();
                break;

            // GUIDA(<exprlistP> -> eps)
            // FOLLOW(<exprlistP>) = {')'}
            case ')':
                break;

            default:
                error("Error in exprlistP() method. Expected " + ANSI_BOLD + "','" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                        + ")");
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
            parser.prog();
            System.out.println(ANSI_GREEN + "Input OK" + ANSI_RESET);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}