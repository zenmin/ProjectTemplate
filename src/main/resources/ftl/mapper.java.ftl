package ${package.Mapper};

import ${package.Entity}.${entity};
import ${superMapperClassPackage};

/**
* Create by Code Generator
* @Author ZengMin
* @Date ${.now?string["yyyy-MM-dd HH:mm:ss"]}
* https://github.com/zenmin/ProjectTemplate
*/

<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {

}
</#if>
