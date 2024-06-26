import {createApp} from 'vue';
import App from './App.vue';
import router from './router';
import './router/guard';
import '@/styles/index.scss';
import 'normalize.css/normalize.css';
import {createPinia} from 'pinia';
import {i18n} from './locales';
import VueClickAway from 'vue3-click-away';
import lazyPlugin from 'vue3-lazy';
import {registerSvgIcon} from '@/icons';
import {registerObSkeleton} from '@/components/LoadingSkeleton';
import 'prismjs/themes/prism.css';
import 'prismjs';
import 'element-plus/theme-chalk/index.css';
import {components, plugins} from './plugins/element-plus';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import infiniteScroll from 'vue3-infinite-scroll-better';
import v3ImgPreview from 'v3-img-preview';
import api from './api/api';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css';
import locale from 'element-plus/es/locale/lang/zh-cn'

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

export const app = createApp(App)
    .use(ElementPlus, {locale})
    .use(mavonEditor)
    .use(router)
    .use(pinia)
    .use(i18n)
    .use(VueClickAway)
    .use(infiniteScroll)
    .use(v3ImgPreview, {})
    .use(lazyPlugin, {
        loading: require('@/assets/default-cover.jpg'),
        error: require('@/assets/default-cover.jpg'),
    });
components.forEach((component) => {
    app.component(component.name, component);
});
plugins.forEach((plugin) => {
    app.use(plugin);
});

registerSvgIcon(app);
registerObSkeleton(app);
app.mount('#app');

console.log('%c 网站作者:长乐予安', 'color:#bada55');
console.log('%c qq:3319023171', 'color:#bada55');
api.report();
