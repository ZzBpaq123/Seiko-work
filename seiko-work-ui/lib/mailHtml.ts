/**
 * 邮件正文处理工具。
 *
 * 邮件正文来自外部发件人，属于不可信内容：任何以 HTML 形式渲染前都必须先清洗，
 * 否则会引入 XSS（脚本、事件属性、javascript: 协议等）。
 *
 * 对外提供两个入口：
 * - sanitizeEmailHtml：清洗服务端返回的原始 HTML 正文，并让链接在新标签页打开
 * - textToHtml：把纯文本正文转成安全 HTML（转义 + 解码实体 + 自动识别链接 + 换行）
 */

/** 直接丢弃的标签：脚本、样式、表单、内嵌框架等潜在风险或会干扰渲染的元素 */
const DROP_TAGS = [
  "script",
  "style",
  "iframe",
  "frame",
  "frameset",
  "object",
  "embed",
  "applet",
  "form",
  "input",
  "button",
  "textarea",
  "select",
  "option",
  "optgroup",
  "meta",
  "link",
  "base",
  "title",
  "noscript",
  "template",
];

/** 常见命名实体，用于纯文本正文的实体解码（&nbsp; 等） */
const NAMED_ENTITIES: Record<string, string> = {
  // 空格类统一还原为普通空格，避免纯文本里出现不换行空格导致的排版异常
  nbsp: " ",
  ensp: " ",
  emsp: " ",
  thinsp: " ",
  amp: "&",
  lt: "<",
  gt: ">",
  quot: '"',
  apos: "'",
  copy: "\u00A9",
  reg: "\u00AE",
  trade: "\u2122",
  hellip: "\u2026",
  mdash: "\u2014",
  ndash: "\u2013",
  middot: "\u00B7",
  bull: "\u2022",
  laquo: "\u00AB",
  raquo: "\u00BB",
  ldquo: "\u201C",
  rdquo: "\u201D",
  lsquo: "\u2018",
  rsquo: "\u2019",
  yen: "\u00A5",
  euro: "\u20AC",
  pound: "\u00A3",
  deg: "\u00B0",
  times: "\u00D7",
  divide: "\u00F7",
  minus: "\u2212",
};

/** 解码 HTML 实体：命名实体 + 十进制/十六进制数字实体 */
export function decodeEntities(input: string): string {
  return input.replace(/&(#x?[0-9a-fA-F]+|[a-zA-Z][a-zA-Z0-9]*);/g, (match, code: string) => {
    if (code[0] === "#") {
      const hex = code[1] === "x" || code[1] === "X";
      const num = parseInt(hex ? code.slice(2) : code.slice(1), hex ? 16 : 10);
      if (Number.isFinite(num) && num > 0 && num <= 0x10ffff) {
        return String.fromCodePoint(num);
      }
      return match;
    }
    return NAMED_ENTITIES[code.toLowerCase()] ?? match;
  });
}

/** 转义 HTML 特殊字符（& 必须最先处理，避免二次转义） */
function escapeHtml(input: string): string {
  return input
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

/**
 * 判断 URL 是否安全。
 * 先剔除控制字符，防止 "java\nscript:" 之类的绕过写法。
 */
function isSafeUrl(raw: string, allowDataImage: boolean): boolean {
  const value = raw.replace(/[\u0000-\u0020\u007f]+/g, "").toLowerCase();
  if (!value) return false;
  if (value.startsWith("data:")) {
    return allowDataImage && value.startsWith("data:image/");
  }
  if (value.startsWith("javascript:") || value.startsWith("vbscript:") || value.startsWith("file:")) {
    return false;
  }
  return true;
}

/**
 * 清洗邮件 HTML 正文：移除危险标签/属性，并统一把链接改为新标签页打开。
 * 服务端渲染（无 DOM）时返回空串，正文只在浏览器端渲染。
 */
export function sanitizeEmailHtml(html: string): string {
  if (!html) return "";
  if (typeof window === "undefined" || typeof DOMParser === "undefined") return "";

  const doc = new DOMParser().parseFromString(html, "text/html");

  // 1. 移除危险/干扰标签
  doc.body.querySelectorAll(DROP_TAGS.join(",")).forEach((el) => el.remove());

  // 2. 移除 HTML 注释
  const walker = doc.createTreeWalker(doc.body, NodeFilter.SHOW_COMMENT);
  const comments: Node[] = [];
  while (walker.nextNode()) comments.push(walker.currentNode);
  comments.forEach((node) => node.parentNode?.removeChild(node));

  // 3. 逐元素清理属性
  doc.body.querySelectorAll("*").forEach((el) => {
    for (const attr of Array.from(el.attributes)) {
      const name = attr.name.toLowerCase();
      const value = attr.value;

      // 事件处理器、内联框架文档、表单提交地址一律移除
      if (name.startsWith("on") || name === "srcdoc" || name === "formaction") {
        el.removeAttribute(attr.name);
        continue;
      }
      if (name === "href" || name === "xlink:href") {
        if (!isSafeUrl(value, false)) el.removeAttribute(attr.name);
        continue;
      }
      if (name === "src") {
        if (!isSafeUrl(value, true)) el.removeAttribute(attr.name);
        continue;
      }
      // 内联样式里可能藏着 expression()/javascript: url()
      if (name === "style" && /expression\s*\(|url\s*\(\s*['"]?\s*javascript:/i.test(value)) {
        el.removeAttribute(attr.name);
      }
    }

    // 4. 链接统一在新标签页打开
    if (el.tagName === "A" && el.getAttribute("href")) {
      el.setAttribute("target", "_blank");
      el.setAttribute("rel", "noopener noreferrer nofollow");
    }
  });

  // 5. 移除无法加载的内嵌图片（cid: 指向附件，浏览器取不到，避免显示裂图）
  doc.body.querySelectorAll("img").forEach((img) => {
    const src = (img.getAttribute("src") || "").trim().toLowerCase();
    if (!src || src.startsWith("cid:")) img.remove();
  });

  return doc.body.innerHTML;
}

/** 识别正文中的裸链接（http/https/www.），并把结尾标点排除在链接之外 */
const URL_PATTERN = /(https?:\/\/[^\s<>"']+|www\.[^\s<>"']+)/gi;
const TRAILING_PUNCT = /[.,;:!?，。；：！？、）)】\]》”"']+$/;

/**
 * 纯文本正文 → 安全 HTML。
 * 顺序很重要：先解码实体再转义，保证 &nbsp; 等显示为正常字符，同时不会引入标签。
 */
export function textToHtml(text: string): string {
  if (!text) return "";
  const escaped = escapeHtml(decodeEntities(text));
  return escaped
    .replace(URL_PATTERN, (match) => {
      const trailing = match.match(TRAILING_PUNCT)?.[0] ?? "";
      const url = trailing ? match.slice(0, -trailing.length) : match;
      if (!url) return match;
      const href = /^www\./i.test(url) ? `https://${url}` : url;
      return `<a href="${href}" target="_blank" rel="noopener noreferrer nofollow">${url}</a>${trailing}`;
    })
    .replace(/\n/g, "<br>");
}

/** 依据邮件正文选择合适的渲染方式：优先 HTML，其次纯文本 */
export function buildMailBodyHtml(contentHtml: string | null, contentText: string | null): string {
  const html = contentHtml?.trim();
  if (html) {
    const sanitized = sanitizeEmailHtml(html);
    // 清洗后若只剩空白（例如整封都是 script/style），回退到纯文本
    if (sanitized.replace(/<[^>]*>/g, "").replace(/&nbsp;/g, " ").trim()) return sanitized;
  }
  return textToHtml(contentText ?? "");
}
