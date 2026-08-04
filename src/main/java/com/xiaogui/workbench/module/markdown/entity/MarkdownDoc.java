package com.xiaogui.workbench.module.markdown.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaogui.workbench.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("markdown_doc")
public class MarkdownDoc extends BaseEntity {

    private Long userId;
    private String title;
    private String category;
    private String content;
    private String tags;
    private String status;
    private String filePath;
    private Integer wordCount;
}
