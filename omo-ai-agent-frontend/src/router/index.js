import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/views/HomePage.vue'
import LoveChatPage from '@/views/LoveChatPage.vue'
import ManusChatPage from '@/views/ManusChatPage.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/love-chat',
    name: 'LoveChat',
    component: LoveChatPage
  },
  {
    path: '/manus-chat',
    name: 'ManusChat',
    component: ManusChatPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router