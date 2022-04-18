# custom-db-action

### 简介
custom-db-action为自定义的一款集成数据源```JDBC```操作工具，底层为最原始的```JDBC```，使用阿里的druid作为连接池，将```JDBC```进行了大量封装，将之变成一款可极大简化操作数据的轻量级第三方```ORM```工具，集成了```Mybatis-Plus```的条件构造器，在此之上添加了``多表连接查询```的条件构造，使增删改查变得更容易。
### 说明：
- ```com.custom.sqlparser.CustomDao```，该类提供了多种增删改查方法以供用户自定义使用，只需要编写部分的条件```sql```，即可完成单表的大部分增删改查操作。
- 只需要创建实体类，并添加上自定义的几个注解，即可生成对应的表结构。
- 支持可配置的表关联查询以及逻辑删除，```sql```语句打印输出，下划线转驼峰等功能。
- 该工具已完成```springboot```的自动配置,在```springboot```项目中引入该依赖即可，无需另外配置，轻松便捷。

#### 安装依赖

```
         <dependency>
            <groupId>com.custom</groupId>
            <artifactId>custom-db-action</artifactId>
            <version>1.0.0</version>
        </dependency>
```

#### 配置数据源

1.  ```SpringBoot```项目配置数据源：因DataSource类为本工具自定义，所以在```application.yml(properties)```中进行如下基本配置即可，```mysql```驱动默认为```mysql8.0```--->```com.mysql.cj.jdbc.Driver```(配置文件中可不写)

```
custom.db.datasource.url=jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC
custom.db.datasource.username=root
custom.db.datasource.password=123456
```


#### 使用说明

1.  该工具提供大量的增删改查方法API
- 示例

```
示例1：List<Employee> list = customDao.selectList(Employee.class, " and a.age > ?", 20);
```
```
示例2：DbPageRows<Employee> dbPageRows = customDao.selectPageRows(Employee.class, " and a.name = ?", new DbPageRows<Employee>().setPageIndex(1).setPageSize(10), "张三");
```
```
示例3：Employee employee = customDao.selectOneByKey(Employee.class, 25);
```
```
示例4：List<Employee> employeeList = customDao.selectListByKeys(Employee.class, Arrays.asList(21, 23));
```
```
示例5：
        // 插入一条记录
        Employee employee = new Employee();
        employee.setEmpName("张三");
        employee.setAddress("西雅图");
        employee.setAge(28);
        customDao.insert(employee);
```

```
示例6：List<Employee> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Employee e = new Employee();
            e.setEmpName("员-工bb-"+i);
            e.setSex(i % 2 == 1);
            e.setAddress("bbbb->" + i);
            e.setAge(24-i);
            e.setAreaId(i);
            e.setDeptId(2);
            e.setBirthday(new Date());
            e.setState(0);
            list.add(e);
        }

        // 插入多条记录
        customDao.insert(list);
```

- 查询

```

    查询多条记录: 例1（and a.name = ?, "张三"），例2 (and a.name = "张三") 
    public <T> List<T> selectList(Class<T> t, String condition, Object... params) throws Exception;


    根据多个主键查询多条记录
    public <T> List<T> selectListByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;


    根据sql查询多条记录: 例（select * from table ）
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params) throws Exception;


    根据条件进行分页查询: 例（and a.name = ?, "张三"）
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, Object... params) throws Exception;


    根据条件进行分页查询并排序: 例（and a.name = ? , 1, 10, "id desc", "张三"）
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, String orderBy, Object... params) throws Exception;


    根据主键查询一条记录：例 (25）
    public <T> T selectOneByKey(Class<T> t, Object key) throws Exception;

    纯sql查询一条记录：例（select * from table where age = ?, 25）
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception;

    纯sql查询单个值：例（select name from table where age = ?, 25）
    public Object selectObjBySql(String sql, Object... params) throws Exception;

    根据条件查询一条记录
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception;
```

- 删除

```
    根据主键删除一条记录
    public <T> int deleteByKey(Class<T> t, Object key) throws Exception;

    根据主键删除多条记录
    public <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;

    根据条件删除记录
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception;
```


- 修改

```
    根据主键修改一条记录（updateFields：指定要修改的表字段  为空则按主键修改全部字段）
    public <T> int updateByKey(T t, String... updateDbFields) throws Exception;

    根据主键修改一条记录
    public <T> int updateByKey(T t) throws Exception;
```

- 添加

```
    插入一条记录
    public <T> long insert(T t) throws Exception;

    插入一条记录并生成新的主键
    public <T> int insertGenerateKey(T t) throws Exception;

    插入多条记录
    public <T> int insert(List<T> tList) throws Exception;

    插入多条记录并生成新的主键
    public <T> int insertGenerateKey(List<T> tList) throws Exception;
```
- 创建表

1. 创建实体类，并在表字段上标注上注解，```@key```为主键注解，```@DbField```为一般字段注解
```
@DbTable(table = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTemp {


    @DbKey
    private int id;

    @DbField(value="emp_name", desc="员工名字")
    private String empName;

    @DbField(desc="性别")
    private boolean sex;

    @DbField(desc="年龄")
    private int age;

    @DbField(desc="居住地'")
    private String address;

    @DbField(desc="生日")
    private Date birthday;

    @DbField(value="dept_id", desc="部门id")
    private int deptId;

    @DbField(value="area_id", desc="地区id")
    private int areaId;

    @DbFiel(desc="状态,0-未删除，1-已删除")
    private int state;
```
2. 第二步，执行```createTables```方法即可

```
customDao.createTables(EmployeeTemp.class);
```

3. 执行结果

```
 create table `employee_temp` (
`id` int(11) primary key not null auto_increment comment '主键' 
, `emp_name` varchar(50)  comment '员工名字'
,`sex` bit(1)  comment '性别'
,`age` int(11)  comment '年龄'
,`address` varchar(50)  comment '居住地'
,`birthday` date  comment '生日'
,`dept_id` int(11)  comment '部门id'
,`area_id` int(11)  comment '地区id'
,`state` int(11)  comment '状态,0-未删除，1-已删除'
) 
```



#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
