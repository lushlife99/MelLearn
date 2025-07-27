import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';
import SignupPage from '../SignupPage';
import userEvent from '@testing-library/user-event';

const mockSignup = vi.fn();

vi.mock('@/features/auth/hooks/useAuth', () => {
  return {
    default: () => ({ signup: mockSignup }),
  };
});

describe('SignupPage', () => {
  it('회원가입 폼 제출 시 signUp 함수가 호출되어야 한다.', async () => {
    render(
      <MemoryRouter>
        <SignupPage />
      </MemoryRouter>
    );
    await userEvent.type(screen.getByPlaceholderText('이름'), 'testName');
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

    expect(mockSignup).toHaveBeenCalledWith({
      name: 'testName',
      memberId: 'testUser',
      password: 'testPassword',
    });
  });
});
