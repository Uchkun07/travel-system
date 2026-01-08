<template>
  <div v-if="visible" class="ai-chat-window">
    <!-- 头部 -->
    <div class="chat-header">
      <div class="header-left">
        <img src="@/assets/imgs/wxx.png" alt="AI助手" class="header-icon" />
        <span class="header-title">问小星-您的专属AI旅游助手</span>
      </div>
      <div class="header-actions">
        <el-button link size="small" @click="clearChat" title="清空会话">
          <el-icon><Delete /></el-icon>
        </el-button>
        <el-button link size="small" @click="closeWindow" title="关闭">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="chat-messages" ref="messagesContainer">
      <div v-if="messages.length === 0" class="empty-state">
        <img src="@/assets/imgs/wxx.png" alt="AI助手" class="empty-icon" />
        <p>你好！我是问小星AI旅游助手</p>
        <p class="empty-tip">有什么可以帮助您的吗？</p>
      </div>

      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message-item', msg.role]"
      >
        <div class="message-avatar">
          <img
            v-if="msg.role === 'assistant'"
            src="@/assets/imgs/wxx.png"
            alt="AI"
          />
          <el-icon v-else><User /></el-icon>
        </div>
        <div class="message-content">
          <!-- 用户消息：纯文本 -->
          <div v-if="msg.role === 'user'" class="message-bubble">
            {{ msg.content }}
          </div>
          <!-- AI消息：Markdown渲染 -->
          <div v-else class="message-bubble ai-message">
            <MdPreview
              previewTheme="vuepress"
              :codeFoldable="false"
              editorId="preview"
              :modelValue="
                Array.isArray(msg.content) ? msg.content.join('') : msg.content
              "
            />
            <div v-if="msg.loading" class="loading">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入框 -->
    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="2"
        placeholder="输入您的问题... (Enter发送，Shift+Enter换行)"
        :disabled="isLoading"
        @keydown="handleKeydown"
      />
      <el-button
        type="primary"
        :disabled="!inputMessage.trim() || isLoading"
        :loading="isLoading"
        @click="handleSend"
      >
        发送
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from "vue";
import { ElMessage } from "element-plus";
import { Close, Delete, User } from "@element-plus/icons-vue";
import { MdPreview } from "md-editor-v3";
import "md-editor-v3/lib/style.css";

// API基础URL - 使用环境变量配置
const baseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

interface Message {
  role: "user" | "assistant";
  content: string | string[]; // 用户消息是string，AI消息是string[]用于流式累积
  loading?: boolean;
}

interface Props {
  visible: boolean;
}

interface Emits {
  (e: "update:visible", value: boolean): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const messages = ref<Message[]>([]);
const inputMessage = ref("");
const isLoading = ref(false);
const messagesContainer = ref<HTMLElement>();
const sessionId = ref<string>("");

// 关闭窗口
const closeWindow = () => {
  emit("update:visible", false);
};

// 清空会话
const clearChat = async () => {
  if (messages.value.length === 0) return;

  try {
    if (sessionId.value) {
      await fetch(`${baseUrl}/api/ai/chat/session/${sessionId.value}`, {
        method: "DELETE",
      });
    }
    messages.value = [];
    sessionId.value = "";
    ElMessage.success("会话已清空");
  } catch (error) {
    console.error("清空会话失败:", error);
    ElMessage.error("清空会话失败");
  }
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

// 处理键盘事件
const handleKeydown = (event: KeyboardEvent) => {
  // Enter发送，Shift+Enter换行
  if (event.key === "Enter" && !event.shiftKey) {
    event.preventDefault();
    handleSend();
  }
};

// 发送消息
const handleSend = () => {
  const message = inputMessage.value.trim();
  if (!message || isLoading.value) {
    return;
  }

  // 添加用户消息
  messages.value.push({
    role: "user",
    content: message,
  });

  // 添加AI消息占位
  messages.value.push({
    role: "assistant",
    content: [],
    loading: true,
  });

  isLoading.value = true;
  inputMessage.value = "";

  // 构建查询参数
  const params = new URLSearchParams({
    message: message,
  });

  if (sessionId.value) {
    params.append("sessionId", sessionId.value);
  }

  // 使用EventSource进行SSE连接
  const eventSource = new EventSource(
    `${baseUrl}/api/ai/chat/stream?${params.toString()}`
  );

  let isNormalClose = false; // 标记是否正常关闭

  eventSource.onmessage = (event) => {
    const data = event.data;
    console.log("收到数据:", data);

    if (data === "end" || data === "[DONE]") {
      isNormalClose = true;
      closeEventSource();
      return;
    }

    try {
      // 尝试解析JSON
      const jsonData = JSON.parse(data);

      // 处理会话ID
      if (jsonData.sessionId) {
        sessionId.value = jsonData.sessionId;
      }

      // 处理完成消息
      if (jsonData.message === "完成") {
        isNormalClose = true;
        closeEventSource();
        return;
      }

      // 处理错误
      if (jsonData.error) {
        ElMessage.error(jsonData.error);
        closeEventSource();
        return;
      }

      // 处理内容
      if (jsonData.content) {
        const lastMessage = messages.value[messages.value.length - 1];
        if (
          lastMessage &&
          lastMessage.role === "assistant" &&
          Array.isArray(lastMessage.content)
        ) {
          lastMessage.content.push(jsonData.content);
          scrollToBottom();
        }
      }
    } catch (e) {
      // 如果不是JSON，直接作为内容添加
      const lastMessage = messages.value[messages.value.length - 1];
      if (
        lastMessage &&
        lastMessage.role === "assistant" &&
        Array.isArray(lastMessage.content)
      ) {
        lastMessage.content.push(data);
        scrollToBottom();
      }
    }
  };

  eventSource.onerror = (error) => {
    console.error("SSE连接错误:", error);
    // 只有在非正常关闭的情况下才显示错误提示
    // if (!isNormalClose) {
    //   ElMessage.error("连接失败，请重试");
    // }
    closeEventSource();
  };

  const closeEventSource = () => {
    eventSource.close();
    const lastMessage = messages.value[messages.value.length - 1];
    if (lastMessage && lastMessage.role === "assistant") {
      lastMessage.loading = false;

      // 如果没有收到任何内容，显示错误提示
      if (
        Array.isArray(lastMessage.content) &&
        lastMessage.content.length === 0
      ) {
        lastMessage.content = ["抱歉，没有收到响应，请重试。"];
      }
    }
    isLoading.value = false;
    scrollToBottom();
  };
};

// 默认欢迎消息
const defaultWelcomeMessage = `你好！很高兴为你服务！😊我是你的旅游助手，无论你想了解景点、规划行程，还是需要旅行小贴士，我都可以帮你。

你可以这样问我：

- **推荐目的地**：比如"推荐一些适合夏天去的海边城市"
- **行程规划**：比如"帮我规划一个3天的北京行程"
- **旅行建议**：比如"去西藏需要注意什么？"
- **其他问题**：比如"如何办理签证？"或"机票怎么订更便宜？"

告诉我你的需求，我们开始吧！✈️🌍`;

// 监听窗口显示状态
watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      // 如果是首次打开且没有消息，添加欢迎消息
      if (messages.value.length === 0) {
        messages.value.push({
          role: "assistant",
          content: defaultWelcomeMessage,
          loading: false,
        });
      }
      scrollToBottom();
    }
  }
);
</script>

