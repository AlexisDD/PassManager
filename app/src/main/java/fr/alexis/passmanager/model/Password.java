package fr.alexis.passmanager.model;

public class Password {

    private String description;
    private String account;
    private String cipherPassword;

    public Password(String description, String account, String cipherPassword){
        this.description = description;
        this.account = account;
        this.cipherPassword = cipherPassword;
    }

    public Password(String description, String cipherPassword){
        this.description = description;
        this.cipherPassword = cipherPassword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCipherPassword() {
        return cipherPassword;
    }

    public void setCipherPassword(String cipherPassword) {
        this.cipherPassword = cipherPassword;
    }

}
