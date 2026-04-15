import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { userService, roleService } from '../services'
import { useAuthStore } from '../store/authStore'
import { Save, Loader2, Camera } from 'lucide-react'
import toast from 'react-hot-toast'

export default function ProfilePage() {
  const { user: storeUser, setUser } = useAuthStore()
  const qc = useQueryClient()

  const { data, isLoading } = useQuery({
    queryKey: ['me'],
    queryFn: () => userService.getMe(),
  })

  const { data: rolesData } = useQuery({
    queryKey: ['roles'],
    queryFn: () => roleService.getAll(),
  })

  const user = data?.data?.data || storeUser
  const roles = rolesData?.data?.data || []

  const { register, handleSubmit, formState: { isDirty } } = useForm({
    values: {
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      phone: user?.phone || '',
      department: user?.department || '',
      position: user?.position || '',
    }
  })

  const mutation = useMutation({
    mutationFn: (data) => userService.update(user.id, data),
    onSuccess: (res) => {
      const updated = res.data.data
      setUser(updated)
      qc.invalidateQueries({ queryKey: ['me'] })
      toast.success('Profile updated successfully!')
    },
  })

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="animate-spin text-primary-600" size={28} />
      </div>
    )
  }

  return (
    <div className="max-w-2xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">My Profile</h1>
        <p className="text-sm text-gray-500 mt-1">Manage your personal information</p>
      </div>

      {/* Avatar section */}
      <div className="card flex items-center gap-5">
        <div className="relative">
          <div className="w-20 h-20 rounded-full bg-gradient-to-br from-primary-500 to-purple-600 flex items-center justify-center text-white font-bold text-2xl">
            {user?.firstName?.[0]}{user?.lastName?.[0]}
          </div>
          <button className="absolute -bottom-1 -right-1 w-7 h-7 bg-white border border-gray-200 rounded-full flex items-center justify-center text-gray-500 hover:text-gray-700 shadow-sm">
            <Camera size={13} />
          </button>
        </div>
        <div>
          <p className="text-lg font-semibold text-gray-900">{user?.fullName}</p>
          <p className="text-sm text-primary-600">{user?.role?.displayName}</p>
          <p className="text-xs text-gray-400 mt-1">{user?.email}</p>
        </div>
        <div className="ml-auto hidden sm:block">
          <span className={`badge ${user?.enabled ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}>
            {user?.enabled ? 'Active' : 'Inactive'}
          </span>
        </div>
      </div>

      {/* Edit form */}
      <div className="card">
        <h2 className="font-semibold text-gray-900 mb-5">Personal Information</h2>
        <form onSubmit={handleSubmit(d => mutation.mutate(d))} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="label">First Name</label>
              <input {...register('firstName', { required: true })} className="input" />
            </div>
            <div>
              <label className="label">Last Name</label>
              <input {...register('lastName', { required: true })} className="input" />
            </div>
          </div>

          <div>
            <label className="label">Phone Number</label>
            <input {...register('phone')} className="input" placeholder="+1 234 567 8901" />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="label">Department</label>
              <input {...register('department')} className="input" placeholder="Engineering" />
            </div>
            <div>
              <label className="label">Position / Title</label>
              <input {...register('position')} className="input" placeholder="Senior Developer" />
            </div>
          </div>

          {/* Read-only fields */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-4 border-t border-gray-100">
            <div>
              <label className="label text-gray-400">Role (Admin only)</label>
              <input
                value={user?.role?.displayName || ''}
                disabled
                className="input bg-gray-50"
              />
            </div>
            <div>
              <label className="label text-gray-400">Manager</label>
              <input
                value={user?.managerName || 'No manager'}
                disabled
                className="input bg-gray-50"
              />
            </div>
          </div>

          <div className="flex justify-end pt-2">
            <button
              type="submit"
              disabled={mutation.isPending || !isDirty}
              className="btn-primary"
            >
              <Save size={16} />
              {mutation.isPending ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>

      {/* Account info */}
      <div className="card">
        <h2 className="font-semibold text-gray-900 mb-4">Account Information</h2>
        <div className="space-y-3 text-sm">
          {[
            ['Email', user?.email],
            ['Member Since', user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : '—'],
            ['Last Login', user?.lastLoginAt ? new Date(user.lastLoginAt).toLocaleString() : '—'],
            ['Email Verified', user?.emailVerified ? 'Yes' : 'No'],
          ].map(([k, v]) => (
            <div key={k} className="flex justify-between py-2 border-b border-gray-50 last:border-0">
              <span className="text-gray-500">{k}</span>
              <span className="text-gray-900 font-medium">{v}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
