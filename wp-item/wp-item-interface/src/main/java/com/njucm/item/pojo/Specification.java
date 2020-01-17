package com.njucm.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_specification")
public class Specification {

    private Long id;

    @Id
    private Long categoryId;

    private String specifications;
}
