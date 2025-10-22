import { createApp } from 'vue'
import './styles/style.css'
import App from './App.vue'
import { createPinia } from 'pinia';
import { router } from './routers';

const app = createApp(App);
const store = createPinia();
app.use(store);
app.use(router);
app.mount('#app');
