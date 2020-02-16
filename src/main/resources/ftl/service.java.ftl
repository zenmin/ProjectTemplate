package ${package.Service};

import ${package.Entity}.base.Pager;
import ${package.Entity}.${entity};
import java.util.List;

/**
*
* @Author ${author}
* @Date ${.now?string["yyyy-MM-dd HH:mm:ss"]}
*/
public interface ${table.serviceName} {

    /**
     * 查询一条数据
     * @param id
     * @return
     */
    ${entity} getOne(String id);

    /**
     * 不分页查询
     * @param ${table.name}
     * @return
     */
    List<${entity}> list(${entity} ${table.name});

    /**
     * 分页查询
     * @param pager
     * @param ${table.name}
     * @return
     */
    Pager listByPage(Pager pager,${entity} ${table.name});

    /**
     * 新增或更新
     * @param ${table.name}
     * @return
     */
    ${entity} save(${entity} ${table.name});

    /**
     * 批量删除
     * @param ids
     * @return
     */
    boolean delete(String ids);

}
