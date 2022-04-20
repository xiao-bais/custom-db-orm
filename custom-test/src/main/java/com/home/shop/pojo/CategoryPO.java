package com.home.shop.pojo;

 import com.custom.comm.annotations.DbField;
 import com.custom.comm.annotations.DbKey;
 import com.custom.comm.annotations.DbTable;
 import com.custom.comm.enums.KeyStrategy;


/**
 * @Author Xiao-Bai
 *
 */

@DbTable(table = "shop_category")
public class CategoryPO {

    /**
     * 
     */
    @DbKey(value = "id")
    private Integer id;

    /**
     * 产品种类名称
     */
    @DbField(value = "name")
    private String name;

    /**
     * 种类说明
     */
    @DbField(value = "category_desc")
    private String categoryDesc;

    /**
     * 父级种类ID
     */
    @DbField(value = "parent_id")
    private Integer parentId;

    /**
     * 种类样式图
     */
    @DbField(value = "image")
    private String image;

    /**
     * 创建时间
     */
    @DbField(value = "create_time")
    private Integer createTime;

    /**
     * 修改时间
     */
    @DbField(value = "update_time")
    private Integer updateTime;

    /**
     * 状态：0-正常，1-已删除
     */
    @DbField(value = "state")
    private Integer state;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }


}