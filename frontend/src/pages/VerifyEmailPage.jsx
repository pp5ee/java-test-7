import { useState, useRef, useEffect } from 'react'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import { Mail, RefreshCw, CheckCircle } from 'lucide-react'
import { authService } from '../services'
import toast from 'react-hot-toast'

export default function VerifyEmailPage() {
  const [code, setCode] = useState(['', '', '', '', '', ''])
  const [loading, setLoading] = useState(false)
  const [resending, setResending] = useState(false)
  const [cooldown, setCooldown] = useState(0)
  const inputs = useRef([])
  const navigate = useNavigate()
  const location = useLocation()
  const email = location.state?.email || ''

  useEffect(() => {
    if (cooldown > 0) {
      const timer = setTimeout(() => setCooldown(c => c - 1), 1000)
      return () => clearTimeout(timer)
    }
  }, [cooldown])

  const handleChange = (index, value) => {
    if (!/^\d*$/.test(value)) return
    const newCode = [...code]
    newCode[index] = value.slice(-1)
    setCode(newCode)
    if (value && index < 5) inputs.current[index + 1]?.focus()
  }

  const handleKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !code[index] && index > 0) {
      inputs.current[index - 1]?.focus()
    }
  }

  const handlePaste = (e) => {
    const paste = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6)
    if (paste.length === 6) {
      setCode(paste.split(''))
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const codeStr = code.join('')
    if (codeStr.length !== 6) {
      toast.error('Please enter all 6 digits')
      return
    }
    setLoading(true)
    try {
      await authService.verifyEmail(email, codeStr)
      toast.success('Email verified! You can now log in.')
      navigate('/login')
    } catch {
      // handled
    } finally {
      setLoading(false)
    }
  }

  const handleResend = async () => {
    if (cooldown > 0) return
    setResending(true)
    try {
      await authService.sendVerification(email)
      toast.success('Verification code resent!')
      setCooldown(60)
    } catch {
      // handled
    } finally {
      setResending(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-600 via-purple-600 to-primary-800 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-white/20 backdrop-blur rounded-2xl mb-4">
            <Mail className="text-white" size={28} />
          </div>
          <h1 className="text-3xl font-bold text-white">Verify Email</h1>
          <p className="text-white/70 mt-1">
            Enter the 6-digit code sent to<br />
            <span className="text-white font-medium">{email}</span>
          </p>
        </div>

        <div className="bg-white rounded-2xl shadow-2xl p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="flex justify-center gap-3" onPaste={handlePaste}>
              {code.map((digit, i) => (
                <input
                  key={i}
                  ref={el => inputs.current[i] = el}
                  type="text"
                  inputMode="numeric"
                  maxLength={1}
                  value={digit}
                  onChange={e => handleChange(i, e.target.value)}
                  onKeyDown={e => handleKeyDown(i, e)}
                  className="w-12 h-12 text-center text-xl font-bold border-2 border-gray-300 rounded-xl
                             focus:outline-none focus:border-primary-500 focus:ring-2 focus:ring-primary-200"
                />
              ))}
            </div>

            <button type="submit" disabled={loading} className="btn-primary w-full py-2.5">
              <CheckCircle size={16} />
              {loading ? 'Verifying...' : 'Verify Email'}
            </button>
          </form>

          <div className="text-center mt-4">
            <button
              onClick={handleResend}
              disabled={resending || cooldown > 0}
              className="text-sm text-primary-600 hover:underline flex items-center gap-1 mx-auto disabled:opacity-50"
            >
              <RefreshCw size={14} className={resending ? 'animate-spin' : ''} />
              {cooldown > 0 ? `Resend in ${cooldown}s` : 'Resend Code'}
            </button>
          </div>

          <p className="text-center text-sm text-gray-500 mt-4">
            <Link to="/login" className="text-primary-600 font-medium hover:underline">
              Back to Login
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
