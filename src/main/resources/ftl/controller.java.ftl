package ${package.Controller};

<#if swagger2>
import io.swagger.annotations.*;
</#if>
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import ${cfg.packageName}.common.ResponseEntity;
import ${package.Entity}.DO.Pager;
import ${package.Entity}.${entity};
import ${package.Service}.${entity}Service;
import org.springframework.beans.factory.annotation.Autowired;


/**
* Create by Code Generator
* @Author ZengMin
* @Date ${.now?string["yyyy-MM-dd HH:mm:ss"]}
* https://github.com/zenmin/ProjectTemplate
*/

<#if swagger2>
@Api(tags = "${table.comment}")
</#if>
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
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
    @ApiOperation(value = "根据id查询一条数据", response = ResponseEntity.class)
    @ApiImplicitParam(name = "id",value = "主键",required = true)
    </#if>
    @PostMapping("/getOne")
    public ResponseEntity getOne(Long id){
        return ResponseEntity.success(${table.name}Service.getOne(id));
    }

    /**
     * 查询全部 可带条件
     * @param ${table.name}
     * @return
     */
    <#if swagger2>
    @ApiOperation(value = "查询全部 可带条件", response = ResponseEntity.class)
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
    @ApiOperation(value = "查全部 可带条件分页", response = ResponseEntity.class)
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
    @ApiOperation(value = "带ID更新 不带ID新增", response = ResponseEntity.class)
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
    @ApiOperation(value = "根据id删除 多个用,隔开", response = ResponseEntity.class)
    @ApiImplicitParam(name = "ids",value = "主键 多个用,隔开",required = true)
    </#if>
    @PostMapping("/delete")
    public ResponseEntity delete(String ids){
        return ResponseEntity.success(${table.name}Service.delete(ids));
    }


}