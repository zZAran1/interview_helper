<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getProfile, type UserProfile } from '@/api/auth'
import {
  viewResume,
  viewEducation,
  viewProjects,
  viewWorks,
  type ResumeData,
} from '@/api/resume'
import DashboardLayout from '@/components/DashboardLayout.vue'

const userName = ref('求职者')
const resume = ref<ResumeData | null>(null)
const loaded = ref(false)

const eduCount = ref(0)
const projectCount = ref(0)
const workCount = ref(0)

const completeness = ref(0)

const WEIGHTED_FIELDS: { key: keyof ResumeData; weight: number }[] = [
  { key: 'real_name', weight: 1 },
  { key: 'desired_position', weight: 1 },
  { key: 'education', weight: 1 },
  { key: 'gender', weight: 1 },
  { key: 'phone_number', weight: 1 },
  { key: 'skill', weight: 1.5 },
  { key: 'summary', weight: 1.5 },
  { key: 'certificate', weight: 1 },
]

onMounted(async () => {
  try {
    const profile: UserProfile = await getProfile()
    userName.value = profile.username
  } catch {
    // keep default
  }

  try {
    const data: ResumeData | null = await viewResume()
    if (data) {
      resume.value = data
      computeCompleteness(data)

      const [eduR, projR, workR] = await Promise.allSettled([
        viewEducation(),
        viewProjects(),
        viewWorks(),
      ])
      if (eduR.status === 'fulfilled') eduCount.value = eduR.value.length
      if (projR.status === 'fulfilled') projectCount.value = projR.value.length
      if (workR.status === 'fulfilled') workCount.value = workR.value.length
    }
  } catch {
    // 后端不可用时保持空态
  } finally {
    loaded.value = true
  }
})

function computeCompleteness(data: ResumeData) {
  let total = 0
  let got = 0
  for (const f of WEIGHTED_FIELDS) {
    total += f.weight
    const v = data[f.key]
    if (typeof v === 'string' ? v.trim() !== '' : Number(v) > 0) got += f.weight
  }
  completeness.value = Math.round((got / total) * 100)
}

const completenessLevel = () => {
  if (completeness.value >= 80) return { text: '准备充分', tone: 'good' }
  if (completeness.value >= 50) return { text: '基本完善', tone: 'mid' }
  if (completeness.value >= 20) return { text: '有待补充', tone: 'low' }
  return { text: '尚未开始', tone: 'low' }
}
</script>

<template>
  <DashboardLayout>
    <div class="dashboard">
      <section class="welcome">
        <h3 class="welcome-greeting">你好，{{ userName }}</h3>
        <p class="welcome-desc">专注准备，从容应对每一次面试</p>
      </section>

      <!-- 简历备战卡 -->
      <section class="hero-card" aria-label="简历备战进度">
        <div class="hero-main">
          <div class="hero-head">
            <h4 class="hero-title">简历备战</h4>
            <span
              :class="['status-badge', `status-${completenessLevel().tone}`]"
            >
              {{ resume ? completenessLevel().text : '尚未开始' }}
            </span>
          </div>
          <p class="hero-desc">
            {{
              resume
                ? resume.desired_position
                  ? `目标职位：${resume.desired_position}`
                  : '完善基本信息，让简历更聚焦'
                : '创建你的第一份简历，开启面试备战'
            }}
          </p>

          <div class="progress-row">
            <div class="progress-track" role="progressbar" :aria-valuenow="completeness" aria-valuemin="0" aria-valuemax="100">
              <div class="progress-fill" :style="{ transform: `scaleX(${completeness / 100})` }"></div>
            </div>
            <span class="progress-value">{{ completeness }}%</span>
          </div>
        </div>

        <div class="hero-action">
          <RouterLink :to="{ name: 'resume' }" class="btn btn-primary">
            {{ resume ? '继续完善简历' : '创建简历' }}
          </RouterLink>
        </div>
      </section>

      <!-- 简历内容统计（真实数据） -->
      <section class="stats" aria-label="简历内容统计">
        <div class="stat-card">
          <span class="stat-value">{{ eduCount }}</span>
          <span class="stat-label">教育经历</span>
          <span class="stat-hint">已录入 {{ eduCount }} 条</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ projectCount }}</span>
          <span class="stat-label">项目经历</span>
          <span class="stat-hint">已录入 {{ projectCount }} 条</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ workCount }}</span>
          <span class="stat-label">工作经历</span>
          <span class="stat-hint">已录入 {{ workCount }} 条</span>
        </div>
      </section>

      <!-- 快捷入口 -->
      <section class="quick-actions">
        <h4 class="section-label">快捷入口</h4>
        <div class="action-grid">
          <RouterLink :to="{ name: 'resume' }" class="action-card">
            <svg class="action-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M14 2H7a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7z"></path>
              <path d="M14 2v5h5"></path>
              <path d="M9 13h6"></path>
              <path d="M9 17h6"></path>
            </svg>
            <div class="action-body">
              <span class="action-title">{{ resume ? '编辑简历' : '创建简历' }}</span>
              <span class="action-desc">
                {{ resume ? '完善个人信息与经历，提升专业度' : '完善个人信息，为面试做好准备' }}
              </span>
            </div>
            <span class="action-arrow">&rarr;</span>
          </RouterLink>

          <RouterLink :to="{ name: 'profile' }" class="action-card">
            <svg class="action-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
            <div class="action-body">
              <span class="action-title">个人资料</span>
              <span class="action-desc">查看和管理你的账户信息</span>
            </div>
            <span class="action-arrow">&rarr;</span>
          </RouterLink>

          <RouterLink :to="{ name: 'interview' }" class="action-card">
            <svg class="action-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M12 2a3 3 0 0 0-3 3v6a3 3 0 0 0 6 0V5a3 3 0 0 0-3-3z"></path>
              <path d="M19 10v2a7 7 0 0 1-14 0v-2"></path>
              <line x1="12" y1="19" x2="12" y2="22"></line>
            </svg>
            <div class="action-body">
              <span class="action-title">模拟面试</span>
              <span class="action-desc">AI 面试官多轮提问，结束后生成评分与建议</span>
            </div>
            <span class="action-arrow">&rarr;</span>
          </RouterLink>
        </div>
      </section>
    </div>
  </DashboardLayout>
