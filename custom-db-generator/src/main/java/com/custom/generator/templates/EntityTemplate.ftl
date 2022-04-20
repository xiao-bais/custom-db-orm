package ${sourcePackage}

<#list importOtherPackages as index>
 ${index}
</#list>

<#list importJavaPackages as index>
${index}
</#list>

<#if lombok>
@Data
</#if>
@DbTable(table = "${table}")
public class ${entityName} {

<#list columnStructModels as column>
    /**
     * ${column.desc}
     */
    ${column.dbFieldAnnotation}
    @ApiModelProperty(value = "${column.desc}")
    ${column.outputFieldInfo}

</#list>

<#if !lombok>
<#list columnStructModels as column>
    public ${column.fieldTypeName} ${column.getterMethodName}() {
        return ${column.fieldName};
    }

    public void ${column.setterMethodName} (${column.fieldTypeName} ${column.fieldName}) {
        this.${column.fieldName} = ${column.fieldName};
    }

</#list>
</#if>

}