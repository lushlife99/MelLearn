import { useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import type { LoginFormValues, SignupFormValues } from '../types';
import { loginApi, signupApi } from '../services/authApi';
import { ROUTES } from '@/services/router';

export default function useAuth() {
  const navigate = useNavigate();

  const { mutate: login } = useMutation({
    mutationFn: (form: LoginFormValues) => loginApi(form),
    onSuccess: (data) => {
      sessionStorage.setItem('accessToken', data.accessToken);
      toast.success('로그인 성공!');
      navigate(ROUTES.SPOTIFY_LOGIN);
    },
    onError: () => {
      toast.error('로그인 실패');
      navigate(ROUTES.SPOTIFY_LOGIN);
    },
  });

  const { mutate: signup } = useMutation({
    mutationFn: (form: SignupFormValues) => signupApi(form),
    onSuccess: () => {
      toast.success('회원가입 성공!');
      navigate('/login');
    },
    onError: () => {
      toast.error('회원가입 실패');
    },
  });

  return { login, signup };
}