<style scoped>
.ai-chat-window {
  position: fixed;
  right: 30px;
  bottom: 100px;
  width: 600px;
  height: 650px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 1000;
  overflow: hidden;
}

/* 头部 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: white;
  padding: 4px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.header-actions .el-button {
  color: white;
}

.header-actions .el-button:hover {
  color: rgba(255, 255, 255, 0.8);
}

/* 消息列表 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-state p {
  margin: 4px 0;
  font-size: 14px;
}

.empty-tip {
  color: #c0c4cc;
  font-size: 12px;
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e4e7ed;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-avatar .el-icon {
  font-size: 20px;
  color: #909399;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  white-space: pre-wrap;
  font-size: 14px;
  line-height: 1.6;
}

.message-item.user .message-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 12px;
}

.message-item.assistant .message-bubble {
  background: white;
  color: #303133;
  border-radius: 12px 12px 12px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 0;
}

/* AI消息Markdown样式 */
.message-bubble.ai-message {
  padding: 0;
}

.message-bubble.ai-message :deep(.md-editor-previewOnly) {
  border-radius: 12px;
  background: transparent;
}

.message-bubble.ai-message :deep(.md-editor-preview-wrapper) {
  padding: 12px 16px;
}

.message-bubble.ai-message :deep(p) {
  margin: 8px 0;
  line-height: 1.6;
}

.message-bubble.ai-message :deep(code) {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: "Courier New", monospace;
}

.message-bubble.ai-message :deep(pre) {
  background: #282c34;
  color: #abb2bf;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 8px 0;
}

.message-bubble.ai-message :deep(pre code) {
  background: transparent;
  padding: 0;
  color: inherit;
}

.message-bubble.ai-message :deep(ul),
.message-bubble.ai-message :deep(ol) {
  margin: 8px 0;
  padding-left: 24px;
}

.message-bubble.ai-message :deep(li) {
  margin: 4px 0;
}

.message-bubble.ai-message :deep(h1),
.message-bubble.ai-message :deep(h2),
.message-bubble.ai-message :deep(h3) {
  margin: 12px 0 8px;
  font-weight: 600;
}

.message-bubble.ai-message :deep(blockquote) {
  border-left: 3px solid #667eea;
  padding-left: 12px;
  margin: 8px 0;
  color: #606266;
}

/* 加载动画 */
.loading {
  display: flex;
  gap: 6px;
  padding: 12px 16px;
  justify-content: center;
}

.dot {
  width: 8px;
  height: 8px;
  background: #909399;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}

.dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%,
  80%,
  100% {
    transform: scale(0.6);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 输入框 */
.chat-input {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

.chat-input .el-textarea {
  flex: 1;
}

.chat-input .el-button {
  align-self: flex-end;
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #c0c4cc;
}
</style>
