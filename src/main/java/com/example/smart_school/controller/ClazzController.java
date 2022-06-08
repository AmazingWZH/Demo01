package com.example.smart_school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.smart_school.pojo.Clazz;
import com.example.smart_school.service.ClazzService;
import com.example.smart_school.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;


@Api(tags = "班级管理器")
@RestController
@RequestMapping("/sms/clazzController")
public class ClazzController {
    @Autowired
    private ClazzService clazzService;

    //获取班级信息
    @ApiOperation("获取全部班级信息")
    @GetMapping("/getClazz")
    public Result getClazz(){
        List<Clazz> clazzs = clazzService.getClazzs();
        return Result.ok(clazzs);
    }

    @ApiOperation("删除班级信息")
    @DeleteMapping("/deleteClazz")
    public Result deleteClazz(
            @ApiParam("删除所有选中的Clazz对象") @RequestBody List<Integer> ids)
    {
        clazzService.removeByIds(ids);

        return Result.ok();
    }

    ///sms/clazzController/saveOrUpdateClazz
    @ApiOperation("修改或增加班级信息")
    @PostMapping("/saveOrUpdateClazz")
    public Result saveOrUpdateClazz(
            @ApiParam("JSON形式的Clazz对象") @RequestBody Clazz clazz
    ){
        clazzService.saveOrUpdate(clazz);
        return Result.ok();
    }




    @ApiOperation("查询所有班级信息")
    @GetMapping("/getClazzs")
    public Result getClazzs(){
        List<Clazz> grades  = clazzService.getClazzs();
        return Result.ok(grades);

    }

    @ApiOperation("分页带条件查询班级信息")
    @RequestMapping("/getClazzsByOpr/{PageNo}/{PageSize}")
    public Result getClazzByOpr(
            @ApiParam("分页查询的页码数") @PathVariable("PageNo") Integer pageNo,
            @ApiParam("分页查询页大小")@PathVariable("PageSize") Integer pageSize,
            @ApiParam("分页查询的查询条件")Clazz clazz
    ){
        Page<Clazz> page = new Page<>(pageNo,pageSize);
        IPage<Clazz> ipage = clazzService.getClazzsByOpr(page,clazz);

        return Result.ok(ipage);
    }

}
