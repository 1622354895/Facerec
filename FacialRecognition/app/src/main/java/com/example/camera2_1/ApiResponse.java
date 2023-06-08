package com.example.camera2_1;

public class ApiResponse {
    private String stateCode;
    private String message;
    private ApiData data;

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiData getData() {
        return data;
    }

    public void setData(ApiData data) {
        this.data = data;
    }

    public static class ApiData {
        private String accessToken;
        private int expireTime;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }
    }
}
