import { useNavigate } from 'react-router-dom';

export default function NotFound() {
  const navigate = useNavigate();
  // 나중에 홈으로 돌아가기로 변경
  return (
    <div className='min-h-screen flex flex-col justify-center items-center bg-secondary px-4 text-center'>
      <h1 className='text-4xl font-bold text-gray-800 mb-4'>
        404 - 페이지를 찾을 수 없어요
      </h1>
      <p className='text-gray-600 mb-6'>
        존재하지 않는 페이지이거나, 잘못된 경로로 접근하셨어요.
      </p>
      <button
        onClick={() => navigate(-1)}
        className='bg-primary hover:bg-primary-hover text-white px-6 py-2 rounded'
      >
        돌아가기
      </button>
    </div>
  );
}
