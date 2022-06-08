package com.example.smart_school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.smart_school.pojo.Teacher;
import com.example.smart_school.service.TeacherService;
import com.example.smart_school.util.MD5;
import com.example.smart_school.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "教师管理器")
@RestController
@RequestMapping("/sms/teacherController")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    // /deleteTeacher
    @ApiOperation("删除教师信息")
    @DeleteMapping("/deleteTeacher")
    public Result deleteTeacher(
            @ApiParam("选中的教师集合")@RequestBody List<Integer> ids
    ){
        teacherService.removeByIds(ids);
        return Result.ok();
    }


    // /saveOrUpdateTeacher
    @ApiOperation("增加或修改老师信息")
    @PostMapping("/saveOrUpdateTeacher")
    public Result saveOrUpdateTeacher(
            @ApiParam("JSON格式的教师对象")@RequestBody Teacher teacher
    ){
        Integer id = teacher.getId();
        if(null == id || 0 == id){
            //增加教师
            teacher.setPassword(MD5.encrypt(teacher.getPassword()));
        }
        teacherService.saveOrUpdate(teacher);
        return Result.ok();
    }


    // /sms/teacherController/getTeachers/1/3?name=wang&clazzName
    @ApiOperation("分页查询所有教师信息")
    @GetMapping("/getTeachers/{pageNo}/{pageSize}")
    public Result getTeachers(
            @ApiParam("页码数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("页码大小")@PathVariable("pageSize") Integer pageSize,
            @ApiParam("教师类型")Teacher teacher
    ){
        Page<Teacher> page = new Page<>(pageNo,pageSize);
        IPage<Teacher> iPage = teacherService.getTeachers(page,teacher);

        return Result.ok(iPage);

    }




}
