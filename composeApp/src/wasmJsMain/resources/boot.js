// Boot guard and on-device diagnostics for the wasm app. Loaded before composeApp.js (CSP forbids
// inline scripts). Two jobs:
//  1. If the app fails before or after it starts, say why on screen instead of leaving the spinner
//     or a blank page: wasm compile/link errors, uncaught Kotlin/JS exceptions, a lost or missing
//     WebGL2 context, or nothing painted a few seconds after start.
//  2. With ?diag in the URL, keep a small panel with the facts needed to debug a device we do not
//     have: user agent, viewport, canvas size, WebGL renderer and every captured error.
(function () {
  var SLOW_LOAD_MS = 45000, PAINT_CHECK_MS = 4000;
  var diag = /[?&]diag(=|&|$)/.test(location.search);
  var errors = [];
  var started = false;

  function loader() { return document.getElementById('app-loading'); }
  function el(tag, cls, text) { var e = document.createElement(tag); if (cls) e.className = cls; if (text != null) e.textContent = text; return e; }

function addCopyButton(p) {
    var b = document.createElement('button'); b.type = 'button'; b.textContent = 'คัดลอก';
    b.style.cssText = 'position:sticky;top:0;float:right;margin:-4px -4px 4px 8px;padding:6px 12px;border:0;border-radius:999px;background:#1B83D8;color:#fff;font:600 12px -apple-system,sans-serif';
    b.onclick = function () {
      var pre = p.querySelector('pre'); var text = pre ? pre.textContent : '';
      var done = function () { b.textContent = 'คัดลอกแล้ว'; };
      if (navigator.clipboard && navigator.clipboard.writeText) navigator.clipboard.writeText(text).then(done, function () { window.prompt('คัดลอกข้อความนี้', text); });
      else window.prompt('คัดลอกข้อความนี้', text);
    };
    p.insertBefore(b, p.firstChild);
    if (navigator.share) {
      var sh = document.createElement('button'); sh.type = 'button'; sh.textContent = 'แชร์';
      sh.style.cssText = b.style.cssText.replace('#1B83D8', '#374151');
      sh.onclick = function () { var pre = p.querySelector('pre'); navigator.share({ title: 'app diagnostics', text: pre ? pre.textContent : '' }).catch(function () {}); };
      p.insertBefore(sh, p.firstChild);
    }
  }

function setPanelText(p, text) {
    if (typeof p === 'string') { text = p; p = panel(); }
    var pre = p.querySelector('pre'); if (!pre) { pre = document.createElement('pre'); pre.style.cssText = 'margin:0;font:inherit;white-space:pre-wrap;word-break:break-word'; p.appendChild(pre); }
    pre.textContent = text;
  }

  function panel() {
    var p = document.getElementById('app-diag');
    if (p) return p;
    p = el('div'); p.id = 'app-diag'; p.setAttribute('role', 'alert');
    p.style.cssText = 'position:fixed;left:8px;right:8px;bottom:8px;max-height:60vh;overflow:auto;z-index:1000;background:#111827;color:#f9fafb;font:12px/1.4 -apple-system,BlinkMacSystemFont,"Noto Sans Thai",sans-serif;padding:12px;border-radius:12px;white-space:pre-wrap;word-break:break-word;box-shadow:0 8px 24px rgba(0,0,0,.35)';
    document.body.appendChild(p);
    addCopyButton(p);
    return p;
  }

  function fail(title, why, detail) {
    var l = loader();
    if (l && !l.classList.contains('done')) {
      l.classList.add('failed');
      var hint = l.querySelector('.hint'); if (hint) hint.textContent = title;
      l.appendChild(el('div', 'why', why));
      if (detail) l.appendChild(el('div', 'detail', String(detail).slice(0, 300)));
      var retry = el('button', null, 'ลองใหม่'); retry.type = 'button'; retry.onclick = function () { location.reload(); };
      l.appendChild(retry);
      return;
    }
    var p = panel();
    setPanelText(p, title + '\n' + why + (detail ? '\n\n' + String(detail).slice(0, 600) : '') + '\n\n' + facts());
  }

  function canvasInfo() {
    var found = null;
    (function visit(root) {
      var all = root.querySelectorAll('*');
      for (var i = 0; i < all.length && !found; i++) {
        if (all[i].tagName === 'CANVAS') found = all[i];
        else if (all[i].shadowRoot) visit(all[i].shadowRoot);
      }
    })(document);
    if (!found) return { canvas: null };
    var r = found.getBoundingClientRect();
    return { canvas: found, size: found.width + 'x' + found.height + ' css ' + Math.round(r.width) + 'x' + Math.round(r.height) };
  }

  function glInfo() {
    try {
      var c = document.createElement('canvas');
      var gl = c.getContext('webgl2');
      if (!gl) return 'webgl2: unavailable';
      var dbg = gl.getExtension('WEBGL_debug_renderer_info');
      var r = dbg ? gl.getParameter(dbg.UNMASKED_RENDERER_WEBGL) : gl.getParameter(gl.RENDERER);
      var maxTex = gl.getParameter(gl.MAX_TEXTURE_SIZE);
      var lose = gl.getExtension('WEBGL_lose_context'); if (lose) lose.loseContext();
      return 'webgl2: ' + r + ' maxTex ' + maxTex;
    } catch (e) { return 'webgl2: error ' + e; }
  }

  function facts() {
    var ci = canvasInfo();
    return (errors.length ? 'ERROR ' + errors[errors.length - 1] + '\n\n' : '') + [
      'ua: ' + navigator.userAgent,
      'viewport: ' + innerWidth + 'x' + innerHeight + ' dpr ' + devicePixelRatio + ' body ' + document.body.clientWidth + 'x' + document.body.clientHeight,
      'canvas: ' + (ci.canvas ? ci.size : 'none'),
      glInfo(),
      'wasm: ' + (typeof WebAssembly === 'object' ? 'yes' : 'NO') + ' jstag: ' + (typeof WebAssembly === 'object' && 'JSTag' in WebAssembly ? 'yes' : 'no'),
      'started: ' + started + ' errors: ' + errors.length
    ].join('\n');
  }

  function trimStack(text) {
    var lines = String(text).split('\n').filter(function (l) { return !/createJsError|Throwable\.<init>|Exception\.<init>|Error\.<init>|RuntimeException\.<init>|IllegalArgumentException\.<init>|IllegalStateException\.<init>|THROW_|throwUninitialized/.test(l); });
    return lines.map(function (l) { return l.replace(/@<[^>]*>:wasm-function\[\d+\]/, '').replace(/@https?:\/\/[^\s]+/, '').replace(/androidx\.compose\./g, '').trim(); }).filter(Boolean).slice(0, 16).join('\n').slice(0, 1800);
  }

  function record(e) {
    var msg = e && (e.stack || e.message || String(e)) || 'unknown';
    errors.push(trimStack(msg));
    if (diag) { setPanelText(facts()); }
  }

  function bootError(error) {
    record(error);
    var name = error && error.name;
    var why = (name === 'CompileError' || name === 'LinkError' || name === 'TypeError')
      ? 'เบราว์เซอร์นี้ยังไม่รองรับแอป ลองอัปเดต iOS หรือ Safari เป็นรุ่นล่าสุด หรือเปิดด้วย Chrome'
      : 'ตรวจการเชื่อมต่ออินเทอร์เน็ตแล้วลองใหม่';
    fail(started ? 'แอปหยุดทำงาน' : 'เปิดแอปไม่สำเร็จ', WEBKIT_272 ? webkitAdvice() : (started ? 'เกิดข้อผิดพลาดหลังเปิดแอป' : why), error && (error.message || error));
  }

  // WebKit 27.2 (Safari 27.2 on macOS and iOS; other iPhone browsers show "OS 27_2") miscompiles
  // WebAssembly after some use: Compose's text measurement returns garbage and the page stays
  // blank. The page cannot work around it, so it says so and names the only known remedy.
  var WEBKIT_272 = /Version\/27\.2(\.|\s|$)/.test(navigator.userAgent) || /(iPhone|iPad).* OS 27_2([_\s]|$)/.test(navigator.userAgent);
  function webkitAdvice() {
    var onPhone = /iPhone|iPad/.test(navigator.userAgent);
    return 'Safari รุ่น 27.2 มีบั๊กใน WebAssembly ทำให้แอปนี้แสดงหน้าขาว ' + (onPhone
      ? 'แก้ชั่วคราว: ตั้งค่า › แอป › Safari › ขั้นสูง › Feature Flags เปิด "Experimental WebAssembly Debugger (Disables JIT)" แล้วปิด Safari เปิดใหม่ (แอปจะช้าลง) หรือใช้คอมพิวเตอร์แทน'
      : 'แก้ชั่วคราว: Safari › Settings › Advanced เปิด "Show features for web developers" แล้วไปที่ Develop › WebKit Feature Flags เปิด "Experimental WebAssembly Debugger (Disables JIT)" แล้วเปิด Safari ใหม่ หรือใช้ Chrome/Firefox');
  }
  if (WEBKIT_272) {
    var bar = el('div', null, webkitAdvice()); bar.setAttribute('role', 'alert');
    bar.style.cssText = 'position:fixed;left:0;right:0;top:0;z-index:1001;padding:10px 44px 10px 14px;background:#fde68a;color:#111827;font:13px/1.45 -apple-system,BlinkMacSystemFont,"Noto Sans Thai",sans-serif';
    var x = el('button', null, '✕'); x.type = 'button'; x.setAttribute('aria-label', 'ปิด');
    x.style.cssText = 'position:absolute;right:8px;top:6px;border:0;background:transparent;font-size:18px;padding:4px 8px';
    x.onclick = function () { bar.remove(); };
    bar.appendChild(x); document.documentElement.appendChild(bar);
  }

  if (typeof WebAssembly !== 'object') {
    fail('เปิดแอปไม่สำเร็จ', 'เบราว์เซอร์ปิด WebAssembly อยู่ ถ้าเปิดโหมด Lockdown ให้ยกเว้นเว็บนี้ (กด aA › การตั้งค่าเว็บไซต์) หรือปิดโหมด Lockdown แล้วลองใหม่', null);
    return;
  }
  window.addEventListener('error', function (e) { bootError(e.error || e.message); });
  window.addEventListener('unhandledrejection', function (e) { bootError(e.reason); });
  window.addEventListener('webglcontextlost', function () { fail('แอปหยุดวาดหน้าจอ', 'WebGL context หาย (หน่วยความจำหรือ GPU ของอุปกรณ์ไม่พอ) กดลองใหม่', null); }, true);
  window.addEventListener('webglcontextcreationerror', function (e) { fail('เปิดแอปไม่สำเร็จ', 'สร้าง WebGL2 ไม่ได้', e && e.statusMessage); }, true);

  // The app marks #app-loading done when it starts, then removes it; poll for either.
  var startPoll = setInterval(function () {
    var l = loader();
    if (l && !l.classList.contains('done')) return;
    clearInterval(startPoll); started = true;
    setTimeout(function () {
        var ci = canvasInfo();
        if (!ci.canvas) fail('แอปเปิดแล้วแต่ไม่มีหน้าจอ', 'ไม่พบ canvas ที่ใช้วาด', null);
        else if (ci.canvas.width === 0 || ci.canvas.height === 0) fail('แอปเปิดแล้วแต่ไม่มีหน้าจอ', 'canvas มีขนาด 0', ci.size);
        if (diag) setPanelText(facts());
    }, PAINT_CHECK_MS);
  }, 250);
  setTimeout(function () { if (!started) fail('ยังโหลดไม่เสร็จ', 'ใช้เวลานานกว่าปกติ ตรวจการเชื่อมต่อแล้วลองใหม่', null); }, SLOW_LOAD_MS);
  if (diag) setTimeout(function () { setPanelText(facts()); }, 1500);
})();
