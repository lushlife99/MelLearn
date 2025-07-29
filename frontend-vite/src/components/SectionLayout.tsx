import React from 'react';

interface Props {
  title: string;
  children: React.ReactNode;
}

export default function SectionLayout({ title, children }: Props) {
  return (
    <section className='bg-white/5 backdrop-blur-lg rounded-2xl p-6 sm:p-8 border border-white/10 mb-8'>
      <h2 className='text-2xl font-bold text-white mb-6'>{title}</h2>
      {children}
    </section>
  );
}
