<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import {
  startInterview,
  viewInterviews,
  deleteInterview,
  sendMessage,
  viewMessages,
  endInterview,
  type InterviewData,
  type SessionMessageData,
} from '@/api/interview'
import DashboardLayout from '@/components/DashboardLayout.vue'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'

const { success, error } = useToast()
const { confirm } = useConfirm()

const interviews = ref<InterviewData[]>([])
const loading = ref(true)
const starting = ref(false)

const active = ref<InterviewData | null>(null)
const messages = ref<SessionMessageData[]>([])
const messageLoading = ref(false)
const sending = ref(false)
const evaluating = ref(false)
const inputText = ref('')
const messagesBox = ref<HTMLElement | null>(null)

const mobileView = ref<'list' | 'chat'>('list')

onMounted(fetchInterviews)

async function fetchInterviews() {
  try {
    interviews.value = await viewInterviews()
  } catch {
    // 错误提示已由 client 统一处理
  } finally {
    loading.value = false
  }
}

function isEvaluated(item: InterviewData | null): boolean {
  return !!item && ((item.rating != null && item.rating > 0) || (item.suggestion != null && item.suggestion.length > 0))
}

async function startNew() {
  if (starting.value) return
  starting.value = true
  try {
    const data = await startInterview()
    active.value = data
    messages.value = data.first_question
      ? [{ id: -1, role: 'ai', messagetext: data.first_question, send_date: data.created_at }]
      : []
    mobileView.value = 'chat'
    await fetchInterviews()
    scrollToBottom()
  } catch (e: any) {
    error(e.message || '开始面试失败，请稍后重试')
  } finally {
    starting.value = false
  }
}

async function openInterview(item: InterviewData) {
  if (active.value?.id === item.id) return
  active.value = item
  messages.value = []
  messageLoading.value = true
  mobileView.value = 'chat'
  try {
    messages.value = await viewMessages(item.id)
  } catch (e: any) {
    error(e.message || '加载对话失败')
  } finally {
    messageLoading.value = false
    scrollToBottom()
  }
}

function backToList() {
  mobileView.value = 'list'
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || sending.value || !active.value || isEvaluated(active.value)) return
  if (text.length > 1000) {
    error('回答内容不能超过1000字')
    return
  }
  sending.value = true
  inputText.value = ''
  try {
    messages.value = await sendMessage(active.value.id, text)
    scrollToBottom()
  } catch (e: any) {
    error(e.message || '发送失败，请重试')
  } finally {
    sending.value = false
  }
}

async function handleFinish() {
  if (!active.value || evaluating.value || isEvaluated(active.value)) return
  const ok = await confirm('确定结束本次面试并生成评估吗？')
  if (!ok) return
  evaluating.value = true
  try {
    const data = await endInterview(active.value.id)
    active.value = data
    const idx = interviews.value.findIndex((i) => i.id === data.id)
    if (idx >= 0) interviews.value[idx] = data
    success('评估完成')
  } catch (e: any) {
    error(e.message || '评估失败，请重试')
  } finally {
    evaluating.value = false
  }
}

async function handleDelete(item: InterviewData) {
  const ok = await confirm('确定删除这场面试吗？相关聊天记录也会一并删除。')
  if (!ok) return
  try {
    await deleteInterview(item.id)
    interviews.value = interviews.value.filter((i) => i.id !== item.id)
    if (active.value?.id === item.id) {
      active.value = null
      messages.value = []
    }
    success('已删除')
  } catch (e: any) {
    error(e.message || '删除失败')
  }
}

async function scrollToBottom() {
  await nextTick()
  if (messagesBox.value) {
    messagesBox.value.scrollTop = messagesBox.value.scrollHeight
  }
}

watch(messages, scrollToBottom)
watch(sending, (v) => {
  if (v) scrollToBottom()
})

function pad(n: number): string {
  return String(n).padStart(2, '0')
}

