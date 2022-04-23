package com.home.shop.controller;

import com.custom.comm.BackResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/20 9:05
 * @Desc：
 **/
@Controller
@RestController
@RequestMapping("/shop")
public class MainController {

//    @Autowired
//    private ProductService productService;
//
//
//    @GetMapping("/getInfo")
//    public BackResult<List<ProductPO>> getInfo() throws Exception {
//        return BackResult.bySuccess(productService.getList());
//    }

}
