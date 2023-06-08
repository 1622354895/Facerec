package com.example.camera2_1;

import java.io.FileInputStream;
import java.io.InputStream;

public class Base64Img {
    /**
     * @Title: getImageStrFromUrl
     * @Description: 将一张网络图片转化成Base64字符串
     * @param imgURL 网络资源位置
     * @return Base64字符串
     */
    public static String getImageStrFromUrl(String imgURL) {
        byte[] data = null;
        try {
            data = HttpUtil.getByte(imgURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回Base64编码过的字节数组字符串
        return Base64Util.encode(data);
    }

    /**
     * @Title: getImageStrFromPath
     * @Description: 将一张本地图片转化成Base64字符串
     * @param imgPath
     * @return
     */
    public static String getImageStrFromPath(String imgPath) {
        byte[] data = null;
        // 读取图片字节数组
        try( InputStream in = new FileInputStream(imgPath)) {
            data = new byte[in.available()];
            in.read(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        // 返回Base64编码过的字节数组字符串
        return Base64Util.encode(data);
    }
}
