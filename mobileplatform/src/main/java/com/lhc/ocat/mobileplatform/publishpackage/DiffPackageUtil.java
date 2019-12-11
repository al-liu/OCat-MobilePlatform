package com.lhc.ocat.mobileplatform.publishpackage;

import ch.qos.logback.core.util.FileUtil;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-10-23 16:40
 */
@Log4j2
public class DiffPackageUtil {

    private static final Logger logger = LoggerFactory.getLogger(DiffPackageUtil.class);

    private static final String CURRENT_PATH = "./";

    private DiffPackageUtil() {}

    /**
     * 比较两个资源列表的差异（递归），将变更内容和删除内容存入列表。
     * @param newList 新版本的资源列表
     * @param oldList 旧版本的资源列表
     * @param changeResourceList 变更内容
     * @param removeResourceList 删除内容
     */
    public static void diffResources(List<Resource> newList, List<Resource> oldList, List<Resource> changeResourceList, List<Resource> removeResourceList) {
        List<Resource> sameList = newList.stream()
                .filter(resource -> oldList.contains(resource))
                .collect(Collectors.toList());
        List<Resource> removeList = oldList.stream()
                .filter(resource -> !sameList.contains(resource))
                .collect(Collectors.toList());
        List<Resource> changeList = newList.stream()
                .filter(resource -> !sameList.contains(resource))
                .collect(Collectors.toList());

        changeResourceList.addAll(changeList);
        removeResourceList.addAll(removeList);

        // 一个资源列表中，已经区分出删除资源和变更资源，需要递归查找相同文件夹的资源列表的删除资源和变更资源。
        sameList.stream().filter(resource -> resource.getType().equals(Resource.Type.DIRECTORY)).forEach(resource -> {
            int nextDirOfIndex1 = newList.indexOf(resource);
            int nextDirOfIndex2 = oldList.indexOf(resource);
            List<Resource> nextList1 = newList.get(nextDirOfIndex1).getResources();
            List<Resource> nextList2 = oldList.get(nextDirOfIndex2).getResources();
            diffResources(nextList1, nextList2, changeResourceList, removeResourceList);
        });
    }

    /**
     * 从生产包中打出补丁文件到指定目录
     * @param fromBasePath 生产包的基础路径
     * @param toBasePath 补丁包的基础路径
     * @param resources 补丁信息
     */
    public static void createPatchPackage(String fromBasePath, String toBasePath, List<Resource> resources) throws IOException{
        for (Resource resource:
        resources) {
            String fromPath = Paths.get(fromBasePath,
                    resource.getPath().replace(CURRENT_PATH, "")).toString();
            File from = new File(fromPath);

            if (resource.getType().equals(Resource.Type.DIRECTORY)) {
                createPatchPackage(fromBasePath, toBasePath, resource.getResources());
            } else {
                String toPath = Paths.get(toBasePath,
                        resource.getPath().replace(CURRENT_PATH, "")).toString();
                File to = new File(toPath);
                File parent = to.getParentFile();
                if (!parent.exists()) {
                    boolean result = parent.mkdirs();
                }
                Files.copy(from, to);
            }
        }
    }

    /**
     * 将上传生产包解压缩到指定目录
     * @param file 上传的生产包
     * @param destination 指定目录
     */
    public static void unzipUploadPackage(MultipartFile file, File destination) throws IOException {
        if (!destination.getParentFile().exists()) {
            boolean result = destination.getParentFile().mkdirs();
        }
        file.transferTo(destination);
        ZipFile zipFile = new ZipFile(destination);
        zipFile.extractAll(destination.getParent());
    }

    /**
     * 生成一个目录中所有文件的 Resource，并保存到一个根 Resource 的资源列表中。
     * @param source 目录，文件列表
     * @param basePath 基础目录，用于把文件的绝对路径替换成相对路径
     * @param resources 根 Resource 的 Resource 列表
     */
    public static void drawResourceMap(File[] source, String basePath, List<Resource> resources) {
        List<File> fileList = Arrays.asList(source);
        // TODO: 直接用 forEach 替代，不用 stream.forEach
        fileList.stream().forEach(f -> {
            Resource resource = createResource(f, basePath, resources);
            if (f.isDirectory()) {
                drawResourceMap(f.listFiles(), basePath, resource.getResources());
            }
        });
    }

    /**
     * 生成一个文件或目录的 Resource
     * @param file 文件或目录
     * @param basePath 基础路径
     * @param resources 资源列表
     * @return Resource
     */
    private static Resource createResource(File file, String basePath, List<Resource> resources) {
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
        return resource;
    }

    /**
     * 将指定目录的所有文件 copy 到指定的基础路径中
     * @param target 要被 copy 的目录
     * @param fromBase  copy 目录的根目录
     * @param toBase 目的地的根目录
     * @throws IOException IO异常
     */
    public static void moveFiles(File target, String fromBase, String toBase) throws IOException{
        File[] files = target.listFiles();
        for (File file:
                files) {
            if (file.isDirectory()) {
                moveFiles(file, fromBase, toBase);
            } else {
                String toFilePath = file.getPath().replace(fromBase, toBase);
                File toFile = new File(toFilePath);
                if (!toFile.getParentFile().exists()) {
                    toFile.getParentFile().mkdirs();
                }
                Files.copy(file, toFile);
            }
        }
    }

    static void copyFiles(String fromPath, String toPath) {
        File fromPathFile = new File(fromPath);
        if (fromPathFile.exists() && fromPathFile.isDirectory()) {
            File[] files = fromPathFile.listFiles();
            for (File file :
                    files) {
                String filename = file.getName();
                String targetPath = Paths.get(fromPath, filename).toString();
                String destPath = Paths.get(toPath, filename).toString();

                File targetFile = new File(targetPath);
                File destFile = new File(destPath);
                if (file.isDirectory()) {
                    boolean result = destFile.mkdirs();
                    if (result) {
                        copyFiles(targetPath, destPath);
                    }
                } else {
                    try {
                        if (!destFile.getParentFile().exists()) {
                            destFile.getParentFile().mkdirs();
                        }
                        Files.copy(targetFile, destFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 清理指定目录中的所有文件
     * @param target 目标目录
     */
    public static void cleanFiles(File target) {

        if (target.isDirectory()) {
            File[] files = target.listFiles();
            for (File file:
                    files) {
                cleanFiles(file);
            }
            target.delete();
        } else {
            target.delete();
        }
    }

}
