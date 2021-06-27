package com.controll;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 验证码类
 *
 * @author aotmd
 */
@Controller
public class Captcha {
    /**
     * 获取验证码图片
     */
    @RequestMapping("/getVerifyCode")
    public void getVerificationCode(HttpServletResponse response, HttpServletRequest request) {
        try {
            int width = 94*7;
            int height = 32*7;
            BufferedImage verifyImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            //生成对应宽高的初始图片
            String randomText = VerifyCode.drawRandomText(width, height, verifyImg);
            //单独的一个类方法，出于代码复用考虑，进行了封装。
            //功能是生成验证码字符并加上噪点，干扰线，返回值为验证码字符
            System.out.println("验证码:" + randomText);
            request.getSession().setAttribute("verifyCode", randomText);
            //必须设置响应内容类型为图片，否则前台不识别
            response.setContentType("image/png");
            //获取文件输出流
            OutputStream os = response.getOutputStream();
            //输出图片流
            ImageIO.write(verifyImg, "png", os);
            //关闭流
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 对图片进行处理的类和方法
 */
class VerifyCode {
    public static String drawRandomText(int width, int height, BufferedImage verifyImg) {
        String randomText = "";
        Graphics2D graphics = (Graphics2D) verifyImg.getGraphics();
        /*设置画笔颜色-验证码背景色*/
        graphics.setColor(Color.WHITE);
        /*填充背景*/
        graphics.fillRect(0, 0, width, height);
        /*数字和字母的组合*/
        graphics.setFont(new Font("微软雅黑", Font.BOLD, (width - 10) / 4 + 1));
        String baseNumLetter = "123456789abcdefghijklmnopqrstuvwxyz";
        /*旋转原点的 x 坐标*/
        int x = 10;
        String ch;
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            /*设置字体旋转角度*/
            graphics.setColor(getRandomColor());
            /*角度小于30度*/
            int degree = random.nextInt() % 30;
            int dot = random.nextInt(baseNumLetter.length());
            ch = baseNumLetter.charAt(dot) + "";
            randomText += ch;
            graphics.rotate(degree * Math.PI / 180, x, height * 0.65);
            /*反向旋转*/
            graphics.drawString(ch, x, (int) (height * 0.65));
            graphics.rotate(-degree * Math.PI / 180, x, height * 0.65);
            x += (width - 10) / 4;
        }
        /*画干扰线*/
        for (int i = 0; i < 6; i++) {
            /* 设置随机颜色*/
            graphics.setColor(getRandomColor());
            /* 随机画线*/
            graphics.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }
        /*添加噪点*/
        for (int i = 0; i < 30; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            graphics.setColor(getRandomColor());
            graphics.fillRect(x1, y1, 2, 2);
        }
        return randomText;
    }

    /**
     * 随机取色
     */
    private static Color getRandomColor() {
        Random ran = new Random();
        return new Color(ran.nextInt(256), ran.nextInt(256), ran.nextInt(256));
    }
}
