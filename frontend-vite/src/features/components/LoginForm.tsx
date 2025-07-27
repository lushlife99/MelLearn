import type { FieldErrors, UseFormRegister } from 'react-hook-form';
import type { LoginFormValues } from '../auth/types';
import FormButton from './FormButton';
import InputField from './InputField';

interface Props {
  onSubmit: () => void;
  register: UseFormRegister<LoginFormValues>;
  errors: FieldErrors<LoginFormValues>;
}

export default function LoginForm({ onSubmit, register, errors }: Props) {
  return (
    <form onSubmit={onSubmit} className='flex flex-col w-full gap-3'>
      <label htmlFor='memberId' className='sr-only'>
        아이디
      </label>
      <InputField
        id='memberId'
        placeholder='아이디'
        register={register('memberId', { required: '아이디를 입력해주세요' })}
        error={errors.memberId}
      />
      <InputField
        id='password'
        placeholder='비밀번호'
        type='password'
        register={register('password', { required: '비밀번호를 입력해주세요' })}
        error={errors.password}
      />
      <FormButton label='로그인' />
    </form>
  );
}
