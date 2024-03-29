

# custom-springboot-starter

### 简介
简易ORM操作工具，纯原生JDBC+阿里的Druid连接池，集成条件构造器，使用简单，一看就会，一用就爽，让增删改查变得更容易，支持```ActiveRecord```以及```链式查询```。
### 说明：
- ```com.custom.action.core.JdbcDao```，该类提供了多种增删改查方法以供用户自定义使用，使用时只需要在service或controller注入该对象即可，只需要编写部分的条件```sql```，即可完成单表的大部分增删改查操作。
- 只需要创建实体类，并添加上自定义的几个注解，即可生成对应的表结构。
- 暂时只支持Mysql
- 支持可配置的表关联查询以及逻辑删除，```sql```语句打印输出，下划线转驼峰等功能。
- 该工具已完成```springboot```的自动配置,在```springboot```项目中引入该依赖即可，无需另外配置，轻松便捷。

#### 安装依赖

##### SpringBoot-自动配置

```xml
         <dependency>
            <groupId>com.xb-custom</groupId>
            <artifactId>custom-springboot-starter</artifactId>
            <version>1.0.0</version>
            <!-- 或最新版本  -->
        </dependency>
```

**springboot方式 -- 配置数据源**

```SpringBoot```项目配置数据源：因dataSource类为本工具自定义，所以在```application.yml(properties)```中进行如下基本配置即可，```mysql```驱动默认为```mysql8.0```--->```com.mysql.cj.jdbc.Driver```(配置文件中可不写)

```properties
custom.db.datasource.url=jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC
custom.db.datasource.username=root
custom.db.datasource.password=123456
```

**纯依赖-手动配置**

```xml
         <dependency>
            <groupId>com.xb-custom</groupId>
            <artifactId>custom-db-action</artifactId>
            <version>1.0.0</version>
        </dependency>
```

**手动方式 -- 配置数据源**

```java
		// 数据库连接配置
        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");
        // Driver不用填，默认mysql8.0

        // 全局配置
        DbGlobalConfig globalConfig = new DbGlobalConfig();

        // 策略配置
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        // sql打印开关
        dbCustomStrategy.setSqlOutPrinting(true);
        // sql打印时， true为可执行的sql(即参数?已经替换为真实的值)， 默认false
        dbCustomStrategy.setSqlOutPrintExecute(true);
        // 是否下划线转驼峰?
        dbCustomStrategy.setUnderlineToCamel(true);

        // 逻辑删除的字段(表字段)
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        // 逻辑删除的标识值
        dbCustomStrategy.setDeleteLogicValue(1);
        // 未逻辑删除的标识值
        dbCustomStrategy.setNotDeleteLogicValue(0);

        globalConfig.setStrategy(dbCustomStrategy);
```



#### 使用说明

```java
import com.custom.action.core.JdbcDao;

// 注入JdbcDao，即可使用
@Autowired
private JdbcDao jdbcDao;
```



1.  该工具提供大量的增删改查方法API
- 一般查询

```java

    查询多条记录: 例1(and a.name = ?, "张三")，例2 (and a.name = "张三") 
    public <T> List<T> selectList(Class<T> t, String condition, Object... params);


    根据多个主键查询多条记录
    public <T> List<T> selectListByKeys(Class<T> t, Collection<? extends Serializable> keys);


    根据sql查询多条记录: 例(select * from table)
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params);


    根据条件进行分页查询并排序: 例(and a.name = ?, 1, 10, "张三")
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, Object... params);


    根据主键查询一条记录: 例 (25)
    public <T> T selectOneByKey(Class<T> t, Object key);

    纯sql查询一条记录: 例(select * from table where age = ?, 25)
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params);

    纯sql查询单个值: 例(select name from table where age = ?, 25)
    public Object selectObjBySql(String sql, Object... params);

    根据条件查询一条记录
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params);
```

- 条件构造查询**(**与mybatis-plus的条件构造器相差无几)

  `selectPageRows(查询分页)`

  `selectList(查询多条)`

  `selectCount(查询记录数)`

  `selectOne(查询单条记录)`

  `selectObj(查询单列字段，并且只有一个值，若有多个，只返回第一个满足条件的值)`

  `selectObjs(同上，但允许会返回多个值)`
  
```java
  public <T> DbPageRows<T> selectPageRows(ConditionWrapper<T> wrapper);
  public <T> List<T> selectList(ConditionWrapper<T> wrapper);
  public <T> T selectOne(ConditionWrapper<T> wrapper);
  public <T> long selectCount(ConditionWrapper<T> wrapper);
  public <T> Object selectObj(ConditionWrapper<T> wrapper);
  public <T> List<Object> selectObjs(ConditionWrapper<T> wrapper);
```

  使用示例

