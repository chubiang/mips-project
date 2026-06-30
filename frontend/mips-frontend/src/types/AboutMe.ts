// src/types/AboutMe.ts

export interface SkillGroup {
  category: string
  items: string[]
  color: string
}

export type TimelineType = 'work' | 'education' | 'cert'

export interface TimelineItem {
  period: string
  title: string
  organization: string
  type: TimelineType // 'work' | 'education' | 'cert'
  highlights: string[]
}

export interface WorkStyleCard {
  emoji: string
  title: string
  desc: string
}
