import { describe, expect, it, vi } from 'vitest';
import SignupForm from '../SignupForm';
import type { SignupFormValues } from '@/features/auth/types';
import { useForm } from 'react-hook-form';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

function TestSignupForm({ onSubmit = () => {} }) {
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<SignupFormValues & { confirmPassword: string }>();

  return (
    <SignupForm
      onSubmit={handleSubmit(onSubmit)}
      register={register}
      errors={errors}
      watch={watch}
    />
  );
}

describe('SignupForm', () => {
  it('회원가입 폼이 올바르게 렌더링되어야 한다.', () => {
    render(<TestSignupForm />);
    expect(screen.getByPlaceholderText('이름')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('아이디')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('비밀번호')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('비밀번호 확인')).toBeInTheDocument();
  });

  it('필수 입력 필드가 비어 있으면 에러 메시지가 표시되어야 한다.', async () => {
    render(<TestSignupForm />);
    const submitButton = screen.getByRole('button', { name: '회원가입' });

    await userEvent.click(submitButton);

    expect(screen.getByText('이름을 입력해주세요')).toBeInTheDocument();
    expect(screen.getByText('아이디를 입력해주세요')).toBeInTheDocument();
    expect(screen.getByText('비밀번호를 입력해주세요')).toBeInTheDocument();
    expect(screen.getByText('비밀번호 확인은 필수입니다.')).toBeInTheDocument();
  });

  it('비밀번호와 비밀번호 확인이 일치하지 않으면 에러 메시지가 표시되어야 한다.', async () => {
    render(<TestSignupForm />);
    const passwordInput = screen.getByPlaceholderText('비밀번호');
    const confirmPasswordInput = screen.getByPlaceholderText('비밀번호 확인');
    const submitButton = screen.getByRole('button', { name: '회원가입' });

    await userEvent.type(passwordInput, 'testPassword');
    await userEvent.type(confirmPasswordInput, 'differentPassword');
    await userEvent.click(submitButton);

    expect(
      screen.getByText('비밀번호가 일치하지 않습니다')
    ).toBeInTheDocument();
  });

  it('모든 필드가 올바르게 입력되면 onSubmit이 호출되어야 한다.', async () => {
    const mockOnSubmit = vi.fn();

    render(<TestSignupForm onSubmit={mockOnSubmit} />);
    await userEvent.type(screen.getByPlaceholderText('이름'), 'Test User');
    await userEvent.type(screen.getByPlaceholderText('아이디'), 'testUser');
    await userEvent.type(
      screen.getByPlaceholderText('비밀번호'),
      'testPassword'
    );
    await userEvent.type(
      screen.getByPlaceholderText('비밀번호 확인'),
      'testPassword'
    );

    await userEvent.click(screen.getByRole('button', { name: '회원가입' }));
    expect(mockOnSubmit).toHaveBeenCalled();
  });
});
