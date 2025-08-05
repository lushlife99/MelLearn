import { type LucideIcon } from 'lucide-react';
interface Props {
  onClick: () => void;
  label?: string;
  Icon: LucideIcon;
  buttonClass: string;
  iconClass: string;
}

export default function TrackActionButton({
  onClick,
  label,
  Icon,
  buttonClass,
  iconClass,
}: Props) {
  return (
    <button
      onClick={onClick}
      className={`flex items-center 
      bg-gradient-to-r from-pink-500
      to-violet-500 hover:from-pink-600 hover:to-violet-600 
      text-white    transition-all hover:scale-105 ${buttonClass}`}
    >
      <Icon className={iconClass} />
      {label && <span className='font-semibold text-white'>{label}</span>}
    </button>
  );
}
