package com.xiaogui.workbench.module.markdown.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.module.markdown.entity.MarkdownDoc;
import com.xiaogui.workbench.module.markdown.mapper.MarkdownMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class MarkdownService extends ServiceImpl<MarkdownMapper, MarkdownDoc> {

    public List<MarkdownDoc> list(Long userId) {
        return this.list(new LambdaQueryWrapper<MarkdownDoc>()
                .eq(MarkdownDoc::getUserId, userId)
                .orderByDesc(MarkdownDoc::getUpdateTime));
    }

    public PageResult<MarkdownDoc> getPage(Long userId, int current, int size) {
        Page<MarkdownDoc> page = new Page<>(current, size);
        Page<MarkdownDoc> result = this.page(page, new LambdaQueryWrapper<MarkdownDoc>()
                .eq(MarkdownDoc::getUserId, userId)
                .orderByDesc(MarkdownDoc::getUpdateTime));
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    public void add(MarkdownDoc entity) {
        if (entity.getContent() != null) {
            entity.setWordCount(entity.getContent().length());
        }
        this.save(entity);
    }

    public void update(MarkdownDoc entity) {
        if (entity.getId() == null) {
            throw new BizException("更新失败：id不能为空");
        }
        if (entity.getContent() != null) {
            entity.setWordCount(entity.getContent().length());
        }
        this.updateById(entity);
    }

    public void delete(Long id) {
        this.removeById(id);
    }

    /**
     * 确保文档有对应的本地文件，然后用指定程序/默认程序打开。
     * @param doc 文档记录
     * @param appPath 应用路径，如 D:\tools\Typora\Typora.exe；null 表示用系统默认程序打开
     * @return 执行信息
     */
    public String ensureFileThenOpen(MarkdownDoc doc, String appPath) throws IOException, InterruptedException {
        Path filePath;
        if (doc.getFilePath() == null || doc.getFilePath().isBlank()
                || !Files.exists(Paths.get(doc.getFilePath()))) {
            // 没有文件，先写一个到 D:/workbench-docs
            String dir = "D:/workbench-docs";
            Path dirPath = Paths.get(dir);
            if (!Files.exists(dirPath)) Files.createDirectories(dirPath);
            String safeTitle = (doc.getTitle() == null ? "doc_" + doc.getId() : doc.getTitle())
                    .replaceAll("[\\\\/:*?\"<>|]", "_");
            String ext = resolveExt(doc.getCategory());
            Path target = dirPath.resolve(safeTitle + ext);
            try (OutputStream out = Files.newOutputStream(target)) {
                out.write((doc.getContent() == null ? "" : doc.getContent()).getBytes(StandardCharsets.UTF_8));
            }
            doc.setFilePath(target.toAbsolutePath().toString());
            this.updateById(doc);
            filePath = target;
        } else {
            filePath = Paths.get(doc.getFilePath());
        }

        // 启动外部程序
        ProcessBuilder pb;
        if (appPath != null && !appPath.isBlank()) {
            pb = new ProcessBuilder(appPath, filePath.toString());
        } else {
            // Windows 下调用默认程序打开
            pb = new ProcessBuilder("rundll32", "shell32,ShellExec_RunDLL", filePath.toString());
        }
        pb.directory(filePath.getParent() != null ? filePath.getParent().toFile() : null);
        pb.start();
        return "已启动打开：" + filePath;
    }

    private String resolveExt(String category) {
        if (category == null) return ".md";
        return switch (category) {
            case "Word" -> ".docx";
            case "Excel" -> ".xlsx";
            case "PDF" -> ".pdf";
            case "TXT" -> ".txt";
            default -> ".md";
        };
    }
}
