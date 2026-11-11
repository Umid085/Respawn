const http = require("http");
const fs = require("fs");
const path = require("path");

function getApkPath() {
  const candidates = [
    "/app/applet/Respawn.apk",
    "/app/applet/app/Respawn.apk",
    path.resolve(process.cwd(), "Respawn.apk"),
    path.resolve(__dirname, "Respawn.apk"),
    path.resolve(__dirname, "app/build/outputs/apk/debug/app-debug.apk"),
    "/app/applet/app/build/outputs/apk/debug/app-debug.apk"
  ];
  for (const candidate of candidates) {
    if (fs.existsSync(candidate)) {
      return candidate;
    }
  }
  return null;
}

const server = http.createServer((req, res) => {
  const url = req.url.split("?")[0];
  if (url === "/Respawn.apk" || url === "/app/Respawn.apk" || url.endsWith(".apk")) {
    const apkFile = getApkPath();
    if (apkFile) {
      const stat = fs.statSync(apkFile);
      res.writeHead(200, {
        "Content-Type": "application/vnd.android.package-archive",
        "Content-Length": stat.size,
        "Content-Disposition": 'attachment; filename="Respawn.apk"',
        "Cache-Control": "no-cache"
      });
      return fs.createReadStream(apkFile).pipe(res);
    } else {
      res.writeHead(404, { "Content-Type": "text/plain" });
      return res.end("APK file not found on disk");
    }
  }

  // Web landing page
  res.writeHead(200, { "Content-Type": "text/html; charset=utf-8" });
  res.end(`<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Download Respawn APK</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
      background: #080D14;
      color: #F0F6FC;
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      padding: 24px;
    }
    .card {
      background: #111827;
      border: 1px solid #1F2937;
      border-radius: 20px;
      padding: 36px 28px;
      max-width: 460px;
      width: 100%;
      text-align: center;
      box-shadow: 0 20px 45px rgba(0,0,0,0.6);
    }
    .badge-icon {
      font-size: 52px;
      line-height: 1;
      margin-bottom: 16px;
      display: inline-block;
    }
    h1 {
      font-size: 26px;
      font-weight: 800;
      letter-spacing: -0.5px;
      margin-bottom: 8px;
      color: #FFFFFF;
    }
    .subtitle {
      color: #9CA3AF;
      font-size: 14px;
      line-height: 1.5;
      margin-bottom: 24px;
    }
    .download-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      width: 100%;
      padding: 16px 24px;
      background: linear-gradient(135deg, #00E5FF 0%, #0091EA 100%);
      color: #000000;
      font-weight: 800;
      font-size: 16px;
      border-radius: 14px;
      text-decoration: none;
      box-shadow: 0 6px 20px rgba(0, 229, 255, 0.4);
      transition: transform 0.15s ease, box-shadow 0.15s ease;
    }
    .download-btn:hover {
      box-shadow: 0 8px 26px rgba(0, 229, 255, 0.55);
      transform: translateY(-1px);
    }
    .download-btn:active {
      transform: translateY(1px);
    }
    .info-list {
      margin-top: 28px;
      background: #0B111A;
      border: 1px solid #1E2633;
      border-radius: 12px;
      padding: 16px;
      text-align: left;
      font-size: 13px;
      color: #D1D5DB;
    }
    .info-row {
      display: flex;
      justify-content: space-between;
      padding: 6px 0;
      border-bottom: 1px solid #161F2E;
    }
    .info-row:last-child {
      border-bottom: none;
    }
    .info-label {
      color: #9CA3AF;
    }
    .info-val {
      font-weight: 600;
      color: #00E5FF;
    }
    .install-hint {
      margin-top: 20px;
      font-size: 12px;
      color: #6B7280;
      line-height: 1.5;
    }
  </style>
</head>
<body>
  <div class="card">
    <div class="badge-icon">⚡🎮⚡</div>
    <h1>Respawn Mobile</h1>
    <p class="subtitle">Gaming Addiction Recovery & Dopamine Reset Tracker</p>
    
    <a href="/Respawn.apk" class="download-btn" download="Respawn.apk">
      <span>📥</span>
      <span>Download APK (23 MB)</span>
    </a>

    <div class="info-list">
      <div class="info-row">
        <span class="info-label">File:</span>
        <span class="info-val">Respawn.apk</span>
      </div>
      <div class="info-row">
        <span class="info-label">Version:</span>
        <span class="info-val">v1.1.0 (Latest)</span>
      </div>
      <div class="info-row">
        <span class="info-label">Package:</span>
        <span class="info-val">com.aistudio.respawn.zxkq</span>
      </div>
      <div class="info-row">
        <span class="info-label">Android OS:</span>
        <span class="info-val">Android 8.0+ (Oreo to Android 15)</span>
      </div>
    </div>

    <p class="install-hint">
      Tip: After downloading, open the APK file on your Android phone and tap "Install". If prompted, enable "Install from this source".
    </p>
  </div>
</body>
</html>`);
});

const PORT = 3000;
server.listen(PORT, "0.0.0.0", () => {
  console.log(`Web Download server running on http://0.0.0.0:${PORT}`);
});
