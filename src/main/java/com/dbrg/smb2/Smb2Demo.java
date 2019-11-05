package com.dbrg.smb2;


import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangying
 * Created on 2019/11/5.
 */
@Slf4j
public class Smb2Demo {

    private static final Path TARGET_PATH = Paths.get("D:/SMB2-TEST/", "mySQL.msi");
    private static final String SOURCE_PATH = "D:/SMB2-TEST/Spring.pdf";
    private static final String TARGET_DIR = "testDir";

    public static void main(String[] args) throws IOException {
        CIFSContext context = withNTLMCredentials(SingletonContext.getInstance());
        testRead(context);
        testWrite(context);
    }

    private static void testWrite(CIFSContext context) throws IOException {
        long start = System.currentTimeMillis();
        String dir = getShareRootURL() + TARGET_DIR;
        String targetPath = dir + File.separator + "Spring.pdf";
        SmbFileWriter.createDirectory(dir, context);
        boolean result = SmbFileWriter.writeSmbFile(SOURCE_PATH, targetPath, context);
        log.info("Write File success:{}", result);
        long end = System.currentTimeMillis();
        log.info("Write File success,use time:{} {}" ,(end - start),"ms");
    }

    private static void testRead(CIFSContext context) throws IOException {
        long start = System.currentTimeMillis();
        SmbFileReader reader = new SmbFileReader();
        InputStream in = reader.readSmbFile(getShareRootURL() + ShareProperties.FILE_PATH, context);
        Files.copy(in, TARGET_PATH, StandardCopyOption.REPLACE_EXISTING);
        long end = System.currentTimeMillis();
        log.info("Read File success,use time:" + (end - start) + "ms");
    }


    public static CIFSContext withNTLMCredentials(CIFSContext ctx) {
        return ctx.withCredentials(new NtlmPasswordAuthenticator(ShareProperties.DOMAIN,
                ShareProperties.USERNAME, ShareProperties.PASSWORD));
    }

    public static String getShareRootURL() {
        return "smb://" + ShareProperties.SERVER_NAME + "/" + ShareProperties.SHARE_ROOT + "/";
    }

    static class SmbFileReader {
        public InputStream readSmbFile(String path, CIFSContext context) throws IOException {
            SmbFile file = new SmbFile(path, context);
            if (Objects.isNull(file) || !file.exists()) {
                throw new FileNotFoundException(path);
            }
            return file.getInputStream();
        }
    }

    static class SmbFileWriter {
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
                    try (SmbFile parent = new SmbFile(file.getParent(), context)) {
                        if (!parent.exists()) {
                            createDirectory(file.getParent(), context);
                        }
                        if (!file.exists()) {
                            file.createNewFile();
                        }
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

    class ShareProperties {

        public static final String DOMAIN = "";
        public static final String USERNAME = "administrator";
        public static final String PASSWORD = "pass@word1";
        public static final String SHARE_ROOT = "SHAREFOLDER";
        public static final String FILE_PATH = "mysql-installer-community-5.7.20.msi";
        public static final String SERVER_NAME = "WIN-NM5I0S6N5MJ";
    }

}
