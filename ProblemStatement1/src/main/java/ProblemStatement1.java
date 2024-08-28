import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.json.JSONObject;
import org.json.JSONTokener;

public class ProblemStatement1 {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <path to json file>");
            System.exit(1);
        }

        String prnNumber = args[0];
        String jsonFilePath = args[1];

        // Validate PRN Number
        if (prnNumber.trim().isEmpty()) {
            System.out.println("PRN Number cannot be empty.");
            System.exit(1);
        }

        // Read JSON file and find the value of the first instance of "destination"
        String destinationValue = extractDestinationValue(jsonFilePath);
        if (destinationValue == null) {
            System.out.println("Key 'destination' not found in the JSON file.");
            System.exit(1);
        }

        // Generate random 8-character alphanumeric string
        String randomAlphanumeric = createRandomString(8);

        // Concatenate PRN Number, Destination Value, and Random String
        String combinedString = prnNumber + destinationValue + randomAlphanumeric;

        // Generate MD5 hash
        String md5Hash = computeMD5Hash(combinedString);

        // Output the result
        System.out.println(md5Hash + ";" + randomAlphanumeric);
    }

    private static String extractDestinationValue(String jsonFilePath) {
        try (FileInputStream fileInputStream = new FileInputStream(jsonFilePath)) {
            JSONTokener jsonTokener = new JSONTokener(fileInputStream);
            JSONObject jsonObject = new JSONObject(jsonTokener);
            return findDestinationValueInJson(jsonObject);
        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private static String findDestinationValueInJson(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            if (key.equals("destination")) {
                return jsonObject.getString(key);
            }
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                String result = findDestinationValueInJson((JSONObject) value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String createRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return stringBuilder.toString();
    }

    private static String computeMD5Hash(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hashBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hashBuilder.append(String.format("%02x", b));
            }
            return hashBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