</template>

<style scoped>
.dashboard {
  max-width: 960px;
}

.welcome {
  margin-bottom: var(--space-6);
}

.welcome-greeting {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.01em;
  margin-bottom: 4px;
}

.welcome-desc {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
}

/* ---- 简历备战卡 ---- */
.hero-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-8);
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  padding: var(--space-8);
  box-shadow: var(--shadow-sm);
  margin-bottom: var(--space-6);
}

.hero-main {
  flex: 1;
  min-width: 0;
}

.hero-head {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: 6px;
}

.hero-title {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--color-text);
}

.status-badge {
  font-size: 0.72rem;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 999px;
}

.status-good { background: var(--color-success-bg); color: var(--color-success-text); }
.status-mid  { background: var(--color-primary-ghost); color: var(--color-primary); }
.status-low  { background: var(--color-bg-alt); color: var(--color-text-secondary); }

.hero-desc {
  font-size: 0.85rem;
  color: var(--color-text-secondary);
  margin-bottom: var(--space-5);
}

.progress-row {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  max-width: 420px;
}

.progress-track {
  flex: 1;
  height: 8px;
  border-radius: 999px;
  background: var(--color-bg-alt);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  width: 100%;
  border-radius: 999px;
  background: var(--color-primary);
  transform-origin: left center;
  transition: transform var(--duration-slow) var(--ease-out);
}

.progress-value {
  font-size: 0.85rem;
  font-weight: 700;
  color: var(--color-text);
  font-variant-numeric: tabular-nums;
}

.hero-action .btn {
  white-space: nowrap;
}

/* ---- 统计 ---- */
.stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-4);
  margin-bottom: var(--space-8);
}

.stat-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  box-shadow: var(--shadow-xs);
}

.stat-value {
  display: block;
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-primary);
  letter-spacing: -0.02em;
  font-variant-numeric: tabular-nums;
  line-height: 1.2;
  margin-bottom: 2px;
}

.stat-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 2px;
}

.stat-hint {
  font-size: 0.75rem;
  color: var(--color-text-muted);
}

/* ---- 快捷入口 ---- */
.quick-actions {
  margin-bottom: var(--space-8);
}

.section-label {
  font-size: 0.8125rem;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: 0.02em;
  margin-bottom: var(--space-4);
}

.action-grid {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.action-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xs);
  text-decoration: none;
  transition: border-color var(--duration-fast) var(--ease-out),
              box-shadow var(--duration-fast) var(--ease-out),
              transform var(--duration-fast) var(--ease-out);
}

.action-card:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
}

.action-icon {
  width: 42px;
  height: 42px;
  padding: 11px;
  background: var(--color-primary-ghost);
  border-radius: var(--radius-md);
  flex-shrink: 0;
  color: var(--color-primary);
}

.action-body {
  flex: 1;
  min-width: 0;
}

.action-title {
  display: block;
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 2px;
}

.action-desc {
  font-size: 0.8125rem;
  color: var(--color-text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.action-arrow {
  font-size: 1.1rem;
  color: var(--color-text-muted);
  flex-shrink: 0;
  transition: color var(--duration-fast) var(--ease-out),
              transform var(--duration-fast) var(--ease-out);
}

.action-card:hover .action-arrow {
  color: var(--color-primary);
  transform: translateX(4px);
}

@media (max-width: 720px) {
  .hero-card {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-5);
    padding: var(--space-6);
  }

  .hero-action .btn {
    width: 100%;
  }

  .stats {
    grid-template-columns: 1fr;
    gap: var(--space-3);
  }
}
</style>
