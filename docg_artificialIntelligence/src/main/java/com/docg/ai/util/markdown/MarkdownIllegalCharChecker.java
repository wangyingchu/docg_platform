package com.docg.ai.util.markdown;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownIllegalCharChecker {
    // 定义非法字符的正则表达式模式
    // 匹配控制字符 (除了换行、回车、制表符)
    private static final Pattern ILLEGAL_CHARS_PATTERN =
            Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]");

    public static List<IllegalCharInfo> checkIllegalChars(String content) {
        List<IllegalCharInfo> illegalChars = new ArrayList<>();
        Matcher matcher = ILLEGAL_CHARS_PATTERN.matcher(content);

        while (matcher.find()) {
            String charStr = matcher.group();
            int position = matcher.start();
            int code = charStr.charAt(0);

            illegalChars.add(new IllegalCharInfo(charStr, position, code));
        }

        return illegalChars;
    }

    // 用于存储非法字符信息的内部类
    public static class IllegalCharInfo {
        String charStr;
        int position;
        int code;

        public IllegalCharInfo(String charStr, int position, int code) {
            this.charStr = charStr;
            this.position = position;
            this.code = code;
        }
    }

    // 检查未转义的 Markdown 特殊字符
    public static List<IllegalCharInfo> checkUnescapedMarkdownChars(String content) {
        List<IllegalCharInfo> unescapedChars = new ArrayList<>();

        // 定义需要转义的 Markdown 特殊字符
        Pattern markdownCharsPattern = Pattern.compile("([\\*\\_\\`\\#\\+\\-\\!\\[\\]\\(\\)\\{\\}\\.\\>])");
        Matcher matcher = markdownCharsPattern.matcher(content);

        while (matcher.find()) {
            // 检查字符是否已被转义
            int pos = matcher.start();
            if (pos == 0 || content.charAt(pos - 1) != '\\') {
                String charStr = matcher.group();
                unescapedChars.add(new IllegalCharInfo(charStr, pos, (int) charStr.charAt(0)));
            }
        }

        return unescapedChars;
    }

    // 检查不匹配的 Markdown 语法元素
    public static List<String> checkUnmatchedMarkdownSyntax(String content) {
        List<String> issues = new ArrayList<>();

        // 检查不匹配的代码块
        Pattern codeBlockPattern = Pattern.compile("```");
        Matcher codeBlockMatcher = codeBlockPattern.matcher(content);
        int codeBlockCount = 0;
        while (codeBlockMatcher.find()) {
            codeBlockCount++;
        }
        if (codeBlockCount % 2 != 0) {
            issues.add("发现不匹配的代码块标记 (```)");
        }

        // 检查不匹配的行内代码
        Pattern inlineCodePattern = Pattern.compile("`");
        Matcher inlineCodeMatcher = inlineCodePattern.matcher(content);
        int inlineCodeCount = 0;
        while (inlineCodeMatcher.find()) {
            inlineCodeCount++;
        }
        if (inlineCodeCount % 2 != 0) {
            issues.add("发现不匹配的行内代码标记 (`)");
        }

        // 可以添加更多检查...

        return issues;
    }

    // 定义需要转义的 Markdown 特殊字符
    private static final Pattern MARKDOWN_CHARS_PATTERN =
            Pattern.compile("([\\\\`*_{}\\[\\]()#+\\-.!])");

    /**
     * 转义 Markdown 特殊字符
     * @param text 原始文本
     * @return 转义后的文本
     */
    public static String escapeMarkdown(String text) {
        if (text == null) {
            return "";
        }

        // 使用正则表达式匹配需要转义的字符，并在前面添加反斜杠
        return MARKDOWN_CHARS_PATTERN.matcher(text).replaceAll("\\\\$1");
    }
}
