package Model;

public class Email {

    private String subject;

    private String body;

    private String fromDate;

    private String sender;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String toString() {
        return " \n subject : " + this.getSubject() + "  \n recipient :  " + this.getSender() + "   \n content :  " + this.body + "  \n sent date :  " + this.fromDate;
    }
}
