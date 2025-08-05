export default function WelcomeSection() {
  return (
    <div className='mb-12 text-center'>
      <h2 className='text-2xl sm:text-3xl lg:text-4xl xl:text-5xl font-bold text-white mb-4'>
        음악과 함께하는
        <span className='block bg-gradient-to-r from-pink-400 to-violet-400 bg-clip-text text-transparent'>
          특별한 학습 경험
        </span>
      </h2>
      <p className='text-gray-300 text-base sm:text-lg lg:text-xl max-w-2xl mx-auto'>
        당신만을 위한 맞춤형 음악 추천과 학습을 시작해보세요
      </p>
    </div>
  );
}
