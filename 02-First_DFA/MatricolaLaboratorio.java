import java.util.Random;

public class MatricolaLaboratorio {

    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
            case 0:
                if (Character.isDigit(ch) && ch % 2 == 1)
                    state = 1;
                else if (Character.isDigit(ch) && ch % 2 == 0)
                    state = 2;
                else
                    state = -1;
                break;

            case 1:
                if (Character.isDigit(ch) && ch % 2 == 0)
                    state = 5;
                else if (Character.isDigit(ch) && ch % 2 == 1)
                    state = 6;
                else
                    state = -1;
                break;

            case 2:
                if (Character.isDigit(ch) && ch % 2 == 0)
                    state = 3;
                else if (Character.isDigit(ch) && ch % 2 == 1)
                    state = 4;
                else
                    state = -1;
                break;

            case 3:
                if (Character.isDigit(ch) && ch % 2 == 0)
                    state = 3;
                else if (Character.isDigit(ch) && ch % 2 == 1)
                    state = 4;
                else if (Character.isLetter(ch) && ch >= 'A' && ch <= 'K')
                    state = 7;
                else
                    state = -1;
                break;

            case 4:
                if (Character.isDigit(ch) && ch % 2 == 0)
                    state = 5;
                else if (Character.isDigit(ch) && ch % 2 == 1)
                    state = 6;
                else if (Character.isLetter(ch) && ch >= 'A' && ch <= 'K')
                    state = 7;
                else
                    state = -1;
                break;

            case 5:
                if (Character.isDigit(ch) && ch % 2 == 0)
                    state = 3;
                else if (Character.isDigit(ch) && ch % 2 == 1)
                    state = 4;
                else if (Character.isLetter(ch) && ch >= 'L' && ch <= 'Z')
                    state = 7;
                else
                    state = -1;
                break;

            case 6:
                if (Character.isDigit(ch) && ch % 2 == 0)
                    state = 5;
                else if (Character.isDigit(ch) && ch % 2 == 1)
                    state = 6;
                else if (Character.isLetter(ch) && ch >= 'L' && ch <= 'Z')
                    state = 7;
                else
                    state = -1;
                break;

            case 7:
                if (Character.isLetter(ch) && Character.isLowerCase(ch))
                    state = 7;
                else
                    state = -1;
                break;
            }
        }
        return state == 7;
    }

    public static void main(String[] args) {

        for (int i = 0; i < args.length; ++i) {
            System.out.println(args[i] + ": " + (scan(args[i]) ? "OK" : "NOPE"));
        }
        System.out.println("--------------------");

        String[] nome = { "Paolo", "Francesco", "Alberto", "Bianco", "Lucia", "Sara", "Beatrice", "Davide", "Mirco",
                "Z" };
        Random x = new Random();
        int matricola = 0;
        String test = "";
        for (int i = 0; i < 10; ++i) {
            matricola = Math.abs(x.nextInt());
            test = matricola + nome[i];
            System.out.println(test + ": " + (scan(test) ? "OK" : "NOPE"));
        }
    }
}
