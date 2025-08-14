import axios from 'axios'

const API_BASE_URL = 'http://localhost:8123/api'

class ApiService {
  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      }
    })
  }

  // AI恋爱大师 - SSE聊天
  chatWithLoveApp(message, chatId) {
    const url = `${API_BASE_URL}/ai/love_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${chatId}`
    return new EventSource(url)
  }

  // AI超级智能体 - SSE聊天
  chatWithManus(message) {
    const url = `${API_BASE_URL}/ai/manus/chat?message=${encodeURIComponent(message)}`
    return new EventSource(url)
  }

  // 生成唯一聊天室ID
  generateChatId() {
    return 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
  }
}

export default new ApiService()