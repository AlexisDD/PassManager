package fr.alexis.passmanager.model;

public class Password {

    private String description;
    private String cipherPassword;

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

    public String getCipherPassword() {
        return cipherPassword;
    }

    public void setCipherPassword(String cipherPassword) {
        this.cipherPassword = cipherPassword;
    }
}
