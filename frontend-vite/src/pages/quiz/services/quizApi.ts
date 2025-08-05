import { apiClient } from '@/services/axios';

// track Id와 가사로 가능한 퀴즈 카테 고리 요청
export async function fetchCategories(id: string, lyric: string) {
  const { data } = await apiClient.post(
    `/api/support/quiz/category/${id}`,
    lyric
  );
  return Object.keys(data);
}

// 퀴즈 생성
export async function createQuiz(
  musicId: string,
    category: string,
    lyric?: string,
  signal?: AbortSignal
) {
  const quizType = category.toUpperCase();
  const modifiedLyric = lyric?.replace(/\[.*?\]/g, '').replace(/\n/g, '.\n');

  return await apiClient.post(
    `/api/quiz/${category}`,
    {
      musicId,
      quizType,
      lyric: modifiedLyric,
    },
    { signal }
  );
}

export async function submitQuiz(
  musicId: string,
  category: string,
  answers: number[]
) {
  const quizType = category.toUpperCase();
  return await apiClient.post(`/api/quiz/submit/${category}`, {
    musicId,
    quizType,
    answers,
  });
}
