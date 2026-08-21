<script setup lang="ts">
export interface FieldDef {
  key: string
  label: string
  type?: 'text' | 'textarea' | 'date' | 'select'
  placeholder?: string
  required?: boolean
  rows?: number
}

import { useConfirm } from '@/composables/useConfirm'

const { confirm } = useConfirm()

const props = defineProps<{
  title: string
  items: Record<string, any>[]
  fields: FieldDef[]
  loading: boolean
}>()

const emit = defineEmits<{
  create: [data: Record<string, any>]
  update: [data: Record<string, any>]
  delete: [data: Record<string, any>]
}>()

function cardTitle(item: Record<string, any>): string {
  const first = props.fields.find((f) => f.required)
  if (first) {
    const v = item[first.key]
    if (typeof v === 'string' && v.trim()) return v.trim()
  }
  for (const f of props.fields) {
    const v = item[f.key]
    if (typeof v === 'string' && v.trim() && f.type !== 'date') return v.trim()
  }
  return '未命名'
}

function handleAdd() {
  const empty: Record<string, any> = {}
  props.fields.forEach((f) => {
    empty[f.key] = ''
  })
  emit('create', empty)
}

function handleFieldChange(item: Record<string, any>, idx: number) {
  /* Only emit update for items that already exist on the server (have an id).
     New items without an id are v-model-bound directly and sent via create on save. */
  if (item.id != null) {
    emit('update', { ...item, _idx: idx })
  }
}

async function handleDelete(item: Record<string, any>, idx: number) {
  const ok = await confirm(`确定删除「${cardTitle(item)}」吗？`)
  if (!ok) return
  emit('delete', { ...item, _idx: idx })
}
</script>

<template>
  <div class="section">
    <div class="section-head">
      <h4 class="section-title">{{ title }}</h4>
      <button class="btn btn-outline btn-sm" @click="handleAdd" :disabled="loading">
        <span class="add-plus" aria-hidden="true">+</span> 添加
      </button>
    </div>

    <!-- Empty -->
    <div v-if="!loading && items.length === 0" class="empty-hint">
      暂无记录，点击"添加"开始填写
    </div>

    <!-- Items as inline editable forms -->
    <div v-for="(item, idx) in items" :key="item.id || idx" class="item-card">
      <div class="item-head">
        <span class="item-title">{{ cardTitle(item) }}</span>
        <button class="del-btn" @click="handleDelete(item, idx)">删除</button>
      </div>

      <div class="form-grid">
        <div v-for="f in fields" :key="f.key" :class="['field', { 'field-full': f.type === 'textarea' }]">
          <label class="field-label">
            {{ f.label }}
            <span v-if="f.required" class="required-mark">*</span>
          </label>
          <input
            v-if="f.type === 'date'"
            type="date"
            v-model="item[f.key]"
            class="input"
            @change="handleFieldChange(item, idx)"
          />
          <textarea
            v-else-if="f.type === 'textarea'"
            v-model="item[f.key]"
            class="textarea"
            :placeholder="f.placeholder || ''"
            :rows="f.rows || 3"
            @change="handleFieldChange(item, idx)"
          ></textarea>
          <input
            v-else
            :type="f.type || 'text'"
            v-model="item[f.key]"
            class="input"
            :placeholder="f.placeholder || ''"
            @change="handleFieldChange(item, idx)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.section {
  margin-top: var(--space-2);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}

.section-title {
  font-size: 0.8125rem;
  font-weight: 700;
  color: var(--color-text-secondary);
  letter-spacing: 0.02em;
}

.add-plus {
  font-size: 1rem;
  line-height: 1;
}

.empty-hint {
  padding: var(--space-5) 0;
  font-size: 0.85rem;
  color: var(--color-text-muted);
}

/* ---- Item card ---- */
.item-card {
  padding: var(--space-5);
  margin-bottom: var(--space-4);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  transition: border-color var(--duration-fast) var(--ease-out),
              box-shadow var(--duration-fast) var(--ease-out);
}

.item-card:hover {
  border-color: var(--color-border-strong);
  box-shadow: var(--shadow-xs);
}

.item-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
  padding-bottom: var(--space-3);
  border-bottom: 1px solid var(--color-border-light);
}

.item-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.del-btn {
  padding: 4px 12px;
  border: 1px solid var(--color-error-border);
  border-radius: var(--radius-sm);
  background: transparent;
  color: var(--color-error-text);
  font-size: 0.75rem;
  font-weight: 500;
  flex-shrink: 0;
  cursor: pointer;
  transition: background var(--duration-fast) var(--ease-out);
}

.del-btn:hover {
  background: var(--color-error-bg);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
}

.field-full {
  grid-column: 1 / -1;
}

@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
