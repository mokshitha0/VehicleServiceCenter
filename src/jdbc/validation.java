package jdbc;

public class validation {

    // Check whether input is empty
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Check whether input contains only alphabets and spaces
    public static boolean containsOnlyAlphabets(String value) {
        return value != null && value.matches("[A-Za-z ]+");
    }

    // Check whether input contains alphabets, numbers and spaces
    public static boolean isValidAlphaNumeric(String value) {
        return value != null && value.matches("[A-Za-z0-9 ]+");
    }

    // Check whether input contains only numbers
    public static boolean containsOnlyNumbers(String value) {
        return value != null && value.matches("\\d+");
    }

    // Validate email
    public static boolean isValidEmail(String email) {
        return email != null &&
               email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    // Validate phone number
    public static boolean isValidPhone(String phone) {
        return phone != null &&
               phone.matches("[6-9][0-9]{9}");
    }

    // Validate service cost
    public static boolean isValidCost(String value) {
        try {
            double cost = Double.parseDouble(value);
            return cost > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Validate service date in yyyy-MM-dd format
    public static boolean isValidDate(String date) {
        return date != null &&
               date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    // Validate service status
    public static boolean isValidStatus(String status) {
        return status != null &&
               (status.equalsIgnoreCase("BOOKED") ||
                status.equalsIgnoreCase("COMPLETED") ||
                status.equalsIgnoreCase("CANCELLED"));
    }
}