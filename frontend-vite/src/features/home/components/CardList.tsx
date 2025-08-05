import type { Artist, Track } from '../types/home';
import CustomCarousel from './CustomCarousel';

interface Props {
  items: Artist[] | Track[];
  type: 'track' | 'artist';
  onClick: (item: Artist | Track) => void;
}

export default function CardList({ items, type, onClick }: Props) {
  return (
    <CustomCarousel>
      {items.map((item) => (
        <div
          key={item.id}
          onClick={() => onClick(item)}
          className='flex-shrink-0 bg-black rounded-lg hover:opacity-70 cursor-pointer'
        >
          <img
            src={
              type === 'artist'
                ? (item as Artist).images?.[0]?.url
                : (item as Track)?.album.images?.[0]?.url
            }
            alt={item.name}
            className='rounded-t-lg w-full aspect-square object-cover'
          />
          <div className='p-2'>
            <div className='text-white text-sm font-semibold truncate'>
              {item.name}
            </div>
            {type === 'track' && (
              <div className='text-gray-400 text-xs truncate'>
                {(item as Track).artists.map((a) => a.name).join(', ')}
              </div>
            )}
          </div>
        </div>
      ))}
    </CustomCarousel>
  );
}