function formatTime(iso: string): string {
  if (!iso) return ''
  const d = new Date(iso)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function formatShortTime(iso: string): string {
  if (!iso) return ''
  const d = new Date(iso)
  return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function ratingText(r: number | null): string {
  if (r == null) return '-'
  return Number.isInteger(r) ? String(r) : r.toFixed(1)
}
</script>

<template>
  <DashboardLayout>
    <div class="interview-page">
      <!-- 首屏空态 -->
      <div v-if="!loading && interviews.length === 0 && !active" class="welcome-card">
        <div class="welcome-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M12 2a3 3 0 0 0-3 3v6a3 3 0 0 0 6 0V5a3 3 0 0 0-3-3z"></path>
            <path d="M19 10v2a7 7 0 0 1-14 0v-2"></path>
            <line x1="12" y1="19" x2="12" y2="22"></line>
          </svg>
        </div>
        <h3 class="welcome-title">开始你的模拟面试</h3>
        <p class="welcome-desc">
          AI 面试官将基于你的简历进行多轮提问，结束后生成评分与改进建议。
          首次面试无需任何准备，直接开始即可。
        </p>
        <button class="btn btn-primary btn-lg" :disabled="starting" @click="startNew">
          <span v-if="starting" class="spinner-sm"></span>
          <span v-else class="plus" aria-hidden="true">+</span>
          开始新的模拟面试
        </button>
      </div>

      <!-- 加载态 -->
      <div v-else-if="loading" class="state-box">
        <span class="spinner"></span>
        加载中...
      </div>

      <!-- 工作区：历史列表 + 对话 -->
      <div v-else class="workspace">
        <aside class="history-pane" :class="{ 'mobile-hidden': mobileView === 'chat' }">
          <button class="btn btn-primary btn-block new-btn" :disabled="starting" @click="startNew">
            <span v-if="starting" class="spinner-sm"></span>
            <span v-else class="plus" aria-hidden="true">+</span>
            开始新的模拟面试
          </button>

          <div class="history-title">面试记录</div>
          <div class="history-list">
            <div v-if="interviews.length === 0" class="empty-hint">暂无面试记录</div>
            <button
              v-for="item in interviews"
              :key="item.id"
              class="history-item"
              :class="{ active: active?.id === item.id }"
              @click="openInterview(item)"
            >
              <div class="history-item-main">
                <div class="history-item-head">
                  <span class="history-item-title">模拟面试</span>
                  <span :class="['history-badge', isEvaluated(item) ? 'badge-done' : 'badge-ongoing']">
                    {{ isEvaluated(item) ? `${ratingText(item.rating)} 分` : '进行中' }}
                  </span>
                </div>
                <div class="history-item-foot">
                  <span class="history-item-time">{{ formatShortTime(item.created_at) }} 开始</span>
                  <button class="delete-btn" title="删除" aria-label="删除这场面试" @click.stop="handleDelete(item)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <path d="M3 6h18"></path>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"></path>
                      <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                  </button>
                </div>
              </div>
            </button>
          </div>
        </aside>

        <section v-if="active" class="chat-pane" :class="{ 'mobile-hidden': mobileView === 'list' }">
          <header class="chat-head">
            <button class="back-btn" aria-label="返回列表" @click="backToList">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <path d="M19 12H5"></path>
                <path d="M12 19l-7-7 7-7"></path>
              </svg>
            </button>
            <div class="chat-head-info">
              <span class="chat-title">模拟面试</span>
              <span class="chat-sub">{{ formatTime(active.created_at) }}</span>
            </div>
            <button class="btn btn-soft btn-sm" :disabled="evaluating || isEvaluated(active)" @click="handleFinish">
              <span v-if="evaluating" class="spinner-sm"></span>
              {{ isEvaluated(active) ? '已评估' : '结束面试并评估' }}
            </button>
          </header>

          <div class="chat-body" ref="messagesBox">
            <div v-if="messageLoading" class="state-box">
              <span class="spinner"></span>
              加载对话...
            </div>
            <template v-else>
              <div v-for="m in messages" :key="m.id" class="msg-row" :class="m.role">
                <div class="msg-avatar">{{ m.role === 'ai' ? '面' : '我' }}</div>
                <div class="msg-content">
                  <div class="msg-bubble">{{ m.messagetext }}</div>
                  <div class="msg-time">{{ formatTime(m.send_date) }}</div>
                </div>
              </div>
              <div v-if="sending" class="msg-row ai">
                <div class="msg-avatar">面</div>
                <div class="msg-content">
                  <div class="msg-bubble typing">
                    <span></span><span></span><span></span>
                  </div>
                </div>
              </div>
            </template>
          </div>

          <div v-if="isEvaluated(active)" class="eval-panel">
            <div class="eval-head">
              <span class="eval-label">面试评估</span>
              <span class="eval-rating">
                <strong>{{ ratingText(active.rating) }}</strong><em>/ 10</em>
              </span>
            </div>
            <p class="eval-suggestion">{{ active.suggestion }}</p>
          </div>

          <footer class="chat-input">
            <textarea
              v-model="inputText"
              class="textarea"
              rows="2"
              placeholder="输入你的回答，Enter 发送，Shift+Enter 换行"
              :disabled="sending || isEvaluated(active)"
              @keydown="onKeydown"
            ></textarea>
            <button class="btn btn-primary send-btn" :disabled="sending || !inputText.trim() || isEvaluated(active)" @click="handleSend">
              <span v-if="sending" class="spinner-sm"></span>
              发送
            </button>
          </footer>
        </section>

        <section v-else class="chat-pane chat-placeholder" :class="{ 'mobile-hidden': mobileView === 'list' }">
          <div class="placeholder-box">
            <div class="placeholder-icon">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
              </svg>
            </div>
            <p class="placeholder-title">选择一场面试查看对话</p>
            <p class="placeholder-desc">或点击左侧「开始新的模拟面试」</p>
          </div>
        </section>
      </div>
    </div>
  </DashboardLayout>
</template>

<style scoped>
.interview-page {
  max-width: 1120px;
  margin: 0 auto;
}

/* ---- 空态 ---- */
.welcome-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  padding: var(--space-12) var(--space-8);
  text-align: center;
  max-width: 560px;
  margin: 48px auto;
}

.welcome-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto var(--space-5);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--color-primary-ghost);
  color: var(--color-primary);
}

