package com.lhc.ocat.mobileplatform;

import com.lhc.ocat.mobileplatform.publishpackage.DiffPackageUtil;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author lhc
 * @date 2019-10-23 10:53
 */
public class ZipFileTest {

    private static Logger logger = LoggerFactory.getLogger(ZipFileTest.class);

    @Test
    public void testZip() {
        try {
            File zipFile = new File("/Users/lhc/Documents/WorkSpace/Projects/OCat/mobileplatform/temp/");
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            new ZipFile("v3.zip", "a123".toCharArray()).addFolder(zipFile, zipParameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRenameFile() {
        File fromFile = new File("/Users/lhc/Documents/WorkSpace/Projects/OCat/mobileplatform/workshop/1.0.0");
        File toFile = new File("/Users/lhc/Documents/WorkSpace/Projects/OCat/docker/nginx/html/ocat_demo");
        try {
            DiffPackageUtil.moveFiles(fromFile, fromFile.getPath(), toFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteDir() {
        File file = new File("/Users/lhc/Documents/WorkSpace/Projects/OCat/mobileplatform/workshop");
        DiffPackageUtil.cleanFiles(file);
    }

    @Test
    public void testFileName() {
        File file = new File("/Users/lhc/Documents/WorkSpace/Projects/OCat/docker/nginx/html/download/packages/1.0.0/all.zip");
        logger.info("file name:" + file.getName());
    }

}
