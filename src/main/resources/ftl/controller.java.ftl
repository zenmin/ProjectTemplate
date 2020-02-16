package ${package.Controller};

<#if swagger2>
import io.swagger.annotations.*;
</#if>
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import ${cfg.packageName}.common.ResponseEntity;
import ${package.Entity}.base.Pager;
import ${package.Entity}.${entity};
import ${package.Service}.${entity}Service;
import org.springframework.beans.factory.annotation.Autowired;


/**
*
* @Author ${author}
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
@RequestMapping("/api<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

    @Autowired
    ${entity}Service ${table.name}Service;

    /**
     * 根据id查询一条数据
     * @param id
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "查询一条数据", response = ResponseEntity.class)
    @ApiImplicitParam(name = "id",value = "主键",required = true)
    </#if>
    @PostMapping("/getOne")
    public ResponseEntity getOne(@RequestParam(required = true) String id){
        return ResponseEntity.success(${table.name}Service.getOne(id));
    }

    /**
     * 查询全部 可带条件
     * @param ${table.name}
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "查询全部", response = ResponseEntity.class)
    </#if>
    @PostMapping("/list")
    public ResponseEntity list(${entity} ${table.name}){
        return ResponseEntity.success(${table.name}Service.list(${table.name}));
    }

    /**
     * 查全部 可带条件分页
     * @param pager
     * @param ${table.name}
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "查询全部（分页）", response = ResponseEntity.class)
    </#if>
    @PostMapping("/listByPage")
    public ResponseEntity listByPage(Pager pager,${entity} ${table.name}){
        return ResponseEntity.success(${table.name}Service.listByPage(pager,${table.name}));
    }

    /**
     * 带ID更新 不带ID新增
     * @param ${table.name}
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "新增/更新", notes = "带id更新 不带id新增", response = ResponseEntity.class)
    </#if>
    @PostMapping("/save")
    public ResponseEntity saveOrUpdate(${entity} ${table.name}){
        return ResponseEntity.success(${table.name}Service.save(${table.name}));
    }

    /**
     * 根据id删除   多个用,隔开
     * @param ids
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "删除数据", response = ResponseEntity.class)
    @ApiImplicitParam(name = "ids",value = "主键 多个用,隔开",required = true)
    </#if>
    @PostMapping("/delete")
    public ResponseEntity delete(@RequestParam(required = true) String ids){
        return ResponseEntity.success(${table.name}Service.delete(ids));
    }


}