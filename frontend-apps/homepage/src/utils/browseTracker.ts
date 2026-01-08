import { recordBrowse } from "@/apis/browse";

/**
 * 浏览追踪器类
 * 用于自动追踪用户在景点详情页的浏览时长
 */
export class BrowseTracker {
  private userId: number;
  private attractionId: number;
  private startTime: number;
  private isTracking: boolean = false;
  private reportInterval: number = 30000; // 默认30秒上报一次
  private timer: number | null = null;
  private deviceInfo: string;

  constructor(
    userId: number,
    attractionId: number,
    reportInterval: number = 30000
  ) {
    this.userId = userId;
    this.attractionId = attractionId;
    this.reportInterval = reportInterval;
    this.startTime = Date.now();
    this.deviceInfo = this.getDeviceInfo();
  }

  /**
   * 获取设备信息
   */
  private getDeviceInfo(): string {
    const ua = navigator.userAgent;
    let browser = "Unknown";
    let os = "Unknown";

    // 检测浏览器
    if (ua.includes("Firefox")) browser = "Firefox";
    else if (ua.includes("Chrome") && !ua.includes("Edg")) browser = "Chrome";
    else if (ua.includes("Safari") && !ua.includes("Chrome"))
      browser = "Safari";
    else if (ua.includes("Edg")) browser = "Edge";

    // 检测操作系统
    if (ua.includes("Win")) os = "Windows";
    else if (ua.includes("Mac")) os = "macOS";
    else if (ua.includes("Linux")) os = "Linux";
    else if (ua.includes("Android")) os = "Android";
    else if (ua.includes("iOS")) os = "iOS";

    return `${browser}/${os}`;
  }

  /**
   * 开始追踪
   */
  start() {
    if (this.isTracking) return;

    this.isTracking = true;
    this.startTime = Date.now();

    // 定时上报浏览时长
    this.timer = window.setInterval(() => {
      this.report();
    }, this.reportInterval);

    console.log(
      `[BrowseTracker] 开始追踪 - 用户:${this.userId}, 景点:${this.attractionId}`
    );
  }

  /**
   * 停止追踪并上报最后一次数据
   */
  async stop() {
    if (!this.isTracking) return;

    this.isTracking = false;

    // 清除定时器
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }

    // 上报最后一次数据
    await this.report();

    console.log(
      `[BrowseTracker] 停止追踪 - 用户:${this.userId}, 景点:${this.attractionId}`
    );
  }

  /**
   * 上报浏览时长
   */
  private async report() {
    const now = Date.now();
    const duration = Math.floor((now - this.startTime) / 1000); // 转换为秒

    if (duration < 1) return; // 浏览时长小于1秒不上报

    try {
      await recordBrowse({
        userId: this.userId,
        attractionId: this.attractionId,
        browseDuration: duration,
        deviceInfo: this.deviceInfo,
      });

      console.log(`[BrowseTracker] 上报成功 - 时长:${duration}秒`);

      // 重置开始时间为当前时间，下次上报增量时长
      this.startTime = now;
    } catch (error) {
      console.error("[BrowseTracker] 上报失败:", error);
    }
  }

  /**
   * 暂停追踪（用于页面不可见时）
   */
  pause() {
    if (!this.isTracking) return;

    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }

    console.log(`[BrowseTracker] 暂停追踪`);
  }

  /**
   * 恢复追踪（用于页面重新可见时）
   */
  resume() {
    if (!this.isTracking || this.timer) return;

    this.timer = window.setInterval(() => {
      this.report();
    }, this.reportInterval);

    console.log(`[BrowseTracker] 恢复追踪`);
  }
}

/**
 * 创建浏览追踪器实例的便捷函数
 */
export function createBrowseTracker(
  userId: number,
  attractionId: number,
  options?: {
    reportInterval?: number; // 上报间隔（毫秒）
    autoStart?: boolean; // 是否自动开始
    trackVisibility?: boolean; // 是否追踪页面可见性
  }
): BrowseTracker {
  const {
    reportInterval = 30000,
    autoStart = true,
    trackVisibility = true,
  } = options || {};

  const tracker = new BrowseTracker(userId, attractionId, reportInterval);

  if (autoStart) {
    tracker.start();
  }

  // 监听页面可见性变化
  if (trackVisibility) {
    const handleVisibilityChange = () => {
      if (document.hidden) {
        tracker.pause();
      } else {
        tracker.resume();
      }
    };

    document.addEventListener("visibilitychange", handleVisibilityChange);

    // 返回清理函数
    const originalStop = tracker.stop.bind(tracker);
    tracker.stop = async () => {
      document.removeEventListener("visibilitychange", handleVisibilityChange);
      await originalStop();
    };
  }

  // 监听页面卸载，确保最后一次数据上报
  const handleBeforeUnload = () => {
    // 使用 sendBeacon 确保数据发送
    const now = Date.now();
    const duration = Math.floor((now - tracker["startTime"]) / 1000);

    if (duration >= 1) {
      const data = {
        userId: tracker["userId"],
        attractionId: tracker["attractionId"],
        browseDuration: duration,
        deviceInfo: tracker["deviceInfo"],
      };

      // 使用 Blob 确保正确的 Content-Type
      const blob = new Blob([JSON.stringify(data)], {
        type: "application/json",
      });

      // sendBeacon 在页面卸载时也能保证发送
      const baseUrl =
        import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
      navigator.sendBeacon(`${baseUrl}/api/browse/record`, blob);
    }
  };

  window.addEventListener("beforeunload", handleBeforeUnload);

  return tracker;
}
