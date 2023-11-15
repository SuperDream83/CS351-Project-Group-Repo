import java.io.Serializable;

public class Account implements Serializable {

    private String userName;
    private String password;
    private Integer balance;

    public Account(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.balance = 1000;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Integer getBalance() {
        return balance;
    }


    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public void incrementBalance(Integer amount){
        this.setBalance(balance+amount);
    }

    public void decrementBalance(Integer amount){
        this.setBalance(balance-amount);
    }



    @Override
    public String toString() {
        return "Account: " +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance;
    }

}
