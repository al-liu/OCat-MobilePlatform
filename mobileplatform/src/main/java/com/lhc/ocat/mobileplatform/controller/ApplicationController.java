package com.lhc.ocat.mobileplatform.controller;

import com.lhc.ocat.mobileplatform.domain.dos.ApplicationDO;
import com.lhc.ocat.mobileplatform.domain.dto.Application;
import com.lhc.ocat.mobileplatform.domain.param.AppSignUpParam;
import com.lhc.ocat.mobileplatform.domain.param.PageParam;
import com.lhc.ocat.mobileplatform.domain.vo.PageVO;
import com.lhc.ocat.mobileplatform.domain.vo.PatchVO;
import com.lhc.ocat.mobileplatform.domain.vo.ResourceVO;
import com.lhc.ocat.mobileplatform.domain.vo.Result;
import com.lhc.ocat.mobileplatform.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lhc
 * @date 2019-11-14 18:28
 */
@Validated
@RestController
@RequestMapping(value="/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("")
    public Result signUp(@RequestBody AppSignUpParam appSignUpParam) {
        String appId = applicationService.signup(appSignUpParam.getName(), appSignUpParam.getDescription());
        return Result.success(appId);
    }

    @PutMapping("/{id:[0-9]+}")
    public Result update(@PathVariable Long id, @Validated @RequestBody AppSignUpParam appParam) {
        applicationService.updateApplication(id, appParam.getName(), appParam.getDescription());
        return Result.success();
    }

    @GetMapping("/{id:[0-9]+}")
    public Result getDetail(@PathVariable Long id) {
        Application application =  applicationService.getApplicationById(id);
        return Result.success(application);
    }

    @DeleteMapping(value = "/{id:[0-9]+}")
    public Result deleteApp(@PathVariable Long id) {
        applicationService.removeApplication(id);
        return Result.success();
    }

    @GetMapping("/all")
    public Result allApps() {
        List<Application> list = applicationService.listApps();
        return Result.success(list);
    }

    @GetMapping("/search")
    public Result searchByKeyword(PageParam pageParam, @RequestParam String keyword) {
        PageVO<Application> pageVO = applicationService.listApplicationByKeyword(keyword, pageParam.getCurrent(), pageParam.getSize());
        return Result.success(pageVO);
    }

    @GetMapping("/{applicationId:[0-9]+}/resources")
    public Result getResourceList(PageParam pageParam, @PathVariable Long applicationId) {
        PageVO<ResourceVO> pageVO = applicationService.listResourceByApplicationId(applicationId, pageParam.getCurrent(), pageParam.getSize());
        return Result.success(pageVO);
    }

    @GetMapping("/{applicationId:[0-9]+}/resource/{version}/patchs")
    public Result getResourceList(@PathVariable Long applicationId, @PathVariable String version) {
        List<PatchVO> list = applicationService.listPatchByApplicationId(applicationId, version);
        return Result.success(list);
    }
}
