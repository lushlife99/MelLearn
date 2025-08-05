import type { LucideIcon } from 'lucide-react';

interface Props {
  Icon: LucideIcon;
}
export default function SearchNotFound({ Icon }: Props) {
  return (
    <div className='text-center py-12'>
      <Icon className='w-16 h-16 text-white/30 mx-auto mb-4' />
      <p className='text-white/50 text-lg'>검색 결과가 없습니다</p>
    </div>
  );
}
