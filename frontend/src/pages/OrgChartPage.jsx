import { useQuery } from '@tanstack/react-query'
import { userService } from '../services'
import { Loader2 } from 'lucide-react'

function OrgNode({ user, allUsers, depth = 0 }) {
  const reports = allUsers.filter(u => u.managerId === user.id)

  return (
    <div className="flex flex-col items-center">
      {/* Card */}
      <div className={`
        bg-white border-2 rounded-xl p-3 text-center shadow-sm w-40 flex-shrink-0
        ${depth === 0 ? 'border-primary-500' : depth === 1 ? 'border-purple-400' : 'border-gray-200'}
      `}>
        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-primary-500 to-purple-600 flex items-center justify-center text-white font-bold text-sm mx-auto mb-2">
          {user.firstName?.[0]}{user.lastName?.[0]}
        </div>
        <p className="text-xs font-semibold text-gray-900 leading-tight">{user.fullName}</p>
        <p className="text-xs text-primary-600 mt-0.5">{user.role?.displayName}</p>
        {user.department && <p className="text-xs text-gray-400 mt-0.5 truncate">{user.department}</p>}
      </div>

      {/* Children */}
      {reports.length > 0 && (
        <>
          <div className="w-px h-6 bg-gray-300" />
          <div className="flex items-start gap-6 relative">
            {/* Horizontal connector */}
            {reports.length > 1 && (
              <div className="absolute top-0 left-0 right-0 h-px bg-gray-300"
                   style={{ left: '20px', right: '20px' }} />
            )}
            {reports.map(child => (
              <div key={child.id} className="flex flex-col items-center">
                <div className="w-px h-6 bg-gray-300" />
                <OrgNode user={child} allUsers={allUsers} depth={depth + 1} />
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  )
}

export default function OrgChartPage() {
  const { data, isLoading } = useQuery({
    queryKey: ['orgChart'],
    queryFn: () => userService.getOrgChart(),
  })

  const users = data?.data?.data || []
  const roots = users.filter(u => !u.managerId)

  return (
    <div className="space-y-5">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Organization Chart</h1>
        <p className="text-sm text-gray-500 mt-1">Visual hierarchy of your organization</p>
      </div>

      <div className="card overflow-auto">
        {isLoading ? (
          <div className="flex items-center justify-center py-16">
            <Loader2 className="animate-spin text-primary-600" size={28} />
          </div>
        ) : roots.length === 0 ? (
          <p className="text-center py-16 text-gray-400">No organizational data available</p>
        ) : (
          <div className="p-8 overflow-x-auto">
            <div className="flex gap-12 justify-center">
              {roots.map(root => (
                <OrgNode key={root.id} user={root} allUsers={users} />
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Legend */}
      <div className="flex flex-wrap gap-4 text-xs text-gray-500">
        <div className="flex items-center gap-1.5">
          <div className="w-4 h-0.5 border-t-2 border-primary-500 rounded" />
          <span>C-Level</span>
        </div>
        <div className="flex items-center gap-1.5">
          <div className="w-4 h-0.5 border-t-2 border-purple-400 rounded" />
          <span>Management</span>
        </div>
        <div className="flex items-center gap-1.5">
          <div className="w-4 h-0.5 border-t-2 border-gray-300 rounded" />
          <span>Staff</span>
        </div>
      </div>
    </div>
  )
}
