interface Props {
  label: string;
}

export default function FormButton({ label }: Props) {
  return (
    <button className='w-full py-3 bg-gradient-to-r from-pink-500 to-violet-500 text-white font-semibold rounded-lg hover:shadow-lg hover:scale-105 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100'>
      {label}
    </button>
  );
}
