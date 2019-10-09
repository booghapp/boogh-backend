package org.boogh.clientservice;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessor {

    private final static Logger log = LoggerFactory.getLogger(ImageProcessor.class);


    /**
     * Transform a list of base 64 string representations of images to a list
     * of File representations of images
     * @param base64Images
     * @param reportId
     * @return
     * @throws IOException
     */
    public static List<File> base64ToFile(List<String> base64Images, Long reportId) throws IOException {
        List<File> images = new ArrayList<>();

        List<int[]> imageSizes = new ArrayList<>();
        imageSizes.add(new int[]{320, 240});
        imageSizes.add(new int[]{480, 360});
        imageSizes.add(new int[]{640, 480});

        for (int i = 0; i < base64Images.size(); i++) {
            // tokenize the data
            String strImage = base64Images.get(i);
            String[] parts = strImage.split(",");
            String imageString = parts[1];

            // create a buffered image
            byte[] imageByte;
            imageByte = Base64.decodeBase64(imageString.getBytes());
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);

            BufferedImage img = ImageIO.read(bis);
            bis.close();

            // Scale the image so that it maintains its aspect ratio
            Dimension imgDim = new Dimension(img.getWidth(), img.getHeight());

            for (int j = 0; j < imageSizes.size(); j ++) {
                int[] imageSize = imageSizes.get(j);
                Dimension boundaryDim = new Dimension(imageSize[0], imageSize[1]);
                Dimension scaledDim = getScaledDimension(imgDim, boundaryDim);
                BufferedImage bi = new BufferedImage(scaledDim.width, scaledDim.height, BufferedImage.SCALE_DEFAULT);
                Graphics2D grph = (Graphics2D) bi.getGraphics();
                grph.scale(scaledDim.getWidth() / (long) img.getWidth(), scaledDim.getHeight() / (long) img.getHeight());

                grph.drawImage(img, 0, 0, null);
                grph.dispose();

                File outputFile = new File(reportId + "-" + i + "-" + j + ".jpg");
                ImageIO.write(bi, "jpg", outputFile);
                images.add(outputFile);
            }
        }

        return images;
    }

    /**
     * Delete local files that were sent to S3
     * @param imageFiles
     */
    public static void cleanUp(List<File> imageFiles){
        for (File imageFile : imageFiles) {
            imageFile.delete();
        }
    }

    /**
     * Transform list of byteArray representations of images to a list of base 64 string
     * representations of images.
     * @param images
     * @return
     */
    public static List<String> byteArrayToBase64(List<byte[]> images) {
        List<String> base64Images = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            String base64Image = Base64.encodeBase64String(images.get(i));
            base64Images.add(base64Image);
        }

        return base64Images;
    }

    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.SCALE_DEFAULT);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int originalWidth = imgSize.width;
        int originalHeight = imgSize.height;
        int boundaryWidth = boundary.width;
        int boundaryHeight = boundary.height;

        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // first check if we need to scale width
        if (originalWidth > boundaryWidth) {
            //scale width to fit
            newWidth = boundaryWidth;
            //scale height to maintain aspect ratio
            newHeight = (newWidth * originalHeight) / originalWidth;
        }

        // then check if we need to scale even with the new height
        if (newHeight > boundaryHeight) {
            //scale height to fit instead
            newHeight = boundaryHeight;
            //scale width to maintain aspect ratio
            newWidth = (newHeight * originalWidth) / originalHeight;
        }

        return new Dimension(newWidth, newHeight);
    }
}
