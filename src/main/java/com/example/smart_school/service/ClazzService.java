package com.example.smart_school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.smart_school.pojo.Clazz;

import java.util.List;

public interface ClazzService extends IService<Clazz> {
    List<Clazz> getClazzs();

    IPage<Clazz> getClazzsByOpr(Page<Clazz> page, Clazz clazz);
}
