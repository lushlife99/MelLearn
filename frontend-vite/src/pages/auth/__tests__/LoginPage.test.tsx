import { describe, expect, it, vi } from 'vitest';
import LoginPage from '../LoginPage';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';

const mockLogin = vi.fn();

vi.mock('@/features/auth/hooks/useAuth', () => {
  return {
    default: () => ({ login: mockLogin }),
  };
});
describe('LoginPage', () => {
  it('로그인 폼 제출 시 useAuth의 login 함수가 호출되어야 한다.', async () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>
    );
    await userEvent.type(screen.getByPlaceholderText('아이디'), 'testUser');
    await userEvent.type(
      screen.getByPlaceholderText('비밀번호'),
      'testPassword'
    );
    await userEvent.click(screen.getByRole('button', { name: '로그인' }));

    expect(mockLogin).toHaveBeenCalledWith({
      memberId: 'testUser',
      password: 'testPassword',
    });
  });
});
