import type {
  FieldErrors,
  UseFormRegister,
  UseFormWatch,
} from 'react-hook-form';
import type { SignupFormValues } from '../types';
import FormButton from './FormButton';
import InputField from './InputField';

interface Props {
  onSubmit: () => void;
  register: UseFormRegister<SignupFormValues & { confirmPassword: string }>;
  errors: FieldErrors<SignupFormValues & { confirmPassword: string }>;
  watch: UseFormWatch<SignupFormValues & { confirmPassword: string }>;
}

export default function SignupForm({
  onSubmit,
  register,
  errors,
  watch,
}: Props) {
  return (
    <form onSubmit={onSubmit} className='flex flex-col w-full gap-3'>
      <InputField
        id='name'
        placeholder='이름'
        register={register('name', { required: '이름을 입력해주세요' })}
        error={errors.name}
      />
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
      <InputField
        id='confirmPassword'
        placeholder='비밀번호 확인'
        type='password'
        register={register('confirmPassword', {
          required: '비밀번호 확인은 필수입니다.',
          validate: (value) =>
            value === watch('password') || '비밀번호가 일치하지 않습니다',
        })}
        error={errors.confirmPassword}
      />
      <FormButton label='회원가입' />
    </form>
  );
}
