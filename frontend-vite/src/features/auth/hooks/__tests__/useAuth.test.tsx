import { act, renderHook } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import useAuth from '../useAuth';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

vi.mock('../../services/authApi', () => ({
  loginApi: vi.fn(),
  signupApi: vi.fn(),
}));

vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  useNavigate: () => mockNavigate,
}));

const queryClient = new QueryClient();

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <QueryClientProvider client={queryClient}>{children} </QueryClientProvider>
);

describe('useAuth', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    sessionStorage.clear();
  });

  it('로그인 성공 시 sessionStorage, toast, navigate가 호출되어야한다.', async () => {
    const { loginApi } = await import('../../services/authApi');
    const toast = (await import('react-hot-toast')).default;
    (loginApi as ReturnType<typeof vi.fn>).mockResolvedValue({
      accessToken: 'mockAccessToken',
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      result.current.login({ memberId: 'testUser', password: 'testPassword' });
    });

    await new Promise((resolve) => setTimeout(resolve, 0));
    expect(sessionStorage.getItem('accessToken')).toBe('mockAccessToken');
    expect(toast.success).toHaveBeenCalledWith('로그인 성공!');
    expect(mockNavigate).toHaveBeenCalled();
  });

  it('회원가입 성공 시 toast, navigate가 호출되어야 한다.', async () => {
    const { signupApi } = await import('../../services/authApi');
    const toast = (await import('react-hot-toast')).default;
    (signupApi as ReturnType<typeof vi.fn>).mockResolvedValue(undefined);

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      result.current.signup({
        name: 'testUser',
        memberId: 'testUser',
        password: 'testPassword',
      });
    });

    await new Promise((resolve) => setTimeout(resolve, 0));
    expect(toast.success).toHaveBeenCalledWith('회원가입 성공!');
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  it('로그인 실패 시 toast.error가 호출되어야한다.', async () => {
    const { loginApi } = await import('../../services/authApi');
    const toast = (await import('react-hot-toast')).default;
    (loginApi as ReturnType<typeof vi.fn>).mockRejectedValue(
      new Error('로그인 실패')
    );

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      result.current.login({ memberId: 'testUser', password: 'testPassword' });
    });

    await new Promise((resolve) => setTimeout(resolve, 0));
    expect(toast.error).toHaveBeenCalledWith('로그인 실패');
  });

  it('회원가입 실패 시 toast.error가 호출되어야한다.', async () => {
    const { signupApi } = await import('../../services/authApi');
    const toast = (await import('react-hot-toast')).default;
    (signupApi as ReturnType<typeof vi.fn>).mockRejectedValue(
      new Error('회원가입 실패')
    );

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      result.current.signup({
        name: 'testUser',
        memberId: 'testUser',
        password: 'testPassword',
      });
    });

    await new Promise((resolve) => setTimeout(resolve, 0));
    expect(toast.error).toHaveBeenCalledWith('회원가입 실패');
  });
});
