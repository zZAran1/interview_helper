import { ref } from 'vue'

interface ConfirmState {
  message: string
  resolve: (value: boolean) => void
}

const state = ref<ConfirmState | null>(null)

export function useConfirm() {
  function confirm(message: string): Promise<boolean> {
    return new Promise((resolve) => {
      state.value = { message, resolve }
    })
  }

  function handleResult(result: boolean) {
    state.value?.resolve(result)
    state.value = null
  }

  return { confirmState: state, confirm, handleResult }
}
