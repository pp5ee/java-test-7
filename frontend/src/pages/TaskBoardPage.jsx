import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { taskService, userService } from '../services'
import { Plus, X, Calendar, User, Flag, Loader2 } from 'lucide-react'
import { useAuthStore } from '../store/authStore'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { format } from 'date-fns'

const COLUMNS = [
  { key: 'todo', label: 'To Do', badge: 'badge-todo' },
  { key: 'inProgress', label: 'In Progress', badge: 'badge-in-progress' },
  { key: 'inReview', label: 'In Review', badge: 'badge-in-review' },
  { key: 'done', label: 'Done', badge: 'badge-done' },
]

const PRIORITY_COLORS = {
  LOW: 'text-green-500',
  MEDIUM: 'text-yellow-500',
  HIGH: 'text-orange-500',
  CRITICAL: 'text-red-500',
}

function TaskCard({ task, onClick }) {
  return (
    <div className="task-card" onClick={() => onClick(task)}>
      <div className="flex items-start justify-between gap-2 mb-2">
        <p className="text-sm font-medium text-gray-900 leading-snug">{task.title}</p>
        <Flag size={14} className={`flex-shrink-0 mt-0.5 ${PRIORITY_COLORS[task.priority] || ''}`} />
      </div>
      {task.description && (
        <p className="text-xs text-gray-500 line-clamp-2 mb-2">{task.description}</p>
      )}
      <div className="flex items-center justify-between mt-3 text-xs text-gray-400">
        <div className="flex items-center gap-1">
          {task.assignee ? (
            <>
              <User size={12} />
              <span>{task.assignee.fullName?.split(' ')[0]}</span>
            </>
          ) : <span className="text-gray-300">Unassigned</span>}
        </div>
        {task.dueDate && (
          <div className="flex items-center gap-1">
            <Calendar size={12} />
            <span>{format(new Date(task.dueDate), 'MMM d')}</span>
          </div>
        )}
      </div>
      {task.subTasks?.length > 0 && (
        <p className="text-xs text-gray-400 mt-1">↳ {task.subTasks.length} subtask(s)</p>
      )}
    </div>
  )
}

