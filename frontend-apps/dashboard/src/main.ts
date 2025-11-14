import { createApp } from "vue";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import "./styles/style.css";
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import "@fortawesome/fontawesome-free/css/all.min.css";
import App from "./App.vue";
import router from "./router";

const app = createApp(App);
const pinia = createPinia();

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.use(pinia);
app.use(ElementPlus);
app.use(router);
app.mount("#app");
