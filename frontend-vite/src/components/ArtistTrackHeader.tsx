import ArrowBack from './ArrowBack';

interface Props {
  title: string;
  description: string;
  onClick: () => void;
}

export default function ArtistTrackHeader({
  title,
  description,
  onClick,
}: Props) {
  return (
    <div className='flex items-center mb-6 space-x-3'>
      <ArrowBack onClick={onClick} />

      <div className='flex items-center space-x-3'>
        <div>
          <h1 className='text-3xl sm:text-4xl font-bold text-white'>{title}</h1>
          <p className='text-white/70 mt-1'>{description}</p>
        </div>
      </div>
    </div>
  );
}
