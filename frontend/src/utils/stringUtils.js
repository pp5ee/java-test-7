/**
 * Get the initials from a user's first and last name.
 * e.g. { firstName: "John", lastName: "Doe" } → "JD"
 */
export function getUserInitials(user) {
  if (!user) return '?'
  const first = user.firstName?.[0] ?? ''
  const last = user.lastName?.[0] ?? ''
  return (first + last).toUpperCase() || '?'
}

/**
 * Returns the full display name for a user object.
 */
export function getFullName(user) {
  if (!user) return ''
  return user.fullName || `${user.firstName ?? ''} ${user.lastName ?? ''}`.trim()
}

/**
 * Truncate a string to maxLength, appending "…" if truncated.
 */
export function truncate(str, maxLength = 50) {
  if (!str) return ''
  return str.length > maxLength ? str.slice(0, maxLength) + '…' : str
}

/**
 * Capitalise the first letter of a string and lowercase the rest.
 */
export function capitalise(str) {
  if (!str) return ''
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()
}

/**
 * Convert a snake_case or UPPER_CASE enum string to Title Case.
 * e.g. "IN_PROGRESS" → "In Progress"
 */
export function enumToLabel(str) {
  if (!str) return ''
  return str
    .split('_')
    .map(word => capitalise(word))
    .join(' ')
}
