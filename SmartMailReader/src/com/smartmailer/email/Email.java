package com.smartmailer.email;

public class Email {

	private String subject;

	private String body;

	private String fromDate;

	private String recipient;

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

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String toString() {
		return " \n subject : " + this.getSubject() + "  \n recipient :  "
				+ this.getRecipient() + "   \n content :  " + this.body
				+ "  \n sent date :  " + this.fromDate;
	}
}
