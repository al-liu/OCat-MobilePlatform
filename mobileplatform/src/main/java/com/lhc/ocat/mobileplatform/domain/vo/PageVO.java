package com.lhc.ocat.mobileplatform.domain.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author lhc
 * @date 2019-11-26 18:50
 */
@Data
public class PageVO<T> {

    /**
     * 查询数据列表
     */
    private List<T> records;

    /**
     * 总数
     */
    private Long total;
    /**
     * 每页显示条数，默认 10
     */
    private Long size;

    /**
     * 当前页
     */
    private Long current;
}
