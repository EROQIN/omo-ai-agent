<template>
  <div class="home-page">
    <div class="header">
      <h1>AI 聊天应用中心</h1>
      <p>选择您想要使用的AI应用</p>
    </div>
    
    <div class="app-cards">
      <div class="app-card" @click="goToLoveChat">
        <div class="app-icon love-icon">💕</div>
        <h3>AI 恋爱大师</h3>
        <p>专业的恋爱建议与情感分析，助您在感情路上一帆风顺</p>
        <button class="enter-btn">立即体验</button>
      </div>
      
      <div class="app-card" @click="goToManusChat">
        <div class="app-icon manus-icon">🤖</div>
        <h3>AI 超级智能体</h3>
        <p>强大的通用AI助手，解答各种问题，提供智能建议</p>
        <button class="enter-btn">立即体验</button>
      </div>
    </div>
    
    <div class="footer">
      <a href="https://github.com/EROQIN/omo-ai-agent" target="_blank" class="github-link">
        <svg class="github-icon" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
        </svg>
        <span>GitHub 仓库</span>
      </a>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'HomePage',
  setup() {
    const router = useRouter()
    
    const goToLoveChat = () => {
      router.push('/love-chat')
    }
    
    const goToManusChat = () => {
      router.push('/manus-chat')
    }
    
    return {
      goToLoveChat,
      goToManusChat
    }
  }
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 60px;
  color: white;
}

.header h1 {
  font-size: 3.5rem;
  margin-bottom: 30px;
  font-weight: 800;
  background: linear-gradient(135deg, #ffffff, #f0f8ff, #e6f3ff);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 30px rgba(255, 255, 255, 0.5),
               0 0 60px rgba(255, 255, 255, 0.3),
               0 0 90px rgba(255, 255, 255, 0.1);
  animation: glow 2s ease-in-out infinite alternate;
  letter-spacing: 2px;
  position: relative;
}

.header h1::after {
  content: '';
  position: absolute;
  bottom: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 200px;
  height: 3px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8), transparent);
  animation: shimmer 3s ease-in-out infinite;
}

.header p {
  font-size: 1.4rem;
  opacity: 0.95;
  font-weight: 300;
  letter-spacing: 1px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  animation: fadeInUp 1s ease-out 0.5s both;
}

@keyframes glow {
  from {
    text-shadow: 0 0 30px rgba(255, 255, 255, 0.5),
                 0 0 60px rgba(255, 255, 255, 0.3),
                 0 0 90px rgba(255, 255, 255, 0.1);
  }
  to {
    text-shadow: 0 0 40px rgba(255, 255, 255, 0.8),
                 0 0 80px rgba(255, 255, 255, 0.5),
                 0 0 120px rgba(255, 255, 255, 0.3);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 0.95;
    transform: translateY(0);
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-50%) translateX(-100px);
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: translateX(-50%) translateX(100px);
    opacity: 0;
  }
}

.app-cards {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
  justify-content: center;
}

.app-card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  width: 320px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.app-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.app-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.love-icon {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  color: white;
}

.manus-icon {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  color: white;
}

.app-card h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #333;
}

.app-card p {
  color: #666;
  margin-bottom: 30px;
  line-height: 1.6;
}

.enter-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 1rem;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.enter-btn:hover {
  transform: scale(1.05);
}

@media (max-width: 768px) {
  .app-cards {
    flex-direction: column;
    align-items: center;
  }
  
  .header h1 {
    font-size: 2rem;
  }
  
  .app-card {
    width: 280px;
    padding: 30px;
  }
}

.footer {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
}



.github-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 0.85rem;
  padding: 8px 16px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.github-link:hover {
  color: #ffffff;
  border-color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.github-icon {
  width: 16px;
  height: 16px;
}

@media (max-width: 768px) {
  .footer {
    position: relative;
    bottom: auto;
    margin-top: 40px;
  }
  
  .author-text {
    font-size: 0.8rem;
  }
  
  .github-link {
    font-size: 0.75rem;
    padding: 6px 12px;
  }
}</style>