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
            int lnext_prog = code.newLabel(); // Label per il return finale (L0: )
            statlist(lnext_prog);
            code.emitLabel(lnext_prog); // L0:
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
                code.emit(OpCode.GOto, slNext);
                break;

            // NOTE
            // default:
            // error("Error in statlistP() method. Expected " + ANSI_BOLD + ";" +
            // ANSI_RESET_ERROR + ", " + ANSI_BOLD
            // + "}"
            // + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "EOF" + ANSI_RESET_ERROR);
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
                idlist(Tag.ASSIGN);
                code.emit(OpCode.pop);
                break;

            // print (Exprlist())
            case Tag.PRINT:
                match(Tag.PRINT);
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR
                            + " after print command!");

                exprlist(OpCode.invokestatic);
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

                idlist(Tag.READ);
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
                int wFalse = code.newLabel();
                code.emitLabel(begin);

                bexpr(wFalse);

                if (look.tag == ')')
                    match(')');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after boolean expression!");

                stat(begin);
                code.emitLabel(wFalse);

                break;

            // if(Bexpr())Stat()StatP()
            case Tag.IF:
                match(Tag.IF);
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR
                            + " after if command!");

                int iFalse = code.newLabel();
                int iEnd = code.newLabel();

                bexpr(iFalse);

                if (look.tag == ')')
                    match(')');
                else
                    error("Error in stat() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after boolean expression!");

                stat(iEnd);

                code.emit(OpCode.GOto, sNext);
                code.emitLabel(iFalse);

                statp(sNext);

                code.emitLabel(iEnd);
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

            case ';':
            case '}':
            case Tag.EOF:
                break;

            default:
                error("Error in stat() method. Expected " + ANSI_BOLD + "assign" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                        + "print" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "read" + ANSI_RESET_ERROR + ", " + ANSI_BOLD
                        + "while" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "if" + ANSI_RESET_ERROR + " or " + ANSI_BOLD
                        + "'{'" + ANSI_RESET_ERROR);

        }
    }

    public void statp(int sNext) {
        switch (look.tag) {

            // else Stat() end
            case Tag.ELSE:
                match(Tag.ELSE);
                stat(sNext);
                if (look.tag == Tag.END) {
                    code.emit(OpCode.GOto, sNext);
                    match(Tag.END);
                } else
                    error("Error in statp() method. Expected " + ANSI_BOLD + "end" + ANSI_RESET_ERROR
                            + " after a statement!");
                break;

            // end
            case Tag.END:
                match(Tag.END);
                break;
        }
    }

    private void idlist(int from) {
        if (look.tag == Tag.ID) {
            int id_addr = st.lookupAddress(((Word) look).lexeme);
            if (id_addr == -1) {
                id_addr = count;
                st.insert(((Word) look).lexeme, count++);
            }
            match(Tag.ID);

            if (from == Tag.READ) {
                code.emit(OpCode.invokestatic, 0);
                code.emit(OpCode.istore, id_addr);
            }
            if (from == Tag.ASSIGN) {
                code.emit(OpCode.dup);
                code.emit(OpCode.istore, id_addr);
            }

            idlistP(from);
        } else {
            error("Error in idlist() method. Expected " + ANSI_BOLD + "ID" + ANSI_RESET_ERROR + "!");
        }
    }

    private void idlistP(int from) {
        switch (look.tag) {
            case ',':
                match(',');
                idlist(from);
                break;

            case ')':
            case ';':
            case Tag.EOF:
                break;

            // NOTE
            // default:
            // error("Error in idlistP() method. Expected " + ANSI_BOLD + "','" +
            // ANSI_RESET_ERROR + ", " + ANSI_BOLD
            // + ")"
            // + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "EOF" + ANSI_RESET_ERROR);
        }
    }

    /**
     * Salta alla label 'label' se la relazione è FALSA
     * 
     * @param label label a cui saltare
     */
    private void bexpr(int label) {
        switch (look.tag) {

            // GUIDA(<bexpr> -> RELOP <expr> <expr>)
            case Tag.RELOP: {
                String rel = ((Word) look).lexeme;

                match(Tag.RELOP);
                expr();
                expr();

                switch (rel) {
                    case "<":
                        code.emit(OpCode.if_icmpge, label);
                        break;

                    case ">":
                        code.emit(OpCode.if_icmple, label);
                        break;

                    case "<=":
                        code.emit(OpCode.if_icmpgt, label);
                        break;

                    case ">=":
                        code.emit(OpCode.if_icmplt, label);
                        break;

                    case "==":
                        code.emit(OpCode.if_icmpne, label);
                        break;

                    case "<>":
                        code.emit(OpCode.if_icmpeq, label);
                        break;
                }

                break;
            }

            // GUIDA(<bexpr> -> AND <bexpr1> <bexpr2>)
            case Tag.AND: {
                match(Tag.AND);

                bexpr(label); // Se <bexpr1> è falsa salto a label
                bexpr(label); // Altrimenti controllo <bexpr2>, se è falsa salto a label

                break;
            }

            // GUIDA(<bexpr> -> OR <bexpr1> <bexpr2>)
            case Tag.OR: {
                match(Tag.OR);

                int oTrue = code.newLabel();
                int oFalse = code.newLabel();

                bexpr(oFalse); // Se <bexpr1> è falsa salto a oFalse per controllare <bexpr2>
                code.emit(OpCode.GOto, oTrue); // Altrimenti salto a oTrue per eseguire il codice

                code.emitLabel(oFalse);

                bexpr(label); // Se <bexpr2> è falsa salto a label

                code.emit(OpCode.GOto, oTrue); // Altrimenti salto a oTrue per eseguire il codice
                code.emitLabel(oTrue);
                break;
            }

            // GUIDA(<bexpr> -> ! <bexpr1>)
            case '!': {
                match('!');

                int nTrue = code.newLabel();

                bexpr(nTrue); // Se <bexpr1> è falsa salto a nTrue
                code.emit(OpCode.GOto, label); // Altrimenti salto a label

                code.emitLabel(nTrue);
                break;
            }

            default:
                error("Error in bexpr() method. Expected {" + ANSI_BOLD + "<, >, <=, >=, ==, <>" + ANSI_RESET_ERROR
                        + "}!");
                break;
        }
    }

    private void expr() {
        switch (look.tag) {
            case '+':
                match('+');
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in expr() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR + " after '+'!");
                exprlist(OpCode.iadd);
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in expr() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after list expression!");
                break;

            case '*':
                match('*');
                if (look.tag == '(')
                    match('(');
                else
                    error("Error in expr() method. Expected " + ANSI_BOLD + "'('" + ANSI_RESET_ERROR + " after '*'!");
                exprlist(OpCode.imul);
                if (look.tag == ')')
                    match(')');
                else
                    error("Error in expr() method. Expected " + ANSI_BOLD + "')'" + ANSI_RESET_ERROR
                            + " after list expression!");
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
                code.emit(OpCode.iload, st.lookupAddress(((Word) look).lexeme));
                match(Tag.ID);
                break;

            default:
                error("Error in expr() method. Expected " + ANSI_BOLD + "+" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "-"
                        + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "*" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "/"
                        + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "NUM" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "ID");

        }
    }

    private void exprlist(OpCode opcode) {
        if (look.tag == '+' || look.tag == '*' || look.tag == '-' || look.tag == '/' || look.tag == Tag.NUM
                || look.tag == Tag.ID) {
            expr();

            // per sapere se <exprlist> è dentro ad una <print>
            if (opcode == OpCode.invokestatic) {
                code.emit(OpCode.invokestatic, 1);
            }

            exprlistP(opcode);
        } else {
            error("Error in exprlist() method. Expected " + ANSI_BOLD + "+" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "-"
                    + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "*" + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "/"
                    + ANSI_RESET_ERROR + ", " + ANSI_BOLD + "NUM" + ANSI_RESET_ERROR + " or " + ANSI_BOLD + "ID");
        }
    }

    private void exprlistP(OpCode opcode) {
        switch (look.tag) {
            case ',':
                match(',');
                expr();
                if (opcode == OpCode.invokestatic) {
                    code.emit(OpCode.invokestatic, 1);
                } else {

                    code.emit(opcode);
                }
                exprlistP(opcode);
                break;

            case ')':
                break;

            // NOTE
            // default:
            // error("Error in exprlistP() method. Expected " + ANSI_BOLD + "','" +
            // ANSI_RESET_ERROR + ", " + ANSI_BOLD
            // + ")");
        }
    }

    public static void main(String[] args) {
        System.out.println("\n !! Inserire il percorso ad un file di test come argomento per testare quel file\n");

        Lexer lex = new Lexer();
        String path = (args.length == 0) ? "./test/TESTS.lft" : args[0];

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