function CreateTaskModal({ onClose, parentId }) {
  const qc = useQueryClient()
  const { data: usersData } = useQuery({
    queryKey: ['usersAll'],
    queryFn: () => userService.getAll({ size: 100 }),
  })
  const users = usersData?.data?.data?.content || []

  const { register, handleSubmit, formState: { errors } } = useForm()

  const mutation = useMutation({
    mutationFn: (data) => taskService.create(data),
    onSuccess: () => {
      toast.success('Task created!')
      qc.invalidateQueries({ queryKey: ['kanban'] })
      onClose()
    },
  })

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md">
        <div className="flex items-center justify-between p-6 border-b">
          <h2 className="font-semibold text-gray-900">Create Task</h2>
          <button onClick={onClose} className="btn-ghost p-1"><X size={18} /></button>
        </div>
        <form
          onSubmit={handleSubmit(d => mutation.mutate({ ...d, parentTaskId: parentId }))}
          className="p-6 space-y-4"
        >
          <div>
            <label className="label">Title *</label>
            <input {...register('title', { required: 'Required' })} className="input" placeholder="Task title" />
            {errors.title && <p className="text-red-500 text-xs mt-1">{errors.title.message}</p>}
          </div>
          <div>
            <label className="label">Description</label>
            <textarea {...register('description')} className="input resize-none" rows={3} placeholder="Describe the task..." />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Priority</label>
              <select {...register('priority')} className="input">
                <option value="LOW">Low</option>
                <option value="MEDIUM" selected>Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">Critical</option>
              </select>
            </div>
            <div>
              <label className="label">Status</label>
              <select {...register('status')} className="input">
                <option value="TODO">To Do</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="IN_REVIEW">In Review</option>
              </select>
            </div>
          </div>
          <div>
            <label className="label">Assign To</label>
            <select {...register('assigneeId')} className="input">
              <option value="">Unassigned</option>
              {users.map(u => (
                <option key={u.id} value={u.id}>{u.fullName}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Due Date</label>
            <input type="date" {...register('dueDate')} className="input" />
          </div>
          <div className="flex gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn-secondary flex-1">Cancel</button>
            <button type="submit" disabled={mutation.isPending} className="btn-primary flex-1">
              {mutation.isPending ? 'Creating...' : 'Create Task'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

function TaskDetailModal({ task, onClose }) {
  const qc = useQueryClient()
  const { data: usersData } = useQuery({
    queryKey: ['usersAll'],
    queryFn: () => userService.getAll({ size: 100 }),
  })
  const users = usersData?.data?.data?.content || []

  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => taskService.update(id, data),
    onSuccess: () => {
      toast.success('Task updated')
      qc.invalidateQueries({ queryKey: ['kanban'] })
      onClose()
    },
  })

  const deleteMutation = useMutation({
    mutationFn: (id) => taskService.delete(id),
    onSuccess: () => {
      toast.success('Task deleted')
      qc.invalidateQueries({ queryKey: ['kanban'] })
      onClose()
    },
  })

  const { register, handleSubmit } = useForm({
    defaultValues: {
      title: task.title,
      description: task.description || '',
      status: task.status,
      priority: task.priority,
      assigneeId: task.assignee?.id || '',
      dueDate: task.dueDate || '',
    }
  })

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between p-6 border-b sticky top-0 bg-white">
          <h2 className="font-semibold text-gray-900">Edit Task</h2>
          <button onClick={onClose} className="btn-ghost p-1"><X size={18} /></button>
        </div>
        <form
          onSubmit={handleSubmit(d => updateMutation.mutate({ id: task.id, data: d }))}
          className="p-6 space-y-4"
        >
          <div>
            <label className="label">Title</label>
            <input {...register('title')} className="input" />
          </div>
          <div>
            <label className="label">Description</label>
            <textarea {...register('description')} className="input resize-none" rows={3} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label">Status</label>
              <select {...register('status')} className="input">
                <option value="TODO">To Do</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="IN_REVIEW">In Review</option>
                <option value="DONE">Done</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>
            <div>
              <label className="label">Priority</label>
              <select {...register('priority')} className="input">
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">Critical</option>
              </select>
            </div>
          </div>
          <div>
            <label className="label">Assigned To</label>
            <select {...register('assigneeId')} className="input">
              <option value="">Unassigned</option>
              {users.map(u => (
                <option key={u.id} value={u.id}>{u.fullName}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Due Date</label>
            <input type="date" {...register('dueDate')} className="input" />
          </div>
          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={() => window.confirm('Delete this task?') && deleteMutation.mutate(task.id)}
              className="btn-danger flex-1"
              disabled={deleteMutation.isPending}
            >
              Delete
            </button>
            <button type="submit" disabled={updateMutation.isPending} className="btn-primary flex-1">
              {updateMutation.isPending ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default function TaskBoardPage() {
  const [showCreate, setShowCreate] = useState(false)
  const [selectedTask, setSelectedTask] = useState(null)

  const { data, isLoading } = useQuery({
    queryKey: ['kanban'],
    queryFn: () => taskService.getKanban(),
    refetchInterval: 30000,
  })

  const board = data?.data?.data || {}

  return (
    <div className="space-y-5 h-full flex flex-col">
      <div className="flex items-center justify-between flex-shrink-0">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Task Board</h1>
          <p className="text-sm text-gray-500 mt-1">Kanban view of your tasks</p>
        </div>
        <button onClick={() => setShowCreate(true)} className="btn-primary">
          <Plus size={16} />
          New Task
        </button>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center flex-1">
          <Loader2 className="animate-spin text-primary-600" size={32} />
        </div>
      ) : (
        <div className="flex gap-4 overflow-x-auto pb-4 flex-1">
          {COLUMNS.map(col => {
            const tasks = board[col.key] || []
            return (
              <div key={col.key} className="kanban-column">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-2">
                    <h3 className="font-semibold text-gray-700 text-sm">{col.label}</h3>
                    <span className={`badge text-xs ${col.badge}`}>{tasks.length}</span>
                  </div>
                </div>
                <div className="space-y-3">
                  {tasks.map(task => (
                    <TaskCard key={task.id} task={task} onClick={setSelectedTask} />
                  ))}
                  {tasks.length === 0 && (
                    <p className="text-xs text-gray-400 text-center py-6">No tasks here</p>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      )}

      {showCreate && <CreateTaskModal onClose={() => setShowCreate(false)} />}
      {selectedTask && <TaskDetailModal task={selectedTask} onClose={() => setSelectedTask(null)} />}
    </div>
  )
}
