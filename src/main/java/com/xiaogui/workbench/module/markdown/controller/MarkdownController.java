package com.xiaogui.workbench.module.markdown.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaogui.workbench.common.BizException;
import com.xiaogui.workbench.common.PageResult;
import com.xiaogui.workbench.common.Result;
import com.xiaogui.workbench.module.markdown.entity.MarkdownDoc;
import com.xiaogui.workbench.module.markdown.service.MarkdownService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/markdown")
public class MarkdownController {

    @Resource
    private MarkdownService service;

    @GetMapping("/list")
    public Result<List<MarkdownDoc>> list() {
        return Result.ok(service.list(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/page")
    public Result<PageResult<MarkdownDoc>> page(@RequestParam(defaultValue = "1") int current,
                                                 @RequestParam(defaultValue = "10") int size) {
        return Result.ok(service.getPage(StpUtil.getLoginIdAsLong(), current, size));
    }

    @PostMapping("")
    public Result<Void> add(@RequestBody MarkdownDoc entity) {
        entity.setUserId(StpUtil.getLoginIdAsLong());
        service.add(entity);
        return Result.ok();
    }

    @PutMapping("")
    public Result<Void> update(@RequestBody MarkdownDoc entity) {
        service.update(entity);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }

    // ========= 文件处理相关接口 =========

    /**
     * 上传文件：支持 .md/.txt/.docx/.xlsx/.pdf 等，
     * 先写入本地磁盘（filePath），再保存元数据到 DB
     */
    @PostMapping("/upload")
    public Result<MarkdownDoc> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "saveDir", required = false) String saveDir) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new BizException("请选择文件");
        }
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }

        // 自动识别分类
        if (category == null || category.isBlank()) {
            category = switch (ext) {
                case ".md" -> "Skill";
                case ".docx", ".doc" -> "Word";
                case ".xlsx", ".xls" -> "Excel";
                case ".pdf" -> "PDF";
                case ".txt" -> "TXT";
                default -> "其他";
            };
        }

        // 默认保存目录
        String dir = (saveDir != null && !saveDir.isBlank()) ? saveDir : "D:/workbench-docs";
        Path dirPath = Paths.get(dir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String safeName = System.currentTimeMillis() + "_" + (originalName == null ? "file" : originalName);
        Path target = dirPath.resolve(safeName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        // 读取 md / txt 正文入库
        String content = null;
        if (".md".equals(ext) || ".txt".equals(ext)) {
            try {
                content = Files.readString(target, StandardCharsets.UTF_8);
            } catch (Exception ignored) {
                // 读取失败不中断
            }
        }

        MarkdownDoc doc = new MarkdownDoc();
        doc.setUserId(StpUtil.getLoginIdAsLong());
        String title = originalName != null && originalName.contains(".")
                ? originalName.substring(0, originalName.lastIndexOf("."))
                : (originalName == null ? "未命名" : originalName);
        doc.setTitle(title);
        doc.setCategory(category);
        doc.setStatus("已归档");
        doc.setFilePath(target.toAbsolutePath().toString());
        doc.setContent(content);
        if (content != null) {
            doc.setWordCount(content.length());
        } else {
            doc.setWordCount(0);
        }
        service.add(doc);
        return Result.ok(doc);
    }

    /**
     * 下载文件：优先从 filePath 读磁盘文件，否则从 content 动态生成 .md
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse resp) throws Exception {
        MarkdownDoc doc = service.getById(id);
        if (doc == null) throw new BizException("文件不存在");

        resp.setContentType("application/octet-stream");

        if (doc.getFilePath() != null && !doc.getFilePath().isBlank()) {
            Path p = Paths.get(doc.getFilePath());
            if (Files.exists(p) && Files.isRegularFile(p)) {
                String fn = p.getFileName().toString();
                resp.setHeader("Content-Disposition",
                        "attachment;filename*=UTF-8''" + URLEncoder.encode(fn, StandardCharsets.UTF_8));
                try (OutputStream out = resp.getOutputStream()) {
                    Files.copy(p, out);
                }
                return;
            }
        }

        // 回退：content 写成 .md 下载
        String filename = (doc.getTitle() == null ? "doc" : doc.getTitle()) + ".md";
        resp.setHeader("Content-Disposition",
                "attachment;filename*=UTF-8''" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
        String body = doc.getContent() == null ? "" : doc.getContent();
        try (OutputStream out = resp.getOutputStream()) {
            out.write(body.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 把 DB 中的 content 持久化到本地文件（filePath 为空时写入 D:/workbench-docs）
     */
    @PostMapping("/save-to-file/{id}")
    public Result<Map<String, String>> saveToFile(@PathVariable Long id,
                                                   @RequestParam(value = "dir", required = false) String dir) throws Exception {
        MarkdownDoc doc = service.getById(id);
        if (doc == null) throw new BizException("记录不存在");

        String targetDir = (dir != null && !dir.isBlank()) ? dir : "D:/workbench-docs";
        Path dirPath = Paths.get(targetDir);
        if (!Files.exists(dirPath)) Files.createDirectories(dirPath);

        String safeTitle = (doc.getTitle() == null ? "doc_" + id : doc.getTitle())
                .replaceAll("[\\\\/:*?\"<>|]", "_");

        // 根据分类选扩展名
        String ext = switch (doc.getCategory() == null ? "" : doc.getCategory()) {
            case "Word" -> ".docx";
            case "Excel" -> ".xlsx";
            case "PDF" -> ".pdf";
            case "TXT" -> ".txt";
            default -> ".md";
        };

        Path target = dirPath.resolve(safeTitle + ext);
        try (OutputStream out = Files.newOutputStream(target)) {
            out.write((doc.getContent() == null ? "" : doc.getContent()).getBytes(StandardCharsets.UTF_8));
        }

        doc.setFilePath(target.toAbsolutePath().toString());
        service.updateById(doc);
        return Result.ok(Map.of("path", target.toAbsolutePath().toString()));
    }

    /**
     * 用本机 Typora 打开文件（.md），其他类型走默认打开
     */
    @PostMapping("/open-typora/{id}")
    public Result<String> openTypora(@PathVariable Long id) throws Exception {
        MarkdownDoc doc = service.getById(id);
        if (doc == null) throw new BizException("记录不存在");
        return Result.ok(service.ensureFileThenOpen(doc, "D:\\tools\\Typora\\Typora.exe"));
    }

    /**
     * 用本机默认程序打开（Windows: rundll32 shell32,ShellExec_RunDLL）
     */
    @PostMapping("/open-default/{id}")
    public Result<String> openDefault(@PathVariable Long id) throws Exception {
        MarkdownDoc doc = service.getById(id);
        if (doc == null) throw new BizException("记录不存在");
        return Result.ok(service.ensureFileThenOpen(doc, null));
    }
}
