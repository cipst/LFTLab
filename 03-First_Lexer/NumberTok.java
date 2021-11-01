public class NumberTok extends Token {
    public int number = 0;

    public NumberTok(int n) {
        super(Tag.NUM);
        number = n;
    }

    public String toString() {
        return "<" + Tag.NUM + ", " + number + ">";
    }

}
