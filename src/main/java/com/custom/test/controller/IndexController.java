//package com.custom.test.controller;
//
//import com.alibaba.fastjson.JSONArray;
//import com.custom.comm.BackResult;
//import com.custom.test.Employee;
//import com.custom.test.JdbcTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * @Author Xiao-Bai
// * @Date 2021/11/24 17:05
// * @Desc：
// **/
//@RestController
//@RequestMapping("aaa")
//public class IndexController {
//
//    private JdbcTest jdbcTest;
//
//    public IndexController(JdbcTest jdbcTest) {
//       this.jdbcTest = jdbcTest;
//    }
//
//
//    @GetMapping("/getIndex")
//    public BackResult<String> getIndex(){
//        List<Employee> list = jdbcTest.getList();
//        String jsonString = JSONArray.toJSONString(list);
//        return BackResult.bySuccess("成功11", jsonString);
//    }
//
//
//}
