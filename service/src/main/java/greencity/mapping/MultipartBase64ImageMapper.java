package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.NotSavedException;
import greencity.service.MultipartFileImpl;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartBase64ImageMapper extends AbstractConverter<String, MultipartFile> {
    /**
     * Method for converting Base64 encoded image into MultipartFile.
     *
     * @param image encoded in Base64 format to convert.
     * @return image converted to MultipartFile.
     */
    @Override
    public MultipartFile convert(String image) {
        String imageToConvert = image.substring(image.indexOf(',') + 1);
        File tempFile = new File("tempImage.jpg");
        byte[] imageByte = decodeBase64(imageToConvert);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);) {
            BufferedImage bufferedImage = ImageIO.read(bis);
            ImageIO.write(bufferedImage, "png", tempFile);
            FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(tempFile.toPath()),
                false, tempFile.getName(), (int) tempFile.length(), tempFile.getParentFile());
            try (InputStream input = new FileInputStream(tempFile);
                OutputStream outputStream = fileItem.getOutputStream()) {
                IOUtils.copy(input, outputStream);
                outputStream.flush();
                return new MultipartFileImpl("mainFile", tempFile.getName(),
                    Files.probeContentType(tempFile.toPath()), Files.readAllBytes(tempFile.toPath()));
            }
        } catch (IOException e) {
            throw new NotSavedException(ErrorMessage.CONVERT_TO_BASE64_FAILED);
        } finally {
            tempFile.deleteOnExit();
        }
    }
}
