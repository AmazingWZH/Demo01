package com.example.smart_school.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.smart_school.pojo.Admin;
import com.example.smart_school.pojo.LoginForm;
import com.example.smart_school.pojo.Student;
import com.example.smart_school.pojo.Teacher;
import com.example.smart_school.service.AdminService;
import com.example.smart_school.service.StudentService;
import com.example.smart_school.service.TeacherService;
import com.example.smart_school.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Api(tags = "系统管理器")
@RestController
@RequestMapping("/sms/system")
public class SystemController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    // POST
    //sms/system/updatePwd/123456/654321
    @ApiOperation("用户修改自己密码")
    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result updatePwd(
            @ApiParam("用户token")@RequestHeader("token") String token,
            @ApiParam("旧密码")@PathVariable("oldPwd") String oldPwd,
            @ApiParam("新密码")@PathVariable("newPwd") String newPwd
    ){
        //首先判断登录是否过期
        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration){
            return Result.fail().message("您的登录已过期，请重新登录");
        }

        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        oldPwd = MD5.encrypt(oldPwd);
        newPwd = MD5.encrypt(newPwd);

        switch (userType){
            case 1:
                QueryWrapper<Admin> QueryWrapper1 = new QueryWrapper<>();
                QueryWrapper1.eq("id",userId.intValue());
                QueryWrapper1.eq("password",oldPwd);
                Admin admin = adminService.getOne(QueryWrapper1);
                if(admin != null){
                    //修改密码
                    admin.setPassword(newPwd);
                    adminService.saveOrUpdate(admin);
                }else{
                    return Result.fail().message("原密码有误");
                }
                break;
            case 2:
                QueryWrapper<Student> QueryWrapper2 = new QueryWrapper<>();
                QueryWrapper2.eq("id",userId.intValue());
                QueryWrapper2.eq("password",oldPwd);
                Student student = studentService.getOne(QueryWrapper2);
                if(student != null){
                    //修改密码
                    student.setPassword(newPwd);
                    studentService.saveOrUpdate(student);
                }else{
                    return Result.fail().message("原密码有误");
                }
                break;
            case 3:
                QueryWrapper<Teacher> QueryWrapper3 = new QueryWrapper<>();
                QueryWrapper3.eq("id",userId.intValue());
                QueryWrapper3.eq("password",oldPwd);
                Teacher teacher = teacherService.getOne(QueryWrapper3);
                if(teacher != null){
                    //修改密码
                    teacher.setPassword(newPwd);
                    teacherService.saveOrUpdate(teacher);
                }else{
                    return Result.fail().message("原密码有误");
                }
                break;
        }
            return Result.ok();
    }

    // POST
    //	http://localhost:8080/sms/system/headerImgUpload

    @ApiOperation("文件上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(
            @ApiParam("头像文件") @RequestPart("multipartFile") MultipartFile multipartFile,
            HttpServletRequest request
    ){
        String uuid = UUID.randomUUID().toString().replace("_", "").toLowerCase();
        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String newfilename = uuid.concat(originalFilename.substring(i));
        //保存文件，将文件发送到第三方/文件服务器上
        String Portiait = "D:/Idea_workplace/smart_school/target/classes/public/upload".concat(newfilename);
        try {
            multipartFile.transferTo(new File(Portiait));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //相应图片的 路径
        String path = "/upload".concat(newfilename);
        return Result.ok(path);
    }



    @GetMapping("/getInfo")
    public Result getInfoByToken(@RequestHeader("token") String token){
        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration){
            //token过期了
            return Result.build(null, ResultCodeEnum.CODE_ERROR);
        }
        //没过期的情况下，从token中解析出用户id和用户类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        Map<String,Object> map = new LinkedHashMap<>();
        switch (userType){
            case 1:
                Admin admin = adminService.getAdminById(userId);
                map.put("userType",1);
                map.put("user",admin);
                break;
            case 2:
                Student student = studentService.getStudentById(userId);
                map.put("userType",2);
                map.put("user",student);
                break;
            case 3:
                Teacher teacher = teacherService.getTeacherById(userId);
                map.put("userType",3);
                map.put("user",teacher);
                break;
        }


        return Result.ok(map);
    }

    /*
    * 登录函数
    * */
    @PostMapping("/login")
    public Result login(@RequestBody LoginForm loginForm,HttpServletRequest res){
        //验证码校验
        HttpSession session = res.getSession();
        String  session_verifiCode = (String)session.getAttribute("verifiCode");
        String login_verifiCode = loginForm.getVerifiCode();
        if("".equals(session_verifiCode)|| null==session_verifiCode){
            return Result.fail().message("验证码失效，请刷新后重试");
        }
        if(!session_verifiCode.equalsIgnoreCase(login_verifiCode)){
            return Result.fail().message("验证码有误，请小心输入后重试");
        }
        //验证码验证过以后要从Session域中移除
        session.removeAttribute("verifiCode");
        //分用户类型进行校验

        //准备一个Map用户存放相应的数据
        Map<String,Object> map  = new LinkedHashMap<>();
        switch (loginForm.getUserType()){
            case 1:
                try {
                    Admin admin = adminService.login(loginForm);
                    if(null != admin){
                        //用户id和用户的类型转换成一个密文，以token的名称向客户端反馈
                        map.put("token", JwtHelper.createToken(admin.getId().longValue(),1));
                    }else{
                        throw new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }

            case 2:
                try {
                    Student student = studentService.login(loginForm);
                    if(null != student){
                        //用户id和用户的类型转换成一个密文，以token的名称向客户端反馈
                        map.put("token", JwtHelper.createToken(student.getId().longValue(),2));
                    }else{
                        throw new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 3:
                try {
                    Teacher teacher = teacherService.login(loginForm);
                    if(null != teacher){
                        //用户id和用户的类型转换成一个密文，以token的名称向客户端反馈
                        map.put("token", JwtHelper.createToken(teacher.getId().longValue(),3));
                    }else{
                        throw new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }


        }
        return Result.fail().message("查无此用户");
    }

    /*
    * 该方法用来在登录时显示验证码
    * */
    @GetMapping("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpServletRequest res, HttpServletResponse resp){
        //获取图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();
        //获取图片上的验证码
        String verifiCode = new String(CreateVerifiCodeImage.getVerifiCode());
        // 将验证码文本放入到session域，为下一次验证做准备
        HttpSession session = res.getSession();
        session.setAttribute("verifiCode",verifiCode);
        //将验证码图片响应给浏览器
        try {
            ImageIO.write(verifiCodeImage,"JPEG",resp.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
