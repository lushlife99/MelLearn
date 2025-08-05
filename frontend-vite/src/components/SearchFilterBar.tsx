import { Filter, Search } from 'lucide-react';
interface Props {
  placeholder: string;
  query: string;
  sortBy: string;
  sortOptions: { value: string; label: string }[];
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSelectChange: (e: React.ChangeEvent<HTMLSelectElement>) => void;
}

export default function SearchFilterBar({
  placeholder,
  query,
  sortBy,
  sortOptions,
  onChange,
  onSelectChange,
}: Props) {
  return (
    <div className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 border border-white/10'>
      <div className='flex flex-col sm:flex-row gap-4'>
        <div className='relative flex-1'>
          <Search className='absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-white/50' />
          <input
            type='text'
            placeholder={placeholder}
            value={query}
            onChange={onChange}
            className='w-full pl-10 pr-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all duration-300'
          />
        </div>
        <div className='relative'>
          <Filter className='absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-white/50' />
          <select
            value={sortBy}
            onChange={onSelectChange}
            className='pl-10 pr-8 py-3 bg-white/10 border border-white/20 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all duration-300 appearance-none cursor-pointer'
          >
            {sortOptions.map(({ value, label }) => (
              <option key={value} value={value} className='bg-gray-800'>
                {label}
              </option>
            ))}
          </select>
        </div>
      </div>
    </div>
  );
}
