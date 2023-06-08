package com.example.camera2_1;

public class FaceRecognitionResponse {
    private String stateCode;

    private String message;

    private FaceRecognitionData data;


    public String getStateCode() {
        return stateCode;
    }

    public String getMessage() {
        return message;
    }

    public FaceRecognitionData getData() {
        return data;
    }

    public void setData(FaceRecognitionData data) {
        this.data = data;
    }

    public  void  setStateCode(String stateCode){
        this.stateCode = stateCode;
    }

    public  void  setMessage(String Message){
        this.message = Message;
    }

    public static class FaceRecognitionData {

        private String confidence;

        private String url;

        private String name;

        public void setConfidence(String confidence) {
            this.confidence = confidence;
        }

        public  void  setUrl(String url){
            this.url = url;
        }

        public  void  setName(String name){
            this.name = name;
        }

        public String getConfidence() {
            return confidence;
        }

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }
    }
}
