<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  viewResume,
  createResume,
  updateResume,
  deleteResume,
  type ResumeData,
  viewEducation,
  createEducation,
  updateEducation,
  deleteEducation,
  type ResumeEducationData,
  viewProjects,
  createProject,
  updateProject,
  deleteProject,
  type ResumeProjectData,
  viewWorks,
  createWork,
  updateWork,
  deleteWork,
  type ResumeWorkData,
  optimizeResume,
} from '@/api/resume'
import DashboardLayout from '@/components/DashboardLayout.vue'
import SubModuleSection, { type FieldDef } from '@/components/SubModuleSection.vue'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'

const { success, error } = useToast()
const { confirm } = useConfirm()

const resume = ref<ResumeData | null>(null)
const resumeLoading = ref(true)
const saving = ref(false)
const creating = ref(false)

const educationList = ref<ResumeEducationData[]>([])
const educationLoading = ref(false)
const projectList = ref<ResumeProjectData[]>([])
const projectLoading = ref(false)
const workList = ref<ResumeWorkData[]>([])
const workLoading = ref(false)

type PendingOp = { action: 'create' | 'update' | 'delete'; data: Record<string, any> }
const pendingEduDeletes = ref<PendingOp[]>([])
const pendingProjDeletes = ref<PendingOp[]>([])
const pendingWorkDeletes = ref<PendingOp[]>([])

const aiOptimizing = ref(false)

const form = ref<ResumeData>({
  real_name: '',
  age: 0,
  gender: '',
  education: '',
  desired_position: '',
  phone_number: '',
  skill: '',
  summary: '',
  certificate: '',
})

const genderOptions = [
  { label: '男', value: '男' },
  { label: '女', value: '女' },
]

const educationOptions = [
  { label: '请选择', value: '' },
  { label: '高中及以下', value: '高中及以下' },
  { label: '大专', value: '大专' },
  { label: '本科', value: '本科' },
  { label: '硕士', value: '硕士' },
  { label: '博士', value: '博士' },
]

const eduFields: FieldDef[] = [
  { key: 'school_name', label: '学校名称', placeholder: '如：清华大学', required: true },
  { key: 'degree', label: '学历', placeholder: '如：本科' },
  { key: 'field_of_study', label: '专业', placeholder: '如：计算机科学与技术' },
  { key: 'start_date', label: '开始时间', type: 'date' },
  { key: 'end_date', label: '结束时间', type: 'date' },
  { key: 'core_courses', label: '主修课程', type: 'textarea', placeholder: '如：数据结构、操作系统、计算机网络', rows: 2 },
  { key: 'appendix', label: '其他', type: 'textarea', placeholder: '其他信息', rows: 5 },
]

const projFields: FieldDef[] = [
  { key: 'project_name', label: '项目名称', placeholder: '如：在线商城系统', required: true },
  { key: 'role', label: '担任角色', placeholder: '如：后端开发工程师' },
  { key: 'start_date', label: '开始时间', type: 'date' },
  { key: 'end_date', label: '结束时间', type: 'date' },
  { key: 'description', label: '项目描述', type: 'textarea', placeholder: '描述项目背景、技术栈和你的贡献', rows: 5 },
]

const workFields: FieldDef[] = [
  { key: 'company_name', label: '公司名称', placeholder: '如：字节跳动', required: true },
  { key: 'position', label: '职位', placeholder: '如：高级前端工程师' },
  { key: 'start_date', label: '入职时间', type: 'date' },
  { key: 'end_date', label: '离职时间', type: 'date' },
  { key: 'description', label: '工作描述', type: 'textarea', placeholder: '描述你的职责和主要成就', rows: 5 },
]

onMounted(async () => {
  await fetchResume()
})

async function fetchResume() {
  resumeLoading.value = true
  try {
    resume.value = await viewResume()
    if (resume.value) {
      form.value = { ...resume.value }
      await fetchSubModules()
    }
  } catch {
    error('加载失败')
  } finally {
    resumeLoading.value = false
  }
}

