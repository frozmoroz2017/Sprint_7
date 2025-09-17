package ru.praktikum.model;
public class Courier {
    private String login;
    private String password;
    private String firstName;

    public Courier(String login, String password, String firstName) {
        this.login = login;
         this.password = password;
         this.firstName = firstName;
     }

     public Courier() {}

     public static Courier getRandom() {
         return new Courier(
                 "courier_" + System.currentTimeMillis(),
                 "password_" + System.currentTimeMillis(),
                 "name_" + System.currentTimeMillis()
         );
     }

     public String getLogin() { return login; }
     public void setLogin(String login) { this.login = login; }
     public String getPassword() { return password; }
     public void setPassword(String password) { this.password = password; }
     public String getFirstName() { return firstName; }
     public void setFirstName(String firstName) { this.firstName = firstName; }
 }