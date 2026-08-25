"use client";

import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";

const sampleMarkdown = `
# Markdown 示例

这是 **粗体**，这是 *斜体*，这是 \\\"行内代码\\"。

- 列表项 1
- 列表项 2

| 名称 | 版本 |
|------|------|
| Next.js | 15 |
| React | 19.2.3 |
| Tailwind CSS | v4 |
`;

export function MarkdownPreview() {
  return (
    <div className="prose dark:prose-invert max-w-none">
      <ReactMarkdown remarkPlugins={[remarkGfm]}>{sampleMarkdown}</ReactMarkdown>
    </div>
  );
}
