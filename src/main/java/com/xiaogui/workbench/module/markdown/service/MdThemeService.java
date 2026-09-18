package com.xiaogui.workbench.module.markdown.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 图文排版流水线：Markdown → 公众号（内联样式）HTML
 * 参考 mdnice 思路，所有样式内联，粘贴进公众号编辑器可直接保留格式。
 * 内置 5 个主题：简约经典 / 科技蓝 / 暖阳橙 / 森系绿 / 雅致紫
 */
@Service
public class MdThemeService {

    // -------- 预编译正则 --------
    private static final Pattern FENCE     = Pattern.compile("^```(\\w*)\\s*$");
    private static final Pattern HEADING   = Pattern.compile("^(#{1,6})\\s+(.*)$");
    private static final Pattern UL        = Pattern.compile("^\\s*[-*+]\\s+(.*)$");
    private static final Pattern OL        = Pattern.compile("^\\s*\\d+\\.\\s+(.*)$");
    private static final Pattern HR        = Pattern.compile("^\\s*(-{3,}|\\*{3,}|_{3,})\\s*$");
    private static final Pattern QUOTE     = Pattern.compile("^>\\s?(.*)$");

    private static final Pattern INLINE_CODE = Pattern.compile("`([^`]+)`");
    private static final Pattern IMG       = Pattern.compile("!\\[([^\\]]*)]\\(([^)\\s]+)(?:\\s+&quot;([^&]*)&quot;)?\\)");
    private static final Pattern LINK      = Pattern.compile("\\[([^\\]]+)]\\(([^)\\s]+)(?:\\s+&quot;([^&]*)&quot;)?\\)");
    private static final Pattern BOLD_STAR = Pattern.compile("\\*\\*([^*]+)\\*\\*");
    private static final Pattern BOLD_UND  = Pattern.compile("__([^_]+)__");
    private static final Pattern DEL       = Pattern.compile("~~([^~]+)~~");
    private static final Pattern IT_STAR   = Pattern.compile("(?<![\\w*])\\*([^*]+)\\*(?![\\w*])");
    private static final Pattern IT_UND    = Pattern.compile("(?<![\\w_])_([^_]+)_(?![\\w_])");

    // -------- 主题定义 --------
    private static final List<Theme> THEMES = buildThemes();

