package com.lhc.ocat.mobileplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhc.ocat.mobileplatform.domain.dos.ApplicationDO;
import com.lhc.ocat.mobileplatform.domain.dos.PatchDO;
import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;
import com.lhc.ocat.mobileplatform.domain.dto.Application;
import com.lhc.ocat.mobileplatform.domain.vo.PageVO;
import com.lhc.ocat.mobileplatform.domain.vo.PatchVO;
import com.lhc.ocat.mobileplatform.domain.vo.ResourceVO;
import com.lhc.ocat.mobileplatform.exception.ApiErrorType;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import com.lhc.ocat.mobileplatform.mapper.ApplicationMapper;
import com.lhc.ocat.mobileplatform.mapper.PatchMapper;
import com.lhc.ocat.mobileplatform.mapper.ResourceMapper;
import com.lhc.ocat.mobileplatform.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author lhc
 * @date 2019-11-14 18:45
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private PatchMapper patchMapper;

    @Override
    public String signup(String name, String description) throws ApiException {
        // 首先查询应用名称是否重复
        ApplicationDO appDO = applicationMapper.selectOne(new LambdaQueryWrapper<ApplicationDO>()
                .eq(ApplicationDO::getName, name));
        if (Objects.nonNull(appDO)) {
            throw new ApiException(ApiErrorType.APP_NAME_DUPLICATION_ERROR);
        }
        // 生成 appId 10 位随机数，验证是否重复
        String appId = getRandom(10);
        // 生成 appSecret 规则：SHA256(name+appId+salt)
        String appSecret = getUUID32();
        ApplicationDO applicationDO = new ApplicationDO();
        applicationDO.setName(name);
        applicationDO.setDescription(description);
        applicationDO.setAppId(appId);
        applicationDO.setAppSecret(appSecret);
        applicationMapper.insert(applicationDO);
        return applicationDO.getAppId();
    }

    @Override
    public Application authenticate(String appId, String appSecret) throws ApiException  {
        ApplicationDO appDO = applicationMapper.selectOne(new LambdaQueryWrapper<ApplicationDO>()
                .eq(ApplicationDO::getAppId, appId));
        if (Objects.isNull(appDO)) {
            throw new ApiException(ApiErrorType.APP_NOT_FOUND_ERROR);
        }
        if (!appSecret.equals(appDO.getAppSecret())) {
            throw new ApiException(ApiErrorType.APP_SECRET_ERROR);
        }
        return Application.toApplication(appDO);
    }

    @Override
    public void updateApplication(Long applicationId,String name, String description) throws ApiException {
        ApplicationDO applicationDO = applicationMapper.selectById(applicationId);
        if (Objects.isNull(applicationDO)) {
            throw new ApiException(ApiErrorType.APP_NOT_FOUND_ERROR);
        }
        applicationDO.setName(name);
        applicationDO.setDescription(description);
        applicationMapper.updateById(applicationDO);
    }

    @Override
    public Application getApplicationById(Long applicationId) throws ApiException {
        ApplicationDO applicationDO = applicationMapper.selectById(applicationId);
        if (Objects.isNull(applicationDO)) {
            throw new ApiException(ApiErrorType.APP_NOT_FOUND_ERROR);
        }
        return Application.toApplication(applicationDO);
    }

    @Override
    public PageVO<Application> listApplicationByKeyword(String keyword, Long currentPage, Long pageSize) throws ApiException {
        Page<ApplicationDO> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        if (Objects.isNull(keyword) || keyword.isEmpty()) {
            // 无条件搜索
            applicationMapper.selectPage(page,
                    new LambdaQueryWrapper<ApplicationDO>()
                            .orderByDesc(ApplicationDO::getCreateTime));
        } else {
            // 关键字搜索名称和描述
            applicationMapper.selectPage(page,
                    new LambdaQueryWrapper<ApplicationDO>()
                            .like(ApplicationDO::getName, keyword)
                            .or()
                            .like(ApplicationDO::getDescription, keyword)
                            .orderByDesc(ApplicationDO::getCreateTime));
        }
        List<Application> applicationList = page.getRecords()
                .stream()
                .map(Application::toApplication)
                .collect(Collectors.toList());
        PageVO<Application> pageVO = new PageVO<>();
        pageVO.setCurrent(page.getCurrent());
        pageVO.setSize(page.getSize());
        pageVO.setTotal(page.getTotal());
        pageVO.setRecords(applicationList);
        return pageVO;
    }

    @Override
    public PageVO<ResourceVO> listResourceByApplicationId(Long applicationId, Long currentPage, Long pageSize) throws ApiException {
        ApplicationDO applicationDO = applicationMapper.selectById(applicationId);
        if (Objects.isNull(applicationDO)) {
            throw new ApiException(ApiErrorType.APP_NOT_FOUND_ERROR);
        }
        Page<ResourceDO> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        resourceMapper.selectPage(page,
                new LambdaQueryWrapper<ResourceDO>()
                        .eq(ResourceDO::getApplicationId, applicationId)
                        .orderByDesc(ResourceDO::getCreateTime));
        List<ResourceVO> resourceList = page.getRecords()
                .stream()
                .map(ResourceVO::toResourceVO)
                .collect(Collectors.toList());
        PageVO<ResourceVO> pageVO = new PageVO<>();
        pageVO.setCurrent(page.getCurrent());
        pageVO.setSize(page.getSize());
        pageVO.setTotal(page.getTotal());
        pageVO.setRecords(resourceList);
        return pageVO;
    }

    @Override
    public List<PatchVO> listPatchByApplicationId(Long applicationId, String resourceVersionName) throws ApiException {
        ApplicationDO applicationDO = applicationMapper.selectById(applicationId);
        if (Objects.isNull(applicationDO)) {
            throw new ApiException(ApiErrorType.APP_NOT_FOUND_ERROR);
        }
        List<PatchDO> patchDOList = patchMapper.selectList(
                new LambdaQueryWrapper<PatchDO>()
                        .eq(PatchDO::getApplicationId, applicationId)
                        .eq(PatchDO::getNewVersion, resourceVersionName));
        return patchDOList.stream().map(PatchVO::toPatchVO).collect(Collectors.toList());
    }

    private static String getRandom(int len) {
        Random r = new Random();
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < len; i++) {
            rs.append(r.nextInt(10));
        }
        return rs.toString();
    }

    private static String getUUID32(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
