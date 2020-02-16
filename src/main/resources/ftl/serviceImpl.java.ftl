package ${package.ServiceImpl};

import ${cfg.packageName}.common.constant.CommonConstant;
import ${package.Service}.${table.serviceName};
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import ${package.Entity}.base.Pager;
import ${package.Entity}.${entity};
import ${package.Mapper}.${entity}Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ${cfg.packageName}.common.CommonException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
*
* @Author ${author}
* @Date ${.now?string["yyyy-MM-dd HH:mm:ss"]}
*/
@Service
public class ${table.serviceImplName} implements ${table.serviceName} {

    @Autowired
    ${entity}Mapper ${table.name}Mapper;

    @Override
    public ${entity} getOne(String id){
        return ${table.name}Mapper.selectById(id);
    }

    @Override
    public List<${entity}> list(${entity} ${table.name}) {
        List<${entity}> ${table.name}s = ${table.name}Mapper.selectList(new QueryWrapper<>(${table.name}));
        return ${table.name}s;
    }

    @Override
    public Pager listByPage(Pager pager, ${entity} ${table.name}) {
        IPage<${entity}> ${table.name}IPage = ${table.name}Mapper.selectPage(new Page<>(pager.getNum(), pager.getSize()), new QueryWrapper<>(${table.name}));
        return Pager.of(${table.name}IPage);
    }

    @Override
    @Transactional(rollbackFor = CommonException.class)
    public ${entity} save(${entity} ${table.name}) {
        if(Objects.nonNull(${table.name}.getId())){
            ${table.name}Mapper.updateById(${table.name});
        }else {
            ${table.name}Mapper.insert(${table.name});
        }
        return ${table.name};
    }

    @Override
    @Transactional(rollbackFor = CommonException.class)
    public boolean delete(String ids) {
        int i = ${table.name}Mapper.deleteBatchIds(Arrays.asList(ids.split(CommonConstant.MAGIC_SPLIT)));
        return i > 0;
    }


}