async function fetchSubModules() {
  educationLoading.value = true
  projectLoading.value = true
  workLoading.value = true

  const [eduR, projR, workR] = await Promise.allSettled([
    viewEducation(),
    viewProjects(),
    viewWorks(),
  ])

  if (eduR.status === 'fulfilled') educationList.value = eduR.value
  if (projR.status === 'fulfilled') projectList.value = projR.value
  if (workR.status === 'fulfilled') workList.value = workR.value

  educationLoading.value = false
  projectLoading.value = false
  workLoading.value = false

  pendingEduDeletes.value = []
  pendingProjDeletes.value = []
  pendingWorkDeletes.value = []
}

function beginCreate() {
  creating.value = true
  form.value = { real_name: '', age: 0, gender: '', education: '', desired_position: '', phone_number: '', skill: '', summary: '', certificate: '' }
}

function cancelCreate() {
  creating.value = false
}

/**
 * 以列表当前状态重建操作队列：
 * - 已有记录（有 id）→ update，新记录 → create
 * - 已删除的旧记录 → delete（新记录被删除则仅从列表移除，无需请求）
 */
function buildOps(
  list: Record<string, any>[],
  deletes: PendingOp[],
): PendingOp[] {
  const ops: PendingOp[] = []
  for (const item of list) {
    const clean = { ...item }
    delete clean._idx
    ops.push(item.id != null ? { action: 'update', data: clean } : { action: 'create', data: clean })
  }
  return [...ops, ...deletes]
}

async function flushPendingOps(
  pending: PendingOp[],
  createFn: (d: any) => Promise<void>,
  updateFn: (d: any) => Promise<void>,
  deleteFn: (d: any) => Promise<void>,
) {
  for (const op of pending) {
    if (op.action === 'create') await createFn(op.data)
    else if (op.action === 'update') await updateFn(op.data)
    else await deleteFn(op.data)
  }
}

async function handleSave() {
  if (!form.value.real_name.trim()) {
    error('请填写真实姓名')
    return
  }
  saving.value = true
  try {
    if (resume.value) {
      await updateResume(form.value)
    } else {
      await createResume(form.value)
      creating.value = false
    }
    await flushPendingOps(
      buildOps(educationList.value, pendingEduDeletes.value),
      createEducation, updateEducation, deleteEducation,
    )
    await flushPendingOps(
      buildOps(projectList.value, pendingProjDeletes.value),
      createProject, updateProject, deleteProject,
    )
    await flushPendingOps(
      buildOps(workList.value, pendingWorkDeletes.value),
      createWork, updateWork, deleteWork,
    )
    success('保存成功')
    await fetchResume()
  } catch (e: unknown) {
    error(e instanceof Error ? e.message : '保存失败')
    await fetchResume()
  } finally {
    saving.value = false
  }
}

async function handleDelete() {
  const ok = await confirm('确定要删除简历吗？此操作不可撤销。')
  if (!ok) return
  resumeLoading.value = true
  try {
    await deleteResume()
    resume.value = null
    educationList.value = []
    projectList.value = []
    workList.value = []
    pendingEduDeletes.value = []
    pendingProjDeletes.value = []
    pendingWorkDeletes.value = []
    success('简历已删除')
  } catch (e: unknown) {
    error(e instanceof Error ? e.message : '删除失败')
  } finally {
    resumeLoading.value = false
  }
}

// ---- Sub-module handlers (optimistic update) ----
function handleCreateEducation(data: Record<string, any>) {
  educationList.value = [...educationList.value, data as ResumeEducationData]
}
function handleUpdateEducation(data: Record<string, any>) {
  const idx = data._idx as number
  const clean = { ...data }
  delete clean._idx
  educationList.value = educationList.value.map((e, i) => (i === idx ? ({ ...clean } as ResumeEducationData) : e))
}
function handleDeleteEducation(data: Record<string, any>) {
  const idx = data._idx as number
  const clean = { ...data }
  delete clean._idx
  if (clean.id != null) pendingEduDeletes.value = [...pendingEduDeletes.value, { action: 'delete', data: clean }]
  educationList.value = educationList.value.filter((_, i) => i !== idx)
}

