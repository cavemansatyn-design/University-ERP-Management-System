
package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;

//pass hashing and verification
public class PasswordUtility {


    public static String hashPassword(String txtPass) {
        //Bcrypt makes salt automatically
        return BCrypt.hashpw(txtPass, BCrypt.gensalt());
    }


    public static boolean checkPassword(String txtPass, String storedHash) {
        if (storedHash == null || !storedHash.startsWith("$2a$")) {

            return false;
        }
        try {
            return BCrypt.checkpw(txtPass, storedHash);
        }
        catch (Exception e) {
            // Handle potential errors (e.g., malformed hash)
            e.printStackTrace();
            return false;
        }
    }
}