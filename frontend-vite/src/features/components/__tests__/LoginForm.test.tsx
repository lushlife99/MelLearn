import { render, screen } from '@testing-library/react';

import { describe, expect, it, vi } from 'vitest';
import LoginForm from '../LoginForm';
import { useForm } from 'react-hook-form';
import type { LoginFormValues } from '@/features/auth/types';
import { userEvent } from '@testing-library/user-event';

function TestLoginForm({ onSubmit = () => {} }) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>();
  return (
    <LoginForm
      onSubmit={handleSubmit(onSubmit)}
      register={register}
      errors={errors}
    />
  );
}
describe('LoginForm', () => {
  it('아이디/비밀번호 입력칸과 로그인 버튼이 화면에 보여야 한다.', () => {
    render(<TestLoginForm />);

    expect(screen.getByPlaceholderText('아이디')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('비밀번호')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '로그인' })).toBeInTheDocument();
  });

  it('입력값이 비어 있으면 에러 메시지가 보여야 한다.', async () => {
    render(<TestLoginForm />);
    const submitButton = screen.getByRole('button', { name: '로그인' });

    await userEvent.click(submitButton);

    expect(screen.getByText('아이디를 입력해주세요')).toBeInTheDocument();
    expect(screen.getByText('비밀번호를 입력해주세요')).toBeInTheDocument();
  });

  it('아이디와 비밀번호를 입력하고 로그인 버튼을 클릭하면 onSubmit이 호출되어야 한다.', async () => {
    const mockOnSubmit = vi.fn();
    render(<TestLoginForm onSubmit={mockOnSubmit} />);

    await userEvent.type(screen.getByPlaceholderText('아이디'), 'testUser');
    await userEvent.type(
      screen.getByPlaceholderText('비밀번호'),
      'testPassword'
    );
    await userEvent.click(screen.getByRole('button', { name: '로그인' }));

    expect(mockOnSubmit).toHaveBeenCalled();
  });

  it('아이디만 입력하고 제출 시 비밀번호 에러 메시지만 보여야 한다.', async () => {
    render(<TestLoginForm />);
    await userEvent.type(screen.getByPlaceholderText('아이디'), 'testUser');
    await userEvent.click(screen.getByRole('button', { name: '로그인' }));

    expect(screen.getByText('비밀번호를 입력해주세요')).toBeInTheDocument();
    expect(screen.queryByText('아이디를 입력해주세요')).not.toBeInTheDocument();
  });
});