function handleCreateProject(data: Record<string, any>) {
  projectList.value = [...projectList.value, data as ResumeProjectData]
}
function handleUpdateProject(data: Record<string, any>) {
  const idx = data._idx as number
  const clean = { ...data }
  delete clean._idx
  projectList.value = projectList.value.map((e, i) => (i === idx ? ({ ...clean } as ResumeProjectData) : e))
}
function handleDeleteProject(data: Record<string, any>) {
  const idx = data._idx as number
  const clean = { ...data }
  delete clean._idx
  if (clean.id != null) pendingProjDeletes.value = [...pendingProjDeletes.value, { action: 'delete', data: clean }]
  projectList.value = projectList.value.filter((_, i) => i !== idx)
}

function handleCreateWork(data: Record<string, any>) {
  workList.value = [...workList.value, data as ResumeWorkData]
}
function handleUpdateWork(data: Record<string, any>) {
  const idx = data._idx as number
  const clean = { ...data }
  delete clean._idx
  workList.value = workList.value.map((e, i) => (i === idx ? ({ ...clean } as ResumeWorkData) : e))
}
function handleDeleteWork(data: Record<string, any>) {
  const idx = data._idx as number
  const clean = { ...data }
  delete clean._idx
  if (clean.id != null) pendingWorkDeletes.value = [...pendingWorkDeletes.value, { action: 'delete', data: clean }]
  workList.value = workList.value.filter((_, i) => i !== idx)
}

async function handleAiOptimizeAll() {
  const hasContent = form.value.summary.trim()
    || educationList.value.some(e => e.appendix?.trim())
    || projectList.value.some(p => p.description?.trim())
    || workList.value.some(w => w.description?.trim())

  if (!hasContent) {
    error('请先填写个人简介或教育/项目/工作经历中的描述内容')
    return
  }

  aiOptimizing.value = true
  try {
    const result = await optimizeResume({
      summary: form.value.summary,
      education_appendix: educationList.value.map(e => e.appendix).filter(Boolean).join('\n\n'),
      project_description: projectList.value.map(p => p.description).filter(Boolean).join('\n\n'),
      work_description: workList.value.map(w => w.description).filter(Boolean).join('\n\n'),
    })

    if (result.summary) {
      form.value.summary = result.summary
    }
    if (result.education_appendix && educationList.value.length > 0) {
      const parts = result.education_appendix.split('\n\n').filter(Boolean)
      if (parts.length === educationList.value.length) {
        educationList.value = educationList.value.map((e, i) => ({ ...e, appendix: parts[i] ?? '' }))
      }
    }
    if (result.project_description && projectList.value.length > 0) {
      const parts = result.project_description.split('\n\n').filter(Boolean)
      if (parts.length === projectList.value.length) {
        projectList.value = projectList.value.map((p, i) => ({ ...p, description: parts[i] ?? '' }))
      }
    }
    if (result.work_description && workList.value.length > 0) {
      const parts = result.work_description.split('\n\n').filter(Boolean)
      if (parts.length === workList.value.length) {
        workList.value = workList.value.map((w, i) => ({ ...w, description: parts[i] ?? '' }))
      }
    }

    success('AI 优化完成')
  } catch (e: unknown) {
    error(e instanceof Error ? e.message : 'AI 优化失败')
  } finally {
    aiOptimizing.value = false
  }
}
</script>

