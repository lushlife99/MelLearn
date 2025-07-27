import { Link } from 'react-router-dom';

export default function LoginFooter() {
  return (
    <footer className='flex flex-col w-full mt-10 text-center'>
      <span className='text-[#a39c9c] text-sm sm:text-base font-bold mb-3'>
        아직 회원이 아니시라구요?
      </span>
      <nav className='flex items-center justify-center w-full gap-2'>
        <div className='h-[2px] bg-white flex-1' />
        <Link
          to='/signup'
          className='text-primary font-bold hover:text-primary-hover text-base whitespace-nowrap px-2'
        >
          회원가입
        </Link>
        <div className='h-[2px] bg-white flex-1' />
      </nav>
    </footer>
  );
}
