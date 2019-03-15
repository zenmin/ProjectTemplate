package ${package.Service};

import ${package.Entity}.DO.Pager;
import ${package.Entity}.${entity};
import java.util.List;

/**
* Create by Code Generator
* @Author ZengMin
* @Date ${.now?string["yyyy-MM-dd HH:mm:ss"]}
* https://github.com/zenmin/ProjectTemplate
*/

public interface ${table.serviceName} {

    ${entity} getOne(Long id);

    List<${entity}> list(${entity} ${table.name});

    Pager listByPage(Pager pager,${entity} ${table.name});

    ${entity} save(${entity} ${table.name});

    boolean delete(String ids);

}
