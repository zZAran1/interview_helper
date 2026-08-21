<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProfile, deleteAccount, type UserProfile } from '@/api/auth'
import DashboardLayout from '@/components/DashboardLayout.vue'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'

const router = useRouter()
const { success, error } = useToast()
const { confirm } = useConfirm()

const profile = ref<UserProfile | null>(null)
const loading = ref(true)
const loadError = ref('')
const deleting = ref(false)

onMounted(async () => {
  try {
    profile.value = await getProfile()
  } catch {
    loadError.value = '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
})

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

function handleLogout() {
  localStorage.removeItem('token')
  success('已退出登录')
  router.replace('/login')
}

async function handleDeleteAccount() {
  const ok = await confirm('确定要注销账号吗？此操作将永久删除你的账号及全部数据，且无法恢复。')
  if (!ok) return

  deleting.value = true
  try {
    await deleteAccount()
    localStorage.removeItem('token')
    success('账号已注销')
    setTimeout(() => router.replace('/login'), 600)
  } catch (e: unknown) {
    error(e instanceof Error ? e.message : '注销失败，请重试')
  } finally {
    deleting.value = false
  }
}
</script>

<template>
  <DashboardLayout>
    <div class="profile-page">
      <!-- Loading / Error -->
      <div v-if="loading" class="state-box">
        <div class="spinner"></div>
        <p>加载中…</p>
      </div>

      <div v-else-if="loadError" class="state-box">
        <p class="load-error">{{ loadError }}</p>
      </div>

      <div v-else-if="profile" class="profile-wrap">
        <div class="profile-card">
          <div class="profile-header">
            <div class="profile-avatar">{{ profile.username.charAt(0) }}</div>
            <div class="profile-id">
              <h3 class="profile-name">{{ profile.username }}</h3>
              <p class="profile-email">{{ profile.email }}</p>
            </div>
          </div>

          <div class="profile-details">
            <div class="detail-row">
              <span class="detail-label">用户名</span>
              <span class="detail-value">{{ profile.username }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">邮箱</span>
              <span class="detail-value">{{ profile.email }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">注册时间</span>
              <span class="detail-value">{{ formatDate(profile.created_at) }}</span>
            </div>
          </div>
        </div>

        <!-- Account management -->
        <div class="account-card">
          <h4 class="account-title">账户操作</h4>
          <div class="account-actions">
            <button class="btn btn-outline" @click="handleLogout">退出登录</button>
          </div>
        </div>

        <!-- Danger zone -->
        <div class="danger-card">
          <div class="danger-info">
            <h4 class="danger-title">注销账号</h4>
            <p class="danger-desc">注销后将永久删除账号及全部简历数据，且无法恢复。请谨慎操作。</p>
          </div>
          <button class="btn btn-danger" @click="handleDeleteAccount" :disabled="deleting">
            <span v-if="deleting" class="spinner-sm"></span>
            {{ deleting ? '注销中…' : '注销账号' }}
          </button>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>

<style scoped>
.profile-page {
  max-width: 600px;
}

.profile-wrap {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.load-error {
  color: var(--color-error-text);
}

.profile-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: var(--space-5);
  padding: var(--space-8);
  background: linear-gradient(135deg, var(--color-primary-ghost), var(--color-bg));
  border-bottom: 1px solid var(--color-border-light);
}

.profile-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1a56db, #3b82f6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.6rem;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(26, 86, 219, 0.25);
}

.profile-id {
  min-width: 0;
}

.profile-name {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: var(--space-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-email {
  font-size: 0.9rem;
  color: var(--color-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-details {
  padding: var(--space-4) var(--space-8);
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3) 0;
  border-bottom: 1px solid var(--color-border-light);
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.detail-value {
  font-size: 0.875rem;
  color: var(--color-text);
  font-weight: 600;
}

/* ---- Account management ---- */
.account-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: var(--space-5) var(--space-6);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
}

.account-title {
  font-size: 0.9375rem;
  font-weight: 700;
  color: var(--color-text);
}

/* ---- Danger zone ---- */
.danger-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-5);
  padding: var(--space-5) var(--space-6);
  background: var(--color-error-bg);
  border: 1px solid var(--color-error-text);
  border-radius: var(--radius-lg);
}

.danger-info {
  min-width: 0;
}

.danger-title {
  font-size: 0.9375rem;
  font-weight: 700;
  color: var(--color-error-text);
  margin-bottom: 2px;
}

.danger-desc {
  font-size: 0.8125rem;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

@media (max-width: 560px) {
  .profile-header {
    padding: var(--space-6);
  }

  .profile-details {
    padding: var(--space-4) var(--space-5);
  }

  .account-card,
  .danger-card {
    flex-direction: column;
    align-items: stretch;
  }

  .account-card .btn,
  .danger-card .btn {
    width: 100%;
  }
}
</style>
