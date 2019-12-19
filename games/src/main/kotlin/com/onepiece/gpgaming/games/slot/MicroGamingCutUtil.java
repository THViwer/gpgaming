package com.onepiece.gpgaming.games.slot;

import com.google.common.collect.Lists;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MicroGamingCutUtil {

    /**
     * 切割图片
     * @param sourceFilePath 源文件路径
     * @param index 水平方向等分切割数
     * @return List<String>   base64编码数组
     */
    public static List<String> cutImageToBase64(String sourceFilePath, int index){
        File file = new File(sourceFilePath);
        if (file.exists()) {
            return cutImageToBase64(file,index);
        }else{
            return null;
        }
    }

    /**
     * 切割图片
     * @param sourceFilePath 源文件路径
     * @param sourceFilePath 保存文件路径
     * @param index 水平方向等分切割数
     * @return List<File>   File数组
     */
    public static List<File> cutImageToFile(String sourceFilePath,String targetDir,int index){
        File file = new File(sourceFilePath);
        if (file.exists()) {
            return cutImageToFile(file, targetDir, index);
        }else{
            return null;
        }
    }
    /**
     * 切割图片
     *
     * @param sourceFile
     *            源文件
     * @param index
     *            水平方向等分切割数
     * @return List<String> base64编码数组
     */
    public static List<String> cutImageToBase64(File sourceFile,int index) {
        List<String> list = new ArrayList<String>();
        int suffixIndex = sourceFile.getName().lastIndexOf(".");
        String suffix = sourceFile.getName().substring(suffixIndex+1);
        try {
            BufferedImage source = ImageIO.read(sourceFile);
            int width = source.getWidth(); // 图片宽度
            int height = source.getHeight(); // 图片高度
            if (index>1) {
                int cWidth = width/index; // 切片宽度
                BufferedImage image = null;
                for (int i = 0; i < index; i++) {
                    // x坐标,y坐标,宽度,高度
                    BASE64Encoder encoder = new BASE64Encoder();
                    int cw = i*cWidth;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image = source.getSubimage(cw,0,cWidth,height);
                    ImageIO.write(image, "PNG", baos);
                    byte[] bytes = baos.toByteArray();
                    list.add("data:image/"+suffix+";base64,"+encoder.encodeBuffer(bytes));
                    baos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 切割图片
     *
     * @param sourceFile
     *            源文件
     * @param index
     *            水平方向等分切割数
     * @return List<String> base64编码数组
     */
    public static List<File> cutImageToFile(File sourceFile,String targetDir,int index) {
        List<File> list = new ArrayList<File>();
        int suffixIndex = sourceFile.getName().lastIndexOf(".");
        String suffix = sourceFile.getName().substring(suffixIndex+1);
        String name =  sourceFile.getName().substring(0,suffixIndex);
        try {
            BufferedImage source = ImageIO.read(sourceFile);
            int width = source.getWidth(); // 图片宽度
            int height = source.getHeight(); // 图片高度
            if (width != 291 || height != 136) {
                return Lists.newArrayList(sourceFile);
            }

            if (index>1) {
                int cWidth = width/index; // 切片宽度
                BufferedImage image = null;
                File file = new File(targetDir);
                if (!file.exists()) { // 存储目录不存在，则创建目录
                    file.mkdirs();
                }
                for (int i = 0; i < index; i++) {
                    // x坐标,y坐标,宽度,高度
                    int cw = i*cWidth;
                    image = source.getSubimage(cw,0,cWidth,height);
                    String fileName = targetDir + "/"+name+"_"+i+ "."+suffix;
                    file = new File(fileName);
                    ImageIO.write(image,"PNG", file);
                    list.add(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
