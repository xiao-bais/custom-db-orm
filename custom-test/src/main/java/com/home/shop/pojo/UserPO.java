package com.home.shop.pojo;

 import com.custom.comm.annotations.DbField;
 import com.custom.comm.annotations.DbKey;
 import com.custom.comm.annotations.DbTable;
 import com.custom.comm.enums.KeyStrategy;


/**
 * @Author Xiao-Bai
 *
 */

@DbTable(table = "shop_user")
public class UserPO {

    /**
     * 
     */
    @DbKey(value = "id")
    private Integer id;

    /**
     * 用户默认ID
     */
    @DbField(value = "user_id")
    private String userId;

    /**
     * 用户昵称
     */
    @DbField(value = "nick_name")
    private String nickName;

    /**
     * 用户手机
     */
    @DbField(value = "phone")
    private String phone;

    /**
     * 登录密码
     */
    @DbField(value = "password")
    private String password;

    /**
     * 用户邮箱
     */
    @DbField(value = "email")
    private String email;

    /**
     * 用户头像
     */
    @DbField(value = "head_img_url")
    private String headImgUrl;

    /**
     * 最后登录时间
     */
    @DbField(value = "last_login_time")
    private Integer lastLoginTime;

    /**
     * 默认收货地址
     */
    @DbField(value = "default_addr")
    private String defaultAddr;

    /**
     * 会员等级
     */
    @DbField(value = "vip_level")
    private Integer vipLevel;

    /**
     * 是否是老顾客？
     */
    @DbField(value = "regular_flag")
    private Boolean regularFlag;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public Integer getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Integer lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getDefaultAddr() {
        return defaultAddr;
    }

    public void setDefaultAddr(String defaultAddr) {
        this.defaultAddr = defaultAddr;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public Boolean getRegularFlag() {
        return regularFlag;
    }

    public void setRegularFlag(Boolean regularFlag) {
        this.regularFlag = regularFlag;
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