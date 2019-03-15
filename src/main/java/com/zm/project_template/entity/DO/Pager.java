package com.zm.project_template.entity.DO;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @Describle This Class Is 分页实体
 * @Author ZengMin
 * @Date 2019/3/15 9:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pager<T> {

    @ApiModelProperty(value = "页码",example = "1")
    private long num = 1;

    @ApiModelProperty(value = "分页大小",example = "10")
    private long size = 10;

    @ApiModelProperty(hidden = true)
    private List<T> data;

    @ApiModelProperty(hidden = true)
    private boolean last;

    @ApiModelProperty(hidden = true)
    private long totalNums;

    @ApiModelProperty(hidden = true)
    private long totalPages;

    public static Pager of(IPage iPage){
        return new Pager<>(iPage.getCurrent(),iPage.getSize(),iPage.getRecords(),iPage.getPages() <= iPage.getCurrent(),iPage.getTotal(),iPage.getPages());
    }

}
