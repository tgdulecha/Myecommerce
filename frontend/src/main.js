import { createApp } from 'vue'
import App from './App.vue'
import './css/OrderManagement.css'
import router from './router/routers.js'


import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
createApp(App)  
.use(router)   
.mount('#app')