<template>
  <DashboardLayout>
    <div class="resume-page">
      <!-- Loading -->
      <div v-if="resumeLoading" class="state-box">
        <div class="spinner"></div>
        <p>加载中…</p>
      </div>

      <!-- Empty: no resume and not creating -->
      <div v-else-if="!resume && !creating" class="empty-state">
        <div class="empty-icon">
          <svg width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M14 2H7a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7z"></path>
            <path d="M14 2v5h5"></path>
            <path d="M9 13h6"></path>
            <path d="M9 17h6"></path>
          </svg>
        </div>
        <h4 class="empty-title">还没有简历</h4>
        <p class="empty-desc">创建你的第一份简历，为面试做好准备</p>
        <button class="btn btn-primary" @click="beginCreate">创建简历</button>
      </div>

      <!-- Resume Document (existing + new draft) -->
      <div v-else class="resume-doc">
        <div class="doc-header">
          <h2 class="doc-title">{{ resume ? '我的简历' : '创建简历' }}</h2>
          <p class="doc-sub" v-if="resume">填写完整可提升工作台中的简历完整度</p>
        </div>

        <!-- Basic Info -->
        <div class="doc-section">
          <h4 class="section-h">基本信息</h4>

          <div class="form-grid">
            <div class="field">
              <label class="field-label">真实姓名 <span class="required-mark">*</span></label>
              <input v-model="form.real_name" class="input" placeholder="请输入真实姓名" />
            </div>
            <div class="field">
              <label class="field-label">年龄</label>
              <input v-model.number="form.age" type="number" class="input" placeholder="请输入年龄" min="0" />
            </div>
            <div class="field">
              <label class="field-label">性别</label>
              <select v-model="form.gender" class="select">
                <option value="">请选择</option>
                <option v-for="opt in genderOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
              </select>
            </div>
            <div class="field">
              <label class="field-label">学历</label>
              <select v-model="form.education" class="select">
                <option v-for="opt in educationOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
              </select>
            </div>
            <div class="field">
              <label class="field-label">期望职位</label>
              <input v-model="form.desired_position" class="input" placeholder="如：Java 后端开发工程师" />
            </div>
            <div class="field">
              <label class="field-label">联系电话</label>
              <input v-model="form.phone_number" class="input" placeholder="请输入手机号" />
            </div>
          </div>

          <div class="field mt-4">
            <label class="field-label">专业技能</label>
            <textarea
              v-model="form.skill"
              class="textarea"
              placeholder="描述你的专业技能，如：Java、Spring Boot、Vue.js…"
              rows="3"
            ></textarea>
          </div>

          <div class="field mt-4">
            <label class="field-label">个人简介</label>
            <textarea
              v-model="form.summary"
              class="textarea summary-area"
              placeholder="简要介绍你的背景和求职意向"
              rows="6"
            ></textarea>
          </div>

          <div class="field mt-4">
            <label class="field-label">证书资质</label>
            <textarea
              v-model="form.certificate"
              class="textarea"
              placeholder="列出你持有的证书或资质"
              rows="2"
            ></textarea>
          </div>

          <div v-if="creating" class="doc-actions">
            <button class="btn btn-ghost" @click="cancelCreate">取消</button>
          </div>
        </div>

        <!-- Sub Modules (visible for both existing resume and new draft) -->
        <template v-if="resume || creating">
          <div class="doc-divider"></div>

          <div class="doc-section">
            <SubModuleSection
              title="教育经历"
              :items="educationList"
              :fields="eduFields"
              :loading="educationLoading"
              @create="handleCreateEducation"
              @update="handleUpdateEducation"
              @delete="handleDeleteEducation"
            />
          </div>

          <div class="doc-divider"></div>

          <div class="doc-section">
            <SubModuleSection
              title="项目经历"
              :items="projectList"
              :fields="projFields"
              :loading="projectLoading"
              @create="handleCreateProject"
              @update="handleUpdateProject"
              @delete="handleDeleteProject"
            />
          </div>

          <div class="doc-divider"></div>

          <div class="doc-section">
            <SubModuleSection
              title="工作经历"
              :items="workList"
              :fields="workFields"
              :loading="workLoading"
              @create="handleCreateWork"
              @update="handleUpdateWork"
              @delete="handleDeleteWork"
            />
          </div>

          <div class="doc-divider"></div>
        </template>

        <div class="doc-footer">
          <button v-if="resume" class="btn btn-danger" @click="handleDelete">删除简历</button>
          <div class="doc-footer-right">
            <button class="btn btn-soft" @click="handleAiOptimizeAll" :disabled="aiOptimizing">
              <span v-if="aiOptimizing" class="spinner-sm"></span>
              AI 智能优化
            </button>
            <button class="btn btn-primary" @click="handleSave" :disabled="saving">
              <span v-if="saving" class="spinner-sm"></span>
              保存
            </button>
          </div>
        </div>
      </div>

      <!-- AI optimizing overlay -->
      <Teleport to="body">
        <div v-if="aiOptimizing" class="ai-overlay">
          <div class="ai-overlay-card">
            <div class="ai-spinner"></div>
            <p class="ai-overlay-text">AI 正在优化简历内容…</p>
            <p class="ai-overlay-sub">请稍候，优化完成后将自动更新</p>
          </div>
        </div>
      </Teleport>
    </div>
  </DashboardLayout>
