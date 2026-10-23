import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class Validation {
    private Validation() { }
    public static boolean validPhone(String phone) { return phone != null && phone.matches("[0-9]{10}"); }
    public static boolean validName(String name) {
        if (name == null) return false;
        String n = name.trim();
        return n.matches("[A-Za-z]+(?:[ '-][A-Za-z]+)+") && n.split("[ '-]+").length >= 2;
    }
    public static boolean validEmail(String email) {
        if (email == null) return false;
        String e = email.trim();
        if (!e.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) return false;
        String extension = e.substring(e.lastIndexOf('.') + 1).toLowerCase();
        return extension.matches("com|in|org|net|edu|gov|co|biz|info|me|io|ai|app|dev|tech|online|site|store|shop|cloud|live|xyz|pro|name|travel|mobi|tv");
    }
    public static boolean validPassword(String password) { return password != null && password.matches("\\d{6}"); }
    public static boolean validAddress(String address) {
        if (address == null) return false;
        String a = address.trim();
        boolean pin = a.matches("(?s).*\\b[1-9][0-9]{5}\\b.*");
        String[] parts = a.split(",");
        boolean enoughParts = parts.length >= 5;
        boolean house = a.matches("(?i)(?s).*(?:flat|house|h\\.?\\s*no|plot|building|bldg|door|room)?\\s*\\d+[A-Za-z]?\\b.*");
        return a.length() >= 30 && pin && enoughParts && house;
    }
    public static boolean validUpi(String upi) { return upi != null && upi.trim().matches("[A-Za-z0-9._-]{2,}@[A-Za-z]{2,}"); }

    public static boolean validCard(String number, String expiry, String cvv) {
        if (number == null || expiry == null || cvv == null) return false;

        // Accept the formatted card number (1234 5678 9012 3456) as well as
        // an unformatted 16-digit number.
        String cardDigits = number.replaceAll("\\D", "");
        if (!cardDigits.matches("\\d{16}")) return false;

        String cvvDigits = cvv.replaceAll("\\D", "");
        if (!cvvDigits.matches("\\d{3}")) return false;

        String exp = expiry.trim().replace('-', '/').replaceAll("\\s", "");
        if (!exp.matches("(0[1-9]|1[0-2])/\\d{2}")) return false;

        return validFutureExpiry(exp);
    }

    public static boolean validFutureExpiry(String expiry) {
        if (expiry == null || !expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) return false;
        try {
            YearMonth cardMonth = YearMonth.parse(expiry, DateTimeFormatter.ofPattern("MM/yy"));
            return !cardMonth.isBefore(YearMonth.now());
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
