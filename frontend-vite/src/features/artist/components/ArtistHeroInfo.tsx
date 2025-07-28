import type { Artist } from '@/features/home/types/home';
import { Users } from 'lucide-react';
import Followers from '@/features/artist/components/Followers';
import HeroTitle from '@/components/HeroTitle';
import Popularity from '@/components/Popularity';
import ActionButton from '@/components/ActionButton';

interface Props {
  artist: Artist;
}

export default function ArtistHeroInfo({ artist }: Props) {
  const { name, followers, popularity, external_urls } = artist;
  return (
    <div className='flex-1 flex flex-col justify-center text-center lg:text-left'>
      <div className='mb-4'>
        <HeroTitle Icon={Users} title='아티스트' name={name} />

        <div className='flex flex-wrap justify-center lg:justify-start gap-6 mb-6'>
          {followers?.total && <Followers followers={followers.total} />}
        </div>

        {popularity && <Popularity popularity={popularity} />}
      </div>

      <ActionButton type='artist' spotify={external_urls.spotify} />
    </div>
  );
}
