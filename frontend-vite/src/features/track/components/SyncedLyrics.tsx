import { useEffect, useRef, useState } from 'react';
import type { Lyric } from '../types/track';

interface Props {
  lyrics?: Lyric[];
  getCurrentTime: () => number;
}

export default function SyncedLyrics({ lyrics = [], getCurrentTime }: Props) {
  const [currentIndex, setCurrentIndex] = useState(0);
  const containerRef = useRef<HTMLDivElement>(null);
  const lineRefs = useRef<(HTMLDivElement | null)[]>([]);

  useEffect(() => {
    let rafId: number;

    const update = () => {
      const currentTime = getCurrentTime();
      const index = lyrics.findIndex((line, i) => {
        const nextTime = lyrics[i + 1]?.time ?? Infinity;
        return currentTime >= line.time && currentTime < nextTime;
      });

      if (index !== -1 && index !== currentIndex) {
        setCurrentIndex(index);

        // 스크롤 중앙으로 맞추기
        const el = lineRefs.current[index];
        if (el && containerRef.current) {
          const offsetTop = el.offsetTop;
          containerRef.current.scrollTo({
            top:
              offsetTop -
              containerRef.current.clientHeight / 2 +
              el.clientHeight / 2,
            behavior: 'smooth',
          });
        }
      }

      rafId = requestAnimationFrame(update);
    };

    rafId = requestAnimationFrame(update);

    return () => cancelAnimationFrame(rafId);
  }, [lyrics, getCurrentTime, currentIndex]);

  return (
    <div
      ref={containerRef}
      className='flex-1 bg-white/5 backdrop-blur-sm rounded-2xl p-6 border border-white/10 max-h-72 overflow-y-auto'
    >
      <h3 className='text-xl font-semibold text-white mb-4'>가사</h3>

      {lyrics.length === 0 ? (
        <div className='text-white/50 text-center py-8'>
          <p>가사 정보가 없습니다</p>
        </div>
      ) : (
        <div className='text-white/80 leading-relaxed whitespace-pre-line'>
          {lyrics.map((lyric, i) => (
            <div
              key={i}
              ref={(el) => {
                lineRefs.current[i] = el;
              }}
              className={`transition-all  ${
                i === currentIndex
                  ? 'text-blue-400 font-bold text-lg'
                  : 'text-white/50'
              }`}
            >
              {lyric.text}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
