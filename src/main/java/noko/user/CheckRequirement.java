package noko.user;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

public class CheckRequirement {
    private String user, email, pass;

    public CheckRequirement(String user, String email, String pass) {
        this.user = user;
        this.email = email;
        this.pass = pass;
    }

    public boolean checkEmail(String currentEmail) {
        boolean check = true;
        if (!email.equals(currentEmail)) {
            try {
                FileReader fileReader = new FileReader("userlist.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
    
                StringTokenizer stringTokenizer;
                String userData = bufferedReader.readLine();
    
                while (userData != null) {
                    stringTokenizer = new StringTokenizer(userData, ",");
                    stringTokenizer.nextToken();
                    stringTokenizer.nextToken();
                    if (stringTokenizer.nextToken().equals(email)) {
                        check = false;
                        break;
                    }
                    userData = bufferedReader.readLine();
                }
    
                fileReader.close();
                bufferedReader.close();
            } catch (Exception e) {
            }
        }

        if (!email.contains("@")) {
            check = false;
        }

        if (email.contains(" ")) {
            check = false;
        }
        return check;
    }

    public boolean checkUser(String currentUser) {
        boolean check = true;

        if (!user.equals(currentUser)) {
            try {
                FileReader fileReader = new FileReader("userlist.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
    
                StringTokenizer stringTokenizer;
                String userData = bufferedReader.readLine();
    
                while (userData != null) {
                    stringTokenizer = new StringTokenizer(userData, ",");
                    if (stringTokenizer.nextToken().equals(user)) {
                        check = false;
                        break;
                    }
                    userData = bufferedReader.readLine();
                }
    
                fileReader.close();
                bufferedReader.close();
            } catch (Exception e) {
            }
        }

        if (user.length() < 4) {
            check = false;
        }

        for (int i = 0; i < user.length(); i++) {
            if (!(Character.isAlphabetic(user.charAt(i)) || Character.isDigit(user.charAt(i)) || user.charAt(i) == ' ')) {
                check = false;
                break;
            }
        }
        return check;
    }

    public boolean checkPass(){
        boolean check = true;

        if (pass.length() < 8) {
            check = false;
        }
        
        if (!(pass.chars().anyMatch(Character::isUpperCase) && pass.chars().anyMatch(Character::isLowerCase) && pass.chars().anyMatch(Character::isDigit))) {
            check = false;
        }

        return check;
    }
}
