public class CognomeMatricola {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {

                // q0 -> stato iniziale
                // Accetto lettere maiuscole tra 'A' e 'K' -> vado in q1
                // Accetto lettere maiuscole tra 'L' e 'Z' -> vado in q2
                case 0:
                    if (Character.isLetter(ch) && ch >= 'A' && ch <= 'K')
                        state = 1;
                    else if (Character.isLetter(ch) && ch >= 'L' && ch <= 'Z')
                        state = 2;
                    else
                        state = -1;
                    break;

                // q1 -> stato per matricole pari con lettera iniziale 'a' - 'k'
                // Accetto numeri pari -> vado in q3
                // Accetto tutte le lettere -> rimango in q1
                // Accetto numeri dispari -> rimango in q1
                case 1:
                    if (Character.isDigit(ch) && ch % 2 == 0)
                        state = 3;
                    else if (Character.isLetter(ch))
                        state = 1;
                    else if (Character.isDigit(ch) && ch % 2 == 1)
                        state = 1;
                    else
                        state = -1;
                    break;

                // q2 -> stato per matricole dispari con lettera iniziale 'l' - 'z'
                // Accetto numeri dispari -> vado in q4
                // Accettp numeri pari -> rimango in q2
                // Accetto tutte le lettere -> rimango in q2
                case 2:
                    if (Character.isDigit(ch) && ch % 2 == 1)
                        state = 4;
                    else if (Character.isDigit(ch) && ch % 2 == 0)
                        state = 2;
                    else if (Character.isLetter(ch))
                        state = 2;
                    else
                        state = -1;
                    break;

                // q3* -> stato per riconoscere ultima cifra
                // Accetto numeri pari -> rimango in q3
                // Accetto numeri dispari -> vado in q1
                case 3:
                    if (Character.isDigit(ch) && ch % 2 == 0)
                        state = 3;
                    else if (Character.isDigit(ch) && ch % 2 == 1)
                        state = 1;
                    else
                        state = -1;
                    break;

                // q4* -> stato per riconoscere ultima cifra
                // Accetto numeri dispari -> rimango in q4
                // Accetto numeri pari -> vado in q2
                case 4:
                    if (Character.isDigit(ch) && ch % 2 == 1)
                        state = 4;
                    else if (Character.isDigit(ch) && ch % 2 == 0)
                        state = 2;
                    else
                        state = -1;
                    break;

            }
        }

        return state == 3 || state == 4;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}
