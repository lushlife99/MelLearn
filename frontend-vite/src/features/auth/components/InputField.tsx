import type { FieldError, UseFormRegisterReturn } from 'react-hook-form';

interface Props {
  id: string;
  type?: string;
  placeholder: string;
  register: UseFormRegisterReturn;
  error?: FieldError;
}

export default function InputField({
  id,
  type = 'text',
  placeholder,
  register,
  error,
}: Props) {
  return (
    <div className='w-full'>
      <label htmlFor={id} className='sr-only'>
        {placeholder}
      </label>
      <input
        id={id}
        type={type}
        placeholder={placeholder}
        {...register}
        className='w-full px-4 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-lg text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-pink-500/50 focus:border-transparent transition-all duration-300 hover:bg-white/15'
      />
      {error && (
        <p className='mt-2 text-red-400 text-sm font-medium'>
          {error.message || `${placeholder}를 입력해주세요`}
        </p>
      )}
    </div>
  );
}
