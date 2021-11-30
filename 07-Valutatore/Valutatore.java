import java.io.*;

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    /* FORMATTAZIONE OUTPUT SU PROMPT */
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public Valutatore(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + ANSI_RED +
                "\n\t" + s +
                "\n\tStavo analizzando il tag: " + look + ANSI_RESET +
                "\nLo StackTrace e':");
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else
            error("Syntax error");
    }

    public void start() {
        int expr_val;
        if (look.tag == '(' || look.tag == Tag.NUM) {
            expr_val = expr();
            match(Tag.EOF);
            System.out.println(ANSI_GREEN + "Value = " + expr_val + ANSI_RESET);
        } else {
            error("Syntax Error in start() method. Expected '(' or a Number");
        }
    }

    private int expr() {
        int term_val, exprp_val;

        if (look.tag == '(' || look.tag == Tag.NUM) {
            term_val = term();
            exprp_val = exprp(term_val);
        } else {
            error("Syntax Error in expr() method. Expected '(' or a Number");
            exprp_val = -1;
        }
        return exprp_val;
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val;
        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;

            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;

            case ')', Tag.EOF:
                exprp_val = exprp_i;
                break;

            default:
                error("Syntax Error in exprp() method. Expected '+', '-', ')' or EOF");
                exprp_val = -1;
                break;
        }
        return exprp_val;
    }

    private int term() {
        int fact_val, term_val;
        if (look.tag == '(' || look.tag == Tag.NUM) { // mi aspetto un numero o una parentesi tonda
            fact_val = fact();
            term_val = termp(fact_val);
        } else {
            error("Syntax Error in termp() method. Expected '(' or a Number");
            term_val = -1;
        }
        return term_val;
    }

    private int termp(int termp_i) {
        int fact_val, termp_val;
        switch (look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;

            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                break;

            case '+', '-', ')', Tag.EOF:
                termp_val = termp_i;
                break;

            default:
                error("Syntax Error in termp() method. Expected '+', '-', '*', '/', ')' or EOF");
                termp_val = -1;
                break;
        }
        return termp_val;
    }

    private int fact() {
        int fact_val;
        switch (look.tag) {
            case '(':
                match('(');
                fact_val = expr();
                if (look.tag == ')') {
                    match(')');
                } else {
                    error("Syntax Error in fact() method. Expected ')'");
                    fact_val = -1;
                }
                break;

            case Tag.NUM:
                if (look instanceof NumberTok) { // controllo che look sia un'istanza di NumberTok
                    NumberTok tmp = (NumberTok) look; // cast
                    match(Tag.NUM);
                    fact_val = tmp.number; // prendo il campo number che contiene il numero effettivo
                } else {
                    error("Syntax Error in fact() method. Number token not instance of NumberTok.");
                    fact_val = -1;
                }
                break;

            default:
                error("Syntax Error in fact() method. Expected '(' or a Number");
                fact_val = -1;
        }
        return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "ValutatoreTest.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
