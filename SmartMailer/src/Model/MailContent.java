/**
 *  Copyright 2015 Jasper Infotech (P) Limited . All Rights Reserved.
 *  JASPER INFOTECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0, 04-Sep-2015
 * @author vikas
 */
public class MailContent {
    List<SentenceDetails> subject = new ArrayList<>();
    List<SentenceDetails> body    = new ArrayList<>();
    String                sender;
    String                date;
    String                uniqueId;

    public List<SentenceDetails> getSubject() {
        return subject;
    }

    public void setSubject(List<SentenceDetails> subject) {
        this.subject = subject;
    }

    public List<SentenceDetails> getBody() {
        return body;
    }

    public void setBody(List<SentenceDetails> body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
