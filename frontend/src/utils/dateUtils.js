import { format, formatDistanceToNow, parseISO, isValid } from 'date-fns'

/**
 * Format an ISO date string to a readable date: "Jan 15, 2025"
 */
export function formatDate(value) {
  if (!value) return '—'
  try {
    const d = typeof value === 'string' ? parseISO(value) : value
    return isValid(d) ? format(d, 'MMM d, yyyy') : '—'
  } catch {
    return '—'
  }
}

/**
 * Format an ISO date string to a short date: "Jan 15"
 */
export function formatShortDate(value) {
  if (!value) return '—'
  try {
    const d = typeof value === 'string' ? parseISO(value) : value
    return isValid(d) ? format(d, 'MMM d') : '—'
  } catch {
    return '—'
  }
}

/**
 * Format an ISO date string to date + time: "Jan 15, 2025 at 3:00 PM"
 */
export function formatDateTime(value) {
  if (!value) return '—'
  try {
    const d = typeof value === 'string' ? parseISO(value) : value
    return isValid(d) ? format(d, 'MMM d, yyyy \'at\' h:mm a') : '—'
  } catch {
    return '—'
  }
}

/**
 * Return relative time: "3 days ago"
 */
export function timeAgo(value) {
  if (!value) return '—'
  try {
    const d = typeof value === 'string' ? parseISO(value) : value
    return isValid(d) ? formatDistanceToNow(d, { addSuffix: true }) : '—'
  } catch {
    return '—'
  }
}

/**
 * Format a date value to "yyyy-MM-dd" for <input type="date">
 */
export function toInputDate(value) {
  if (!value) return ''
  try {
    const d = typeof value === 'string' ? parseISO(value) : value
    return isValid(d) ? format(d, 'yyyy-MM-dd') : ''
  } catch {
    return ''
  }
}