```java
  1: 一般字段构造
      
      DefaultConditionWrapper<Student> wrapper = new DefaultConditionWrapper<>(Student.class);
      wrapper.eq("name", "张三").select("id", "name", "age").pageParams(1, 10);
      DbPageRows<Student> dbPageRows = jdbcDao.selectPageRows(wrapper);
  
  2: lambda表达式构造
      
     	LambdaConditionWrapper<Student> wrapper = new LambdaConditionWrapper<>(Student.class);
     	wrapper.eq(Student::getName, "张三")
          .select(Student::getName, Student::getId, Student::getAge)
          .pageParams(1, 10);
      DbPageRows<Student> dbPageRows = jdbcDao.selectPageRows(wrapper);
  
  3: 使用静态方法实例化
      DbPageRows<ChildStudent> dbPageRows = jdbcDao.selectPageRows(Conditions.lambdaQuery(Student.class)
                  .eq(Student::getName, "张三")
                  .select(Student::getName, Student::getId, Student::getAge)
                  .pageParams(1, 10)
     );
  
  额外说明: 
      1. 使用onlyPrimary()方法时, 可使本次查询只查询主表数据.
      2. 使用select方法时, 可使用部分sql函数(仅支持sum/max/min/ifnull/count/avg)，例如：
      List<Student> students = jdbcDao.selectList(Conditions.lambdaQuery(Student.class)
                  .eq(Student::getName, "张三")
                  .between(Student::getAge, 20, 25)
                  .select(Student::getAge)
                  .select(x -> x.sum(Student::getAge, Student::getSumAge))
                  .groupBy(Student::getAge)
          );
```

- 实时同步查询(一对一、一对多)

```java
/**
 * 同步查询-查询多条记录
 */
<T> List<T> selectList(SyncQueryWrapper<T> wrapper);

/**
 * 同步查询-查询单条记录
 */
<T> T selectOne(SyncQueryWrapper<T> wrapper);

/**
 * 同步查询-分页
 */
<T> DbPageRows<T> selectPage(SyncQueryWrapper<T> wrapper);
```

使用示例

```java
// 查询单个对象
Student student = jdbcDao.selectOne(Conditions.syncQuery(Student.class)
                // student对象的查询(即主对象)
                .primaryEx(x -> x.eq(Student::getNickName, "siyecao"))
                // student对象中某个非持久化属性的查询(即一对一、一对多)
                // t 即是查询后的student对象，作为预判断提前使用
                .property(Student::setModelList, t -> t.getModelList() == null,
                        Conditions.lambdaQuery(Street.class).in(Street::getId, 5012, 5013, 5014, 5015))
                .property(Student::setProvince, t -> t.getProvince() == null,
                        t -> Conditions.lambdaQuery(Province.class).in(t.getProId() != null, Province::getId, t.getProId()))
        );

Province province = student.getProvince();
// 查询多条主对象
List<City> cityList = jdbcDao.selectList(Conditions.syncQuery(City.class)
                                         .primaryEx(x -> x.eq(City::getProvinceId, province.getId()))
                                         .property(City::setLocationList, t -> Conditions.lambdaQuery(Location.class).in(Location::getCityId, t.getId()))
                                        );
province.setCityList(cityList);
```

- 删除

```java
    根据主键删除一条记录
    public <T> int deleteByKey(Class<T> t, Object key);

    根据主键删除多条记录
    public <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys);

    根据条件删除记录
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params);

    根据条件删除记录
    public <T> int deleteSelective(ConditionWrapper<T> wrapper);
```


- 修改

```java
    根据主键修改一条记录
    public <T> int updateByKey(T entity);

    根据条件修改一条记录(只修改entity中属性值 !=null 的字段)
    public <T> int updateByCondition(T entity, String condition, Object... params);

    根据sql set设置器修改n条记录
	public <T> int updateSelective(AbstractUpdateSet<T> updateSet);
```

- 添加

```java
    插入一条记录
    public <T> long insert(T entity);

    插入多条记录
    public <T> int insertBatch(List<T> entityList);
```
- 公共方法

```java
  保存一条记录(根据主键添加或修改)
  public <T> int save(T entity);
  
  执行一条sql(增删改)
  public <T> int executeSql(String sql, Object... params);
  
  删除表
  public void dropTables(Class<?>... arr);
  
  创建表
  public void createTables(Class<?>... arr);
```

  

