package com.mgbt.socialapp_backend.utility_classes;

import java.util.Date;

public class JsonMessageEntity {
    private Long messageId;
    private String messageText;
    private String messagePhoto;
    private Date messageDate;

    public Long getMessageId() {
        return messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessagePhoto() {
        return messagePhoto;
    }

    public Date getMessageDate() {
        return messageDate;
    }
}
