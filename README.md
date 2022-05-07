```

```

custom-springboot-starter

### 简介
custom-db-action为自定义的一款集成数据源```ORM```操作工具，底层为最原始的```JDBC```，使用阿里的druid作为连接池，将```JDBC```进行了一些封装，将之变成一款可极大简化操作数据的轻量级第三方```ORM```工具，集成了```Mybatis-Plus```的条件构造器，在此之上添加了 **多表连接查询** 的条件构造，使增删改查变得更容易。
### 说明：
- ```com.custom.sqlparser.JdbcDao```，该类提供了多种增删改查方法以供用户自定义使用，使用时只需要在service或controller注入该对象即可，只需要编写部分的条件```sql```，即可完成单表的大部分增删改查操作。
- 只需要创建实体类，并添加上自定义的几个注解，即可生成对应的表结构。
- 暂时只支持mysql
- 支持可配置的表关联查询以及逻辑删除，```sql```语句打印输出，下划线转驼峰等功能。
- 该工具已完成```springboot```的自动配置,在```springboot```项目中引入该依赖即可，无需另外配置，轻松便捷。

### 注意
目前依赖还未部署在maven中央仓库，所以需借助aliyun的私服进行管理，使用前，需将【[maven下的settings.xml文件](http://39.108.225.176/downloads/settings.xml)】替换。
#### 安装依赖

```xml
         <dependency>
            <groupId>com.custom</groupId>
            <artifactId>custom-springboot-starter</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
```


#### 配置数据源

1.  ```SpringBoot```项目配置数据源：因dataSource类为本工具自定义，所以在```application.yml(properties)```中进行如下基本配置即可，```mysql```驱动默认为```mysql8.0```--->```com.mysql.cj.jdbc.Driver```(配置文件中可不写)

```properties
custom.db.datasource.url=jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC
custom.db.datasource.username=root
custom.db.datasource.password=123456
```


#### 使用说明

1.  该工具提供大量的增删改查方法API
- 一般查询

```java

    查询多条记录: 例1（and a.name = ?, "张三"），例2 (and a.name = "张三") 
    public <T> List<T> selectList(Class<T> t, String condition, Object... params) throws Exception;


    根据多个主键查询多条记录
    public <T> List<T> selectListByKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;


    根据sql查询多条记录: 例（select * from table ）
    public <T> List<T> selectListBySql(Class<T> t, String sql, Object... params) throws Exception;


    根据条件进行分页查询并排序: 例（and a.name = ? , 1, 10, "张三"）
    public <T> DbPageRows<T> selectPageRows(Class<T> t, String condition, int pageIndex, int pageSize, Object... params) throws Exception;


    根据主键查询一条记录：例 (25)
    public <T> T selectOneByKey(Class<T> t, Object key) throws Exception;

    纯sql查询一条记录：例（select * from table where age = ?, 25）
    public <T> T selectOneBySql(Class<T> t, String sql, Object... params) throws Exception;

    纯sql查询单个值：例（select name from table where age = ?, 25）
    public Object selectObjBySql(String sql, Object... params) throws Exception;

    根据条件查询一条记录
    public <T> T selectOneByCondition(Class<T> t, String condition, Object... params) throws Exception;
```

- **条件构造查询**（**与mybatis-plus的条件构造器相差无几**）

  `selectPageRows(查询分页)`

  `selectList(查询多条)`

  `selectOne(查询单条记录)`

  `selectObj(查询单列字段，并且只有一个值，若有多个，只返回第一个满足条件的值)`

  `selectObjs(同上，但允许会返回多个值)`

  ```java
  public <T> DbPageRows<T> selectPageRows(ConditionWrapper<T> wrapper) throws Exception;
  public <T> List<T> selectList(ConditionWrapper<T> wrapper) throws Exception;
  public <T> T selectOne(ConditionWrapper<T> wrapper) throws Exception;
  public <T> long selectCount(ConditionWrapper<T> wrapper) throws Exception;
  public <T> Object selectObj(ConditionWrapper<T> wrapper) throws Exception;
  public <T> List<Object> selectObjs(ConditionWrapper<T> wrapper) throws Exception;
  ```

  **使用方法**

  ```java
  1: 一般字段构造
      
      ConditionEntity<ChildStudent> cond = new ConditionEntity<>(ChildStudent.class);
      cond.eq("name", "张三").select("id", "name", "age").limit(1, 10);
      DbPageRows<ChildStudent> dbPageRows = jdbcDao.selectPageRows(cond);
  
  2: lambda表达式构造
      
     	LambdaConditionEntity<ChildStudent> cond = new LambdaConditionEntity<>(ChildStudent.class);
     	cond.eq(ChildStudent::getName, "张三")
          .select(ChildStudent::getName, ChildStudent::getId, ChildStudent::getAge)
          .limit(1, 10);
      DbPageRows<ChildStudent> dbPageRows = jdbcDao.selectPageRows(cond);
  
  3: 使用静态方法实例化
      DbPageRows<ChildStudent> dbPageRows = jdbcDao.selectPageRows(Conditions.lambdaQuery(ChildStudent.class)
                  .eq(ChildStudent::getName, "张三")
                  .select(ChildStudent::getName, ChildStudent::getId, ChildStudent::getAge)
                  .limit(1, 10)
     );
  
  额外说明：
      1. 使用onlyPrimary()方法时，可使本次查询只查询主表数据.
      2. 使用select方法时，可使用部分sql函数(仅支持sum、max、min、ifnull、count、avg)，例如：
      List<ChildStudent> childStudents = jdbcDao.selectList(Conditions.lambdaQuery(ChildStudent.class)
                  .eq(ChildStudent::getName, "张三")
                  .between(ChildStudent::getAge, 20, 25)
                  .select(ChildStudent::getAge)
                  .select(x -> x.sum(ChildStudent::getAge, ChildStudent::getSumAge))
                  .groupBy(ChildStudent::getAge)
          );
  ```



- 删除

```java
    根据主键删除一条记录
    public <T> int deleteByKey(Class<T> t, Object key) throws Exception;

    根据主键删除多条记录
    public <T> int deleteBatchKeys(Class<T> t, Collection<? extends Serializable> keys) throws Exception;

    根据条件删除记录
    public <T> int deleteByCondition(Class<T> t, String condition, Object... params) throws Exception;
```


- 修改

```java
    根据主键修改一条记录（updateColumns：指定要修改的表字段  为空则按主键修改全部[不为空]字段）
    public final <T> int updateByKey(T t, SFunction<T, ?>... updateColumns) throws Exception;

    根据主键修改一条记录
    public <T> int updateByKey(T t) throws Exception;
```

- 添加

```java
    插入一条记录
    public <T> long insert(T t) throws Exception;

    插入多条记录
    public <T> int insert(List<T> tList) throws Exception;
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

  | 注解属性 | 说明                                                         |
  | :------- | ------------------------------------------------------------ |
  | value    | 表主键字段，若不填写，则默认与java属性一致，当策略中驼峰转下划线为true时，解析时会自动转下划线 |
  | dataType | 数据库对应字段类型：一共提供十多种类型供选择，为枚举属性，默认为DbType.DbVarchar |
  | desc     | 字段说明                                                     |
  | isNull   | 是否允许为空，该属性仅在创建表时用到                         |

  

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
  | @DbMapper    | 配合@DbJoinTable一起使用，value值必须带上关联表的别名，例如：tea.teacher_name |

  [^@DbJoinTable(s)注解,可支持父子类一起合并使用]: 

  

  表注解：**@DbTable**（仅可标注在java类上)

  | 注解属性               | 说明                                                         |
  | ---------------------- | ------------------------------------------------------------ |
  | table                  | 表名                                                         |
  | alias                  | 别名                                                         |
  | desc                   | 表说明                                                       |
  | mergeSuperDbJoinTables | 默认为true，当子类跟父类同时标注了@DbJoinTable(s)注解时，是否在查询时向上查找父类的@DbJoinTable(s)注解，且合并关联条件 |

  

- 示例

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

    @DbMapper("pro.name")
    private String province;

    @DbMapper("cy.name")
    private String city;
```
2. 第二步，执行```createTables```方法即可

```java
JdbcDao.createTables(Student.class);
```
