import { Book, FileText, History, House, Settings } from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

const navItems = [
  {
    label: '홈',
    icon: House,
    path: '/',
  },
  {
    label: '학습',
    icon: Book,
    path: '/quiz',
  },
  {
    label: '모의고사',
    icon: FileText,
    path: '/exam',
  },
  {
    label: '히스토리',
    icon: History,
    path: '/history',
  },
  {
    label: '설정',
    icon: Settings,
    path: '/settings',
  },
];

export default function NavBar() {
  const location = useLocation();

  const isActive = (path: string) => {
    if (path === '/') {
      return (
        location.pathname === '/' ||
        location.pathname.startsWith('/artists') ||
        location.pathname.startsWith('/charts')
      );
    }
    return location.pathname === path;
  };

  return (
    <>
      <nav className='hidden lg:flex fixed left-0 top-0 h-full w-20 xl:w-64 bg-black/20 backdrop-blur-xl border-r border-white/10 flex-col items-center xl:items-start py-8 z-40'>
        <div className='mb-12 xl:px-6'>
          <div className='w-12 h-12 bg-gradient-to-r from-pink-500 to-violet-500 rounded-xl flex items-center justify-center xl:hidden'>
            <House className='w-6 h-6 text-white' />
          </div>
          <Link to='/'>
            <h1 className='hidden xl:block  text-2xl font-bold bg-gradient-to-r from-pink-400 to-violet-400 bg-clip-text text-transparent'>
              MelLearn
            </h1>
          </Link>
        </div>

        <div className='flex flex-col space-y-4 w-full xl:px-4'>
          {navItems.map(({ label, icon: Icon, path }, i) => {
            const active = isActive(path);

            return (
              <Link
                to={path}
                key={i}
                className={`group relative flex items-center w-full p-3 rounded-xl transition-all duration-300 ${
                  active
                    ? `bg-gradient-to-r from-pink-500 to-violet-500 text-white shadow-lg`
                    : 'text-gray-400 hover:text-white hover:bg-white/10'
                }`}
              >
                {active && (
                  <div className='absolute -left-1 top-1/2 transform -translate-y-1/2 w-1 h-8 bg-white rounded-full xl:hidden'></div>
                )}

                <Icon className='w-6 h-6 min-w-[24px]' />
                <span className='hidden xl:block ml-4 font-medium'>
                  {label}
                </span>

                <div className='xl:hidden absolute left-full ml-4 px-3 py-2 bg-black/80 backdrop-blur-lg text-white text-sm rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none whitespace-nowrap z-50'>
                  {label}
                  <div className='absolute left-0 top-1/2 transform -translate-x-1 -translate-y-1/2 w-2 h-2 bg-black/80 rotate-45'></div>
                </div>
              </Link>
            );
          })}
        </div>
      </nav>

      <nav className='hidden md:flex lg:hidden fixed top-0 left-0 right-0 h-16 bg-black/20 backdrop-blur-xl border-b border-white/10 items-center justify-between px-6 z-40'>
        <h1 className=' text-xl font-bold bg-gradient-to-r from-pink-400 to-violet-400 bg-clip-text text-transparent'>
          MelLearn
        </h1>

        <div className='flex space-x-2'>
          {navItems.map(({ label, icon: Icon, path }, i) => {
            const active = isActive(path);
            return (
              <Link
                to={path}
                key={i}
                className={`flex items-center px-4 py-2 rounded-lg transition-all duration-300 ${
                  active
                    ? `bg-gradient-to-r from-pink-500 to-violet-500 text-white shadow-lg`
                    : 'text-gray-400 hover:text-white hover:bg-white/10'
                }`}
              >
                <Icon className='w-5 h-5' />
                <span className='ml-2 text-sm font-medium'>{label}</span>
              </Link>
            );
          })}
        </div>
      </nav>

      <nav className='md:hidden fixed bottom-0 left-0 right-0 bg-black/20 backdrop-blur-xl border-t border-white/10 z-40'>
        <div className='flex justify-around items-center py-2'>
          {navItems.map(({ label, icon: Icon, path }, i) => {
            const active = isActive(path);

            return (
              <Link
                to={path}
                key={i}
                className={`flex flex-col items-center py-2 px-3 rounded-xl transition-all duration-300 relative ${
                  active ? 'text-white' : 'text-gray-400 hover:text-white'
                }`}
              >
                {active && (
                  <>
                    <div
                      className={`absolute inset-0 bg-gradient-to-r from-pink-500 to-violet-500 rounded-xl opacity-20`}
                    ></div>
                    <div className='absolute -top-1 left-1/2 transform -translate-x-1/2 w-8 h-1 bg-gradient-to-r from-pink-400 to-violet-400 rounded-full'></div>
                  </>
                )}

                <div className='relative z-10'>
                  <Icon className='w-6 h-6 mb-1' />
                  <span className='text-xs font-medium'>{label}</span>
                </div>
              </Link>
            );
          })}
        </div>
      </nav>

      <div className='md:hidden h-20'></div>
      <div className='hidden md:block lg:hidden h-16'></div>
      <div className='hidden lg:block w-20 xl:w-64'></div>
    </>
  );
}
