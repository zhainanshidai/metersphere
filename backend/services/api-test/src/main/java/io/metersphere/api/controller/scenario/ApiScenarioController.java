package io.metersphere.api.controller.scenario;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.metersphere.api.domain.ApiScenario;
import io.metersphere.api.dto.scenario.*;
import io.metersphere.api.service.scenario.ApiScenarioLogService;
import io.metersphere.api.service.scenario.ApiScenarioService;
import io.metersphere.sdk.constants.PermissionConstants;
import io.metersphere.system.log.annotation.Log;
import io.metersphere.system.log.constants.OperationLogType;
import io.metersphere.system.security.CheckOwner;
import io.metersphere.system.utils.PageUtils;
import io.metersphere.system.utils.Pager;
import io.metersphere.system.utils.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/scenario")
@Tag(name = "接口测试-接口场景管理")
public class ApiScenarioController {
    @Resource
    private ApiScenarioService apiScenarioService;

    @PostMapping("/page")
    @Operation(summary = "接口测试-接口场景管理-场景列表(deleted 状态为 1 时为回收站数据)")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ)
    @CheckOwner(resourceId = "#request.getProjectId()", resourceType = "project")
    public Pager<List<ApiScenarioDTO>> getPage(@Validated @RequestBody ApiScenarioPageRequest request) {
        Page<Object> page = PageHelper.startPage(request.getCurrent(), request.getPageSize(),
                StringUtils.isNotBlank(request.getSortString()) ? request.getSortString() : "create_time desc");
        return PageUtils.setPageInfo(page, apiScenarioService.getScenarioPage(request));
    }

    @PostMapping("/trash/page")
    @Operation(summary = "接口测试-接口场景管理-场景列表(deleted 状态为 1 时为回收站数据)")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ)
    @CheckOwner(resourceId = "#request.getProjectId()", resourceType = "project")
    public Pager<List<ApiScenarioDTO>> getTrashPage(@Validated @RequestBody ApiScenarioPageRequest request) {
        Page<Object> page = PageHelper.startPage(request.getCurrent(), request.getPageSize(),
                StringUtils.isNotBlank(request.getSortString()) ? request.getSortString() : "create_time desc");
        request.setDeleted(true);
        return PageUtils.setPageInfo(page, apiScenarioService.getScenarioPage(request));
    }

    @PostMapping("/batch/edit")
    @Operation(summary = "接口测试-接口场景管理-批量编辑")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_UPDATE)
    @CheckOwner(resourceId = "#request.getSelectIds()", resourceType = "api_scenario")
    public void batchUpdate(@Validated @RequestBody ApiScenarioBatchEditRequest request) {
        apiScenarioService.batchEdit(request, SessionUtils.getUserId());
    }

    @GetMapping("follow/{id}")
    @Operation(summary = "接口测试-接口场景管理-关注/ 取消关注")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_UPDATE)
    @CheckOwner(resourceId = "#id", resourceType = "api_scenario")
    public void follow(@PathVariable String id) {
        apiScenarioService.follow(id, SessionUtils.getUserId());
    }

    @PostMapping("/add")
    @Operation(summary = "创建场景")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_ADD)
    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = ApiScenarioLogService.class)
    public ApiScenario add(@Validated @RequestBody ApiScenarioAddRequest request) {
        return apiScenarioService.add(request, SessionUtils.getUserId());
    }

    @PostMapping("/upload/temp/file")
    @Operation(summary = "上传场景所需的文件资源，并返回文件ID")
    @RequiresPermissions(logical = Logical.OR, value = {PermissionConstants.PROJECT_API_SCENARIO_ADD, PermissionConstants.PROJECT_API_SCENARIO_UPDATE})
    public String uploadTempFile(@RequestParam("file") MultipartFile file) {
        return apiScenarioService.uploadTempFile(file);
    }

    @PostMapping("/update")
    @Operation(summary = "更新场景")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_UPDATE)
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#request)", msClass = ApiScenarioLogService.class)
    public ApiScenario update(@Validated @RequestBody ApiScenarioUpdateRequest request) {
        return apiScenarioService.update(request, SessionUtils.getUserId());
    }

    @GetMapping("/delete/{id}")
    @Operation(summary = "删除场景")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_DELETE)
    @Log(type = OperationLogType.DELETE, expression = "#msClass.deleteLog(#id)", msClass = ApiScenarioLogService.class)
    public void delete(@PathVariable String id) {
        apiScenarioService.delete(id);
    }

    @GetMapping("/delete-to-gc/{id}")
    @Operation(summary = "删除场景到回收站")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_DELETE)
    @Log(type = OperationLogType.DELETE, expression = "#msClass.deleteLog(#id)", msClass = ApiScenarioLogService.class)
    public void deleteToGc(@PathVariable String id) {
        apiScenarioService.deleteToGc(id);
    }
}