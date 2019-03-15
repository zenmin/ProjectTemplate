package ${package.ServiceImpl};

import ${package.Service}.${table.serviceName};
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import ${package.Entity}.DO.Pager;
import ${package.Entity}.${entity};
import ${package.Mapper}.${entity}Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import ${cfg.packageName}.util.DateUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
* Create Code Generator
* @Author ZengMin
* @Date ${.now?string["yyyy-MM-dd HH:mm:ss"]}
* https://github.com/zenmin/ProjectTemplate
*/

@Service
public class ${table.serviceImplName} implements ${table.serviceName} {

    @Autowired
    ${entity}Mapper ${table.name}Mapper;

    @Override
    public ${entity} getOne(Long id){
        return ${table.name}Mapper.selectById(id);
    }

    @Override
    public List<${entity}> list(${entity} ${table.name}) {
        if(StringUtils.isNotBlank(${table.name}.getCreateTimeQuery())){
            ${table.name}.setCreateTime(DateUtil.parseToDate(${table.name}.getCreateTimeQuery()));
        }
        List<${entity}> ${table.name}s = ${table.name}Mapper.selectList(new QueryWrapper<>(${table.name}));
        return ${table.name}s;
    }

    @Override
    public Pager listByPage(Pager pager, ${entity} ${table.name}) {
        if(StringUtils.isNotBlank(${table.name}.getCreateTimeQuery())){
            ${table.name}.setCreateTime(DateUtil.parseToDate(${table.name}.getCreateTimeQuery()));
        }
        IPage<${entity}> ${table.name}IPage = ${table.name}Mapper.selectPage(new Page<>(pager.getNum(), pager.getSize()), new QueryWrapper<>(${table.name}));
        return pager.of(${table.name}IPage);
    }

    @Override
    @Transactional
    public ${entity} save(${entity} ${table.name}) {
        if(Objects.nonNull(${table.name}.getId())){
            ${table.name}Mapper.updateById(${table.name});
        }else {
            ${table.name}Mapper.insert(${table.name});
        }
        return ${table.name};
    }

    @Override
    @Transactional
    public boolean delete(String ids) {
        List<Long> list = Lists.newArrayList();
        if(ids.indexOf(",") != -1){
            List<String> asList = Arrays.asList(ids.split(","));
            asList.stream().forEach(o -> list.add(Long.valueOf(o)));
        }else {
            list.add(Long.valueOf(ids));
        }
        int i = ${table.name}Mapper.deleteBatchIds(list);
        return i > 0;
    }


}
