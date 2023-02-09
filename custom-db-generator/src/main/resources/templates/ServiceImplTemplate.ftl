package ${sourcePackage}.impl;

<#list importPackages as index>
${index}
</#list>

/**
* @author  ${author}
* @since  ${createDate}
*/

@Service
public class ${serviceImplName} implements ${serviceName} {

    @Autowired
    private JdbcDao jdbcDao;




}