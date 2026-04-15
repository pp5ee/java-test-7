import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Search, UserX, ChevronLeft, ChevronRight, Loader2 } from 'lucide-react'
import { userService } from '../services'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'

function Avatar({ user, size = 'sm' }) {
  const s = size === 'sm' ? 'w-8 h-8 text-xs' : 'w-10 h-10 text-sm'
  return (
    <div className={`${s} rounded-full bg-gradient-to-br from-primary-500 to-purple-600 flex items-center justify-center text-white font-semibold flex-shrink-0`}>
      {user.firstName?.[0]}{user.lastName?.[0]}
    </div>
  )
}

function RoleBadge({ role }) {
  const colors = {
    1: 'bg-red-100 text-red-700',
    2: 'bg-orange-100 text-orange-700',
    3: 'bg-yellow-100 text-yellow-700',
    4: 'bg-blue-100 text-blue-700',
    5: 'bg-green-100 text-green-700',
    6: 'bg-teal-100 text-teal-700',
    7: 'bg-gray-100 text-gray-700',
  }
  return (
    <span className={`badge ${colors[role.level] || 'bg-gray-100 text-gray-600'}`}>
      {role.displayName}
    </span>
  )
}

export default function UsersPage() {
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(0)
  const { isAdmin } = useAuthStore()
  const qc = useQueryClient()

  const { data, isLoading } = useQuery({
    queryKey: ['users', { search, page }],
    queryFn: () => userService.getAll({ search, page, size: 15 }),
  })

  const deleteMutation = useMutation({
    mutationFn: (id) => userService.delete(id),
    onSuccess: () => {
      toast.success('User removed successfully')
      qc.invalidateQueries({ queryKey: ['users'] })
    },
  })

  const handleDelete = (user) => {
    if (window.confirm(`Remove ${user.fullName}? This action is irreversible.`)) {
      deleteMutation.mutate(user.id)
    }
  }

  const pageData = data?.data?.data

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Employees</h1>
        <span className="text-sm text-gray-500">
          {pageData?.totalElements ?? 0} total
        </span>
      </div>

      {/* Search */}
      <div className="relative">
        <Search className="absolute left-3 top-2.5 text-gray-400" size={16} />
        <input
          type="text"
          placeholder="Search by name or email..."
          value={search}
          onChange={e => { setSearch(e.target.value); setPage(0) }}
          className="input pl-9"
        />
      </div>

      {/* Table */}
      <div className="card p-0 overflow-hidden">
        {isLoading ? (
          <div className="flex items-center justify-center py-16">
            <Loader2 className="animate-spin text-primary-600" size={28} />
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-100">
                  <th className="text-left px-4 py-3 font-medium text-gray-600">Employee</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600 hidden md:table-cell">Role</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600 hidden lg:table-cell">Department</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600 hidden lg:table-cell">Manager</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">Status</th>
                  {isAdmin() && <th className="px-4 py-3" />}
                </tr>
              </thead>
              <tbody>
                {pageData?.content?.map(user => (
                  <tr key={user.id} className="border-b border-gray-50 hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        <Avatar user={user} />
                        <div>
                          <p className="font-medium text-gray-900">{user.fullName}</p>
                          <p className="text-gray-500 text-xs">{user.email}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-4 py-3 hidden md:table-cell">
                      {user.role && <RoleBadge role={user.role} />}
                    </td>
                    <td className="px-4 py-3 hidden lg:table-cell text-gray-600">
                      {user.department || '—'}
                    </td>
                    <td className="px-4 py-3 hidden lg:table-cell text-gray-600">
                      {user.managerName || '—'}
                    </td>
                    <td className="px-4 py-3">
                      <span className={`badge ${user.enabled ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}>
                        {user.enabled ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    {isAdmin() && (
                      <td className="px-4 py-3 text-right">
                        <button
                          onClick={() => handleDelete(user)}
                          disabled={deleteMutation.isPending}
                          className="text-red-400 hover:text-red-600 transition-colors"
                          title="Remove user"
                        >
                          <UserX size={16} />
                        </button>
                      </td>
                    )}
                  </tr>
                ))}
                {!pageData?.content?.length && (
                  <tr>
                    <td colSpan={6} className="text-center py-12 text-gray-400">
                      No employees found
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        {pageData && pageData.totalPages > 1 && (
          <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100">
            <p className="text-xs text-gray-500">
              Page {page + 1} of {pageData.totalPages}
            </p>
            <div className="flex gap-2">
              <button
                onClick={() => setPage(p => p - 1)}
                disabled={page === 0}
                className="btn-secondary p-1.5 disabled:opacity-40"
              >
                <ChevronLeft size={14} />
              </button>
              <button
                onClick={() => setPage(p => p + 1)}
                disabled={pageData.last}
                className="btn-secondary p-1.5 disabled:opacity-40"
              >
                <ChevronRight size={14} />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
