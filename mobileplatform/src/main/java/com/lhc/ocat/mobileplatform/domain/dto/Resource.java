package com.lhc.ocat.mobileplatform.domain.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author lhc
 * @date 2019-10-22 09:40
 */
@Getter
@Setter
@ToString
public class Resource {
    /**
     * 如果类型是文件则是文件名，如果是目录则是相对路径
     */
    private String name;
    /**
     * 相对路径 ./
     */
    private String path;
    /**
     * 如果类型是文件则是文件内容 md5 hash，如果是目录则是相对路径的 md5 hash
     */
    private String contentHash;
    /**
     * 如果类型是目录，则是下一层目录的资源列表
     */
    private List<Resource> resources;
    /**
     * 文件 或 目录
     */
    private Type type;
    /**
     * 生产包资源对应的版本号，eg 1.0, 2.0
     */
    private String versionName;

    private Integer versionCode;

    public enum Type {
        /**
         * 文件夹类型
         */
        DIRECTORY,
        /**
         * 文件类型
         */
        FILE
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return getPath().equals(resource.getPath()) &&
                getContentHash().equals(resource.getContentHash()) &&
                getType() == resource.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath(), getContentHash(), getType());
    }

    static public ResourceDO toResourceDO(Resource resource) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String data = objectMapper.writeValueAsString(resource);
            ResourceDO resourceDO = new ResourceDO();
            resourceDO.setVersionName(resource.versionName);
            resourceDO.setVersionCode(resource.versionCode);
            resourceDO.setData(data);
            return resourceDO;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public Resource toResource(ResourceDO resourceDO) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Resource resource = objectMapper.readValue(resourceDO.getData(), Resource.class);
            return resource;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
