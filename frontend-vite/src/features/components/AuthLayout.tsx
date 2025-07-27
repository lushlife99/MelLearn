import { useNavigate } from 'react-router-dom';
import BgCircle from './BgCircle';
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
    <main className='relative bg-secondary flex justify-center items-center min-h-screen overflow-hidden'>
      <BgCircle />

      <section className='z-10 bg-secondary w-full max-w-md rounded-3xl flex flex-col px-6 py-10 sm:px-10 sm:py-16 items-center'>
        <header className='w-full flex flex-col items-start mb-10 relative'>
          {showBackButton && (
            <ChevronLeft
              onClick={() => navigate(-1)}
              className='w-8 h-8 text-primary hover:text-primary-hover transition-colors cursor-pointer mb-4'
            />
          )}
          <h1 className='text-primary font-extrabold text-4xl sm:text-5xl text-center w-full'>
            {title}
          </h1>
          {description && (
            <p className='mt-4 font-bold text-[#a39c9c] text-base sm:text-lg text-center w-full'>
              {description}
            </p>
          )}
        </header>

        {children}
      </section>
    </main>
  );
}
