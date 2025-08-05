import { useSpotifyCallback } from '../hooks/useSpotifyCallback';

export default function SpotifyCallback() {
  const { status, error } = useSpotifyCallback();

  return (
    <div className='flex flex-col justify-center items-center min-h-[40vh] gap-3'>
      {status === 'loading' && (
        <div className='flex items-center gap-2 text-lg text-gray-600'>
          <Spinner />
          <span>스포티파이 연동 중입니다...</span>
        </div>
      )}
      {status === 'error' && (
        <div className='flex items-center gap-2 text-red-500 font-semibold'>
          <ErrorIcon />
          <span>에러: {error}</span>
        </div>
      )}
      {status === 'success' && (
        <div className='flex items-center gap-2 text-spotify font-semibold'>
          <SuccessIcon />
          <span>연동 성공! 홈으로 이동 중...</span>
        </div>
      )}
    </div>
  );
}

function Spinner() {
  return (
    <svg className='animate-spin h-5 w-5 text-spotify' viewBox='0 0 24 24'>
      <circle
        className='opacity-25'
        cx='12'
        cy='12'
        r='10'
        stroke='currentColor'
        strokeWidth='4'
        fill='none'
      />
      <path
        className='opacity-75'
        fill='currentColor'
        d='M4 12a8 8 0 018-8v8z'
      />
    </svg>
  );
}
function SuccessIcon() {
  return (
    <svg className='h-5 w-5' fill='none' viewBox='0 0 24 24' stroke='#1DB954'>
      <path
        strokeLinecap='round'
        strokeLinejoin='round'
        strokeWidth={2}
        d='M5 13l4 4L19 7'
      />
    </svg>
  );
}
function ErrorIcon() {
  return (
    <svg className='h-5 w-5' fill='none' viewBox='0 0 24 24' stroke='#F87171'>
      <path
        strokeLinecap='round'
        strokeLinejoin='round'
        strokeWidth={2}
        d='M6 18L18 6M6 6l12 12'
      />
    </svg>
  );
}
