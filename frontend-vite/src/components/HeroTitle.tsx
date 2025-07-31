interface Props {
  name: string;
}

export default function HeroTitle({ name }: Props) {
  return (
    <h1 className='text-4xl sm:text-2xl lg:text-3xl font-bold text-white mb-4'>
      {name}
    </h1>
  );
}
