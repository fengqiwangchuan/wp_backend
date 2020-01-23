package com.njucm.item.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_specification")
public class Specification {

    @Id
    @Column(name = "category_id")
    private Long categoryId;

    private String specifications;
}
