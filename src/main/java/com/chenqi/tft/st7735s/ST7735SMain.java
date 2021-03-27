package com.chenqi.tft.st7735s;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ST7735SMain {

    private static Logger LOG = Logger.getLogger(ST7735SMain.class);
    public static void main(String[] args) throws IOException, InterruptedException {

        //展示天气图片
        ST7735sDriver.getInstance().drawImg16BitColorOptimization(DrawImg.getWeatherImg());

    }

    /**
     * 获取jar包同路径下的所有后缀为jpg的图片并展示
     */
    public static void showPicsFromSameFolder() throws IOException, InterruptedException {
        for (; ; ) {

            String path = ST7735SMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            path = path.substring(0, path.lastIndexOf(File.separator) + 1);
            System.out.println(path);
            File file = new File(path);
            File[] files = file.listFiles();
            for(File eachFile : files){
                if(eachFile.getName().toLowerCase().endsWith("jpg"))
                {
                    String imgPath = eachFile.getCanonicalPath();
                    System.out.println("imgPath = " + imgPath);
                    Image srcImage = ImageIO.read(new File(imgPath));
                    ST7735sDriver.getInstance().drawImg16BitColorOptimization(srcImage);
                    Thread.sleep(2000);
                }
            }
        }
    }
}
