public class IntegerValidator implements TextValidator {
    public boolean isValid(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException err) {
            return false;
        }
    }
}
