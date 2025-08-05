import React from 'react';

export default function SpotifyLayout({
  title = 'Spotify 계정으로 로그인',
  description = '음악 기반 서비스를 시작하려면 Spotify 인증이 필요합니다.',
  children,
}: {
  title?: string;
  description?: string;
  children: React.ReactNode;
}) {
  return (
    <div className='min-h-screen bg-gradient-to-br bg-spotify flex flex-col items-center justify-center'>
      <div className='bg-white/90 rounded-2xl px-10 py-10 flex flex-col items-center shadow-lg max-w-md w-full'>
        <img
          src='/spotify-logo.png'
          alt='Spotify'
          width={52}
          className='mb-5 drop-shadow'
        />
        <h1 className='text-2xl font-bold text-[#191414] mb-2'>{title}</h1>
        <p className='text-gray-600 mb-7 text-center'>{description}</p>
        {children}
      </div>
    </div>
  );
}
