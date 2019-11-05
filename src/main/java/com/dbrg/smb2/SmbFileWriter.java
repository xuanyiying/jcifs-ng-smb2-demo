package com.dbrg.smb2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.smb.SmbFile;

/**
 * @author wangying
 * Created on 2019/11/5.
 */
public class SmbFileWriter {
    public static boolean writeSmbFile(String source, String target, CIFSContext context) throws IOException {
        if (StringUtil.isEmpty(source) || StringUtil.isEmpty(target)) {
            return false;
        }
        return writeSmbFile(Files.newInputStream(Paths.get(source)),
                target, context);
    }

    public static boolean writeSmbFile(InputStream in, String target, CIFSContext context) throws IOException {
        if (Objects.nonNull(in) && StringUtil.isNotEmpty(target)) {
            try (SmbFile file = new SmbFile(target, context)) {
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (OutputStream os = file.getOutputStream()) {
                    byte[] bytes = new byte[1024];
                    while (in.read(bytes) != -1) {
                        os.write(bytes);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static SmbFile createDirectory(String targetDir, CIFSContext context) throws MalformedURLException,
            CIFSException {
        try (SmbFile dir = new SmbFile(targetDir, context)) {
            dir.mkdir();
            return dir;
        }
    }

}
