<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getProfile, type UserProfile } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const userName = ref('求职者')
const userEmail = ref('')

defineSlots<{ default(): any }>()

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    router.replace('/login')
    return
  }
  try {
    const profile: UserProfile = await getProfile()
    userName.value = profile.username
    userEmail.value = profile.email
  } catch {
    // keep default name
  }
})

function handleLogout() {
  localStorage.removeItem('token')
  router.replace('/login')
}

interface NavItem {
  key: string
  label: string
  icon: string
}

const navItems: NavItem[] = [
  { key: 'home', label: '工作台', icon: 'grid' },
  { key: 'resume', label: '我的简历', icon: 'doc' },
  { key: 'interview', label: '模拟面试', icon: 'mic' },
  { key: 'profile', label: '个人资料', icon: 'user' },
]

function iconPath(name: string): string {
  switch (name) {
    case 'grid':
      return '<rect x="3" y="3" width="7" height="7" rx="1.5"></rect><rect x="14" y="3" width="7" height="7" rx="1.5"></rect><rect x="3" y="14" width="7" height="7" rx="1.5"></rect><rect x="14" y="14" width="7" height="7" rx="1.5"></rect>'
    case 'doc':
      return '<path d="M14 2H7a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7z"></path><path d="M14 2v5h5"></path><path d="M9 13h6"></path><path d="M9 17h6"></path>'
    case 'mic':
      return '<path d="M12 2a3 3 0 0 0-3 3v6a3 3 0 0 0 6 0V5a3 3 0 0 0-3-3z"></path><path d="M19 10v2a7 7 0 0 1-14 0v-2"></path><line x1="12" y1="19" x2="12" y2="22"></line>'
    case 'user':
      return '<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle>'
    case 'logout':
      return '<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><path d="M16 17l5-5-5-5"></path><path d="M21 12H9"></path>'
    default:
      return ''
  }
}
</script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="sidebar-brand">
        <div class="brand-logo">面</div>
        <div class="brand-text">
          <span class="brand-name">面试帮</span>
          <span class="brand-sub">Interview Helper</span>
        </div>
      </div>

      <nav class="sidebar-nav" aria-label="主导航">
        <RouterLink
          v-for="item in navItems"
          :key="item.key"
          :to="{ name: item.key }"
          :class="['nav-item', { active: route.name === item.key }]"
        >
          <svg
            class="nav-icon"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="1.8"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
            v-html="iconPath(item.icon)"
          ></svg>
          <span class="nav-label">{{ item.label }}</span>
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <div class="user-box">
          <div class="user-avatar">{{ userName.charAt(0) }}</div>
          <div class="user-info">
            <span class="user-name">{{ userName }}</span>
            <span class="user-email">{{ userEmail || '—' }}</span>
          </div>
          <button class="logout-btn" title="退出登录" aria-label="退出登录" @click="handleLogout">
            <svg
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
              stroke-linejoin="round"
              aria-hidden="true"
              v-html="iconPath('logout')"
            ></svg>
          </button>
        </div>
      </div>
    </aside>

    <main class="main">
      <header class="topbar">
        <div class="topbar-left">
          <div class="mobile-logo">面</div>
          <h2 class="page-title">
            {{ navItems.find((i) => route.name === i.key)?.label || '工作台' }}
          </h2>
        </div>
      </header>

      <div class="content">
        <slot />
      </div>
    </main>

    <!-- Mobile bottom navigation -->
    <nav class="mobile-nav" aria-label="移动端导航">
      <RouterLink
        v-for="item in navItems"
        :key="item.key"
        :to="{ name: item.key }"
        :class="['mobile-nav-item', { active: route.name === item.key }]"
      >
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="1.8"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
          v-html="iconPath(item.icon)"
        ></svg>
        <span>{{ item.label }}</span>
      </RouterLink>
      <button class="mobile-nav-item" @click="handleLogout">
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="1.8"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
          v-html="iconPath('logout')"
        ></svg>
        <span>退出</span>
      </button>
    </nav>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
  background: var(--color-bg);
}

/* ---- Sidebar ---- */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: var(--sidebar-width);
  background: var(--color-surface);
  border-right: 1px solid var(--color-border-light);
  display: flex;
  flex-direction: column;
  z-index: 20;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-5) var(--space-5);
  border-bottom: 1px solid var(--color-border-light);
}

.brand-logo,
.mobile-logo {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  background: linear-gradient(135deg, #1a56db, #3b82f6);
  color: #fff;
  font-size: 1.05rem;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(26, 86, 219, 0.3);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
  min-width: 0;
}

.brand-name {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.01em;
}

.brand-sub {
  font-size: 0.68rem;
  color: var(--color-text-muted);
  letter-spacing: 0.04em;
}

.sidebar-nav {
  flex: 1;
  padding: var(--space-3);
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: 9px var(--space-3);
  border: none;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
  text-decoration: none;
  transition: background var(--duration-fast) var(--ease-out),
              color var(--duration-fast) var(--ease-out);
}

.nav-item:hover {
  background: var(--color-bg-alt);
  color: var(--color-text);
}

.nav-item.active {
  background: var(--color-primary-ghost);
  color: var(--color-primary);
  font-weight: 600;
}

.nav-icon {
  flex-shrink: 0;
  opacity: 0.85;
}

.nav-item.active .nav-icon {
  opacity: 1;
}

.sidebar-footer {
  padding: var(--space-4);
  border-top: 1px solid var(--color-border-light);
}

.user-box {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--color-primary-ghost);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.85rem;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-width: 0;
  flex: 1;
}

.user-name {
  font-size: 0.8125rem;
  font-weight: 600;
  color: var(--color-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-email {
  font-size: 0.72rem;
  color: var(--color-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.logout-btn {
  border: none;
  background: none;
  color: var(--color-text-muted);
  padding: 6px;
  border-radius: var(--radius-sm);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: color var(--duration-fast) var(--ease-out),
              background var(--duration-fast) var(--ease-out);
}

.logout-btn:hover {
  color: var(--color-error-text);
  background: var(--color-error-bg);
}

/* ---- Main ---- */
.main {
  flex: 1;
  margin-left: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--space-8);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--color-border-light);
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.mobile-logo {
  display: none;
  width: 30px;
  height: 30px;
  font-size: 0.9rem;
}

.page-title {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.01em;
}

.content {
  flex: 1;
  padding: var(--space-8);
  padding-bottom: calc(var(--space-8) + 64px);
}

/* ---- Mobile bottom nav ---- */
.mobile-nav {
  display: none;
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 30;
  height: 56px;
  background: var(--color-surface);
  border-top: 1px solid var(--color-border-light);
  padding-bottom: env(safe-area-inset-bottom);
}

.mobile-nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  border: none;
  background: none;
  color: var(--color-text-muted);
  font-size: 0.68rem;
  font-weight: 500;
  text-decoration: none;
  transition: color var(--duration-fast) var(--ease-out);
}

.mobile-nav-item.active {
  color: var(--color-primary);
  font-weight: 600;
}

@media (max-width: 768px) {
  .sidebar {
    display: none;
  }

  .main {
    margin-left: 0;
  }

  .mobile-logo {
    display: inline-flex;
  }

  .content {
    padding: var(--space-5);
    padding-bottom: calc(var(--space-5) + 68px);
  }

  .topbar {
    padding: 0 var(--space-5);
  }

  .mobile-nav {
    display: flex;
  }
}
</style>
