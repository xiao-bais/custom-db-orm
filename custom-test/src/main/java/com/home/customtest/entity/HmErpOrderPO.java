package com.home.customtest.entity;

import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import lombok.Data;

@Data
@DbTable(value = "hm_erp_order")
public class HmErpOrderPO {
    /**
     *
     */
    @DbKey
    private Integer id;

    /**
     * 客资ID
     */
    private String clientId;

    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 订单来源，字典表diccode
     */
    private Integer orderSource;

    /**
     * 套餐ID
     */
    private Integer mealId;

    /**
     * 套餐名称
     */
    private String mealName;

    /**
     * 订单原价
     */
    private Integer amount;

    /**
     * 套餐优惠价
     */
    private Integer activAmount;

    /**
     * 已收金额
     */
    private Integer haveAmount;

    /**
     * 订单总成本
     */
    private Integer totalCost;

    /**
     *
     */
    private Integer companyId;

    /**
     * 订单时间
     */
    private Integer orderTime;

    /**
     * 拍摄类型，字典表diccode
     */
    private Integer orderType;

    /**
     * 订单备注
     */
    private String orderMemo;

    /**
     *
     */
    private Boolean delFlag;

    /**
     * 门店编号
     */
    private Integer shopId;

    /**
     * 服务门店编号
     */
    private Integer serviceShopId;

    /**
     * 底片数量
     */
    private Integer negativeNum;

    /**
     * 精修数量
     */
    private Integer psNum;

    /**
     * 入册数量
     */
    private Integer albumNum;

    /**
     * 衣服总套数
     */
    private Integer totalClothesNum;

    /**
     * 内景衣服套数
     */
    private Integer insideClothesNum;

    /**
     * 外景衣服套数
     */
    private Integer outsideClothesNum;

    /**
     * MV数量
     */
    private Integer mvNum;

    /**
     * 订单进度（1：暂存；2：下单待付款；3：完成）
     */
    private Integer orderSpeed;

    /**
     * 下单人ID
     */
    private Integer createId;

    /**
     * 创建时间
     */
    private Integer createTime;

    /**
     * 酒店等级diccode
     */
    private Integer hotelLevel;

    /**
     * mv地址
     */
    private String mvAddress;

    /**
     * 电子相册地址
     */
    private String photoAddress;

    /**
     * 教程地址
     */
    private String courseAddress;

    /**
     * 附件（图片地址)
     */
    private String attachment;

    /**
     * 照片发送：0未发 ，1已发
     */
    private Integer addressStatus;

    /**
     * 订单权限
     */
    private Integer orderLevel;

    /**
     * 剩余能量点
     */
    private Integer energyPoint;

    /**
     * 精修增加张数
     */
    private Integer addPsNum;

    /**
     * 入册增加张数
     */
    private Integer addAlbumNum;

    /**
     * 增加的优惠券价格
     */
    private Integer addCouponAmount;

    /**
     * 成本价
     */
    private Integer costPrice;

    /**
     * 修片要求查看标识
     */
    private Boolean lookFlag;

    /**
     * 修片要求客户是否编辑
     */
    private Boolean editFlag;

    /**
     * 成本百分比
     */
    private Double costPercent;

    /**
     * 50 张数
     */
    private Integer basePs;

    /**
     * 80张数
     */
    private Integer outRangePs;

    /**
     *
     */
    private String costPercentName;

    /**
     * 是否是补录订单
     */
    private Boolean refillFlag;

    /**
     * 博城订单号
     */
    private String bcOrderNum;

    /**
     * 授权人ID
     */
    private Integer authorizeMan;

    /**
     * 转介绍类型  现目前写死 之后字典表配置
     */
    private Integer zjsType;

    /**
     * 订单状态 ： 0 : 正常 1 ： 正常退单 2 ：作废
     */
    private Integer orderStatus;

    /**
     * 增加酒店价格
     */
    private Integer addHotelPrice;

    /**
     * 是否使用授权码
     */
    private Boolean isCostPercent;

    /**
     * 是否推荐单（需审核）
     */
    private Boolean recommendFlag;
}

