package com.home.shop.pojo;

 import com.custom.comm.annotations.DbField;
 import com.custom.comm.annotations.DbKey;
 import com.custom.comm.annotations.DbTable;
 import lombok.Data;
 import io.swagger.annotations.ApiModelProperty;
 import com.custom.comm.enums.KeyStrategy;


/**
 * @Author Xiao-Bai
 * @Date 2022-12-16 15:11
 */

@Data
@DbTable(table = "shop_user")
public class UserPO {

    /**
     * 
     */
    @DbKey
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 用户默认ID
     */
    @DbField
    @ApiModelProperty(value = "用户默认ID")
    private String userId;

    /**
     * 用户昵称
     */
    @DbField
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 用户手机
     */
    @DbField
    @ApiModelProperty(value = "用户手机")
    private String phone;

    /**
     * 登录密码
     */
    @DbField
    @ApiModelProperty(value = "登录密码")
    private String password;

    /**
     * 用户邮箱
     */
    @DbField
    @ApiModelProperty(value = "用户邮箱")
    private String email;

    /**
     * 用户头像
     */
    @DbField
    @ApiModelProperty(value = "用户头像")
    private String headImgUrl;

    /**
     * 最后登录时间
     */
    @DbField
    @ApiModelProperty(value = "最后登录时间")
    private Integer lastLoginTime;

    /**
     * 默认收货地址
     */
    @DbField
    @ApiModelProperty(value = "默认收货地址")
    private String defaultAddr;

    /**
     * 会员等级
     */
    @DbField
    @ApiModelProperty(value = "会员等级")
    private Integer vipLevel;

    /**
     * 是否是老顾客？
     */
    @DbField
    @ApiModelProperty(value = "是否是老顾客？")
    private Boolean regularFlag;

    /**
     * 创建时间
     */
    @DbField
    @ApiModelProperty(value = "创建时间")
    private Integer createTime;

    /**
     * 修改时间
     */
    @DbField
    @ApiModelProperty(value = "修改时间")
    private Integer updateTime;

    /**
     * 状态：0-正常，1-已删除
     */
    @DbField
    @ApiModelProperty(value = "状态：0-正常，1-已删除")
    private Integer state;



}