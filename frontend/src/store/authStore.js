import { create } from 'zustand'
import { persist } from 'zustand/middleware'

export const useAuthStore = create(
  persist(
    (set, get) => ({
      token: null,
      refreshToken: null,
      user: null,

      setAuth: (token, refreshToken, user) => set({ token, refreshToken, user }),

      setUser: (user) => set({ user }),

      logout: () => set({ token: null, refreshToken: null, user: null }),

      isAdmin: () => {
        const user = get().user
        if (!user?.role) return false
        return user.role.level <= 2
      },

      isManagerOrAbove: () => {
        const user = get().user
        if (!user?.role) return false
        return user.role.level <= 3
      },
    }),
    {
      name: 'hr-auth',
      partialize: (state) => ({
        token: state.token,
        refreshToken: state.refreshToken,
        user: state.user,
      }),
    }
  )
)
