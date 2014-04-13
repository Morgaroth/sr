package pl.morgaroth.checkers.server.core;

public class ServerCheck {
    private final String owner;
    private final int number;

    public ServerCheck(String owner, int number) {
        this.owner = owner;
        this.number = number;
    }

    @Override
    public String toString() {
        return "ServerCheck{" +
                "owner='" + owner + '\'' +
                ", number=" + number +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerCheck that = (ServerCheck) o;

        if (number != that.number) return false;
        if (!owner.equals(that.owner)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + number;
        return result;
    }

    public String getOwner() {
        return owner;
    }

    public int getNumber() {
        return number;
    }
}
