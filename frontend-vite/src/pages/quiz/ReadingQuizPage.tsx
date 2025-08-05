import {
  ArrowLeft,
  CheckCircle,
  Circle,
  Target,
  BookOpen,
  FileText,
  Headphones,
  PenTool,
  Mic,
} from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';
import { useLocation, useParams } from 'react-router-dom';
import useQuiz from './hooks/useQuiz';

// Mock quiz data
const mockQuiz = {
  id: 1,
  level: 2,
  musicId: 'track123',
  quizzes: [
    {
      id: 1,
      question: '다음 문장에서 올바른 문법 형태를 선택하세요:',
      answer: 2,
      comment: '현재완료 시제는 have/has + 과거분사 형태로 사용됩니다.',
      optionList: [
        'I have went to the store yesterday.',
        'I have gone to the store already.',
        'I have go to the store now.',
        'I have going to the store.',
      ],
    },
    {
      id: 2,
      question: '빈칸에 들어갈 가장 적절한 전치사는?',
      answer: 1,
      comment: "시간을 나타낼 때는 'at'을 사용합니다.",
      optionList: ['at', 'on', 'in', 'by'],
    },
    {
      id: 3,
      question: '다음 중 복수형이 올바른 것은?',
      answer: 3,
      comment: 'child의 복수형은 children입니다.',
      optionList: ['childs', 'childes', 'children', 'child'],
    },
  ],
};

const categoryIcons = {
  listening: Headphones,
  grammar: BookOpen,
  reading: FileText,
  writing: PenTool,
  speaking: Mic,
};

const categoryColors = {
  listening: 'from-blue-500 to-cyan-500',
  grammar: 'from-green-500 to-emerald-500',
  reading: 'from-purple-500 to-violet-500',
  writing: 'from-orange-500 to-red-500',
  speaking: 'from-pink-500 to-rose-500',
};

