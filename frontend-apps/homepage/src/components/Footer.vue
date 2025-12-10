<template>
  <footer class="site-footer">
    <div class="footer-content">
      <!-- 关于环球探索 -->
      <div class="footer-section">
        <h3 class="footer-title">关于我们</h3>
        <p class="footer-description">
          我们致力于通过专业旅行顾问团队，为您提供独特、深度、难忘的旅行体验。让您用全新的视角认识这个世界。
        </p>
        <div class="social-links">
          <a href="#" class="social-icon" aria-label="微博">
            <ElIcon><Connection /></ElIcon>
          </a>
          <a href="#" class="social-icon" aria-label="微信">
            <ElIcon><ChatDotRound /></ElIcon>
          </a>
          <a href="#" class="social-icon" aria-label="抖音">
            <ElIcon><VideoPlay /></ElIcon>
          </a>
          <a href="#" class="social-icon" aria-label="Instagram">
            <ElIcon><Picture /></ElIcon>
          </a>
        </div>
      </div>

      <!-- 热门目的地 -->
      <div class="footer-section">
        <h3 class="footer-title">热门目的地</h3>
        <ul class="footer-links">
          <li><a href="#">东南亚旅游</a></li>
          <li><a href="#">欧洲经典</a></li>
          <li><a href="#">海岛度假</a></li>
          <li><a href="#">中东文化</a></li>
          <li><a href="#">非洲探险</a></li>
        </ul>
      </div>

      <!-- 帮助中心 -->
      <div class="footer-section">
        <h3 class="footer-title">帮助中心</h3>
        <ul class="footer-links">
          <li><a href="#">预订指南</a></li>
          <li><a href="#">支付方式</a></li>
          <li><a href="#">签证信息</a></li>
          <li><a href="#">旅行保险</a></li>
          <li><a href="#">常见问题</a></li>
        </ul>
      </div>

      <!-- 联系我们 -->
      <div class="footer-section">
        <h3 class="footer-title">联系我们</h3>
        <ul class="footer-contact">
          <li>
            <ElIcon><Phone /></ElIcon>
            <span>192-5240-2035</span>
          </li>
          <li>
            <ElIcon><Message /></ElIcon>
            <span>SuperMartian0413@outlook.com</span>
          </li>
          <li>
            <ElIcon><Location /></ElIcon>
            <span>乌鲁木齐市瑞达世贸大厦1805号</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="footer-bottom">
      <p>© 2023 WayStar | 新ICP备12345678号 | 旅行社业经营许可证：L-SH-9999</p>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { ElIcon } from "element-plus";
import {
  Connection,
  ChatDotRound,
  VideoPlay,
  Picture,
  Phone,
  Message,
  Location,
} from "@element-plus/icons-vue";
</script>

<style scoped>
.site-footer {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: #ffffff;
  padding: 60px 0 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px 40px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.footer-title {
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid rgba(52, 152, 219, 0.5);
}

.footer-description {
  font-size: 14px;
  line-height: 1.8;
  color: #bdc3c7;
  margin-bottom: 20px;
}

.social-links {
  display: flex;
  gap: 12px;
}

.social-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 18px;
  transition: all 0.3s ease;
  text-decoration: none;
}

.social-icon:hover {
  background: #3498db;
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.4);
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 12px;
}

.footer-links a {
  color: #bdc3c7;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  display: inline-block;
}

.footer-links a:hover {
  color: #3498db;
  padding-left: 5px;
}

.footer-contact {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-contact li {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  color: #bdc3c7;
  font-size: 14px;
}

.footer-contact .el-icon {
  color: #3498db;
  font-size: 16px;
}

.footer-bottom {
  background: rgba(0, 0, 0, 0.2);
  padding: 20px 30px;
  text-align: center;
}

.footer-bottom p {
  margin: 0;
  font-size: 13px;
  color: #95a5a6;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
    padding: 0 20px 30px;
  }

  .site-footer {
    padding: 40px 0 0;
  }

  .footer-title {
    font-size: 16px;
  }

  .footer-description,
  .footer-links a,
  .footer-contact li {
    font-size: 13px;
  }
}
</style>
