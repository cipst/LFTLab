public class Commenti {

    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    if (ch == '/')
                        state = 1;
                    else
                        state = -1;
                    break;

                case 1:
                    if (ch == '*')
                        state = 2;
                    else
                        state = -1;
                    break;

                case 2:
                    if (ch == 'a' || ch == '/')
                        state = 2;
                    else if (ch == '*')
                        state = 3;
                    else
                        state = -1;
                    break;

                case 3:
                    if (ch == 'a')
                        state = 2;
                    else if (ch == '*')
                        state = 3;
                    else if (ch == '/')
                        state = 4;
                    else
                        state = -1;
                    break;

                case 4:
                    state = -1;
                    break;

            }
        }
        return state == 4;
    }

    public static void main(String[] args) {

        for (int i = 0; i < args.length; ++i) {
            System.out.println(args[i] + ": " + (scan(args[i]) ? "OK" : "NOPE"));
        }
        System.out.println("--------------------");

        System.out.print("CASE \"/**/a\": ");
        System.out.println(scan("/**/a") ? "OK" : "NOPE");
        
        System.out.print("CASE \"/*/\": ");
        System.out.println(scan("/*/") ? "OK" : "NOPE");
        
        System.out.print("CASE \"/**/\": ");
        System.out.println(scan("/**/") ? "OK" : "NOPE");
        
        System.out.print("CASE \"/*aaa*/\": ");
        System.out.println(scan("/*aaa*/") ? "OK" : "NOPE");
        
        System.out.print("CASE \"/*a***/\": ");
        System.out.println(scan("/*a***/") ? "OK" : "NOPE");
        
        System.out.print("CASE \"/*aa*/a*/\": ");
        System.out.println(scan("/*aa*/a*/") ? "OK" : "NOPE");
    }
}