.welcome-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: var(--space-3);
  letter-spacing: -0.01em;
}

.welcome-desc {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  line-height: 1.7;
  max-width: 400px;
  margin: 0 auto var(--space-8);
}

.plus {
  font-size: 1.05rem;
  line-height: 1;
}

/* ---- 工作区 ---- */
.workspace {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: var(--space-5);
  align-items: stretch;
}

/* ---- 历史列表 ---- */
.history-pane {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  max-height: 680px;
}

.new-btn {
  flex-shrink: 0;
}

.history-title {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: var(--color-text-muted);
  text-transform: uppercase;
  margin: var(--space-5) 0 var(--space-3);
}

.history-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  min-height: 0;
}

.empty-hint {
  padding: var(--space-8) 0;
  text-align: center;
  font-size: 0.8125rem;
  color: var(--color-text-muted);
}

.history-item {
  display: block;
  width: 100%;
  text-align: left;
  border: 1px solid var(--color-border-light);
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  padding: var(--space-3) var(--space-4);
  cursor: pointer;
  transition: border-color var(--duration-fast) var(--ease-out),
              box-shadow var(--duration-fast) var(--ease-out);
}

.history-item:hover {
  border-color: var(--color-border-strong);
}

.history-item.active {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary-ring);
  background: var(--color-primary-ghost);
}

.history-item-main {
  min-width: 0;
}

.history-item-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-2);
  margin-bottom: 2px;
}

.history-item-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-text);
}

.history-badge {
  font-size: 0.68rem;
  font-weight: 600;
  padding: 1px 8px;
  border-radius: 999px;
  white-space: nowrap;
}

.badge-done {
  background: var(--color-success-bg);
  color: var(--color-success-text);
}

.badge-ongoing {
  background: var(--color-primary-ghost);
  color: var(--color-primary);
}

.history-item-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-2);
}

.history-item-time {
  font-size: 0.72rem;
  color: var(--color-text-muted);
}

.delete-btn {
  border: none;
  background: none;
  color: var(--color-text-muted);
  padding: 3px;
  border-radius: var(--radius-sm);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: color var(--duration-fast) var(--ease-out),
              background var(--duration-fast) var(--ease-out),
              opacity var(--duration-fast) var(--ease-out);
}

.history-item:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  color: var(--color-error-text);
  background: var(--color-error-bg);
}

/* ---- 对话面板 ---- */
.chat-pane {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  height: 680px;
  overflow: hidden;
}

.chat-head {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  border-bottom: 1px solid var(--color-border-light);
}

