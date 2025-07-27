import { apiClient } from '@/services/axios';
import type {
  LoginFormValues,
  LoginResponse,
  SignupFormValues,
} from '../types';

export async function loginApi(form: LoginFormValues): Promise<LoginResponse> {
  const { data } = await apiClient.post('/login', form);
  return data;
}

export async function signupApi(form: SignupFormValues): Promise<void> {
  await apiClient.post('/join', form);
}
