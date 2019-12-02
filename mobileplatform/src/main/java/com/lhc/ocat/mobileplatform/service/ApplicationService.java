package com.lhc.ocat.mobileplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhc.ocat.mobileplatform.domain.dto.Application;
import com.lhc.ocat.mobileplatform.domain.dto.Patch;
import com.lhc.ocat.mobileplatform.domain.dto.Resource;
import com.lhc.ocat.mobileplatform.domain.vo.PageVO;
import com.lhc.ocat.mobileplatform.domain.vo.PatchVO;
import com.lhc.ocat.mobileplatform.domain.vo.ResourceVO;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import com.lhc.ocat.mobileplatform.exception.BaseException;

import java.util.List;

/**
 * @author lhc
 * @date 2019-11-14 18:29
 */
public interface ApplicationService {

    /**
     * 注册应用
     * @param name 应用名称
     * @param bundleId 应用包名
     * @return appId
     * @throws ApiException 业务接口异常
     */
    String signup(String name, String description) throws ApiException;

    /**
     * 认证应用身份
     * @param bundleId 应用包名
     * @param appId 应用ID
     * @param appSecret 应用密钥
     * @throws ApiException 业务接口异常
     */
    Application authenticate(String appId, String appSecret)  throws ApiException;

    /**
     * 修改应用程序基本信息
     * @param applicationId  应用程序id
     * @param name  应用名称
     * @param description 应用描述
     * @throws ApiException 业务接口异常
     */
    void updateApplication(Long applicationId, String name, String description) throws ApiException;

    /**
     * 获取指定 id 的应用程序信息
     * @param applicationId 应用程序Id
     * @return Application
     * @throws ApiException 业务接口异常
     */
    Application getApplicationById(Long applicationId) throws ApiException;

    /**
     * 用关键字搜索应用程序信息
     * @param keyword 关键字，NULL或Empty则查询全部
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return Application 列表
     * @throws ApiException 业务接口异常
     */
    PageVO<Application> listApplicationByKeyword(String keyword, Long currentPage, Long pageSize) throws ApiException;

    /**
     * 查询指定 applicationId 的所有版本资源信息
     * @param applicationId 应用程序id
     * @param currentPage 当前页
     * @param pageSize 每页数量
     * @return ResourceVO 列表
     * @throws ApiException 业务接口异常
     */
    PageVO<ResourceVO> listResourceByApplicationId(Long applicationId, Long currentPage, Long pageSize) throws ApiException;

    /**
     * 查询指定应用和资源版本下所有补丁
     * @param applicationId 应用程序id
     * @param resourceVersionName 资源版本号
     * @return PatchVO 列表
     * @throws ApiException 业务接口异常
     */
    List<PatchVO> listPatchByApplicationId(Long applicationId, String resourceVersionName) throws ApiException;
}
