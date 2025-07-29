import type { Album } from '@/features/home/types/home';
import { Calendar, Disc3, Music } from 'lucide-react';
import { formatReleaseDate } from '../utils/format';
import AlbumItem from './AlbumItem';
import SectionLayout from '@/components/SectionLayout';

interface Props {
  album: Album;
}

export default function AlbumSection({ album }: Props) {
  const { name, release_date, total_tracks } = album;
  return (
    <SectionLayout title='앨범 정보'>
      <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'>
        <AlbumItem Icon={Disc3} title='앨범명' description={name} />
        <AlbumItem
          Icon={Calendar}
          title='발매일'
          description={formatReleaseDate(release_date)}
        />
        <AlbumItem
          Icon={Music}
          title='총 트랙 수'
          description={`${total_tracks}곡`}
        />
      </div>
    </SectionLayout>
  );
}
