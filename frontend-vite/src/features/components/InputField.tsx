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
    <>
      <label htmlFor={id} className='sr-only'>
        {placeholder}
      </label>
      <input
        id={id}
        type={type}
        placeholder={placeholder}
        {...register}
        className='bg-white rounded-2xl h-12 shadow px-4 focus:ring-2 focus:ring-primary border-none text-base'
      />
      {error && (
        <span className='text-center text-red-500 font-bold text-sm'>
          {error.message || `${placeholder}를 입력해주세요`}
        </span>
      )}
    </>
  );
}
