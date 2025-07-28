interface Props {
  popularity: number;
}
export default function Popularity({ popularity }: Props) {
  return (
    <div className='mb-6'>
      <div className='flex items-center justify-center lg:justify-start space-x-3 mb-2'>
        <span className='text-white/70 text-sm'>인기도</span>
        <span className='text-white text-sm font-medium'>{popularity}%</span>
      </div>
      <div className='w-full max-w-md mx-auto lg:mx-0 bg-white/20 rounded-full h-2'>
        <div
          className='bg-gradient-to-r from-pink-500 to-violet-500 h-2 rounded-full transition-all duration-500'
          style={{ width: `${popularity}%` }}
        />
      </div>
    </div>
  );
}
