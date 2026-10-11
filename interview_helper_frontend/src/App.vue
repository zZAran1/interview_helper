<script setup lang="ts">
import GlobalToast from '@/components/GlobalToast.vue'
import GlobalConfirm from '@/components/GlobalConfirm.vue'
</script>

<template>
  <RouterView v-slot="{ Component }">
    <Transition name="page" mode="out-in">
      <component :is="Component" />
    </Transition>
  </RouterView>
  <GlobalToast />
  <GlobalConfirm />
</template>

<style>
.page-enter-active {
  transition: opacity var(--duration-normal) var(--ease-out),
              transform var(--duration-normal) var(--ease-out);
}

.page-leave-active {
  transition: opacity var(--duration-fast) var(--ease-in);
}

.page-enter-from {
  opacity: 0;
  transform: translateY(4px);
}

.page-leave-to {
  opacity: 0;
}
</style>
