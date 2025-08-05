import { Link } from 'react-router-dom';

export default function LoginFooter() {
  return (
    <footer className='flex flex-col w-full mt-8 text-center'>
      <span className='text-white/60 text-sm font-medium mb-4'>
        아직 회원이 아니시라구요?
      </span>
      <div className='relative'>
        <div className='absolute inset-0 flex items-center'>
          <div className='w-full border-t border-white/20'></div>
        </div>
        <div className='relative flex justify-center'>
          <Link
            to='/signup'
            className='bg-gradient-to-r from-pink-500 to-violet-500 text-white font-semibold px-6 py-2 rounded-lg hover:shadow-lg hover:scale-105 transition-all duration-300'
          >
            회원가입
          </Link>
        </div>
      </div>
    </footer>
  );
}
