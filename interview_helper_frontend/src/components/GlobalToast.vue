<script setup lang="ts">
import { useToast } from '@/composables/useToast'

const { toasts, dismiss } = useToast()
</script>

<template>
  <Teleport to="body">
    <div class="toast-container" aria-live="polite">
      <TransitionGroup name="toast">
        <div
          v-for="t in toasts"
          :key="t.id"
          :class="['toast', t.type]"
          role="status"
          @click="dismiss(t.id)"
        >
          <span class="toast-icon" aria-hidden="true">
            <svg v-if="t.type === 'success'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 6L9 17l-5-5"></path>
            </svg>
            <svg v-else-if="t.type === 'error'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="9"></circle>
              <line x1="9" y1="9" x2="15" y2="15"></line>
              <line x1="15" y1="9" x2="9" y2="15"></line>
            </svg>
            <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="9"></circle>
              <line x1="12" y1="11" x2="12" y2="16"></line>
              <line x1="12" y1="8" x2="12.01" y2="8"></line>
            </svg>
          </span>
          <span class="toast-msg">{{ t.message }}</span>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10001;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: 11px 18px;
  border-radius: var(--radius-md);
  background: var(--color-surface);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--color-border-light);
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--color-text);
  white-space: nowrap;
  cursor: pointer;
  pointer-events: auto;
  max-width: min(90vw, 480px);
  overflow: hidden;
}

.toast-msg {
  overflow: hidden;
  text-overflow: ellipsis;
}

.toast.success .toast-icon { color: var(--color-success-text); }
.toast.error .toast-icon { color: var(--color-error-text); }
.toast.info .toast-icon { color: var(--color-primary); }

.toast-enter-active {
  transition: opacity var(--duration-normal) var(--ease-out),
              transform var(--duration-normal) var(--ease-out);
}

.toast-leave-active {
  transition: opacity var(--duration-fast) var(--ease-in),
              transform var(--duration-fast) var(--ease-in);
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
