package com.dbrg.smb2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import jcifs.CIFSContext;
import jcifs.smb.*;

/**
 * @author wangying
 * Created on 2019/11/5.
 */
public class SmbFileReader {
    public InputStream readSmbFile(String path, CIFSContext context) throws IOException {
        SmbFile file =  new SmbFile(path,context);
        if (Objects.isNull(file) || !file.exists()){
            throw new FileNotFoundException(path) ;
        }
        return file.getInputStream();
    }


}
