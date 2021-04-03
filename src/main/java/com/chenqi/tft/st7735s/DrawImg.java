package com.chenqi.tft.st7735s;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 作图类
 */
public class DrawImg {
    private static Logger LOG = Logger.getLogger(DrawImg.class);

    /**
     * 获取天气预报图片
     *
     * @return
     * @throws IOException
     */
    public static BufferedImage getWeatherImg() throws IOException {
        LOG.debug("start to GetSojsonWeatherImg ");
        int width = ST7735sDriver.WIDTH;
        int height = ST7735sDriver.HEIGHT;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        String city = "南京";
        String quality = "空气质量：优良";
        String week = "星期四";
        String temperature = "温度:26℃~31℃";
        String type = "雷阵雨";
        String wind = "冬风2级";
        g.setFont(new Font("微软雅黑", Font.BOLD, 30));
        g.setColor(new Color(0x25FF22));
        g.drawString(city, 0, 26);
        g.setColor(new Color(0xFFF300));
        g.drawLine(0, 33, 128, 33);
        String date_y = "07月30 15:01:31";
        Color color = new Color(0xFF5D7D);
        g.setColor(color);
        String tip = "请带伞";
        g.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        g.drawString(week, 66, 30);
        g.drawString(date_y, 0, 49);
        g.drawString(wind, 0, 66);
        g.drawString(temperature, 0, 84);
        g.drawString(quality, 0, 102);
        g.setFont(new Font("微软雅黑", Font.BOLD, 20));
        g.drawString(type, 0, 128);
        g.drawString(tip, 0, 150);
        ST7735SMain getLcdImg = new ST7735SMain();
        InputStream is = getLcdImg.getClass().getResourceAsStream("/weatherIcon/b_5.gif");
        Image fbImg = javax.imageio.ImageIO.read(is);
        g.drawImage(fbImg, 73, 110, 50, 46, null);

        return image;
    }

    public static void main(String[] args) throws IOException {
        //生成天气预报图片
        BufferedImage bufferedImage = getWeatherImg();
        ImageIO.write(bufferedImage, "jpg", new File("D:\\weatherFuture.jpg"));

        bufferedImage = getPoetry();
        ImageIO.write(bufferedImage, "jpg", new File("D:\\Poetry.jpg"));
    }

    /**
     * 获取诗歌图片
     *
     * @return
     * @throws IOException
     */
    public static BufferedImage getPoetry() {
        LOG.debug("start to getPoetry ");
        int width = ST7735sDriver.WIDTH;
        int height = ST7735sDriver.HEIGHT;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setFont(new Font("华文新魏", Font.PLAIN, 28));
        g.setColor(new Color(0x25FF22));
        g.drawString("长干行", 10, 28);

        g.setFont(new Font("华文新魏", Font.PLAIN, 20));
        g.setColor(new Color(0x64FFE7));
        g.drawString("李白", 92, 28);

        g.setFont(new Font("华文新魏", Font.PLAIN, 23));
        g.setColor(new Color(0xFFAC25));
        g.drawString("妾发初覆额，", 0, 60);
        g.drawString("折花门前剧。", 0, 85);
        g.drawString("郎骑竹马来，", 0, 110);
        g.drawString("绕床弄青梅。", 0, 135);
        return image;
    }
}
