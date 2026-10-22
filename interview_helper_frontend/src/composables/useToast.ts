import { ref } from 'vue'

export type ToastType = 'error' | 'success' | 'info'

interface ToastState {
  message: string
  type: ToastType
  id: number
}

const toasts = ref<ToastState[]>([])
let nextId = 0

export function useToast() {
  function show(message: string, type: ToastType = 'info', duration = 3000) {
    const id = nextId++
    toasts.value.push({ message, type, id })
    if (duration > 0) {
      setTimeout(() => dismiss(id), duration)
    }
  }

  function success(message: string) {
    show(message, 'success')
  }

  function error(message: string) {
    show(message, 'error')
  }

  function dismiss(id: number) {
    toasts.value = toasts.value.filter((t) => t.id !== id)
  }

  return { toasts, show, success, error, dismiss }
}
