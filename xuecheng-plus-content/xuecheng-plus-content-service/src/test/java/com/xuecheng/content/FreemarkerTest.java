package com.xuecheng.content;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class FreemarkerTest {

    @Autowired
    private CoursePublishService coursePublishService;

    @Test
    public void testGenerateHtml() throws Exception {
        //Template 模板
        Configuration configuration = new Configuration(Configuration.getVersion());
        //拿到classPath路径
        String path = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new java.io.File(path + "/templates/"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板
        Template template = configuration.getTemplate("course_template.ftl");
        //数据
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(18L);
        Map<String, Object> map = new HashMap<>();
        map.put("model", coursePreviewInfo);
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //输入流
        InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
        //输出文件
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\aaavedio\\ww222\\18.html"));
        //使用流将html写入文件
        IOUtils.copy(inputStream, fileOutputStream);
    }
}