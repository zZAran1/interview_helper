import client from './client'

export interface ResumeData {
  real_name: string
  age: number
  gender: string
  education: string
  desired_position: string
  phone_number: string
  skill: string
  summary: string
  certificate: string
}

export async function viewResume(): Promise<ResumeData | null> {
  const res = await client.get<{ data: ResumeData | null }>('/resume/viewResume')
  return res.data.data
}

export async function createResume(data: ResumeData): Promise<void> {
  await client.post('/resume/createResume', data)
}

export async function updateResume(data: ResumeData): Promise<void> {
  await client.put('/resume/updateResume', data)
}

export async function deleteResume(): Promise<void> {
  await client.delete('/resume/deleteResume')
}

// ---- Education ----
export interface ResumeEducationData {
  id?: number
  school_name: string
  degree: string
  field_of_study: string
  start_date: string
  end_date: string
  core_courses: string
  appendix: string
}

export async function viewEducation(): Promise<ResumeEducationData[]> {
  const res = await client.get<{ data: ResumeEducationData[] }>('/resume/viewResumeEducation')
  return res.data.data
}
export async function createEducation(data: ResumeEducationData): Promise<void> {
  await client.post('/resume/createResumeEducation', data)
}
export async function updateEducation(data: ResumeEducationData): Promise<void> {
  await client.put('/resume/updateResumeEducation', data)
}
export async function deleteEducation(data: ResumeEducationData): Promise<void> {
  await client.delete('/resume/deleteResumeEducation', { data })
}

// ---- Project ----
export interface ResumeProjectData {
  id?: number
  project_name: string
  role: string
  start_date: string
  end_date: string
  description: string
}

export async function viewProjects(): Promise<ResumeProjectData[]> {
  const res = await client.get<{ data: ResumeProjectData[] }>('/resume/viewResumeProject')
  return res.data.data
}
export async function createProject(data: ResumeProjectData): Promise<void> {
  await client.post('/resume/createResumeProject', data)
}
export async function updateProject(data: ResumeProjectData): Promise<void> {
  await client.put('/resume/updateResumeProject', data)
}
export async function deleteProject(data: ResumeProjectData): Promise<void> {
  await client.delete('/resume/deleteResumeProject', { data })
}

// ---- Work ----
export interface ResumeWorkData {
  id?: number
  company_name: string
  position: string
  start_date: string
  end_date: string
  description: string
}

export async function viewWorks(): Promise<ResumeWorkData[]> {
  const res = await client.get<{ data: ResumeWorkData[] }>('/resume/viewResumeWork')
  return res.data.data
}
export async function createWork(data: ResumeWorkData): Promise<void> {
  await client.post('/resume/createResumeWork', data)
}
export async function updateWork(data: ResumeWorkData): Promise<void> {
  await client.put('/resume/updateResumeWork', data)
}
export async function deleteWork(data: ResumeWorkData): Promise<void> {
  await client.delete('/resume/deleteResumeWork', { data })
}

// ---- AI Optimize ----
export interface OptimizeContentParams {
  summary?: string
  education_appendix?: string
  project_description?: string
  work_description?: string
}

export interface OptimizeContentResult {
  summary?: string
  education_appendix?: string
  project_description?: string
  work_description?: string
}

export async function optimizeResume(data: OptimizeContentParams): Promise<OptimizeContentResult> {
  const res = await client.post<{ data: OptimizeContentResult }>('/resume/optimizeResume', data)
  return res.data.data
}