    /** 前端列表用 */
    public List<Map<String, Object>> listThemes() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Theme t : THEMES) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("key", t.key);
            m.put("name", t.name);
            m.put("color", t.accent);
            m.put("desc", t.desc);
            list.add(m);
        }
        return list;
    }

    /** 渲染入口 */
    public String render(String markdown, String themeKey) {
        Theme t = resolve(themeKey);
        String body = renderBlocks(markdown == null ? "" : markdown, t);
        return "<section style=\"" + t.s("base") + "\">" + body + "</section>";
    }

    // =====================================================================
    // 主题数据
    // =====================================================================
    private static Theme resolve(String key) {
        if (key != null) {
            for (Theme t : THEMES) if (t.key.equals(key)) return t;
        }
        return THEMES.get(0);
    }

    private static List<Theme> buildThemes() {
        List<Theme> list = new ArrayList<>();

        list.add(buildTheme("classic", "简约经典", "#576b95",
                "清爽通用，适合技术长文与踩坑笔记",
                "#576b95", "#f6f8fa", "#f0f2f5", "#c7254e", "#282c34", "#abb2bf",
                "h2", "font-size:20px;font-weight:bold;margin:28px 0 14px;padding:0 0 0 10px;border-left:4px solid #576b95;color:#262626;line-height:1.4;"));

        list.add(buildTheme("tech", "科技蓝", "#0A84FF",
                "醒目标题条，适合 AI / 技术演示类文章",
                "#0A84FF", "#eef5ff", "#e8f1ff", "#0A84FF", "#1e2a3a", "#e6eef8",
                "h2", "font-size:17px;font-weight:bold;margin:28px 0 14px;padding:8px 14px;background:#0A84FF;color:#ffffff;border-radius:6px;letter-spacing:0.5px;"));

        list.add(buildTheme("orange", "暖阳橙", "#ff7a00",
                "温暖亲和，适合副业 / 成长 / 经验分享",
                "#ff7a00", "#fff5eb", "#fff1e3", "#d46b08", "#3a2a1a", "#f5e6d3",
                "h2", "font-size:19px;font-weight:bold;margin:28px auto 16px;text-align:center;color:#ff7a00;padding:0 12px;display:table;"));

        list.add(buildTheme("green", "森系绿", "#42b983",
                "自然清爽，适合教程 / 读书笔记",
                "#42b983", "#eef9f3", "#e6f6ee", "#2c8a5f", "#1f2d27", "#dff5e9",
                "blockquote", "margin:16px 0;padding:12px 18px;border-left:3px solid #42b983;background:#eef9f3;color:#595959;font-size:15px;line-height:1.8;border-radius:0 8px 8px 0;"));

        list.add(buildTheme("purple", "雅致紫", "#7b5ea7",
                "优雅克制，适合深度思考 / 复盘文章",
                "#7b5ea7", "#f5f0fa", "#f0e9f7", "#7b5ea7", "#2a2338", "#efe7fb",
                "h2", "font-size:20px;font-weight:bold;margin:28px 0 14px;color:#7b5ea7;border-bottom:1px dashed #7b5ea7;padding-bottom:8px;"));

        return list;
    }

    /**
     * 构造一个主题：基础样式 + 一个可覆盖的 key/value
     */
    private static Theme buildTheme(String key, String name, String accent, String desc,
                                    String tAccent, String quoteBg, String codeBg, String codeColor,
                                    String preBg, String preColor,
                                    String overrideKey, String overrideValue) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("base", "font-size:16px;color:#3f3f3f;line-height:1.8;letter-spacing:0.05em;" +
                "font-family:Optima-Regular,Optima,PingFangSC-light,\\\"PingFang SC\\\",\\\"Microsoft YaHei\\\",sans-serif;" +
                "padding:0 4px;");
        m.put("h1", "font-size:24px;font-weight:bold;text-align:center;margin:28px 0 16px;color:#262626;line-height:1.4;");
        m.put("h2", "font-size:20px;font-weight:bold;margin:28px 0 14px;padding:0 0 0 10px;border-left:4px solid " + tAccent + ";color:#262626;line-height:1.4;");
        m.put("h3", "font-size:18px;font-weight:bold;margin:24px 0 12px;color:#262626;");
        m.put("h4", "font-size:16px;font-weight:bold;margin:20px 0 10px;color:#262626;");
        m.put("h5", "font-size:15px;font-weight:bold;margin:18px 0 8px;color:#333333;");
        m.put("h6", "font-size:14px;font-weight:bold;margin:16px 0 6px;color:#666666;");
        m.put("p",  "margin:14px 0;text-align:justify;");
        m.put("strong", "color:" + tAccent + ";font-weight:bold;");
        m.put("em", "font-style:italic;color:#555;");
        m.put("del", "color:#999999;text-decoration:line-through;");
        m.put("blockquote", "margin:16px 0;padding:12px 18px;border-left:3px solid " + tAccent + ";background:" + quoteBg + ";color:#595959;font-size:15px;line-height:1.8;");
        m.put("pre", "background:" + preBg + ";color:" + preColor + ";padding:16px;border-radius:8px;overflow-x:auto;" +
                "font-size:13px;line-height:1.7;font-family:Consolas,Monaco,Menlo,monospace;margin:16px 0;");
        m.put("code", "background:" + codeBg + ";color:" + codeColor + ";padding:2px 6px;border-radius:4px;" +
                "font-size:14px;font-family:Consolas,Monaco,Menlo,monospace;");
        m.put("ul", "margin:14px 0;padding-left:26px;");
        m.put("ol", "margin:14px 0;padding-left:26px;");
        m.put("li", "margin:8px 0;line-height:1.75;");
        m.put("a",  "color:" + tAccent + ";text-decoration:none;border-bottom:1px solid " + tAccent + ";");
        m.put("img", "max-width:100%;border-radius:6px;margin:12px auto;display:block;");
        m.put("hr", "border:none;border-top:1px solid #e8e8e8;margin:28px 0;");
        m.put("table", "width:100%;border-collapse:collapse;margin:16px 0;font-size:14px;");
        m.put("th", "background:" + quoteBg + ";border:1px solid #e5e5e5;padding:8px 12px;font-weight:bold;text-align:left;");
        m.put("td", "border:1px solid #e5e5e5;padding:8px 12px;");

        if (overrideKey != null) m.put(overrideKey, overrideValue);

        Theme t = new Theme();
        t.key = key; t.name = name; t.accent = accent; t.desc = desc;
        t.styles = m;
        return t;
    }

    private static class Theme {
        String key;
        String name;
        String desc;
        String accent;
        Map<String, String> styles;
        String s(String k) {
            String v = styles.get(k);
            return v == null ? "" : v;
        }
    }

    // =====================================================================
    // 块级解析
    // =====================================================================
    private String renderBlocks(String md, Theme t) {
        String[] lines = md.replace("\r\n", "\n").replace('\u00a0', ' ').split("\n", -1);
        StringBuilder out = new StringBuilder();

        StringBuilder codeBuf = new StringBuilder();
        List<String> quoteBuf = new ArrayList<>();
        List<String> paraBuf = new ArrayList<>();

        boolean[] state = { false, false }; // [0]=inUl, [1]=inOl
        boolean inCode = false, inQuote = false;
        String codeLang = "";

        int i = 0;
        while (i < lines.length) {
            String line = lines[i];

            // ---- 代码围栏 ----
            Matcher fence = FENCE.matcher(line);
            if (fence.matches()) {
                if (!inCode) {
                    flushPara(out, paraBuf, t); closeLists(out, state); flushQuote(out, quoteBuf, t);
                    inCode = true; codeLang = fence.group(1); codeBuf.setLength(0);
                } else {
                    out.append("<pre style=\"").append(t.s("pre")).append("\"><code>")
                       .append(escape(stripLastNl(codeBuf))).append("</code></pre>\n");
                    inCode = false;
                }
                i++; continue;
            }
            if (inCode) { codeBuf.append(line).append('\n'); i++; continue; }

            // ---- 空行 ----
            if (line.trim().isEmpty()) {
                flushPara(out, paraBuf, t); flushQuote(out, quoteBuf, t); closeLists(out, state);
                i++; continue;
            }

            // ---- 表格：当前行含 |，下一行是分隔行 ----
            if (isTableRow(line) && i + 1 < lines.length && isTableSep(lines[i + 1])) {
                flushPara(out, paraBuf, t); flushQuote(out, quoteBuf, t); closeLists(out, state);
                List<String> heads = splitRow(line);
                List<String> aligns = parseAlign(lines[i + 1], heads.size());
                i += 2;
                StringBuilder tb = new StringBuilder();
                tb.append("<table style=\"").append(t.s("table")).append("\"><thead><tr>");
                for (int c = 0; c < heads.size(); c++) {
                    tb.append("<th style=\"").append(t.s("th")).append(aligns.get(c)).append("\">")
                      .append(renderInline(heads.get(c), t)).append("</th>");
                }
                tb.append("</tr></thead><tbody>");
                while (i < lines.length && isTableRow(lines[i])) {
                    List<String> cells = splitRow(lines[i]);
                    tb.append("<tr>");
                    for (int c = 0; c < cells.size(); c++) {
                        tb.append("<td style=\"").append(t.s("td")).append(aligns.get(c)).append("\">")
                          .append(renderInline(cells.get(c), t)).append("</td>");
                    }
                    tb.append("</tr>");
                    i++;
                }
                tb.append("</tbody></table>\n");
                out.append(tb);
                continue;
            }

            // ---- 标题 ----
            Matcher hd = HEADING.matcher(line);
            if (hd.matches()) {
                flushPara(out, paraBuf, t); flushQuote(out, quoteBuf, t); closeLists(out, state);
                int lv = hd.group(1).length();
                String tag = "h" + lv;
                out.append('<').append(tag).append(" style=\"").append(t.s(tag)).append("\">")
                   .append(renderInline(hd.group(2).trim(), t))
                   .append("</").append(tag).append(">\n");
                i++; continue;
            }

            // ---- 分割线 ----
            if (HR.matcher(line).matches()) {
                flushPara(out, paraBuf, t); flushQuote(out, quoteBuf, t); closeLists(out, state);
                out.append("<hr style=\"").append(t.s("hr")).append("\"/>\n");
                i++; continue;
            }

            // ---- 引用 ----
            Matcher qm = QUOTE.matcher(line);
            if (qm.matches()) {
                flushPara(out, paraBuf, t); closeLists(out, state);
                inQuote = true;
                quoteBuf.add(qm.group(1).trim());
                i++; continue;
            }
            if (inQuote) flushQuote(out, quoteBuf, t);

            // ---- 无序列表 ----
            Matcher ulm = UL.matcher(line);
            if (ulm.matches()) {
                flushPara(out, paraBuf, t); flushQuote(out, quoteBuf, t);
                if (state[1]) { out.append("</ol>\n"); state[1] = false; }
                if (!state[0]) { out.append("<ul style=\"").append(t.s("ul")).append("\">\n"); state[0] = true; }
                out.append("<li style=\"").append(t.s("li")).append("\">")
                   .append(renderInline(ulm.group(1).trim(), t)).append("</li>\n");
                i++; continue;
            }
            // ---- 有序列表 ----
            Matcher olm = OL.matcher(line);
            if (olm.matches()) {
                flushPara(out, paraBuf, t); flushQuote(out, quoteBuf, t);
                if (state[0]) { out.append("</ul>\n"); state[0] = false; }
                if (!state[1]) { out.append("<ol style=\"").append(t.s("ol")).append("\">\n"); state[1] = true; }
                out.append("<li style=\"").append(t.s("li")).append("\">")
                   .append(renderInline(olm.group(1).trim(), t)).append("</li>\n");
                i++; continue;
            }
            if (state[0]) { out.append("</ul>\n"); state[0] = false; }
            if (state[1]) { out.append("</ol>\n"); state[1] = false; }

            // ---- 段落（连续普通行） ----
            paraBuf.add(line.trim());
            i++;
        }

        // 尾部 flush
        if (inCode) {
            out.append("<pre style=\"").append(t.s("pre")).append("\"><code>")
               .append(escape(stripLastNl(codeBuf))).append("</code></pre>\n");
        }
        flushPara(out, paraBuf, t);
        flushQuote(out, quoteBuf, t);
        if (state[0]) { out.append("</ul>\n"); }
        if (state[1]) { out.append("</ol>\n"); }
        return out.toString();
    }

    private void flushPara(StringBuilder out, List<String> buf, Theme t) {
        if (buf.isEmpty()) return;
        StringBuilder join = new StringBuilder();
        for (int i = 0; i < buf.size(); i++) {
            if (i > 0) join.append("<br/>");
            join.append(renderInline(buf.get(i), t));
        }
        out.append("<p style=\"").append(t.s("p")).append("\">").append(join).append("</p>\n");
        buf.clear();
    }

    private void flushQuote(StringBuilder out, List<String> buf, Theme t) {
        if (buf.isEmpty()) return;
        StringBuilder join = new StringBuilder();
        for (int i = 0; i < buf.size(); i++) {
            if (i > 0) join.append("<br/>");
            join.append(renderInline(buf.get(i), t));
        }
        out.append("<blockquote style=\"").append(t.s("blockquote")).append("\">")
           .append(join).append("</blockquote>\n");
        buf.clear();
    }

    private void closeLists(StringBuilder out, boolean[] state) {
        if (state[0]) { out.append("</ul>\n"); state[0] = false; }
        if (state[1]) { out.append("</ol>\n"); state[1] = false; }
    }

    // =====================================================================
    // 行内解析
    // =====================================================================
    private String renderInline(String text, Theme t) {
        String s = escape(text);

        // 1. 保护行内代码
        List<String> codes = new ArrayList<>();
        Matcher cm = INLINE_CODE.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (cm.find()) {
            codes.add("<code style=\"" + t.s("code") + "\">" + cm.group(1) + "</code>");
            cm.appendReplacement(sb, "\u0001" + (codes.size() - 1) + "\u0001");
        }
        cm.appendTail(sb);
        s = sb.toString();

        // 2. 图片 / 链接
        s = IMG.matcher(s).replaceAll("<img src=\"$2\" alt=\"$1\" style=\"" + t.s("img") + "\"/>");
        s = LINK.matcher(s).replaceAll("<a href=\"$2\" style=\"" + t.s("a") + "\">$1</a>");

        // 3. 粗体
        s = BOLD_STAR.matcher(s).replaceAll("<strong style=\"" + t.s("strong") + "\">$1</strong>");
        s = BOLD_UND.matcher(s).replaceAll("<strong style=\"" + t.s("strong") + "\">$1</strong>");

        // 4. 删除线
        s = DEL.matcher(s).replaceAll("<del style=\"" + t.s("del") + "\">$1</del>");

        // 5. 斜体（带边界保护，避免匹配 snake_case）
        s = IT_STAR.matcher(s).replaceAll("<em style=\"" + t.s("em") + "\">$1</em>");
        s = IT_UND.matcher(s).replaceAll("<em style=\"" + t.s("em") + "\">$1</em>");

        // 6. 还原代码占位
        for (int i = 0; i < codes.size(); i++) {
            s = s.replace("\u0001" + i + "\u0001", codes.get(i));
        }
        return s;
    }

    // =====================================================================
    // 表格 / 工具
    // =====================================================================
    private boolean isTableRow(String line) {
        if (line == null) return false;
        String s = line.trim();
        return s.startsWith("|") && s.endsWith("|") && s.length() > 1;
    }

    private boolean isTableSep(String line) {
        String s = line.trim();
        if (!s.contains("-") || !s.contains("|")) return false;
        if (s.startsWith("|")) s = s.substring(1);
        if (s.endsWith("|")) s = s.substring(0, s.length() - 1);
        String[] cells = s.split("\\|", -1);
        if (cells.length == 0) return false;
        for (String cell : cells) {
            String c = cell.trim();
            if (c.isEmpty()) return false;
            if (!c.matches("^:?-{1,}:?$")) return false;
        }
        return true;
    }

    private List<String> splitRow(String line) {
        String s = line.trim();
        if (s.startsWith("|")) s = s.substring(1);
        if (s.endsWith("|")) s = s.substring(0, s.length() - 1);
        List<String> cells = new ArrayList<>();
        for (String c : s.split("\\|", -1)) cells.add(c.trim());
        return cells;
    }

    private List<String> parseAlign(String sepLine, int cols) {
        List<String> cells = splitRow(sepLine);
        List<String> out = new ArrayList<>();
        for (int c = 0; c < cols; c++) {
            if (c >= cells.size()) { out.add(""); continue; }
            String cell = cells.get(c).trim();
            if (cell.startsWith(":") && cell.endsWith(":")) out.add("text-align:center;");
            else if (cell.endsWith(":")) out.add("text-align:right;");
            else if (cell.startsWith(":")) out.add("text-align:left;");
            else out.add("");
        }
        return out;
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private String stripLastNl(StringBuilder buf) {
        String s = buf.toString();
        if (s.endsWith("\n")) s = s.substring(0, s.length() - 1);
        return s;
    }
}
