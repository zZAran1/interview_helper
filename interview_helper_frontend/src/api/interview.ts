import client from './client'

export interface InterviewData {
  id: number
  rating: number
  suggestion: string | null
  created_at: string
  first_question?: string | null
}

export interface SessionMessageData {
  id: number
  role: 'ai' | 'user'
  messagetext: string
  send_date: string
}

export async function startInterview(): Promise<InterviewData> {
  const res = await client.post<{ data: InterviewData }>('/interview/startInterview')
  return res.data.data
}

export async function viewInterviews(): Promise<InterviewData[]> {
  const res = await client.get<{ data: InterviewData[] }>('/interview/viewInterview')
  return res.data.data
}

export async function deleteInterview(id: number): Promise<void> {
  await client.delete('/interview/deleteInterview', { data: { id } })
}

export async function sendMessage(interviewId: number, messagetext: string): Promise<SessionMessageData[]> {
  const res = await client.post<{ data: SessionMessageData[] }>('/interview/sendMessage', {
    interview_id: interviewId,
    messagetext,
  })
  return res.data.data
}

export async function viewMessages(interviewId: number): Promise<SessionMessageData[]> {
  const res = await client.get<{ data: SessionMessageData[] }>('/interview/viewMessages', {
    params: { interviewId },
  })
  return res.data.data
}

export async function endInterview(id: number): Promise<InterviewData> {
  const res = await client.post<{ data: InterviewData }>('/interview/endInterview', { id })
  return res.data.data
}
