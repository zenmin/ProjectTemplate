package com.zm.project_template.entity.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zm.project_template.util.SqlUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

/**
 * @Describle This Class Is 分页实体
 * @Author ZengMin
 * @Date 2019/3/15 9:40
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Pager<T> {

    @ApiModelProperty(value = "页码", example = "1")
    private Long num = 0L;

    @ApiModelProperty(value = "分页大小", example = "10")
    private Long size = 10L;

    @ApiModelProperty(hidden = true)
    private List<T> data;

    @ApiModelProperty(hidden = true)
    private Boolean last;

    @ApiModelProperty(hidden = true)
    private Long totalNums;

    @ApiModelProperty(hidden = true)
    private Long totalPages;

    public static Pager of(IPage iPage) {
        return new Pager<>(iPage.getCurrent(), iPage.getSize(), iPage.getRecords(), iPage.getPages() <= iPage.getCurrent(), iPage.getTotal(), iPage.getPages());
    }

    public static Pager of(List<?> list, Pager pager, Long allCount) {
        if (Objects.isNull(allCount)) {
            allCount = 0L;
        }
        int pages = Math.toIntExact(allCount % pager.getSize()) == 0 ? Math.toIntExact(allCount / pager.getSize()) : Math.toIntExact(allCount / pager.getSize()) + 1;
        return new Pager<>(pager.getNum(), pager.getSize(), list, pages <= pager.getNum(), allCount, Long.valueOf(pages));
    }

    public static Pager ofPageInfo(IPage iPage) {
        return new Pager<>(iPage.getCurrent(), iPage.getSize(), null, iPage.getPages() <= iPage.getCurrent(), iPage.getTotal(), iPage.getPages());
    }

    public Pager(int num, int size) {
        this.num = Long.parseLong(String.valueOf(num));
        if (num <= 1) {
            this.num = 0L;
        }
        this.size = (long) SqlUtil.checkPageSize(size);
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
        if (num <= 1) {
            this.num = 0L;
        }
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size > SqlUtil.MAX_PAGE_SIZE ? SqlUtil.MAX_PAGE_SIZE : size;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public Long getTotalNums() {
        return totalNums;
    }

    public void setTotalNums(Long totalNums) {
        this.totalNums = totalNums;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }
}
