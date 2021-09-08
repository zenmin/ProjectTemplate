package ${package.Service};

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ${package.Entity}.${entity};
import java.util.List;

/**
 * ${table.comment!}
 *
 * @Author ${author}
 * @Date ${.now?string["yyyy-MM-dd HH:mm:ss"]}
 */
public interface ${table.serviceName} extends IService<${entity}> {

    /**
     * 查询一条数据
     *
     * @param id
     * @return
     */
    ${entity} getOne(String id);

    /**
     * 不分页查询
     *
     * @return
     */
    List<${entity}> list();

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    Page listByPage(Page page);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean delete(String id);

}
