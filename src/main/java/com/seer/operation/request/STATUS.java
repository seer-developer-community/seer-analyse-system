package com.seer.operation.request;

public enum STATUS {
    NORMAL(0, "正常"),
    PAUSED(1, "暂停"),
    SAVE_BLOCK(0, "保存区块"),
    SAVE_TX(0, "保存交易"),
    ROOM_OPENING(0, "开启"),
    ROOM_CLOSED(1, "停止"),
    USER_IS_NOT_PLAYER(0, "不是投注用户"),
    USER_IS_PLAYER(1, "是投注用户"),
    USER_IS_NOT_BOT_PLAYER(0, "不是机器人"),
    USER_IS_BOT_PLAYER(1, "是机器人");

    private Integer code;
    private String title;

    private STATUS(Integer code, String title) {
        this.code = code;
        this.title = title;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
