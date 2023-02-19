package com.custom.tools.testmodel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author  Xiao-Bai
 * @since  2022/12/29 23:22
 * @desc
 */
@Getter
@Setter
@ToString
public class Person {

    private Integer id;

    private String name;

    private String nickName;

    private Integer age;

    private Boolean sex;

    private Integer parentId;

    private List<Person> personList;


}
