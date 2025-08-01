import type { LucideIcon } from 'lucide-react';

export interface Category {
  id: number;
  title: string;
  description: string;
  detailDescription: string;
  Icon: LucideIcon;
  name: string;
  color: string;
  bgColor: string;
  lessons: number;
  duration: string;
  level: string;
  popularity: number;
  features: string[];
}
