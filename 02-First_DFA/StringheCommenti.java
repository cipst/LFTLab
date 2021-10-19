public class StringheCommenti {

    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    if (ch == 'a' || ch == '*')
                        state = 0;
                    else if (ch == '/')
                        state = 1;
                    else
                        state = -1;
                    break;

                case 1:
                    if (ch == 'a')
                        state = 0;
                    else if (ch == '/')
                        state = 1;
                    else if (ch == '*')
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
                        state = 0;
                    else
                        state = -1;
                    break;

            }
        }
        return state == 0 || state == 1;
    }

    public static void main(String[] args) {
        System.out.print("CASE \"/**/a\": ");
        System.out.println(scan("/**/a") ? "OK" : "NOPE");

        System.out.print("CASE \"/**/\": ");
        System.out.println(scan("/**/") ? "OK" : "NOPE");

        System.out.print("CASE \"/*aaa*/\": ");
        System.out.println(scan("/*aaa*/") ? "OK" : "NOPE");

        System.out.print("CASE \"/*a***/\": ");
        System.out.println(scan("/*a***/") ? "OK" : "NOPE");

        System.out.print("CASE \"/*aa*/a*/\": ");
        System.out.println(scan("/*aa*/a*/") ? "OK" : "NOPE");
        
        System.out.print("CASE \"aaa/****/aa\": ");
        System.out.println(scan("aaa/****/aa") ? "OK" : "NOPE");
        
        System.out.print("CASE \"aa/*a*a*/\": ");
        System.out.println(scan("aa/*a*a*/") ? "OK" : "NOPE");
        
        System.out.print("CASE \"aaaa\": ");
        System.out.println(scan("aaaa") ? "OK" : "NOPE");
        
        System.out.print("CASE \"aaa/*/aa\": ");
        System.out.println(scan("aaa/*/aa") ? "OK" : "NOPE");
        
        System.out.print("CASE \"a/**//***a\": ");
        System.out.println(scan("a/**//***a") ? "OK" : "NOPE");

        System.out.print("CASE \"//////\": ");
        System.out.println(scan("//////") ? "OK" : "NOPE");
    }
}
