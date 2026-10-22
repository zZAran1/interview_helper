<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const { success, error } = useToast()

const email = ref('')
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)

const usernameError = ref('')
const emailError = ref('')
const passwordError = ref('')
const confirmError = ref('')

async function handleSubmit() {
  usernameError.value = ''
  emailError.value = ''
  passwordError.value = ''
  confirmError.value = ''

  if (!username.value.trim()) {
    usernameError.value = '请输入用户名'
    return
  }
  if (!email.value.trim()) {
    emailError.value = '请输入邮箱'
    return
  }
  if (!password.value) {
    passwordError.value = '请输入密码'
    return
  }
  if (password.value.length < 6) {
    passwordError.value = '密码长度不能少于6位'
    return
  }
  if (password.value !== confirmPassword.value) {
    confirmError.value = '两次密码输入不一致'
    return
  }

  loading.value = true
  try {
    await register({
      email: email.value.trim(),
      username: username.value.trim(),
      password: password.value,
    })
    success('注册成功，即将跳转登录页')
    setTimeout(() => router.push('/login'), 1200)
  } catch (e: unknown) {
    error(e instanceof Error ? e.message : '操作失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="shell">
    <div class="bg-glow" aria-hidden="true"></div>

    <main class="card">
      <div class="brand">
        <div class="logo-mark" aria-hidden="true">面</div>
        <h1 class="title">面试帮</h1>
        <p class="subtitle">创建账号，开始你的面试备战之旅</p>
      </div>

      <form class="form" @submit.prevent="handleSubmit" novalidate>
        <div class="field">
          <label for="username" class="field-label">用户名</label>
          <input
            id="username"
            v-model="username"
            type="text"
            placeholder="请输入用户名"
            autocomplete="username"
            :class="['input', { 'input-error': usernameError }]"
          />
          <p v-if="usernameError" class="field-error">{{ usernameError }}</p>
        </div>

        <div class="field">
          <label for="email" class="field-label">邮箱</label>
          <input
            id="email"
            v-model="email"
            type="email"
            placeholder="请输入邮箱地址"
            autocomplete="email"
            :class="['input', { 'input-error': emailError }]"
          />
          <p v-if="emailError" class="field-error">{{ emailError }}</p>
        </div>

        <div class="field">
          <label for="password" class="field-label">密码</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="至少6位字符"
            autocomplete="new-password"
            :class="['input', { 'input-error': passwordError }]"
          />
          <p v-if="passwordError" class="field-error">{{ passwordError }}</p>
        </div>

        <div class="field">
          <label for="confirm-password" class="field-label">确认密码</label>
          <input
            id="confirm-password"
            v-model="confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            autocomplete="new-password"
            :class="['input', { 'input-error': confirmError }]"
          />
          <p v-if="confirmError" class="field-error">{{ confirmError }}</p>
        </div>

        <button type="submit" class="btn btn-primary btn-lg btn-block" :disabled="loading">
          <span v-if="loading" class="spinner-sm"></span>
          {{ loading ? '注册中…' : '注册' }}
        </button>
      </form>

      <p class="switch">
        已有账号？<RouterLink to="/login" class="link">立即登录</RouterLink>
      </p>

      <footer class="footnote">面试帮 · 面试准备辅助工具</footer>
    </main>
  </div>
</template>

<style scoped>
.shell {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: var(--space-6);
  position: relative;
  overflow: hidden;
  background: linear-gradient(165deg, #f7f9fc 0%, #eaf1fb 55%, #e2ecfa 100%);
}

.bg-glow {
  position: fixed;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(600px 400px at 82% 8%, rgba(59, 130, 246, 0.1), transparent 65%),
    radial-gradient(560px 420px at 10% 95%, rgba(26, 86, 219, 0.08), transparent 60%);
}

.card {
  position: relative;
  width: 100%;
  max-width: 420px;
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  padding: 44px 44px 28px;
  box-shadow: var(--shadow-lg);
}

.brand {
  text-align: center;
  margin-bottom: 28px;
}

.logo-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #1a56db, #3b82f6);
  color: #fff;
  font-size: 1.35rem;
  font-weight: 700;
  margin-bottom: var(--space-4);
  box-shadow: 0 4px 14px rgba(26, 86, 219, 0.28);
}

.title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.01em;
  margin-bottom: 6px;
}

.subtitle {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.switch {
  margin-top: 24px;
  text-align: center;
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

.link {
  color: var(--color-primary);
  font-weight: 600;
  transition: color var(--duration-fast) var(--ease-out);
}

.link:hover {
  color: var(--color-primary-hover);
}

.footnote {
  margin-top: 22px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border-light);
  text-align: center;
  font-size: 0.75rem;
  color: var(--color-text-muted);
}

@media (max-width: 480px) {
  .card {
    padding: 32px 24px 24px;
    border-radius: var(--radius-lg);
  }
}
</style>
