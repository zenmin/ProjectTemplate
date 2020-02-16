package com.zm.project_template.entity.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * @Describle This Class Is 实体Model
 * @Author ZengMin
 * @Date 2019/3/14 16:36
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@MappedSuperclass
@Data
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public abstract class AbstractEntityModel implements Serializable {

    @Id
    @Column(unique = true, columnDefinition = "varchar(32) COMMENT '主键'")
    @ApiModelProperty(value = "主键 (更新传入)")
    private String id;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = "datetime default now() COMMENT '创建时间' ")
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createDate;

}

