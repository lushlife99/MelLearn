import Carousel from 'react-multi-carousel';
import 'react-multi-carousel/lib/styles.css';

interface Props {
  children: React.ReactNode;
}

const responsive = {
  desktop: {
    breakpoint: { max: 3000, min: 1024 },
    items: 5,
  },
  tablet: {
    breakpoint: { max: 1024, min: 464 },
    items: 3,
  },
  mobile: {
    breakpoint: { max: 464, min: 0 },
    items: 2,
  },
};
export default function CustomCarousel({ children }: Props) {
  return (
    <Carousel
      responsive={responsive}
      infinite={true}
      autoPlay={false}
      keyBoardControl={true}
      customTransition='transform 300ms ease-in-out'
      transitionDuration={300}
      containerClass='carousel-container'
      removeArrowOnDeviceType={['tablet', 'mobile']}
      itemClass='px-2'
    >
      {children}
    </Carousel>
  );
}
