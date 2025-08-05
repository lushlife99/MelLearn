import { useForm } from 'react-hook-form';
import type { SignupFormValues } from '@/features/auth/types';
import AuthLayout from '@/features/auth/components/AuthLayout';
import useAuth from '@/features/auth/hooks/useAuth';
import SignupForm from '@/features/auth/components/SignupForm';

export default function SignupPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<SignupFormValues & { confirmPassword: string }>();

  const { signup } = useAuth();
  const onSubmit = async (data: SignupFormValues) => {
    const { name, memberId, password } = data;

    const form: SignupFormValues = { name, memberId, password };
    signup(form);
  };

  return (
    <AuthLayout title='회원가입' showBackButton={true}>
      <SignupForm
        onSubmit={handleSubmit(onSubmit)}
        register={register}
        errors={errors}
        watch={watch}
      />
    </AuthLayout>
  );
}
