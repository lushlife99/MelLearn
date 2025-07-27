interface Props {
  label: string;
}

export default function FormButton({ label }: Props) {
  return (
    <button className='bg-primary mt-4 rounded-2xl bg-accent h-12 text-white hover:bg-primary-hover font-bold flex items-center justify-center text-base sm:text-lg transition'>
      {label}
    </button>
  );
}
