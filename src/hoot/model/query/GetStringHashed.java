package hoot.model.query;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class GetStringHashed
{
    /**
     * TODO: Use some salt here!
     *
     * @param toHash The String which is about to be hashed
     * @return The created hash string.
     * @throws GeneralSecurityException Could be thrown if f.e. the Algorithm does not exist
     */
    public String execute(String toHash) throws GeneralSecurityException
    {
        StringBuilder hexString = new StringBuilder();
        MessageDigest digest    = MessageDigest.getInstance("SHA-256");
        byte[]        hash      = digest.digest(toHash.getBytes());

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
