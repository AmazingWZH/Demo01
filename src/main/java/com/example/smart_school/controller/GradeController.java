package com.example.smart_school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.smart_school.pojo.Grade;
import com.example.smart_school.service.GradeService;
import com.example.smart_school.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "年级控制器")
@RestController
@RequestMapping("/sms/gradeController")
public class GradeController {
    @Autowired
    private GradeService gradeService;

    @ApiOperation("获取全部年级")
    @GetMapping("/getGrades")
    public Result getGrades(){
        List<Grade> grades = gradeService.getGrades();
        return Result.ok(grades);
    }



    //年级管理的删除功能
    @ApiOperation("删除Grade信息")
    @RequestMapping("/deleteGrade")
    public Result deleteGrade(
            @ApiParam("要删除的所有选中的grade对象") @RequestBody  List<Integer> ids){
        gradeService.removeByIds(ids);
        return Result.ok();
    }

    //年级管理的添加和修改功能
    @ApiOperation("新增或修改Grade,有id属性则表示新增，没有则是增加")
    @PostMapping("/saveOrUpdateGrade")
    public Result saveOrUpdateGrade(
            @ApiParam("JSON形式的grade对象")@RequestBody Grade grade
    ){
        //接受参数
        //调用服务层完成增减或修改操作
        gradeService.saveOrUpdate(grade);
        return Result.ok();
    }

    @ApiOperation("根据年级名称模糊查询,带分页")
    @GetMapping("/getGrades/{pageNo}/{pageSize}")
    public Result getGrades(
            @ApiParam("分页查询的页码数")@PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询的页大小")@PathVariable("pageSize") Integer pageSize,
            @ApiParam("分页查询模糊匹配的名称")String gradeName

    ){
        // 分页 带条件查询
        Page<Grade> page = new Page<>(pageNo,pageSize);
        //通过服务层
        IPage<Grade> pageRs = gradeService.getGradeByOpr(page,gradeName);

        //封装Result对象并返回
        return Result.ok(pageRs);

    }
}
