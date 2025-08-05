interface Props {
  src: string;
  alt: string;
}

export default function HeroImage({ src, alt }: Props) {
  return (
    <div className='flex-shrink-0'>
      <div className='relative'>
        <img
          src={src}
          alt={alt}
          className='w-64 h-64 lg:w-80 lg:h-80 object-cover rounded-2xl shadow-2xl mx-auto lg:mx-0'
        />
        <div className='absolute inset-0 bg-gradient-to-t from-black/20 to-transparent rounded-2xl' />
      </div>
    </div>
  );
}
