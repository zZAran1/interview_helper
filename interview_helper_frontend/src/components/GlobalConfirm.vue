<script setup lang="ts">
import { useConfirm } from '@/composables/useConfirm'

const { confirmState, handleResult } = useConfirm()
</script>

<template>
  <Teleport to="body">
    <Transition name="confirm-fade">
      <div v-if="confirmState" class="confirm-overlay" @click.self="handleResult(false)">
        <div class="confirm-card">
          <div class="confirm-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
          </div>
          <p class="confirm-message">{{ confirmState.message }}</p>
          <div class="confirm-actions">
            <button class="btn btn-outline" @click="handleResult(false)">取消</button>
            <button class="btn btn-primary" @click="handleResult(true)">确定</button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.confirm-overlay {
  position: fixed;
  inset: 0;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(4px);
}

.confirm-card {
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  padding: 36px 40px 28px;
  text-align: center;
  max-width: 380px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
}

.confirm-icon {
  display: flex;
  justify-content: center;
  color: var(--color-warning, #f59e0b);
  margin-bottom: 16px;
}

.confirm-message {
  font-size: 0.95rem;
  color: var(--color-text);
  line-height: 1.6;
  margin-bottom: 24px;
}

.confirm-actions {
  display: flex;
  gap: var(--space-3);
  justify-content: center;
}

/* ---- transitions ---- */
.confirm-fade-enter-active {
  transition: opacity 0.2s ease-out;
}
.confirm-fade-enter-active .confirm-card {
  transition: opacity 0.2s ease-out, transform 0.2s ease-out;
}
.confirm-fade-leave-active {
  transition: opacity 0.15s ease-in;
}
.confirm-fade-leave-active .confirm-card {
  transition: opacity 0.15s ease-in, transform 0.15s ease-in;
}

.confirm-fade-enter-from {
  opacity: 0;
}
.confirm-fade-enter-from .confirm-card {
  opacity: 0;
  transform: scale(0.92);
}
.confirm-fade-leave-to {
  opacity: 0;
}
.confirm-fade-leave-to .confirm-card {
  opacity: 0;
  transform: scale(0.92);
}
</style>
