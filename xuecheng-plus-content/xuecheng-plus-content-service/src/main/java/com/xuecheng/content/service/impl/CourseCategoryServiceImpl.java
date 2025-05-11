package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryDto> queryTreeNodes(String id) {
        if (id==null)
            return null;
        //调用mapper查询数据库
        List<CourseCategoryDto> courseCategoryDtos = courseCategoryMapper.selectTreeNodes(id);

        //找到每个节点的子节点，最终封装为List<CourseCategoryDto>
        //先将List转为Map，key为节点id，value为节点对象
        Map<String, CourseCategoryDto> categoryDtoMap = courseCategoryDtos.stream()
                .filter(item->!id.equals(item.getId()))  //筛选出根节点
                .collect(
                        Collectors.toMap(key -> key.getId(),
                                value -> value,
                                (key1, key2) -> key2));
        //创建list，用于最终封装
        ArrayList<CourseCategoryDto> arrayList = new ArrayList<>();
        //遍历每个节点，找到子节点
        courseCategoryDtos.stream()
                .filter(item->!id.equals(item.getId())) //筛选出根节点
                .forEach(item->{
            if (item.getParentid().equals(id)){
                arrayList.add(item);
            }
            //找到子节点，设置子节点
            CourseCategoryDto courseCategoryParent = categoryDtoMap.get(item.getParentid());
            if (courseCategoryParent!=null){
                if (courseCategoryParent.getChildrenTreeNodes()==null) {
                    //如果子节点为空，创建一个空的list 用于存放节点
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<CourseCategoryDto>());
                }
                //添加子节点
                courseCategoryParent.getChildrenTreeNodes().add(item);
            }
        });
        return arrayList;
    }
}
