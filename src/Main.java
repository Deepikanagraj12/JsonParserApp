
import com.google.gson.JsonElement;
        import com.google.gson.JsonObject;
        import com.google.gson.JsonParser;

        import java.io.FileReader;
        import java.io.IOException;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;
        import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Validate Command-Line Arguments
        if (args.length != 2) {
            System.out.println("Usage: java -jar test.jar <roll_number> <path_to_json_file>");
            return;
        }

        String rollNumber = args[0].toLowerCase(); // Roll number in lowercase
        String filePath = args[1];

        try {
            // Parse the JSON file
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(filePath));
            String destinationValue = findDestinationValue(jsonElement);

            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in JSON file.");
                return;
            }

            // Generate a random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Concatenate values for hash
            String concatenatedString = rollNumber + destinationValue + randomString;

            // Generate MD5 hash
            String hash = generateMD5Hash(concatenatedString);

            // Output the result
            System.out.println(hash + ";" + randomString);
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    // Traverse the JSON to find the first occurrence of the key "destination"
    private static String findDestinationValue(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (String key : jsonObject.keySet()) {
                if (key.equals("destination")) {
                    return jsonObject.get(key).getAsString();
                }
                // Recursive call for nested objects
                String value = findDestinationValue(jsonObject.get(key));
                if (value != null) {
                    return value;
                }
            }
        } else if (jsonElement.isJsonArray()) {
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                String value = findDestinationValue(element);
                if (value != null) {
                    return value;
                }
            }
        }
        return null; // Key not found
    }

    // Generate an 8-character alphanumeric random string
    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Generate MD5 hash
    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder hash = new StringBuilder();
        for (byte b : hashBytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }
}
