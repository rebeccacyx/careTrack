import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import Antd from 'ant-design-vue' // import UI component library
import 'ant-design-vue/dist/reset.css' 

//import './style.css'

createApp(App).use(Antd).use(router).mount('#app')

