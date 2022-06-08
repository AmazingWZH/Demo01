package com.example.smart_school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.smart_school.pojo.Admin;
import com.example.smart_school.service.AdminService;
import com.example.smart_school.util.MD5;
import com.example.smart_school.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "管理员控制器")
@RestController
@RequestMapping("/sms/adminController")
public class AdminController {
    @Autowired
    private AdminService adminService;

    //delete
    // /sms/adminController/deleteAdmin
    @ApiOperation("删除选中的管理员")
    @DeleteMapping("/deleteAdmin")
    public Result deleteAdmin(
            @ApiParam("选中的管理员")@RequestBody List<Integer> ids
    ){
        adminService.removeByIds(ids);
        return Result.ok();
    }

    // POST
    // /sms/adminController/saveOrUpdateAdmin
    @ApiOperation("增加或者修改管理员")
    @PostMapping("/saveOrUpdateAdmin")
    public Result saveOrUpdateAdmin(
            @ApiParam("JSON形式的admin")@RequestBody Admin admin
    ){
        Integer id = admin.getId();
        if(null == id || 0 == id){
            //增加管理员
            admin.setPassword(MD5.encrypt(admin.getPassword()));
        }
        adminService.saveOrUpdate(admin);
        return Result.ok();
    }

    //GET
    //	http://localhost:8080/sms/adminController/getAllAdmin/1/3
    @GetMapping("/getAllAdmin/{pageNo}/{pageSize}")
    public Result getAllAdmin(
            @ApiParam("初始页码") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("每一页显示的个数")@PathVariable("pageSize")Integer pageSize,
            @ApiParam("管理员名字") String adminName
    ){
        //page对象
        Page<Admin> page = new Page(pageNo,pageSize);
        IPage<Admin> iPage = adminService.getAdmin(page,adminName);
        return Result.ok(iPage);

    }


}
