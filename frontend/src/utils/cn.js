import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * Merge Tailwind class names safely.
 * Combines clsx (conditional classes) with tailwind-merge (dedup conflicts).
 */
export function cn(...inputs) {
  return twMerge(clsx(inputs))
}
