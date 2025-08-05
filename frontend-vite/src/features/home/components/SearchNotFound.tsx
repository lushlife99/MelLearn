import { Search } from 'lucide-react';
interface Props {
  searchQuery: string;
}

export default function SearchNotFound({ searchQuery }: Props) {
  return (
    <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-8 border border-white/10'>
      <div className='flex flex-col items-center justify-center py-12'>
        <div className='w-24 h-24 rounded-full bg-white/5 flex items-center justify-center mb-6'>
          <Search className='w-12 h-12 text-white/30' />
        </div>
        <h3 className='text-2xl font-bold text-white mb-2'>
          검색 결과가 없습니다
        </h3>
        <p className='text-white/60 text-center max-w-md'>
          "{searchQuery}"에 대한 검색 결과를 찾을 수 없습니다.
          <br />
          다른 키워드로 검색해보세요.
        </p>
      </div>
    </div>
  );
}