- 事务执行

```java
  事务执行方法
  public void execTrans(TransactionExecutor executor);
```

  使用示例

```java
jdbcDao.execTrans(() -> {
    // 逻辑写在里面即可
    Employee employee = jdbcDao.selectByKey(Employee.class, 10);
    employee.setEmpName("zhangsan");
    jdbcDao.updateByKey(employee);
    int a = 1 / 0;
    employee.setEmpName("lisi");
    jdbcDao.updateByKey(employee);
});
```

- 实体注解介绍

  

  主键注解：**@DbKey**（仅可标注在java属性上)

  | 注解属性 | 说明                                                         |
  | :------- | ------------------------------------------------------------ |
  | value    | 表主键字段，若不填写，则默认与java属性一致，当策略中驼峰转下划线为true时，解析时会自动转下划线 |
  | strategy | 提供三种主键策略：AUTO为自增. UUID为系统UUID，添加时会自动生成. INPUT则需要自行输入主键值，默认AUTO |
  | dbType   | 数据库对应字段类型：一共提供十多种类型供选择，为枚举属性，默认为DbType.DbInt |
  | desc     | 字段说明                                                     |

  

  普通字段注解：**@DbField**（仅可标注在java属性上)

  | 注解属性     | 说明                                                         |
  | :----------- | ------------------------------------------------------------ |
  | value        | 表主键字段，若不填写，则默认与java属性一致，当策略中驼峰转下划线为true时，解析时会自动转下划线 |
  | dataType     | 数据库对应字段类型：一共提供十多种类型供选择，为枚举属性，默认为DbType.DbVarchar |
  | desc         | 字段说明                                                     |
  | isNull       | 是否允许为空，该属性仅在创建表时用到                         |
  | exist        | 是否存在该表字段，作用与DbNotField一致                       |
  | fillStrategy | 自动填充策略，在参数为实体时的插入或者修改(逻辑删除)时，自动填充指定字段的值 |

  

  关联表注解1：**@DbRelated**（仅可标注在java属性上）
  
  | 注解属性  | 说明                                              |
  | :-------- | ------------------------------------------------- |
  | joinTable | 要关联的表，例如：teacher                         |
  | joinAlias | 关联表的别名，例如：tea                           |
  | condition | 关联条件，例如：a.tea_id = tea.id                 |
  | joinStyle | 关联方式，可选：inner join，left join，right join |
  | field     | 注入的字段，也就是要查询的表字段（teacher_name）  |

  

  关联表注解2：**@DbJoinTables**（仅可标注在java类上）
  
  | 注解         | 说明                                                         |
  | ------------ | ------------------------------------------------------------ |
  | @DbJoinTable | @DbJoinTables中内部注解，该注解仅用于配置表关联条件，例如：left join teacher tea on a.tea_id = tea.id |
  | @DbJoinField    | 配合@DbJoinTable一起使用，value值必须带上关联表的别名，例如：tea.teacher_name |

  

  表注解：**@DbTable**（仅可标注在java类上)
  
  | 注解属性 | 说明                                                      |
  | -------- | --------------------------------------------------------- |
  | table    | 表名                                                      |
  | alias    | 别名                                                      |
  | desc     | 表说明                                                    |
  | order    | 若存在动态数据源，则指定该值与dataSource中的order一致即可 |
  
  
  
  非持久化字段注解：**@DbNotField**(标识在java属性上)
  
  在实体中指定忽略的属性，该注解作用与@DbField.exist = false 一致，表示不属于表的字段。
  
  若字段上同时标注了@DbField 或者 @DbKey 与此类注解，则此注解记为无效，
  
- 注解使用示例

```java
@Data
@DbJoinTables({
        @DbJoinTable("left join province pro on pro.id = a.pro_id"),
        @DbJoinTable("left join city cy on cy.id = a.city_id"),
})
@DbTable(table = "student")
public class Student {

    @DbKey(value = "id", strategy = KeyStrategy.AUTO, dbType = DbType.DbInt)
    private Integer id;

    @DbField
    private String name;

    @DbField("nick_code")
    private String nickName;
    
    @DbField
    private Integer areaId;

    @DbJoinField("pro.name")
    private String province;

    @DbJoinField("cy.name")
    private String city;
    
    @DbNotField
    private List<String> childrenList;
```
2. 第二步，执行```createTables```方法即可

```java
JdbcDao.createTables(Student.class);
```

