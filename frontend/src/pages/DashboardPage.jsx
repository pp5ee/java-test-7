import { useQuery } from '@tanstack/react-query'
import { Users, CheckSquare, Clock, TrendingUp } from 'lucide-react'
import { useAuthStore } from '../store/authStore'
import { userService, taskService } from '../services'
import { Link } from 'react-router-dom'

function StatCard({ icon: Icon, label, value, color, to }) {
  const content = (
    <div className={`card flex items-center gap-4 hover:shadow-md transition-shadow ${to ? 'cursor-pointer' : ''}`}>
      <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${color}`}>
        <Icon className="text-white" size={22} />
      </div>
      <div>
        <p className="text-sm text-gray-500">{label}</p>
        <p className="text-2xl font-bold text-gray-900">{value ?? '—'}</p>
      </div>
    </div>
  )
  return to ? <Link to={to}>{content}</Link> : content
}

export default function DashboardPage() {
  const { user } = useAuthStore()

  const { data: usersData } = useQuery({
    queryKey: ['users', { size: 1 }],
    queryFn: () => userService.getAll({ size: 1 }),
  })

  const { data: myTasksData } = useQuery({
    queryKey: ['myTasks', { size: 1 }],
    queryFn: () => taskService.getMyTasks({ size: 1 }),
  })

  const { data: kanbanData } = useQuery({
    queryKey: ['kanban'],
    queryFn: () => taskService.getKanban(),
  })

  const board = kanbanData?.data?.data
  const totalUsers = usersData?.data?.data?.totalElements
  const myTasksCount = myTasksData?.data?.data?.totalElements
  const inProgressCount = board?.inProgress?.length ?? 0
  const doneCount = board?.done?.length ?? 0

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          Good morning, {user?.firstName}! 👋
        </h1>
        <p className="text-gray-500 text-sm mt-1">
          Here's what's happening in your organization today.
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          icon={Users}
          label="Total Employees"
          value={totalUsers}
          color="bg-blue-500"
          to="/users"
        />
        <StatCard
          icon={CheckSquare}
          label="My Tasks"
          value={myTasksCount}
          color="bg-primary-500"
          to="/my-tasks"
        />
        <StatCard
          icon={Clock}
          label="In Progress"
          value={inProgressCount}
          color="bg-yellow-500"
          to="/tasks"
        />
        <StatCard
          icon={TrendingUp}
          label="Completed"
          value={doneCount}
          color="bg-green-500"
          to="/tasks"
        />
      </div>

      {/* Quick Task Preview */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-base font-semibold text-gray-900 mb-4">My Open Tasks</h2>
          {board?.todo?.length === 0 && board?.inProgress?.length === 0 ? (
            <p className="text-sm text-gray-400 text-center py-6">No open tasks</p>
          ) : (
            <ul className="space-y-3">
              {[...(board?.inProgress || []), ...(board?.todo || [])].slice(0, 5).map(task => (
                <li key={task.id} className="flex items-center gap-3 text-sm">
                  <span className={`badge ${task.status === 'IN_PROGRESS' ? 'badge-in-progress' : 'badge-todo'}`}>
                    {task.status === 'IN_PROGRESS' ? 'In Progress' : 'To Do'}
                  </span>
                  <span className="text-gray-700 truncate">{task.title}</span>
                </li>
              ))}
            </ul>
          )}
          <Link to="/tasks" className="btn-secondary w-full mt-4 text-xs">View Kanban Board</Link>
        </div>

        <div className="card">
          <h2 className="text-base font-semibold text-gray-900 mb-4">My Profile</h2>
          <div className="space-y-3 text-sm">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-full bg-gradient-to-br from-primary-500 to-purple-600 flex items-center justify-center text-white font-bold">
                {user?.firstName?.[0]}{user?.lastName?.[0]}
              </div>
              <div>
                <p className="font-semibold text-gray-900">{user?.fullName}</p>
                <p className="text-gray-500">{user?.role?.displayName}</p>
              </div>
            </div>
            {[
              ['Email', user?.email],
              ['Department', user?.department || '—'],
              ['Position', user?.position || '—'],
            ].map(([k, v]) => (
              <div key={k} className="flex justify-between">
                <span className="text-gray-500">{k}</span>
                <span className="text-gray-900 font-medium truncate max-w-[200px]">{v}</span>
              </div>
            ))}
          </div>
          <Link to="/profile" className="btn-secondary w-full mt-4 text-xs">Edit Profile</Link>
        </div>
      </div>
    </div>
  )
}
