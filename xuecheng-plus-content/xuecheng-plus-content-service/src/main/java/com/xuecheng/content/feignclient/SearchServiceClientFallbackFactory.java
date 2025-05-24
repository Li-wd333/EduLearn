package com.xuecheng.content.feignclient;

import com.xuecheng.content.feignclient.po.CourseIndex;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceClientFallbackFactory.class);

    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                log.error("添加课程索引发生失败 熔断异常:{}",throwable.getMessage());
                return false;
            }
        };
    }
}
