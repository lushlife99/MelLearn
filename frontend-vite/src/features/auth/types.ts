export interface LoginFormValues {
  memberId: string;
  password: string;
}

export interface SignupFormValues extends LoginFormValues {
  name: string;
}

export interface LoginResponse {
  accessToken: string;
}
