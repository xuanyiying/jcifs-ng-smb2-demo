package com.dbrg.smb2;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

import jcifs.CIFSException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.junit.Test;

/**
 * @author wangying
 * Created on 2019/11/7.
 */
public class SmbFilesTest {
    private SmbFiles smbFiles = SmbFiles.getInstance(ShareProperties.USERNAME, ShareProperties.PASSWORD);
    private String rootUrl = ShareProperties.SERVER_NAME + "/" + ShareProperties.SHARE_ROOT + "/";
    private Random rand = new Random(System.currentTimeMillis());

    @Test
    public void renameFile() throws CIFSException, MalformedURLException {
        String src = rootUrl + makeRandomName();
        String target = rootUrl + makeRandomName();
        smbFiles.createFile(src);
        smbFiles.renameFile(src, target);
        assert smbFiles.newSmbFile(target).exists() && !smbFiles.newSmbFile(src).exists();
    }

    @Test
    public void getSmbFileName() throws CIFSException, MalformedURLException {
        String fileName = makeRandomName();
        String src = rootUrl + fileName;
        System.out.println(smbFiles.getSmbFileName(smbFiles.createFile(src)));
        assert smbFiles.getSmbFileName(smbFiles.createFile(src)).equals(fileName);
    }

    @Test
    public void moveSmbFileToSmb() throws IOException {
        String src = rootUrl + makeRandomName();
        String target = rootUrl + makeRandomName();
        String content = "Test SmbFiles!";
        SmbFile file = smbFiles.createFile(src);
        file.openOutputStream().write(content.getBytes());
        file.close();
        smbFiles.moveSmbFileToSmb(smbFiles.newSmbFile(src), target);
        assert smbFiles.newSmbFile(target).exists();
    }


    @Test
    public void exists() throws IOException {
        String exists = "//IP/Sample/0507-TH/TEST/0000/index.xml";
        assert true == smbFiles.exists(exists);
    }
    @Test
    public void notExists() throws IOException {
        String notExists = "//IP/Sample/0507-TH/TEST/0000/not-exists-index.xml";
        assert false == smbFiles.exists(notExists);
    }

    @Test
    public void createFile() throws CIFSException, MalformedURLException {
        String src = rootUrl + makeRandomName();
        assert smbFiles.createFile(src).exists();
    }

    @Test
    public void listFiles() throws CIFSException, MalformedURLException {
        String path = "//IP/SHARE-FOLDER/test.xml";
        SmbFile[] files = smbFiles.listFiles(path);
        assert files.length > 0 && files[0].exists();
  }

    @Test
    public void writeSmbFile() throws IOException {
        String target = rootUrl + makeRandomDirectoryName() + "test.pdf";
        smbFiles.writeSmbFile(new FileInputStream(ShareProperties.SOURCE_PATH), target);
        assert smbFiles.newSmbFile(target).exists();
    }

    @Test
    public void getSmbUncPath() throws MalformedURLException {
        System.out.println(smbFiles.getSmbUncPath(rootUrl + ShareProperties.FILE_PATH));
    }

    @Test
    public void getSmbPath() throws MalformedURLException, SmbException {

        SmbFile[] files = smbFiles.listFiles(ShareProperties.SERVER_NAME + "/" + ShareProperties.SHARE_ROOT);
        for (SmbFile file : files) {
            System.out.println("path:" + file.getPath());
            System.out.println("unc path:" + file.getUncPath());
            System.out.println("CanonicalPath:" + file.getCanonicalPath());
            System.out.println("name:" + file.getName());
            System.out.println("CanonicalPath:" + file.getDfsPath());
            System.out.println("CanonicalUNCPath:" + file.getCanonicalUncPath());
        }
    }

    @Test
    public void newSmbFile() throws MalformedURLException, SmbException {
        assert smbFiles.newSmbFile(rootUrl + ShareProperties.FILE_PATH).exists();
    }

    @Test
    public void renameDirectory() throws MalformedURLException, CIFSException {
        String src = rootUrl + makeRandomDirectoryName();
        String target = rootUrl + makeRandomDirectoryName();
        smbFiles.createDirectory(src);
        smbFiles.renameFile(src, target);
        assert smbFiles.newSmbFile(target).getName().equals(target) && !smbFiles.newSmbFile(target).exists();
    }

    private String makeRandomName() {
        return "jcifs-test-" + Math.abs(this.rand.nextLong()) + ".txt";
    }


    private String makeRandomDirectoryName() {
        return makeRandomName() + "/";
    }
}