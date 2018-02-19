package domain;

public class Account {
    private Integer id;
    private String username;
    private String email;
    private Integer kcalGoal;

    public Account(String username, String email, Integer kcalGoal) {
        this.id = null;
        this.username = username;
        this.email = email;
        this.kcalGoal = kcalGoal;
    }
    public Account(Integer id, String username, String email, Integer kcalGoal) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.kcalGoal = kcalGoal;
    }
    public Account(Integer id, String username, String email){
        this.id = id;
        this.username = username;
        this.email = email;
        this.kcalGoal = 0;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getKcalGoal() {
        return kcalGoal;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", kcalGoal=" + kcalGoal +
                '}';
    }

    public void setKcalGoal(Integer kcalGoal) {
        this.kcalGoal = kcalGoal;
    }
}
