package com.example.camera2_1;

public class ApiOrderResponse {
        private String errno;
        private String error;
        private Data data;

        public void SetApiOrderResponse(String errno, String error) {
            this.errno = errno;
            this.error = error;
        }

        public  void SetApiOrderResponseData(Data data){
            this.data = data;
        }

        public String getErrno() {
            return errno;
        }

        public String getError() {
            return error;
        }

        public Data getData() {
            return data;
        }

        public static class Data {
            private String cmd_uuid;

            public void setCmd_uuid(String cmd_uuid) {
                this.cmd_uuid = cmd_uuid;
            }

            public String getCmdUuid() {
                return cmd_uuid;
            }
        }
}
