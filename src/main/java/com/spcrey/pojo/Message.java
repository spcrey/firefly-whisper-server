package com.spcrey.pojo;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.Data;

@Data
public class Message {

    private Integer id;

    @Pattern(groups = SendText.class, regexp = "^[\\s\\S]{1,64}$")
    private String textContent;

    @NotNull(groups = SendImage.class)
    private String imageUrl;

    private Integer sendingUserId;
    
    @NotNull(groups = {SendText.class, SendImage.class})
    private Integer receivingUserId;

    private LocalDateTime createTime;

    // other param
    private Integer lastId;

    private boolean isSendingUser;

    private Integer withUserId;

    public void setIsSendingUser(boolean isSendingUser) {
        this.isSendingUser = isSendingUser;
    }

    public boolean getIsSendingUser() {
        return isSendingUser;
    }

    public interface SendText extends Default {}

    public interface SendImage extends Default {}
}
