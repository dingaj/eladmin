/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.aijie.capitalhistory.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author dingaijie
* @date 2020-12-05
**/
@Entity
@Data
@Table(name="capital_history")
public class CapitalHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Integer id;

    @Column(name = "id_card")
    @ApiModelProperty(value = "idCard")
    private String idCard;

    @Column(name = "contract")
    @ApiModelProperty(value = "contract")
    private String contract;

    @Column(name = "name")
    @ApiModelProperty(value = "name")
    private String name;

    @Column(name = "date")
    @ApiModelProperty(value = "date")
    private String date;

    @Column(name = "money")
    @ApiModelProperty(value = "money")
    private String money;

    @Column(name = "term")
    @ApiModelProperty(value = "term")
    private String term;

    @Column(name = "company")
    @ApiModelProperty(value = "company")
    private String company;

    @Column(name = "car")
    @ApiModelProperty(value = "car")
    private String car;

    @Column(name = "oper")
    @ApiModelProperty(value = "oper")
    private String oper;

    @Column(name = "oper_date")
    @ApiModelProperty(value = "operDate")
    private Timestamp operDate;

    @Column(name = "notes")
    @ApiModelProperty(value = "notes")
    private String notes;

    @Column(name = "address")
    @ApiModelProperty(value = "address")
    private String address;

    @Column(name = "code")
    @ApiModelProperty(value = "code")
    private String code;

    @Column(name = "due_date")
    @ApiModelProperty(value = "dueDate")
    private String dueDate;

    public void copy(CapitalHistory source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}