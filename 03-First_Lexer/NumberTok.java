public class NumberTok extends Token {
    public int lexeme = 0;

    public NumberTok(int s) {
        super(256);
        lexeme = s;
    }

    public String toString() {
        return "<" + tag + ", " + lexeme + ">";
    }

}