</template>

<style scoped>
.resume-page {
  max-width: 780px;
  position: relative;
}

/* ---- Empty state ---- */
.empty-state {
  text-align: center;
  padding: var(--space-16) var(--space-6);
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.empty-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto var(--space-4);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  background: var(--color-primary-ghost);
  color: var(--color-primary);
}

.empty-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: var(--space-2);
}

.empty-desc {
  font-size: 0.875rem;
  color: var(--color-text-secondary);
  margin-bottom: var(--space-6);
}

/* ---- Resume Document ---- */
.resume-doc {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.doc-header {
  padding: var(--space-6) var(--space-8);
  border-bottom: 1px solid var(--color-border-light);
  background: var(--color-bg);
}

.doc-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--color-text);
}

.doc-sub {
  margin-top: 2px;
  font-size: 0.8rem;
  color: var(--color-text-secondary);
}

.doc-section {
  padding: var(--space-6) var(--space-8);
}

.section-h {
  font-size: 0.8125rem;
  font-weight: 700;
  color: var(--color-text-secondary);
  letter-spacing: 0.02em;
  margin-bottom: var(--space-5);
}

.doc-divider {
  height: 1px;
  background: var(--color-border-light);
  margin: 0 var(--space-8);
}

.doc-actions {
  display: flex;
  gap: var(--space-3);
  margin-top: var(--space-6);
}

.doc-footer {
  padding: var(--space-4) var(--space-8) var(--space-6);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-3);
}

.doc-footer-right {
  display: flex;
  gap: var(--space-3);
}

/* ---- Form layout ---- */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
}

.mt-4 {
  margin-top: var(--space-4);
}

.summary-area {
  min-height: 140px;
}

@media (max-width: 640px) {
  .doc-header,
  .doc-section {
    padding-left: var(--space-5);
    padding-right: var(--space-5);
  }

  .doc-divider {
    margin-left: var(--space-5);
    margin-right: var(--space-5);
  }

  .doc-footer {
    padding-left: var(--space-5);
    padding-right: var(--space-5);
    flex-direction: column-reverse;
    align-items: stretch;
  }

  .doc-footer-right {
    flex-direction: column-reverse;
  }

  .doc-footer .btn {
    width: 100%;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<style>
/* ---- AI Optimizing Overlay (Teleported to body) ---- */
.ai-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(16, 24, 40, 0.5);
  backdrop-filter: blur(4px);
  animation: fadeIn 0.2s ease-out;
}

.ai-overlay-card {
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  padding: 44px 52px;
  text-align: center;
  box-shadow: var(--shadow-xl);
  animation: scaleIn 0.25s ease-out;
}

.ai-spinner {
  width: 48px;
  height: 48px;
  margin: 0 auto 20px;
  border: 4px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

.ai-overlay-text {
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 6px;
}

.ai-overlay-sub {
  font-size: 0.8125rem;
  color: var(--color-text-secondary);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to   { opacity: 1; }
}

@keyframes scaleIn {
  from { opacity: 0; transform: scale(0.94); }
  to   { opacity: 1; transform: scale(1); }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
