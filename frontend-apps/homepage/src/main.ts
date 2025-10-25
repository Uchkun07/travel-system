import { createApp } from "vue";
import "./styles/style.css";
import App from "./App.vue";
import { createPinia } from "pinia";
import { router } from "./routers";
import "./routers/guard"; // 引入路由守卫
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import { migrateTokenToCookie } from "./utils/migrate-storage"; // 引入迁移工具
import "./utils/debug-auth"; // 引入调试工具 (开发环境)

// 执行存储迁移 (将旧的 localStorage token 迁移到 Cookie)
migrateTokenToCookie();

const app = createApp(App);
const store = createPinia();
app.use(store);
app.use(router);
app.use(ElementPlus);
app.mount("#app");
