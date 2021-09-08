package ${package.ServiceImpl};

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package.Service}.${table.serviceName};
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${package.Entity}.${entity};
import ${package.Mapper}.${entity}Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * ${table.comment!}
 *
 * @Author ${author}
 * @Date ${.now?string["yyyy-MM-dd HH:mm:ss"]}
 */
@Service
public class ${table.serviceImplName} extends ServiceImpl<${entity}Mapper, ${entity}> implements ${table.serviceName} {

    @Autowired
    ${entity}Mapper ${entity?substring(0, 1)?lower_case +  entity?substring(1)}Mapper;

    @Override
    public ${entity} getOne(String id) {
        return ${entity?substring(0, 1)?lower_case +  entity?substring(1)}Mapper.selectById(id);
    }

    @Override
    public List<${entity}> list() {
        List<${entity}> ${entity?substring(0, 1)?lower_case +  entity?substring(1)}s = ${entity?substring(0, 1)?lower_case +  entity?substring(1)}Mapper.selectList(new QueryWrapper<${entity}>().orderByDesc("create_time"));
        return ${entity?substring(0, 1)?lower_case +  entity?substring(1)}s;
    }

    @Override
    public Page listByPage(Page page) {
        Page<${entity}> pageInfo = ${entity?substring(0, 1)?lower_case +  entity?substring(1)}Mapper.selectPage(page, new QueryWrapper<${entity}>().orderByDesc("create_time"));
        return pageInfo;
    }

    @Override
    public boolean delete(String id) {
        int i = ${entity?substring(0, 1)?lower_case +  entity?substring(1)}Mapper.deleteById(id);
        return i > 0;
    }


}
