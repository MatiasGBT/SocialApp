package com.mgbt.socialapp_backend.utility_classes;

import com.mgbt.socialapp_backend.model.entity.UserApp;

public class JsonUserAppMessage {
    private String message;
    private Integer status;
    private UserApp user;

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

    public UserApp getUser() {
        return user;
    }
}
