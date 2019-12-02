package com.lhc.ocat.mobileplatform;

import com.google.common.io.Files;
import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-10-22 14:43
 */
public class ListFilterTest {

    private static Logger logger = LoggerFactory.getLogger(ListFilterTest.class);

    @Test
    public void testResourceEqueal() {
        Resource resource1 = new Resource();
        resource1.setName("1");
        resource1.setPath("2");
        resource1.setContentHash("3");
        resource1.setType(Resource.Type.DIRECTORY);

        Resource resource2 = new Resource();
        resource2.setName("1");
        resource2.setPath("3");
        resource2.setContentHash("3");
        resource2.setType(Resource.Type.DIRECTORY);

        Assert.assertEquals(resource1, resource2);
    }

    @Test
    public void testResourceListFilter() {
        List<Resource> list1 = new ArrayList<>();
        // 1,2,3,4
        for (int i = 0; i < 5; i++) {
            String value = String.valueOf(i);
            Resource resource = new Resource();
            resource.setName(value);
            resource.setContentHash(value);
            resource.setType(Resource.Type.FILE);
            list1.add(resource);
        }
        List<Resource> list2 = new ArrayList<>();
        // 3,4,5,6,7
        for (int i = 3; i < 8; i++) {
            String value = String.valueOf(i);
            Resource resource = new Resource();
            resource.setName(value);
            resource.setContentHash(value);
            resource.setType(Resource.Type.FILE);
            list2.add(resource);
        }

        List<Resource> sameResourceList = list1.stream().filter(resource -> list2.contains(resource)).collect(Collectors.toList());
        List<Resource> removeList = list2.stream().filter(resource -> !sameResourceList.contains(resource)).collect(Collectors.toList());
        List<Resource> changeList = list1.stream().filter(resource -> !sameResourceList.contains(resource)).collect(Collectors.toList());

        logger.info("相同资源:"+sameResourceList);
        logger.info("移除资源:"+removeList);
        logger.info("变更资源:"+changeList);

        List<Resource> same = list1.stream().collect(Collectors.toList());
        same.retainAll(list2);
        List<Resource> change = list1.stream().collect(Collectors.toList());
        change.removeAll(same);
        List<Resource> remove = list2.stream().collect(Collectors.toList());
        remove.removeAll(same);

        logger.info("相同资源:"+same);
        logger.info("移除资源:"+remove);
        logger.info("变更资源:"+change);
    }

    @Test
    public void testCopyFile() {
        File from = new File("/Users/lhc/Documents/WorkSpace/Projects/OCat/mobileplatform/target/classes/test/version3/fonts/test/home_header.66d0d713.png");
        File to = new File("/Users/lhc/Documents/WorkSpace/Projects/OCat/mobileplatform/temp/hello/world/home_header.66d0d713.png");
        logger.info("getParent:"+to.getParentFile());
//        try {
//            copyFile(from, to);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void copyFile(File from, File to) throws IOException {
        File parent = to.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        Files.copy(from, to);
    }

    @Test
    public void testFilePathSeparator() {
        logger.info("pathSeparator=>" + File.pathSeparator);
        logger.info("pathSeparator2:" + File.pathSeparatorChar);
        File base = new File("/Users/lhc/Documents/WorkSpace/Projects/OCat/mobileplatform/temp");
        String pngPath = base.getPath() + File.pathSeparator + "a.png";
        String pngPath2 = Paths.get(base.getPath(), "a.png").toString();
        logger.info("pngPath:"+pngPath);
        logger.info("pngPath2:"+pngPath2);
    }
}
