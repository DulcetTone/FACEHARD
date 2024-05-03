

// these ugly routines are Java versions of BASIC routines for input and output
// they are not tested or complete, and are intended only to allow FACEHARD.java to
// become (eventually) a roughly backward compatible app to Nathan Okun's FACEHARD 0.55
public class BASIC {

    private static float inputBoundedFloat(String pmt, float min, float max, float defalt) {
         if (defalt >= min && defalt <= max)
                pmt += " [default is " + defalt + "]";

        while (true) {

            String s = INPUT(pmt) ;

           if (s.equals("") && defalt >= min && defalt <= max) {
               PRINTLN("Using " + defalt);
               return defalt;
           }

           Float ff = Float.valueOf(s);
           if (ff != null) {
               float f = ff.floatValue();
               if (f >= min && f <= max)
                   return f;
               else
                   PRINTLN("Input must be >= " + min + " and <= " + max );
           }

        }
    }


    private static int inputBoundedInt(String pmt, int min, int max, int defalt) {
            if (defalt >= min && defalt <= max)
                pmt += " [default is " + defalt + "]";

        while (true) {

            String s = INPUT(pmt + ": ") ;

           if (s.equals("") && defalt >= min && defalt <= max) {
               PRINTLN("Using " + defalt);
               return defalt;
           }

           Integer ff = Integer.valueOf(s);
           if (ff != null) {
               int f = ff.intValue();
               if (f >= min && f <= max)
                   return f;
               else
                   PRINTLN("Input must be an integer >= " + min + " and <= " + max );
           }

        }
    }


    private static boolean _askYesNo(String pmt, boolean useDefault, boolean defalt) {
        String s;
        boolean retval = false;

        if (useDefault)
            pmt += " (Y/N, default " + (defalt ? "Y" : "N") + ") ";
        else
            pmt += " (Y/N) ";

        //
        while (true) {

            s = INPUT(pmt);

            if (s.equals("") && useDefault) {
                s = defalt ? "Y" : "N";
                break;
            }
            s = s.toUpperCase();
            if (s.length() > 0 && (s.charAt(0) == 'Y' || s.charAt(0) == 'N'))
                break ;
        }
        retval = s.charAt(0) == 'Y';
        PRINTLN(retval ? "YES" : "NO");

        return retval;

    }

    static boolean askYesNo(String pmt) {
        return _askYesNo(pmt, false, false)   ;
    }

    static boolean askYesNoDefault(String pmt, boolean defalt) {
        return _askYesNo(pmt, true, defalt);
    }



    // to handle PRINTTAB() and LPRINTTAB functions, we need to track our textual column on screen
    static int displayColumn = 0;

    static String SPC(int howMany) {
        if (howMany <= 0) return "";
        String someSpaces = "                                ";
        String s = "";
        while (s.length() < howMany)
            s = s.concat(someSpaces);
        return s.substring(0, howMany - 1);
    }


    static void CLS() {
        for (int i=0; i < 32; i++)
            PRINTLN();
    }
    static void PRINT(String s) {
        displayColumn += s.length();
        System.out.print(s);
    }
    static void PRINTLN() {
        PRINTLN("");
    }

    static void PRINTLN(String s) {
        PRINT(s+"\n");
        displayColumn = 0;
    }

    static void PRINTTAB(int toColumn) {
        if (displayColumn >= toColumn) return;
        PRINT(SPC(toColumn - displayColumn));
    }

    static String inchesString(float inch, boolean CAPS) {
        String retval;
        retval = "" + inch + " inch";

        if (inch != 1f) retval += "es";
        return CAPS ? retval.toUpperCase() : retval;
    }

    static String degreesString(float degs, boolean CAPS) {
        String retval;
        retval = "" + degs + " degree";

        if (degs != 1f) retval += "s";
        return (CAPS ? retval.toUpperCase() : retval);
    }


    static String INPUT(String pmt) {
        PRINT(pmt);

        // TODO: code up line-based input
        return "";
    }


    // TODO: code me
    static int askBoundedInt(String pmt, int min, int max) {
        return 0;
    }


    // TODO: code me
    static float askBoundedFloat(String pmt, float min, float max) {
        return 0f;
    }


    // TODO: code me
    static int askBoundedInt(String pmt, int min, int max, int defaultValue) {
        return 0;
    }


    // TODO: code me
    static float askBoundedFloat(String pmt, float min, float max, float defaultValue) {
        return 0f;
    }

}
