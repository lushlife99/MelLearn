interface Props {
  followers: number;
}

export default function Followers({ followers }: Props) {
  return (
    <div className='text-center lg:text-left'>
      <div className='text-2xl font-bold text-white'>
        {followers.toLocaleString()}
      </div>
      <div className='text-white/70 text-sm'>팔로워</div>
    </div>
  );
}
