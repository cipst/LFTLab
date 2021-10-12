public class MatricolaCognomeWS {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {

                // Accetto numeri pari -> vado in q2
                // Accetto numeri dispari -> vado in q1
                // Accetto spazi -> rimango in q0
                case 0:
                    if (Character.isDigit(ch) && ch % 2 == 0)
                        state = 2;
                    else if (Character.isDigit(ch) && ch % 2 == 1)
                        state = 1;
                    else if (Character.isWhitespace(ch))
                        state = 0;
                    else
                        state = -1;
                    break;

                // Accetto numeri pari -> vado in q2
                // Accetto numeri dispari -> vado in q1
                // Accetto spazi -> vado in q3
                // Accetto lettere tra 'l' e 'z' -> vado in q5
                case 1:
                    if (Character.isDigit(ch) && ch % 2 == 0)
                        state = 2;
                    else if (Character.isDigit(ch) && ch % 2 == 1)
                        state = 1;
                    else if (Character.isWhitespace(ch))
                        state = 3;
                    else if (Character.isLetter(ch) && Character.toLowerCase(ch) >= 'l'
                            && Character.toLowerCase(ch) <= 'z')
                        state = 5;
                    else
                        state = -1;
                    break;

                // Accetto numeri pari -> rimango in q2 (Mi manca una lettera tra 'a' e 'k')
                // Accetto numeri dispari -> vado in q1 (Mi manca una lettera tra 'l' e 'z')
                // Accetto spazi -> vado in q4
                // Accetto lettere tra 'a' e 'k' -> vado in q5
                case 2:
                    if (Character.isDigit(ch) && ch % 2 == 0)
                        state = 2;
                    else if (Character.isDigit(ch) && ch % 2 == 1)
                        state = 1;
                    else if (Character.isWhitespace(ch))
                        state = 4;
                    else if (Character.isLetter(ch) && Character.toLowerCase(ch) >= 'a'
                            && Character.toLowerCase(ch) <= 'k')
                        state = 5;
                    else
                        state = -1;
                    break;

                // Accetto lettere che siano tra 'l' e 'z' -> vado in q5
                // Accetto spazi -> rimango in q3 (Aspetto lettera tra 'l' e 'z')
                case 3:
                    if (Character.isLetter(ch) && Character.toLowerCase(ch) >= 'l' && Character.toLowerCase(ch) <= 'z')
                        state = 5;
                    else if (Character.isWhitespace(ch))
                        state = 3;
                    else
                        state = -1;
                    break;

                // Accetto lettere che siano tra 'a' e 'k' -> vado in q5
                // Accetto spazi -> rimango in q4 (Aspetto lettera tra 'a' e 'k')
                case 4:
                    if (Character.isLetter(ch) && Character.toLowerCase(ch) >= 'a' && Character.toLowerCase(ch) <= 'k')
                        state = 5;
                    else if (Character.isWhitespace(ch))
                        state = 4;
                    else
                        state = -1;
                    break;

                // Accetto qualsiasi lettera (a-z) -> rimango in q5
                // Accetto lo spazio -> vado in q6
                case 5:
                    if (Character.isLetter(ch))
                        state = 5;
                    else if (Character.isWhitespace(ch))
                        state = 6;
                    else
                        state = -1;
                    break;

                // Accetto spazi -> rimango in q6
                // Accetto lettere maiuscole -> vado in q5 (Vuol dire che lo spazio separava le
                // parti del cognome)
                case 6:
                    if (Character.isWhitespace(ch))
                        state = 6;
                    else if (Character.isLetter(ch) && Character.isUpperCase(ch))
                        state = 5;
                    else
                        state = -1;
                    break;

            }
        }

        // se state == 5 -> la stringa è corretta e non termina con spazi
        // se state == 6 -> la stringa è corretta e termina con spazi
        return state >= 5;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}
