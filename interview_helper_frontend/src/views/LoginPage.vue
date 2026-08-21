<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '@/api/auth'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const route = useRoute()
const { success, error } = useToast()

const email = ref('')
const password = ref('')
const loading = ref(false)
const emailError = ref('')
const passwordError = ref('')

async function handleSubmit() {
  emailError.value = ''
  passwordError.value = ''

  if (!email.value.trim()) {
    emailError.value = '请输入邮箱'
    return
  }
  if (!password.value) {
    passwordError.value = '请输入密码'
    return
  }

  loading.value = true
  try {
    const result = await login({ email: email.value.trim(), password: password.value })
    localStorage.setItem('token', result.token)
    success('登录成功')
    setTimeout(() => router.push((route.query.redirect as string) || '/home'), 600)
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
        <p class="subtitle">欢迎回来，登录你的账号</p>
      </div>

      <form class="form" @submit.prevent="handleSubmit" novalidate>
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
            placeholder="请输入密码"
            autocomplete="current-password"
            :class="['input', { 'input-error': passwordError }]"
          />
          <p v-if="passwordError" class="field-error">{{ passwordError }}</p>
        </div>

        <button type="submit" class="btn btn-primary btn-lg btn-block" :disabled="loading">
          <span v-if="loading" class="spinner-sm"></span>
          {{ loading ? '登录中…' : '登录' }}
        </button>
      </form>

      <p class="switch">
        还没有账号？<RouterLink to="/register" class="link">立即注册</RouterLink>
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
  margin-bottom: 32px;
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
  gap: 18px;
}

.switch {
  margin-top: 26px;
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
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border-light);
  text-align: center;
  font-size: 0.75rem;
  color: var(--color-text-muted);
}

@media (max-width: 480px) {
  .card {
    padding: 34px 24px 24px;
    border-radius: var(--radius-lg);
  }
}
</style>
