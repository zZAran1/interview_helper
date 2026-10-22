import client from './client'

export interface RegisterParams {
  email: string
  username: string
  password: string
}

export interface LoginParams {
  email: string
  password: string
}

export interface LoginResult {
  token: string
}

export interface UserProfile {
  username: string
  email: string
  created_at: string
}

export async function register(params: RegisterParams): Promise<void> {
  await client.post('/user/register', params)
}

export async function login(params: LoginParams): Promise<LoginResult> {
  const res = await client.post<{ data: LoginResult }>('/user/login', params)
  return res.data.data
}

export async function getProfile(): Promise<UserProfile> {
  const res = await client.get<{ data: UserProfile }>('/user/profile')
  return res.data.data
}

export async function deleteAccount(): Promise<void> {
  await client.delete('/user/delete')
}
