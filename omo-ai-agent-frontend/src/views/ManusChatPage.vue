<template>
  <div class="chat-page">
    <div class="chat-header">
      <button class="back-btn" @click="goBack">←</button>
      <h2>AI 超级智能体</h2>
    </div>
    
    <div class="chat-container" ref="chatContainer">
      <div class="messages">
        <div 
          v-for="(message, index) in messages" 
          :key="index"
          :class="['message', message.type]"
        >
          <div class="message-content">
            <div class="message-text" v-html="message.formattedText || message.text"></div>
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
          placeholder="请输入您的问题..."
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
  name: 'ManusChatPage',
  setup() {
    const router = useRouter()
    const messages = ref([])
    const inputMessage = ref('')
    const isLoading = ref(false)
    const isTyping = ref(false)
    const chatContainer = ref(null)
    let eventSource = null

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

    const formatMessage = (text) => {
      if (!text) return ''
      
      // 首先处理转义的换行符
      let processedText = text
        .replace(/\\n/g, '\n')  // 处理\n转义序列
        .replace(/\\r/g, '\r')  // 处理\r转义序列
        .replace(/^"|"$/g, '')  // 移除首尾的引号
      
      // 处理步骤式消息
      const stepPattern = /(Step \d+:|步骤 \d+：)/gi
      const resultPattern = /(返回的结果|思考完成|无需行动|Terminated|Reach max steps)/gi
      
      if (stepPattern.test(processedText) || resultPattern.test(processedText)) {
        let formatted = processedText
          .replace(/Step (\d+):\s*/gi, '<div class="step-header"><strong>步骤 $1:</strong></div>')
          .replace(/步骤 (\d+)：\s*/gi, '<div class="step-header"><strong>步骤 $1:</strong></div>')
          .replace(/返回的结果/g, '<div class="result-label">执行结果:</div>')
          .replace(/思考完成/g, '<div class="thinking-complete">🤔 思考完成</div>')
          .replace(/无需行动/g, '<div class="no-action">✅ 无需进一步行动</div>')
          .replace(/Terminated:\s*Reach max steps \((\d+)\)/gi, '<div class="max-steps">⏰ 达到最大步骤数 ($1)</div>')
          .replace(/\n/g, '<br>')
          .replace(/\r\n/g, '<br>')
        
        return formatted
      }
      
      // 普通消息 - 直接处理换行
      return processedText
        .replace(/\n/g, '<br>')
        .replace(/\r\n/g, '<br>')
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
        eventSource = api.chatWithManus(messageToSend)
        
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
          aiMessage.formattedText = formatMessage(aiMessage.text)
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
        text: '你好！我是AI超级智能体，可以帮你解答各种问题，提供智能建议。无论是学习、工作、生活还是技术问题，我都会尽力为你提供专业的解答。有什么我可以帮你的吗？',
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
      chatContainer,
      goBack,
      sendMessage,
      formatTime,
      formatMessage
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
  background: linear-gradient(135deg, #4facfe, #00f2fe);
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
  line-height: 1.5;
}

.step-header {
  margin: 8px 0 4px 0;
  color: #4facfe;
  font-weight: bold;
}

.result-label {
  margin: 8px 0 4px 0;
  color: #28a745;
  font-weight: bold;
  border-left: 3px solid #28a745;
  padding-left: 8px;
}

.thinking-complete {
  margin: 8px 0;
  color: #6f42c1;
  font-weight: bold;
}

.no-action {
  margin: 8px 0;
  color: #28a745;
  font-weight: bold;
}

.max-steps {
  margin: 8px 0;
  color: #dc3545;
  font-weight: bold;
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
  border-color: #4facfe;
}

.send-btn {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
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