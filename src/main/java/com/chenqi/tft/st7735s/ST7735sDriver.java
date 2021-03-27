package com.chenqi.tft.st7735s;

import com.pi4j.io.gpio.*;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ST7735sDriver {

    public static final int HEIGHT = 160;
    public static final int WIDTH = 128;

    private ST7735sDriver() {
    }

    private static ST7735sDriver st7735Driver = new ST7735sDriver();

    public static ST7735sDriver getInstance() {
        return st7735Driver;
    }

    final static GpioPinDigitalOutput DC;
    final static GpioPinDigitalOutput RST;
    // SPI device
    public static SpiDevice spi;

    static {
        RaspiGpioProvider raspiGpioProvider =
                new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING);
        GpioFactory.setDefaultProvider(raspiGpioProvider);

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        //DC等同于RS
        DC = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_05, "DC", PinState.HIGH);
        RST = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_23, "RST", PinState.HIGH);
        try {
            spi = SpiFactory.getInstance(SpiChannel.CS0, //因为CS连接着CE0，所以使用CS0控制片选
                    1000000,
                    SpiDevice.DEFAULT_SPI_MODE); // default mode 0
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缩放长宽符合屏幕的图片
     *
     * @param inputImage
     * @return
     * @throws IOException
     */
    private static BufferedImage getFitImg(Image inputImage) throws IOException {

        //定义一个BufferedImage对象，用于保存缩小后的位图
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();

        //将原始位图缩小后绘制到bufferedImage对象中
        graphics.drawImage(inputImage, 0, 0, WIDTH, HEIGHT, null);

        return bufferedImage;
    }

    /**
     * 画图（16bit的色彩）优化版本
     *
     * @param inputImage
     * @throws IOException
     */
    public void drawImg16BitColorOptimization(Image inputImage) throws IOException {
        System.out.println("start to write Lcd Img ，drawImg16BitColorOptimization");

        BufferedImage bufferedImage = getFitImg(inputImage);
        byte[] colorBytes = new byte[WIDTH * 2];
        int colorBytesIndex = 0;
        init();
        DC.high();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int rgb = bufferedImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xff; //获取红色的色值，每个色值占8位
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;
                red = red >> 3; //st7735s的红色占5位
                green = green >> 2; //st7735s的绿色占6位
                blue = blue >> 3; //st7735s的蓝色占5位

                int highBit = 0x00;
                int lowBit = 0x00;
                highBit = highBit | (blue << 3) | (green >> 3);
                lowBit = lowBit | (green << 5) | red;

                colorBytes[colorBytesIndex] = (byte) highBit;
                colorBytesIndex++;
                colorBytes[colorBytesIndex] = (byte) lowBit;
                colorBytesIndex++;

            }
            spi.write(colorBytes);
            colorBytesIndex = 0;
        }

    }

    /**
     * 写入指令 DC低电平，写入指令
     *
     * @param date
     * @throws IOException
     */
    public void sendCommand(int date) throws IOException {
        DC.low();
        spi.write((byte) date);
    }

    /**
     * 写入数据 DC高电平，写入数据
     *
     * @param date
     * @throws IOException
     */
    public void sendData(int date) throws IOException {
        DC.high();
        spi.write((byte) date);
    }

    public void LcdReset() {
        RST.low();
        RST.high();
    }

    private boolean isInit = false;

    public void init() throws IOException {
        if (isInit) {
            return;
        }
        LcdReset();
        sendCommand(0x11);
        sendCommand(0x26); //Set Default Gamma
        sendData(0x04);
        sendCommand(0xB1); //Set Frame Rate
        sendData(0x0e);
        sendData(0x10);
        sendCommand(0xC0); //Set VRH1[4:0] & VC[2:0] for VCI1 & GVDD
        sendData(0x08);
        sendData(0x00);
        sendCommand(0xC1); // Set BT[2:0] for AVDD & VCL & VGH & VGL
        sendData(0x05);
        sendCommand(0xC5); //Set VMH[6:0] & VML[6:0] for VOMH & VCOML
        sendData(0x38);
        sendData(0x40);
        sendCommand(0x3a); // Set Color Format
        sendData(0x05);
        sendCommand(0x36); // RGB
        sendData(0xc8);

        sendCommand(0x2A);  // Set Column Address
        sendData(0x00);
        sendData(0x00);
        sendData(0x00);
        sendData(0x7F);
        sendCommand(0x2B);  // Set Page Address
        sendData(0x00);
        sendData(0x00);
        sendData(0x00);
        sendData(0x9F);

        sendCommand(0xB4);
        sendData(0x00);

        sendCommand(0xf2); // Enable Gamma bit
        sendData(0x01);

        sendCommand(0xE0);
        sendData(0x3f); // p1
        sendData(0x22);// p2
        sendData(0x20);// p3
        sendData(0x30);// p4
        sendData(0x29);// p5
        sendData(0x0c);// p6
        sendData(0x4e);// p7
        sendData(0xb7);// p8
        sendData(0x3c);// p9
        sendData(0x19);// p10
        sendData(0x22);// p11
        sendData(0x1e);// p12
        sendData(0x02);// p13
        sendData(0x01);// p14
        sendData(0x00);// p15
        sendCommand(0xE1);
        sendData(0x00); // p1
        sendData(0x1b);// p2
        sendData(0x1f);// p3
        sendData(0x0f);// p4
        sendData(0x16);// p5
        sendData(0x13);// p6
        sendData(0x31);// p7
        sendData(0x84);// p8
        sendData(0x43);// p9
        sendData(0x06);// p10
        sendData(0x1d);// p11
        sendData(0x21);// p12
        sendData(0x3d);// p13
        sendData(0x3e);// p14
        sendData(0x3f);// p15

        sendCommand(0x29); //Display On
        sendCommand(0x2C);

        isInit = true;
    }
}
