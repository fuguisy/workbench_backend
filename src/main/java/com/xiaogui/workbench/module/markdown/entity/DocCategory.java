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
@TableName("doc_category")
public class DocCategory extends BaseEntity {

    private Long userId;

    /** 父节点id，0 表示根节点 */
    private Long parentId;

    /** 分类名称 */
    private String name;

    /** 同级排序，越小越靠前 */
    private Integer sort;
}
