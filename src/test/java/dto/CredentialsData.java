package dto;

import config.Config;

/**
 * Created by kgr on 10/3/2016.
 */
public class CredentialsData {
    private final String login;
    private final String password;

    public CredentialsData(String login, String password) {
        this.login = login;
        this.password = password;
    }


    public CredentialsData() {
        this.login = Config.user;
        this.password = Config.password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "CredentialsData{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
