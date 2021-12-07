import java.io.*;

public class Translator {
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

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;

    public Translator(Lexer l, BufferedReader br) {
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
        if (look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.WHILE
                || look.tag == Tag.IF || look.tag == '{') {
            int lnext_prog = code.newLabel();
            statlist(lnext_prog);
            code.emitLabel(lnext_prog);
            match(Tag.EOF);
            try {
                code.toJasmin();
            } catch (java.io.IOException e) {
                System.out.println("IO error\n");
            }
        } else {
            error("Error in prog() method. Expected " + ANSI_BOLD + "assign" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                    + "print" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "read" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                    + "while" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "if" + ANSI_RESET_ERROR + " or " + ANSI_BOLD
                    + "'{'" + ANSI_RESET_ERROR);

        }
    }

    public void statlist(int slNext) {
        if (look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.WHILE
                || look.tag == Tag.IF || look.tag == '{') {
            int sNext = code.newLabel();
            stat(slNext);
            code.emit(OpCode.GOto, sNext);
            code.emitLabel(sNext);
            statlistP(slNext);
        } else {
            error("Error in statlist() method. Expected " + ANSI_BOLD + "assign" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                    + "print" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "read" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                    + "while" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "if" + ANSI_RESET_ERROR + " or " + ANSI_BOLD
                    + "'{'" + ANSI_RESET_ERROR);
        }
    }

    public void statlistP(int slNext) {
        switch (look.tag) {
            case ';':
                match(';');
                int sNext = code.newLabel();
                stat(slNext);
                code.emit(OpCode.GOto, sNext);
                code.emitLabel(sNext);
                statlistP(slNext);
                break;

            case '}':
            case Tag.EOF:
                break;

            default:
                error("Error in statlistP() method. Expected " + ANSI_BOLD + ";" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                        + "}"
                        + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "EOF" + ANSI_RESET_ERROR);
        }
    }

    public void stat(int sNext) {
        switch (look.tag) {

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

            // print (Exprlist())
            case Tag.PRINT:
                match(Tag.PRINT);
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR
                            + " after print command!");
                code.emit(OpCode.invokestatic, 1);
                exprlist();
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after list expression!");
                break;

            case Tag.READ:
                match(Tag.READ);
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR
                            + " after read command!");
                code.emit(OpCode.invokestatic, 0);
                idlist(/* DA COMPLETARE */);
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after list identifier!");
                break;

            // while(Bexpr())Stat()
            case Tag.WHILE:
                match(Tag.WHILE);
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR
                            + " after while command!");
                int begin = code.newLabel();
                code.emitLabel(begin);
                int wTrue = code.newLabel();
                // int bFalse = sNext;
                bexpr(wTrue, sNext);
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after boolean expression!");
                code.emitLabel(wTrue);
                stat(begin);
                break;

            // if(Bexpr())Stat()StatP()
            case Tag.IF:
                match(Tag.IF);
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR
                            + " after if command!");
                int iTrue = code.newLabel();
                // bFalse = sNext;
                bexpr(iTrue, sNext);
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after boolean expression!");
                code.emitLabel(iTrue);
                stat(sNext);
                code.emit(OpCode.GOto, sNext);
                statp(sNext, sNext);
                break;

            // {Statlist()}
            case '{':
                match('{');
                statlist(sNext);
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

    public void statp(int sNext, int lFalse) {
        switch (look.tag) {

            // else Stat() end
            case Tag.ELSE:
                match(Tag.ELSE);
                code.emitLabel(lFalse);
                stat(sNext);
                if (look.tag == Tag.END)
                    match(Tag.END);
                else
                    error("Error in statp() method. Expected " + ANSI_BOLD + "end" + ANSI_RESET_ERROR
                            + " after a statement!");
                break;

            // end
            case Tag.END:
                match(Tag.END);
                break;
        }
    }

    private void idlist() {
        if (look.tag == Tag.ID) {
            int id_addr = st.lookupAddress(((Word) look).lexeme);
            if (id_addr == -1) {
                id_addr = count;
                st.insert(((Word) look).lexeme, count++);
            }
            match(Tag.ID);
            code.emit(OpCode.istore, id_addr);
            idlistP();
        } else {
            error("Error in idlist() method. Expected " + ANSI_BOLD + "ID" + ANSI_RESET_ERROR + "!");
        }
    }

    private void idlistP() {
        switch (look.tag) {
            case ',':
                match(',');
                if (look.tag == Tag.ID) {
                    int id_addr = st.lookupAddress(((Word) look).lexeme);
                    if (id_addr == -1) {
                        id_addr = count;
                        st.insert(((Word) look).lexeme, count++);
                    }
                    match(Tag.ID);
                    code.emit(OpCode.istore, id_addr);
                } else
                    error("Error in idlistP() method. Expected " + ANSI_BOLD + "ID" + ANSI_RESET_ERROR + " after ','!");
                idlistP();
                break;

            case ')':
            case ';':
            case Tag.EOF:
                break;

            default:
                error("Error in idlistP() method. Expected " + ANSI_BOLD + "','" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                        + ")"
                        + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "EOF" + ANSI_RESET_ERROR);
        }
    }

    private void bexpr(int ltrue, int lfalse) {
        if (look.tag == Tag.RELOP) {
            match(Tag.RELOP);
            expr();
            expr();
        } else {
            error("Error in bexpr() method. Expected {" + ANSI_BOLD + "<, >, <=, >=, ==, <>" + ANSI_RESET_ERROR + "}!");
        }
    }

    private void expr( /* completare */ ) {
        switch (look.tag) {
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
                code.emit(OpCode.iadd);
                break;

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
                code.emit(OpCode.imul);
                break;

            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;

            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;

            case Tag.NUM:
                if (look instanceof NumberTok) { // controllo che look sia un'istanza di NumberTok
                    NumberTok tmp = (NumberTok) look; // cast
                    match(Tag.NUM);
                    code.emit(OpCode.ldc, tmp.number);
                } else {
                    error("Error in expr() method. Number token not instance of NumberTok.");
                }
                break;

            case Tag.ID:
                match(Tag.ID);
                code.emit(OpCode.iload, st.lookupAddress(((Word) look).lexeme));
                break;

            default:
                error("Error in expr() method. Expected " + ANSI_BOLD + "+" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "-"
                        + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "*" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "/"
                        + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "NUM" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "ID");

        }
    }

    private void exprlist() {
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

    private void exprlistP() {
        switch (look.tag) {
            case ',':
                match(',');
                expr();
                exprlistP();
                break;

            case ')':
                break;

            default:
                error("Error in exprlistP() method. Expected " + ANSI_BOLD + "','" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                        + ")");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "TranslatorTest.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            System.out.print(ANSI_RESET);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
