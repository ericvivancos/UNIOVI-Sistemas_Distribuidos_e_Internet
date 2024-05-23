package com.example.sdi2324entrega192;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * clase que gestiona como se guardan las fotos
 */
public class FileUploadUtil {



    /**
     * Metodo que guarda la foto en el directorio
     * @param uploadDir directorio donde se guardara la foto
     * @param file foto a guardar
     * @throws IOException
     */

    public static String saveFile(String uploadDir, MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path uploadPath = Paths.get(uploadDir);//sera uploads/id del usuario
        UUID uuid = UUID.randomUUID();
        fileName = uuid.toString() + "_" + fileName;//hacer que pupedas repetir imagenes en los post

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try {
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName));
            return fileName;
        } catch (IOException ex) {
            throw new IOException("Could not save file: " + fileName, ex);
        }


    }
}

