package com.example.smart_school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.smart_school.pojo.Student;
import com.example.smart_school.service.StudentService;
import com.example.smart_school.util.MD5;
import com.example.smart_school.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags ="学生管理器")
@RestController
@RequestMapping("/sms/studentController")
public class StudentController {
    @Autowired
    private StudentService studentService;

    ///sms/studentController/delStudentById

    @ApiOperation("删除选中的学生信息")
    @DeleteMapping("/delStudentById")
    public Result delStudentById(
            @ApiParam("要删除的所有选中的Student对象") @RequestBody List<Integer> ids
    ){
        studentService.removeByIds(ids);
        return Result.ok();

    }

    // POST sms/studentController/addOrUpdateStudent

    @ApiOperation("学生增加或修改")
    @PostMapping("/addOrUpdateStudent")
    public Result addOrUpdateStudent(
            @ApiParam("要保存或修改的学生信息JSON")@RequestBody Student student
    ){
        Integer id = student.getId();
        if(null == id || 0 == id){
            //增加学生
            student.setPassword(MD5.encrypt(student.getPassword()));
        }
        studentService.saveOrUpdate(student);
        return Result.ok();

    }




    // /sms/studentController/getStudentByOpr/1/3
    @ApiOperation("学生管理信息分页展示")
    @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
    public Result getStudentByOpr(
            @ApiParam("分页查询的页码数")@PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询的页大小")@PathVariable("pageSize") Integer pageSize,
            @ApiParam("查询的条件")Student student
            ){

        //Page对象
        Page<Student> pageParam = new Page<>(pageNo,pageSize);
        IPage<Student> iPage = studentService.getStudentByOpr(pageParam,student);
        return Result.ok(iPage);


    }
}
