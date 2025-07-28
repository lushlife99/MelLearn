import { type LucideIcon } from 'lucide-react';
interface Props {
  onClick: () => void;
  label: string;
  Icon: LucideIcon;
}

export default function TrackActionButton({ onClick, label, Icon }: Props) {
  return (
    <button
      onClick={onClick}
      className='flex items-center space-x-2 bg-gradient-to-r from-pink-500 to-violet-500 hover:from-pink-600 hover:to-violet-600 text-white px-6 py-3 rounded-xl font-semibold transition-all duration-300 hover:scale-105'
    >
      <Icon className='w-5 h-5' />
      <span>{label}</span>
    </button>
  );
}
