package ${package.Controller};

<#if swagger2>
import io.swagger.annotations.*;
</#if>
import org.springframework.web.bind.annotation.*;
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import ${cfg.packageName}.common.response.ApiResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${package.Entity}.${entity};
import ${package.Service}.${entity}Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * ${table.comment}
 *
 * @Author ZengMin
 * @Date ${.now?string["yyyy-MM-dd HH:mm:ss"]}
 */
<#if swagger2>
@Api(tags = "${table.comment}")
</#if>
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("/${entity?substring(0, 1)?lower_case +  entity?substring(1)}")
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

    @Autowired
    ${entity}Service ${entity?substring(0, 1)?lower_case +  entity?substring(1)}Service;

    /**
     * 根据id查询一条数据
     *
     * @param map
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "查询一条数据")
    </#if>
    @PostMapping("/getOne")
    public ApiResult<${entity}> getOne(<#if swagger2>@ApiParam(name = "id", value = "主键", required = true) </#if>@RequestBody Map<String, String> map) {
        return ApiResult.ok(${entity?substring(0, 1)?lower_case +  entity?substring(1)}Service.getOne(map.get("id")));
    }

    /**
     * 查询全部 可带条件
     *
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "查询全部")
    </#if>
    @PostMapping("/list")
    public ApiResult<List<${entity}>> list() {
        return ApiResult.ok(${entity?substring(0, 1)?lower_case +  entity?substring(1)}Service.list());
    }

    /**
     * 查询全部（分页）
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "查询全部（分页）")
    </#if>
    @GetMapping("/listByPage")
    public ApiResult<Page<${entity}>> listByPage(@ApiParam("页码") @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                                 @ApiParam("页大小") @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return ApiResult.ok(${entity?substring(0, 1)?lower_case +  entity?substring(1)}Service.listByPage(new Page(pageNo, pageSize)));
    }

    /**
     * 带ID更新 不带ID新增
     *
     * @param ${entity?substring(0, 1)?lower_case +  entity?substring(1)}
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "新增/更新", notes = "带id更新 不带id新增")
    </#if>
    @PostMapping("/save")
    public ApiResult<Boolean> saveOrUpdate(@RequestBody ${entity} ${entity?substring(0, 1)?lower_case +  entity?substring(1)}) {
        return ApiResult.ok(${entity?substring(0, 1)?lower_case +  entity?substring(1)}Service.saveOrUpdate(${entity?substring(0, 1)?lower_case +  entity?substring(1)}));
    }

    /**
     * 根据id删除
     *
     * @param map
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "删除数据")
    </#if>
    @PostMapping("/delete")
    public ApiResult<Boolean> delete(<#if swagger2>@ApiParam(name = "id", value = "主键", required = true) </#if>@RequestBody Map<String, String> map) {
        return ApiResult.ok(${entity?substring(0, 1)?lower_case +  entity?substring(1)}Service.delete(map.get("id")));
    }


}