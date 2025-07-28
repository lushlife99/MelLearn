import { useNavigate } from 'react-router-dom';
import { ChevronLeft } from 'lucide-react';

interface AuthLayoutProps {
  title: string;
  description?: string;
  children: React.ReactNode;
  showBackButton?: boolean;
}

export default function AuthLayout({
  title,
  description,
  children,
  showBackButton = false,
}: AuthLayoutProps) {
  const navigate = useNavigate();

  return (
    <main className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900 flex justify-center items-center overflow-hidden relative'>
      <div className='absolute inset-0 overflow-hidden'>
        <div className='absolute -top-40 -right-40 w-80 h-80 bg-pink-500/20 rounded-full blur-3xl' />
        <div className='absolute -bottom-40 -left-40 w-80 h-80 bg-violet-500/20 rounded-full blur-3xl' />
        <div className='absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-blue-500/10 rounded-full blur-3xl' />
      </div>

      <section className='relative z-10 bg-white/10 backdrop-blur-lg w-full max-w-md rounded-2xl border border-white/20 shadow-xl p-8 sm:p-10 mx-4'>
        <header className='w-full flex flex-col items-center mb-8 relative'>
          {showBackButton && (
            <button
              onClick={() => navigate(-1)}
              className='absolute -top-2 left-0 p-2 text-white/80 hover:text-white hover:bg-white/10 rounded-full transition-all duration-300'
            >
              <ChevronLeft className='w-6 h-6' />
            </button>
          )}
          <div className='flex items-center space-x-3 mb-4'>
            <div className='w-10 h-10 bg-gradient-to-r from-pink-500 to-violet-500 rounded-full flex items-center justify-center'>
              <span className='text-white font-bold text-lg'>M</span>
            </div>
            <h1 className='text-white font-extrabold text-3xl sm:text-4xl'>
              {title}
            </h1>
          </div>
          {description && (
            <p className='text-white/70 text-sm sm:text-base text-center font-medium'>
              {description}
            </p>
          )}
        </header>

        {children}
      </section>
    </main>
  );
}
