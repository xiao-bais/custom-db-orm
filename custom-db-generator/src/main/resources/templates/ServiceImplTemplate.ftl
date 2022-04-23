package ${sourcePackage}.impl;

<#list importPackages as index>
${index}
</#list>

/**
* @Author ${author}
* @Date ${createDate}
*/

@Service
public class ${serviceImplName} implements ${serviceName} {

    @Autowired
    private JdbcDao jdbcDao;




}