export default function ReadingQuizPage() {
  const [index, setIndex] = useState(0);
  const [progress, setProgress] = useState(0);
  const [answers, setAnswers] = useState(
    new Array(mockQuiz.quizzes.length).fill(0)
  );
  const [isLast, setIsLast] = useState(false);
  const { pathname } = useLocation();
  const category = pathname.split('/')[2] as keyof typeof categoryIcons;
  // Mock data
  const problem = mockQuiz;
  const { id } = useParams();
  const { submit } = useQuiz(id || '');

  const CategoryIcon = categoryIcons[category] || BookOpen;
  const categoryColor =
    categoryColors[category] || 'from-green-500 to-emerald-500';

  const getLevel = (level: number) => {
    switch (level) {
      case 1:
        return '초급';
      case 2:
        return '중급';
      case 3:
        return '고급';
      default:
        return '초급';
    }
  };

  const getLevelColor = (level: number) => {
    switch (level) {
      case 1:
        return 'text-green-400';
      case 2:
        return 'text-yellow-400';
      case 3:
        return 'text-red-400';
      default:
        return 'text-green-400';
    }
  };

  const onChangeAnswer = (answer: number) => {
    if (answer !== undefined) {
      const newArr = [...answers];
      newArr[index] = answer;
      setAnswers(newArr);
    }
  };

  const handleNextProblem = () => {
    if (index <= problem.quizzes.length - 2) {
      setIndex(index + 1);
      setProgress(((index + 1) / problem.quizzes.length) * 100);
    }
    if (index === problem.quizzes.length - 1) {
      setIsLast(true);
    }
  };

  const submitProblem = async () => {
    const hasZeroAnswer = answers.some((answer) => answer === 0);
    if (hasZeroAnswer) {
      toast.error('풀지 않은 문제가 있습니다.');
    } else {
      submit({ category, answers });
    }
  };

  const move = () => {
    if (index !== 0) {
      setIndex(index - 1);
      setProgress((index / problem.quizzes.length) * 100);
      setIsLast(false);
    } else {
      // 뒤로가기
      console.log('Go back');
    }
  };

  const currentQuiz = problem.quizzes[index];

  return (
    <div className='min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900'>
      <div className='relative z-10 md:ml-0 lg:ml-20 xl:ml-64 transition-all duration-300'>
        <div className='max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6'>
          {/* 헤더 */}
          <div className='flex items-center justify-between mb-8'>
            <button
              onClick={move}
              className='p-3 rounded-full bg-white/10 backdrop-blur-sm hover:bg-white/20 transition-all duration-300 hover:scale-110'
            >
              <ArrowLeft className='w-6 h-6 text-white' />
            </button>

            <div className='text-center'>
              <div className='flex items-center justify-center gap-3 mb-2'>
                <div
                  className={`p-2 rounded-lg bg-gradient-to-r ${categoryColor}`}
                >
                  <CategoryIcon className='w-6 h-6 text-white' />
                </div>
                <h1 className='text-2xl sm:text-3xl font-bold text-white'>
                  {category.charAt(0).toUpperCase() + category.slice(1)}
                </h1>
              </div>
              <div className='flex items-center justify-center gap-2'>
                <Target className={`w-4 h-4 ${getLevelColor(problem.level)}`} />
                <span
                  className={`text-lg font-semibold ${getLevelColor(
                    problem.level
                  )}`}
                >
                  {getLevel(problem.level)}
                </span>
              </div>
            </div>

            <div className='w-12'></div>
          </div>

          {/* 진행률 바 */}
          <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 border border-white/10 mb-8'>
            <div className='flex items-center justify-between mb-4'>
              <span className='text-white/70 text-sm font-medium'>진행률</span>
              <span className='text-white font-semibold'>
                {index + 1} / {problem.quizzes.length}
              </span>
            </div>
            <div className='w-full bg-white/20 rounded-full h-2'>
              <div
                className={`bg-gradient-to-r ${categoryColor} h-2 rounded-full transition-all duration-500 shadow-lg`}
                style={{ width: `${progress}%` }}
              />
            </div>
          </div>

          {/* 문제 카드 */}
          <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-8 border border-white/10 mb-8'>
            {/* 문제 번호 및 질문 */}
            <div className='mb-8'>
              <div className='flex items-center gap-3 mb-4'>
                <span
                  className={`text-4xl font-extrabold bg-gradient-to-r ${categoryColor} bg-clip-text text-transparent`}
                >
                  Q{index + 1}.
                </span>
              </div>
              <h2 className='text-2xl sm:text-3xl font-bold text-white leading-relaxed'>
                {currentQuiz.question}
              </h2>
            </div>

            {/* 선택지 */}
            <div className='space-y-4'>
              {currentQuiz.optionList.map((option, idx) => (
                <div
                  key={idx}
                  onClick={() => onChangeAnswer(idx + 1)}
                  className={`group flex items-center gap-4 p-5 rounded-xl cursor-pointer transition-all duration-300 ${
                    answers[index] === idx + 1
                      ? `bg-gradient-to-r ${categoryColor.replace(
                          'to-',
                          'to-opacity-20 '
                        )} border border-white/30`
                      : 'bg-white/5 hover:bg-white/10 border border-white/10 hover:border-white/20'
                  }`}
                >
                  {/* 체크박스 */}
                  <div className='flex-shrink-0'>
                    {answers[index] === idx + 1 ? (
                      <CheckCircle
                        className={`w-6 h-6 ${
                          answers[index] === idx + 1
                            ? 'text-white'
                            : 'text-white/40'
                        }`}
                      />
                    ) : (
                      <Circle className='w-6 h-6 text-white/40 group-hover:text-white/60' />
                    )}
                  </div>

                  {/* 선택지 텍스트 */}
                  <div className='flex-1'>
                    <div className='flex items-center gap-3'>
                      <span
                        className={`text-lg font-semibold ${
                          answers[index] === idx + 1
                            ? 'text-white'
                            : 'text-white/80 group-hover:text-white'
                        }`}
                      >
                        {idx + 1}.
                      </span>
                      <span
                        className={`text-lg ${
                          answers[index] === idx + 1
                            ? 'text-white'
                            : 'text-white/80 group-hover:text-white'
                        }`}
                      >
                        {option.replace(/^\d+\.\s*/, '')}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* 하단 버튼 */}
          <div className='flex justify-center'>
            <button
              onClick={!isLast ? handleNextProblem : submitProblem}
              disabled={answers[index] === 0}
              className={`px-8 py-4 rounded-2xl font-semibold text-lg transition-all duration-300 ${
                answers[index] === 0
                  ? 'bg-white/10 text-white/50 cursor-not-allowed'
                  : `bg-gradient-to-r ${categoryColor} text-white hover:shadow-2xl hover:scale-105 active:scale-95`
              }`}
            >
              {isLast ? '결과 확인' : '다음 문제'}
            </button>
          </div>

          {/* 문제 네비게이션 */}
          <div className='mt-8 flex justify-center'>
            <div className='flex gap-2'>
              {problem.quizzes.map((_, idx) => (
                <button
                  key={idx}
                  onClick={() => {
                    setIndex(idx);
                    setProgress((idx / problem.quizzes.length) * 100);
                    setIsLast(idx === problem.quizzes.length - 1);
                  }}
                  className={`w-3 h-3 rounded-full transition-all duration-300 ${
                    idx === index
                      ? `bg-gradient-to-r ${categoryColor} shadow-lg`
                      : answers[idx] !== 0
                      ? 'bg-white/60'
                      : 'bg-white/20 hover:bg-white/40'
                  }`}
                />
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
