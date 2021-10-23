public class StringheStefano {

    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    state = changeState(ch, 'S', 1, 8);
                    break;

                case 1:
                    state = changeState(ch, 't', 2, 9);
                    break;

                case 2:
                    state = changeState(ch, 'e', 3, 10);
                    break;

                case 3:
                    state = changeState(ch, 'f', 4, 11);
                    break;

                case 4:
                    state = changeState(ch, 'a', 5, 12);
                    break;

                case 5:
                    state = changeState(ch, 'n', 6, 13);
                    break;

                case 6:
                    state = changeState(ch, 'o', 7, 7);
                    break;

                case 7:
                    state = -1;
                    break;

                case 8:
                    state = changeState(ch, 't', 9, -1);
                    break;

                case 9:
                    state = changeState(ch, 'e', 10, -1);
                    break;

                case 10:
                    state = changeState(ch, 'f', 11, -1);
                    break;

                case 11:
                    state = changeState(ch, 'a', 12, -1);
                    break;

                case 12:
                    state = changeState(ch, 'n', 13, -1);
                    break;

                case 13:
                    state = changeState(ch, 'o', 7, -1);
                    break;
            }
        }
        return state == 7;
    }

    private static int changeState(char ch, char c, int nextState1, int nextState2) {
        if (ch == c)
            return nextState1;
        else if (ch != c)
            return nextState2;
        else
            return -1;
    }

    public static void main(String[] args) {

        for (int i = 0; i < args.length; ++i) {
            System.out.println(args[i] + ": " + (scan(args[i]) ? "OK" : "NOPE"));
        }
    }
}
