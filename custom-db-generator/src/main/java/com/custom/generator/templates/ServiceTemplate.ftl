package ${sourcePackage};

<#list importPackages as index>
 ${index}
</#list>

/**
* @Author ${author}
* @Date ${createDate}
*/

@Service
public class ${className} {

    @Autowired
    private JdbcDao jdbcDao;




}