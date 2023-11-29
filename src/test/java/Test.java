public class Test {
    public static void main(String[] args) {
        String originalString = "org.senergy.ams.model.entity.PrivilegeGroup";

        // Find the last index of dot
        int lastDotIndex = originalString.lastIndexOf('.');

        // Check if a dot was found
        if (lastDotIndex != -1) {
            // Remove the last dot
            String resultString = originalString.substring(0, lastDotIndex);

            System.out.println("Original String: " + originalString);
            System.out.println("Result String: " + resultString);
        } else {
            System.out.println("No dot found in the string.");
        }
    }
}