.back-btn {
  display: none;
  border: none;
  background: none;
  color: var(--color-text-secondary);
  padding: 4px;
  border-radius: var(--radius-sm);
  align-items: center;
  justify-content: center;
}

.chat-head-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  line-height: 1.35;
}

.chat-title {
  font-size: 0.9375rem;
  font-weight: 700;
  color: var(--color-text);
}

.chat-sub {
  font-size: 0.72rem;
  color: var(--color-text-muted);
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  background: var(--color-bg);
}

.msg-row {
  display: flex;
  gap: var(--space-3);
  align-items: flex-start;
}

.msg-row.user {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 700;
  flex-shrink: 0;
}

.msg-row.ai .msg-avatar {
  background: linear-gradient(135deg, #1a56db, #3b82f6);
  color: #fff;
  box-shadow: 0 2px 6px rgba(26, 86, 219, 0.3);
}

.msg-row.user .msg-avatar {
  background: var(--color-bg-alt);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
}

.msg-content {
  max-width: 76%;
  min-width: 0;
}

.msg-row.user .msg-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.msg-bubble {
  padding: 10px 14px;
  border-radius: var(--radius-lg);
  font-size: 0.875rem;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.msg-row.ai .msg-bubble {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-top-left-radius: 4px;
  color: var(--color-text);
}

.msg-row.user .msg-bubble {
  background: var(--color-primary);
  color: var(--color-text-inverse);
  border-top-right-radius: 4px;
}

.msg-time {
  font-size: 0.68rem;
  color: var(--color-text-muted);
  margin-top: 4px;
}

.typing {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 13px 16px;
}

.typing span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-text-muted);
  animation: blink 1.2s infinite;
}

.typing span:nth-child(2) { animation-delay: 0.2s; }
.typing span:nth-child(3) { animation-delay: 0.4s; }

@keyframes blink {
  0%, 80%, 100% { opacity: 0.3; transform: translateY(0); }
  40% { opacity: 1; transform: translateY(-3px); }
}

/* ---- 评估面板 ---- */
.eval-panel {
  border-top: 1px solid var(--color-border-light);
  background: var(--color-surface);
  padding: var(--space-4) var(--space-5);
  max-height: 190px;
  overflow-y: auto;
}

.eval-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-2);
}

.eval-label {
  font-size: 0.8125rem;
  font-weight: 700;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.eval-label::before {
  content: '';
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--color-success-text);
}

.eval-rating {
  font-size: 0.8125rem;
  color: var(--color-text-muted);
  font-variant-numeric: tabular-nums;
}

.eval-rating strong {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--color-success-text);
  letter-spacing: -0.02em;
}

.eval-rating em {
  font-style: normal;
  font-size: 0.75rem;
}

.eval-suggestion {
  font-size: 0.8125rem;
  color: var(--color-text-secondary);
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

/* ---- 输入区 ---- */
.chat-input {
  display: flex;
  align-items: flex-end;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  border-top: 1px solid var(--color-border-light);
  background: var(--color-surface);
}

.chat-input .textarea {
  flex: 1;
  min-height: 72px;
  max-height: 140px;
  resize: none;
}

.send-btn {
  flex-shrink: 0;
  height: 72px;
  padding: 0 20px;
}

/* ---- 占位 ---- */
.chat-placeholder {
  align-items: center;
  justify-content: center;
}

.placeholder-box {
  text-align: center;
  color: var(--color-text-muted);
}

.placeholder-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto var(--space-4);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--color-bg-alt);
  color: var(--color-text-muted);
}

.placeholder-title {
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--color-text-secondary);
  margin-bottom: 4px;
}

.placeholder-desc {
  font-size: 0.8125rem;
}

/* ---- 响应式 ---- */
@media (max-width: 900px) {
  .workspace {
    grid-template-columns: 1fr;
  }

  .history-pane {
    max-height: none;
  }

  .chat-pane {
    height: 70vh;
    min-height: 480px;
  }

  .mobile-hidden {
    display: none;
  }

  .back-btn {
    display: inline-flex;
  }
}

@media (max-width: 520px) {
  .chat-pane {
    height: calc(100vh - 220px);
    min-height: 400px;
  }

  .msg-content {
    max-width: 84%;
  }

  .send-btn {
    height: auto;
    padding: 0 14px;
    align-self: stretch;
  }
}
</style>
