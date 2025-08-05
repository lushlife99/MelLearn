import { ArrowLeft } from 'lucide-react';

interface Props {
  onClick: () => void;
}

export default function ArrowBack({ onClick }: Props) {
  return (
    <button
      onClick={onClick}
      className='p-2 rounded-full bg-white/10 hover:bg-white/20 transition-all duration-300'
    >
      <ArrowLeft className='w-6 h-6 text-white' />
    </button>
  );
}
