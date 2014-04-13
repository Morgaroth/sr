package pl.morgaroth.checkers.server.core;

import pl.morgaroth.checkers.api.UserToken;

public class UserTokenImpl implements UserToken {

    private final long serialVersionUID = 1L;

    private String userName;
    private int id;

    public UserTokenImpl(String userName, int id) {
        this.userName = userName;
        this.id = id;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "UserTokenImpl{" +
                "userName='" + userName + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTokenImpl userToken = (UserTokenImpl) o;
        return id == userToken.id && userName.equals(userToken.userName);
    }

    @Override
    public int hashCode() {
        int result = userName.hashCode();
        result = 31 * result + id;
        return result;
    }
}
