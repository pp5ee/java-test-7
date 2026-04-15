import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { taskService } from '../services'
import { Plus, CheckCircle2, Clock, Circle, Loader2, Calendar, Flag } from 'lucide-react'
import { format } from 'date-fns'

const STATUS_CONFIG = {
  TODO: { label: 'To Do', icon: Circle, color: 'text-gray-400' },
  IN_PROGRESS: { label: 'In Progress', icon: Clock, color: 'text-blue-500' },
  IN_REVIEW: { label: 'In Review', icon: Clock, color: 'text-yellow-500' },
  DONE: { label: 'Done', icon: CheckCircle2, color: 'text-green-500' },
  CANCELLED: { label: 'Cancelled', icon: Circle, color: 'text-red-400' },
}

const PRIORITY_COLORS = {
  LOW: 'bg-green-100 text-green-700',
  MEDIUM: 'bg-yellow-100 text-yellow-700',
  HIGH: 'bg-orange-100 text-orange-700',
  CRITICAL: 'bg-red-100 text-red-700',
}

function TaskRow({ task, onStatusChange }) {
  const StatusIcon = STATUS_CONFIG[task.status]?.icon || Circle
  const statusColor = STATUS_CONFIG[task.status]?.color || 'text-gray-400'

  return (
    <div className="flex items-center gap-4 p-4 border-b border-gray-50 hover:bg-gray-50 transition-colors">
      <button
        onClick={() => onStatusChange(task.id, task.status === 'DONE' ? 'TODO' : 'DONE')}
        className={`${statusColor} flex-shrink-0 hover:scale-110 transition-transform`}
        title={task.status === 'DONE' ? 'Mark as Todo' : 'Mark as Done'}
      >
        <StatusIcon size={20} />
      </button>

      <div className="flex-1 min-w-0">
        <p className={`text-sm font-medium ${task.status === 'DONE' ? 'line-through text-gray-400' : 'text-gray-900'}`}>
          {task.title}
        </p>
        {task.description && (
          <p className="text-xs text-gray-400 truncate mt-0.5">{task.description}</p>
        )}
      </div>

      <div className="flex items-center gap-3 flex-shrink-0">
        <span className={`badge text-xs ${PRIORITY_COLORS[task.priority]}`}>
          {task.priority?.toLowerCase()}
        </span>

        {task.dueDate && (
          <span className="text-xs text-gray-400 flex items-center gap-1 hidden sm:flex">
            <Calendar size={12} />
            {format(new Date(task.dueDate), 'MMM d')}
          </span>
        )}

        <span className={`text-xs hidden md:block ${
          task.status === 'DONE' ? 'badge-done' :
          task.status === 'IN_PROGRESS' ? 'badge-in-progress' :
          task.status === 'IN_REVIEW' ? 'badge-in-review' : 'badge-todo'
        } badge`}>
          {STATUS_CONFIG[task.status]?.label}
        </span>
      </div>
    </div>
  )
}

export default function MyTasksPage() {
  const [tab, setTab] = useState('assigned')
  const [page, setPage] = useState(0)
  const qc = useQueryClient()

  const assignedQuery = useQuery({
    queryKey: ['myTasks', page],
    queryFn: () => taskService.getMyTasks({ page, size: 20 }),
    enabled: tab === 'assigned',
  })

  const createdQuery = useQuery({
    queryKey: ['assignedByMe', page],
    queryFn: () => taskService.getAssignedByMe({ page, size: 20 }),
    enabled: tab === 'created',
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, status }) => taskService.update(id, { status }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['myTasks'] })
      qc.invalidateQueries({ queryKey: ['assignedByMe'] })
      qc.invalidateQueries({ queryKey: ['kanban'] })
    },
  })

  const activeQuery = tab === 'assigned' ? assignedQuery : createdQuery
  const pageData = activeQuery.data?.data?.data

  return (
    <div className="space-y-5">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">My Tasks</h1>
        <p className="text-sm text-gray-500 mt-1">Track your personal workload</p>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-gray-200">
        {[
          { key: 'assigned', label: 'Assigned to Me' },
          { key: 'created', label: 'Assigned by Me' },
        ].map(t => (
          <button
            key={t.key}
            onClick={() => { setTab(t.key); setPage(0) }}
            className={`px-4 py-2.5 text-sm font-medium border-b-2 transition-colors ${
              tab === t.key
                ? 'border-primary-500 text-primary-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            {t.label}
            {tab === t.key && pageData && (
              <span className="ml-2 bg-primary-100 text-primary-700 text-xs px-1.5 py-0.5 rounded-full">
                {pageData.totalElements}
              </span>
            )}
          </button>
        ))}
      </div>

      {/* Task list */}
      <div className="card p-0 overflow-hidden">
        {activeQuery.isLoading ? (
          <div className="flex items-center justify-center py-16">
            <Loader2 className="animate-spin text-primary-600" size={28} />
          </div>
        ) : pageData?.content?.length === 0 ? (
          <div className="text-center py-16 text-gray-400">
            <CheckCircle2 size={40} className="mx-auto mb-3 text-gray-200" />
            <p>No tasks found</p>
          </div>
        ) : (
          pageData?.content?.map(task => (
            <TaskRow
              key={task.id}
              task={task}
              onStatusChange={(id, status) => updateMutation.mutate({ id, status })}
            />
          ))
        )}

        {/* Pagination */}
        {pageData && pageData.totalPages > 1 && (
          <div className="flex items-center justify-center gap-3 p-4 border-t border-gray-100">
            <button
              onClick={() => setPage(p => p - 1)}
              disabled={page === 0}
              className="btn-secondary text-xs disabled:opacity-40"
            >
              Previous
            </button>
            <span className="text-xs text-gray-500">
              {page + 1} / {pageData.totalPages}
            </span>
            <button
              onClick={() => setPage(p => p + 1)}
              disabled={pageData.last}
              className="btn-secondary text-xs disabled:opacity-40"
            >
              Next
            </button>
          </div>
        )}
      </div>
    </div>
  )
}
