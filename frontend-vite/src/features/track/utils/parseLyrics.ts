export default function parseLyrics(lyrics: string) {
  const lines = lyrics.split('\n');
  const result = [];
  for (const line of lines) {
    const match = line.match(/\[(\d{2}):(\d{2}\.\d{2})\](.*)/);
    if (!match) continue;
    const [, min, sec, text] = match;
    const time = parseInt(min) * 60 + parseFloat(sec);

    result.push({ time, text: text.trim() });
  }

  return result;
}
