import java.util.regex.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class Personnummer {
    private final Pattern regexPattern =
            Pattern.compile("^(\\d{2})?(\\d{2})(\\d{2})(\\d{2})([-+]?)?(\\d{3})(\\d)$");

    private final String year;
    // private final String century;
    private final String yearLong;
    private final String month;
    private final String day;
    private final String birthNumber;
    private final String controlNumber;

    /**
     * @param personnummer the personnummer as a String.
     * @throws Exception Exception if personnummer is malformed.
     */
    public Personnummer(String personnummer) throws Exception {
        if (personnummer == null) {
            throw new Exception("Sanity check personnummer is null");
        }

        Matcher m = regexPattern.matcher(personnummer);

        if (!m.find()) {
            throw new Exception(
                    "regex could not match personnummer possible causes encoding, length, formating.");
        }

        // Group 1 century
        String century;
        String year = m.group(2);

        if (m.group(1) != null && !m.group(1).isEmpty()) {
            century = m.group(1);
        } else {
            // Process "+" separator
            int now = LocalDate.now().getYear();
            if (!m.group(5).isEmpty() && m.group(5).equals("+")) {
                /*
                 * Note: If decade group 2 is larger than current decade (now()) then it
                 * implies the person was born in 1900 (else 2000). The plus seperator indicates
                 * that this assumption is invalid, we need to subract another century from the
                 * birth date.
                 */
                now -= 100;
            }
            century = Integer.toString(now - Integer.parseInt(year)).substring(0, 2);
        }

        /*
         * This is a bit lazy. Could cause problems. For example if samordningsnummer is passed
         * as personnummer.
         */

        int numericDay = Integer.parseInt(m.group(4));
        numericDay = numericDay >= 60 ? (numericDay - 60) : numericDay;

        this.year = year;
        // this.century = century;
        this.yearLong = century + year;
        this.month = m.group(3);
        this.day = m.group(4);
        this.birthNumber = m.group(6);
        this.controlNumber = m.group(7);

        try {
            DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT)
                    .parse(String.format("%s%s%02d", this.yearLong, this.month, numericDay));
        } catch (DateTimeParseException e) {
            throw new Exception("Not a valid personnummer. Birth date is not a valid date.");
        }

        // check controlNumber ...
        if ((luhn(String.format("%s%s%s%s", this.year, this.month, this.day,
                this.birthNumber))) != Integer.parseInt(this.controlNumber)) {
            throw new Exception("Not a valid personnummer. Failed when checking controlNumber.");
        }
    }

    /**
     * Implementation of Luhn's modulu 10 algorithm.
     * 
     * @param data The data given as a string to be validated by the algorithm
     * @return An integer which is the checksum digit produced by the luhn algorithm.
     */

    private static int luhn(String data) {
        int sum = 0;

        for (int i = 0; i < data.length(); i++) {
            int tmp = Character.getNumericValue(data.charAt(i));
            tmp *= (2 - (i % 2));
            sum += (tmp > 9) ? (1 + tmp % 10) : tmp;
        }
        return (10 - (sum % 10)) % 10;
    }

    /**
     * Tries to call constructor Personnummer.
     * 
     * @see Personnummer
     * 
     * @param data string represantation of the personnummer to get validated.
     * @return true if valid else it will return false if a check fails.
     */

    public static boolean valid(String data) {
        try {
            new Personnummer(data);
            return true;
        } catch (Exception e) {
            System.out.println(data + " is not valid.");
            e.printStackTrace();
            System.out.println();
            return false;
        }
    }
}
