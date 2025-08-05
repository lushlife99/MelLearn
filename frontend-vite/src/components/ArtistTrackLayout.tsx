interface Props {
  children: React.ReactNode;
}
export default function ArtistTrackLayout({ children }: Props) {
  return (
    <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900'>
      <div className='relative z-10 md:ml-0 lg:ml-20 xl:ml-64 transition-all duration-300'>
        <div className='max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8'>
          {children}
        </div>
      </div>
    </div>
  );
}
