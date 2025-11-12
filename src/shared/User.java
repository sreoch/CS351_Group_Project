package shared;

public class User {
    private String username;
    private String password; //TODO Hash this properly

    private boolean online;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.online = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean checkPassword(String password) {
        return password.equals(this.password);
    }
}
