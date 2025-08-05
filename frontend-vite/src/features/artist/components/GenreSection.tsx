interface Props {
  genres: string[];
}

export default function GenreSection({ genres }: Props) {
  return (
    <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10 mb-8'>
      <h2 className='text-2xl font-bold text-white mb-6'>장르</h2>
      <div className='flex flex-wrap gap-3'>
        {genres.map((genre) => (
          <div
            key={genre}
            className='bg-gradient-to-r from-pink-500/20 to-violet-500/20 border border-pink-500/30 text-white px-4 py-2 rounded-xl text-sm font-medium'
          >
            {genre}
          </div>
        ))}
      </div>
    </div>
  );
}
