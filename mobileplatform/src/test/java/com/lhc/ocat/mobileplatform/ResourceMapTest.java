package com.lhc.ocat.mobileplatform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-10-18 17:26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ResourceMapTest {

    private static Logger logger = LoggerFactory.getLogger(ResourceMapTest.class);

    private List<Resource> changeResourceList = new ArrayList<>();
    private List<Resource> removeResourceList = new ArrayList<>();

    @Test
    public void testRecursionFile() {
        try {
            File base1 = ResourceUtils.getFile("classpath:test/version2");
            String basePath = base1.getPath();
            logger.info(String.format("BasePath1:%s", basePath));

            Resource resource1 = new Resource();
            resource1.setVersionName("2.0");
            resource1.setVersionCode(2);
            resource1.setName("./");
            resource1.setPath("./");
            resource1.setResources(new ArrayList<>());

            recursionFile(base1.listFiles(), basePath, resource1.getResources());

            File base2 = ResourceUtils.getFile("classpath:test/version3");
            String basePath2 = base2.getPath();
            logger.info(String.format("basePath2:%s", basePath2));

            Resource resource2 = new Resource();
            resource2.setVersionName("3.0");
            resource2.setVersionCode(3);
            resource2.setName("./");
            resource2.setPath("./");
            resource2.setResources(new ArrayList<>());

            recursionFile(base2.listFiles(), basePath2, resource2.getResources());

            diffResources(resource2.getResources(), resource1.getResources());

            logger.info("差量对比，删除部分:" + removeResourceList);
            logger.info("差量对比，变更部分:" + changeResourceList);
            Patch patch = new Patch();
            patch.setNewVersion("v3");
            patch.setOldVersion("v2");
            patch.setRemoveResources(removeResourceList);
            patch.setChangeResources(changeResourceList);

            logger.info("测试序列化 json ->:");
            String r1Json = resourceToJson(resource1);
            Resource r1 = jsonToResource(r1Json);
            logger.info(String.format("Json To Resource: %s", r1));

            String patchJson = patchToJson(patch);
            Patch patch1 = jsonToPatch(patchJson);
            logger.info(String.format("Json To Patch: %s", patch1));

            createPatchPackage(basePath2+"/",
                    "/Users/lhc/Documents/WorkSpace/Projects/OCat/mobileplatform/temp/",
                    changeResourceList);

            zipFile(new File("/Users/lhc/Documents/WorkSpace/Projects/OCat/mobileplatform/temp/"));
            logger.info("END");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void recursionFile(File[] source, String basePath, List<Resource> resources) {

        List<File> fileList = Arrays.asList(source);
        fileList.stream().forEach(f -> {
            Resource resource = createResource(f, basePath, resources);
            if (f.isDirectory()) {
                recursionFile(f.listFiles(), basePath, resource.getResources());
            }
        });

    }

    private Resource createResource(File file, String basePath, List<Resource> resources) {
        String relativePath = file.getPath().replace(basePath, ".");
        Resource resource = new Resource();
        resource.setPath(relativePath);
        if (file.isDirectory()) {
            resource.setName(relativePath);
            resource.setType(Resource.Type.DIRECTORY);
            String md5 = Hashing.md5().hashUnencodedChars(relativePath).toString();
            resource.setContentHash(md5);
            resource.setResources(new ArrayList<>());
        } else {
            resource.setName(file.getName());
            resource.setType(Resource.Type.FILE);
            try {
                byte[] bytes = Files.asByteSource(file).read();
                String md5 = Hashing.md5().hashBytes(bytes).toString();
                resource.setContentHash(md5);
            } catch (IOException e) {
                resource.setContentHash("");
            }
        }
        resources.add(resource);
        logger.info("resource:"+resource);
        return resource;
    }

    private void diffResources(List<Resource> newList, List<Resource> oldList) {
        List<Resource> sameList = newList.stream().filter(resource -> oldList.contains(resource)).collect(Collectors.toList());
        List<Resource> removeList = oldList.stream().filter(resource -> !sameList.contains(resource)).collect(Collectors.toList());
        List<Resource> changeList = newList.stream().filter(resource -> !sameList.contains(resource)).collect(Collectors.toList());

        changeResourceList.addAll(changeList);
        removeResourceList.addAll(removeList);

        sameList.stream().filter(resource -> resource.getType().equals(Resource.Type.DIRECTORY)).forEach(resource -> {

            logger.info("递归未变更的目录:"+resource.getPath());

            int nextDirOfIndex1 = newList.indexOf(resource);
            int nextDirOfIndex2 = oldList.indexOf(resource);
            List<Resource> nextList1 = newList.get(nextDirOfIndex1).getResources();
            List<Resource> nextList2 = oldList.get(nextDirOfIndex2).getResources();
            diffResources(nextList1, nextList2);
        });
    }

    // "/Users/lhc/Documents/WorkSpace/Projects/OCat/mobileplatform/temp/"
    // ""
    private void createPatchPackage(String fromBasePath, String toBasePath, List<Resource> resources) {
        resources.stream().forEach(resource -> {
            String fromPath = resource.getPath().replace("./", fromBasePath);
            File from = new File(fromPath);

            logger.info("From getPath:" + resource.getPath());
            logger.info("From basePath:" + fromBasePath);
            logger.info("复制 From:" + from.getPath());
            if (resource.getType().equals(Resource.Type.DIRECTORY)) {
                createPatchPackage(fromBasePath, toBasePath, resource.getResources());
            } else {
                String toPath = toBasePath+resource.getPath().replace("./", "");
                // 封装一个 copy file 的方法，支持目的地 file 无对应文件夹就创建对应文件夹
                File to = new File(toPath);
                logger.info("To getName:" + resource.getName());
                logger.info("To getPath:" + resource.getPath());
                logger.info("To basePath:" + toBasePath);
                logger.info("复制 To:" + to.getPath());
                try {
                    copyFile(from, to);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void copyFile(File from, File to) throws IOException {
        File parent = to.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        Files.copy(from, to);
    }

    private void zipFile(File file) {
        try {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            new ZipFile("patch3.zip", "a123".toCharArray()).addFolder(file, zipParameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    private String resourceToJson(Resource resource) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(resource);
            logger.info("Resource 序列化成 JSON:" + json);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Resource jsonToResource(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Resource resource = mapper.readValue(json, Resource.class);
            return resource;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String patchToJson(Patch patch) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(patch);
            logger.info("Patch 序列化成 JSON:" + json);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Patch jsonToPatch(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Patch patch = mapper.readValue(json, Patch.class);
            return patch;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
