package com.dbhstudios.akdmvm.util;


import java.util.Arrays;
import java.util.Objects;

public class JSONResponse {

    private boolean success;
    private String redirectUrl;
    private Integer code;
    private String[] messages;

    public JSONResponse(boolean pSuccess, String pRedirectUrl, Integer pCode, String... pMessages) {
        super();
        success = pSuccess;
        redirectUrl = pRedirectUrl;
        messages = pMessages;
        code = pCode;
    }

    public JSONResponse(boolean pSuccess, String pRedirectUrl, String... pMessages) {
        super();
        success = pSuccess;
        redirectUrl = pRedirectUrl;
        messages = pMessages;
        code = null;
    }

    public JSONResponse(boolean pSuccess, String... pMessages) {
        super();
        success = pSuccess;
        redirectUrl = null;
        messages = pMessages;
        code = null;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONResponse that = (JSONResponse) o;
        return success == that.success && Objects.equals(redirectUrl, that.redirectUrl) && Objects.equals(code, that.code) && Arrays.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(success, redirectUrl, code);
        result = 31 * result + Arrays.hashCode(messages);
        return result;
    }
}
