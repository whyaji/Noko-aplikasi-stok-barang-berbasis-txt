package noko.user;

import java.util.StringTokenizer;

public class UserAccount {
    private String user, email, firstName, lastName, company, pass, joinDate, userLine, uuid;

    public UserAccount(String userLine){
        this.userLine = userLine;
        StringTokenizer stringTokenizer = new StringTokenizer(userLine, ",");
        user = stringTokenizer.nextToken();
        pass = stringTokenizer.nextToken();
        email = stringTokenizer.nextToken();
        firstName = stringTokenizer.nextToken();
        lastName = stringTokenizer.nextToken();
        company = stringTokenizer.nextToken();
        joinDate = stringTokenizer.nextToken();
        uuid = stringTokenizer.nextToken();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserLine() {
        return userLine;
    }

    public void setUserLine(String userLine) {
        this.userLine = userLine;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    
}
