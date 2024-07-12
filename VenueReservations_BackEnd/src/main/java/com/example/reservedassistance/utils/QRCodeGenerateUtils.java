package com.example.reservedassistance.utils;

import cn.hutool.core.io.file.FileNameUtil;
import com.example.reservedassistance.dto.VisitorDto;
import com.example.reservedassistance.dto.VisitorInfDto;
import com.example.reservedassistance.entity.Activity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import com.google.zxing.Writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QRCodeGenerateUtils {

    private static String parseVisitorInfDto(VisitorDto visitor, Activity activity, String stadiumName){

        String telephone = visitor.getTelephone();
        String realName = visitor.getRealName();
        String identificationNum = visitor.getIdentificationNum();
        return
//                "编号：    " + visitor.getId() + "\n" +
                "活动编号：    " + activity.getId() + "\n"
                + "活动名    " + activity.getActivityName() + "\n"
                + "活动场馆    " + stadiumName + "\n"
                + "姓名：    " + realName + "\n"
                + "电话号码：    " + telephone + "\n"
                + "身份证号：    " + identificationNum + "\n";
    }

    public static String generateQRCode(VisitorDto visitor, Activity activity, String stadiumName) throws IOException {

        String content = parseVisitorInfDto(visitor, activity, stadiumName);

        int width = 300;
        int height = 300;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        try {
            Writer writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "png", baos);

//            return baos.toByteArray();

            long l = System.currentTimeMillis();
            String suffix = "png";
            String destPath = FileUtils.BASEPATH + File.separator + l + "." + suffix;

            java.nio.file.Files.write(java.nio.file.Paths.get(destPath), baos.toByteArray());


            return destPath;

        } catch (WriterException e) {
            throw new IOException("Failed to generate QR code.", e);
        }
    }


    public static List<String> generateQRCode(List<VisitorDto> visitors, Activity activity, String stadiumName) throws IOException {

        List<String> results = new ArrayList<>();

        for(VisitorDto visitor : visitors) {
            results.add(generateQRCode(visitor, activity, stadiumName));
        }

        return results;
    }
}
