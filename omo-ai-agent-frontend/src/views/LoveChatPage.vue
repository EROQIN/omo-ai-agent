<template>
  <div class="chat-page">
    <div class="chat-header">
      <button class="back-btn" @click="goBack">←</button>
      <h2>AI 恋爱大师</h2>
    </div>
    
    <div class="chat-container" ref="chatContainer">
      <div class="messages">
        <div 
          v-for="(message, index) in messages" 
          :key="index"
          :class="['message', message.type]"
        >
          <div class="message-content">
            <div class="message-text">{{ message.text }}</div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>
        <div v-if="isTyping" class="message ai typing">
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="input-area">
      <div class="input-container">
        <input 
          v-model="inputMessage" 
          @keyup.enter="sendMessage"
          placeholder="输入您的问题..."
          :disabled="isLoading"
        />
        <button 
          @click="sendMessage" 
          :disabled="!inputMessage.trim() || isLoading"
          class="send-btn"
        >
          {{ isLoading ? '发送中...' : '发送' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

export default {
  name: 'LoveChatPage',
  setup() {
    const router = useRouter()
    const messages = ref([])
    const inputMessage = ref('')
    const isLoading = ref(false)
    const isTyping = ref(false)
    const chatContainer = ref(null)
    let eventSource = null
    
    // 从localStorage获取或生成新的chatId
    let chatId = localStorage.getItem('loveChatId')
    if (!chatId) {
      chatId = api.generateChatId()
      localStorage.setItem('loveChatId', chatId)
    }

    const goBack = () => {
      router.push('/')
    }

    const scrollToBottom = () => {
      nextTick(() => {
        if (chatContainer.value) {
          chatContainer.value.scrollTop = chatContainer.value.scrollHeight
        }
      })
    }

    const formatTime = (timestamp) => {
      return new Date(timestamp).toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const sendMessage = async () => {
      if (!inputMessage.value.trim() || isLoading.value) return

      const userMessage = {
        text: inputMessage.value,
        type: 'user',
        timestamp: new Date().getTime()
      }
      
      messages.value.push(userMessage)
      const messageToSend = inputMessage.value
      inputMessage.value = ''
      isLoading.value = true
      isTyping.value = true
      
      scrollToBottom()

      try {
        // 关闭之前的连接
        if (eventSource) {
          eventSource.close()
        }

        // 创建新的SSE连接
        eventSource = api.chatWithLoveApp(messageToSend, chatId)
        
        let aiMessage = {
          text: '',
          type: 'ai',
          timestamp: new Date().getTime()
        }
        
        eventSource.onmessage = (event) => {
          if (event.data === '[DONE]') {
            isTyping.value = false
            isLoading.value = false
            eventSource.close()
            return
          }
          
          if (event.data.startsWith('[ERROR]')) {
            aiMessage.text = '抱歉，发生了错误：' + event.data.replace('[ERROR]', '')
            isTyping.value = false
            isLoading.value = false
            eventSource.close()
          } else {
            aiMessage.text += event.data
            // 如果这是第一条AI回复，添加新消息
            if (messages.value[messages.value.length - 1]?.type !== 'ai') {
              messages.value.push(aiMessage)
            } else {
              // 更新最后一条AI消息
              messages.value[messages.value.length - 1] = { ...aiMessage }
            }
          }
          
          scrollToBottom()
        }

        eventSource.onerror = (error) => {
          console.error('SSE error:', error)
          isTyping.value = false
          isLoading.value = false
          if (messages.value[messages.value.length - 1]?.type === 'ai' && 
              !messages.value[messages.value.length - 1].text) {
            messages.value[messages.value.length - 1].text = '抱歉，连接出现问题，请重试。'
          }
          eventSource.close()
        }

      } catch (error) {
        console.error('发送消息失败:', error)
        isTyping.value = false
        isLoading.value = false
        messages.value.push({
          text: '发送失败，请检查网络连接',
          type: 'ai',
          timestamp: new Date().getTime()
        })
      }
    }

    // 添加欢迎消息
    onMounted(() => {
      messages.value.push({
        text: '你好！我是AI恋爱大师，很高兴为你提供恋爱建议。无论是追求喜欢的人、处理感情问题，还是提升个人魅力，我都可以帮你分析和建议。请告诉我你的具体情况吧！',
        type: 'ai',
        timestamp: new Date().getTime()
      })
    })

    // 组件卸载时关闭连接
    onMounted(() => {
      return () => {
        if (eventSource) {
          eventSource.close()
        }
      }
    })

    return {
      messages,
      inputMessage,
      isLoading,
      isTyping,
      chatId,
      chatContainer,
      goBack,
      sendMessage,
      formatTime
    }
  }
}
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.chat-header {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  color: white;
  padding: 15px 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.back-btn {
  background: none;
  border: none;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
  margin-right: 15px;
  padding: 5px;
}

.chat-header h2 {
  margin: 0;
  flex: 1;
  font-size: 1.2rem;
}



.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.messages {
  max-width: 800px;
  margin: 0 auto;
}

.message {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-end;
}

.message.user {
  justify-content: flex-end;
}

.message.ai {
  justify-content: flex-start;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  word-wrap: break-word;
}

.message.user .message-content {
  background: #007bff;
  color: white;
}

.message.ai .message-content {
  background: white;
  color: #333;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message-text {
  margin-bottom: 5px;
  line-height: 1.4;
}

.message-time {
  font-size: 0.7rem;
  opacity: 0.7;
  text-align: right;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  background: #999;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.input-area {
  background: white;
  padding: 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.input-container {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  gap: 10px;
}

.input-container input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 25px;
  outline: none;
  font-size: 1rem;
}

.input-container input:focus {
  border-color: #ff6b6b;
}

.send-btn {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: opacity 0.3s ease;
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.send-btn:not(:disabled):hover {
  opacity: 0.9;
}

@media (max-width: 768px) {
  .chat-container {
    padding: 10px;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .input-area {
    padding: 15px;
  }
}
</style>