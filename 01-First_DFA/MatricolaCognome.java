public class MatricolaCognome {

    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    if (Character.isDigit(ch) && ch % 2 == 0)
                        state = 2;
                    else if (Character.isDigit(ch) && ch % 2 == 1)
                        state = 1;
                    else if (Character.isLetter(ch))
                        state = 5;
                    else
                        state = -1;
                    break;

                case 1:
                    if (Character.isDigit(ch) && ch % 2 == 0)
                        state = 2;
                    else if (Character.isDigit(ch) && ch % 2 == 1)
                        state = 1;
                    else if (Character.isLetter(ch) && Character.toLowerCase(ch) >= 'a'
                            && Character.toLowerCase(ch) <= 'k')
                        state = 5;
                    else if (Character.isLetter(ch) && Character.toLowerCase(ch) >= 'l'
                            && Character.toLowerCase(ch) <= 'z')
                        state = 3;
                    else
                        state = -1;
                    break;

                case 2:
                    if (Character.isDigit(ch) && ch % 2 == 0)
                        state = 2;
                    else if (Character.isDigit(ch) && ch % 2 == 1)
                        state = 1;
                    else if (Character.isLetter(ch) && Character.toLowerCase(ch) >= 'a'
                            && Character.toLowerCase(ch) <= 'k')
                        state = 3;
                    else if (Character.isLetter(ch) && Character.toLowerCase(ch) >= 'l'
                            && Character.toLowerCase(ch) <= 'z')
                        state = 5;
                    else
                        state = -1;
                    break;

                case 3:
                    if (Character.isLetter(ch))
                        state = 3;
                    else if (Character.isDigit(ch))
                        state = 5;
                    else
                        state = -1;
                    break;

                case 5:
                    if (Character.isLetter(ch) || Character.isDigit(ch))
                        state = 5;
                    else
                        state = -1;

            }
        }

        return state == 3;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}
