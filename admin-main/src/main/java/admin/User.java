package admin;

public class User {
    private String idKey;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String emailAddress;
    private String role;

    public User(String idKey, String username, String fullName, String phoneNumber, String emailAddress, String role) {
        this.idKey = idKey;
        this.username = username;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.role = role;
    }

    public String getIdKey() {
        return idKey;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getRole() {
        return role;
    }
}
