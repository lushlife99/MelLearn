import useAuth from '@/features/auth/hooks/useAuth';
import type { LoginFormValues } from '@/features/auth/types';
import AuthLayout from '@/features/auth/components/AuthLayout';
import LoginFooter from '@/features/auth/components/LoginFooter';
import LoginForm from '@/features/auth/components/LoginForm';
import { useForm } from 'react-hook-form';

export default function LoginPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>();
  const { login } = useAuth();
  const onSubmit = (data: LoginFormValues) => {
    login(data);
  };

  return (
    <AuthLayout
      title='MelLearn'
      description='신나는 노래와 함께 언어를 공부해보세요'
    >
      <LoginForm
        onSubmit={handleSubmit(onSubmit)}
        register={register}
        errors={errors}
      />

      <LoginFooter />
    </AuthLayout>
  );
}
