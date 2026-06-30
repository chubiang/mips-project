// src/pages/AboutMe.tsx
import { Mail, GitBranch, ExternalLink } from 'lucide-react'
import type { SkillGroup, TimelineItem, WorkStyleCard } from '@/types/AboutMe'
import profileImg from '@/assets/profile_img.png'

// ---------------------------------------------------------------------------
// 섹션 제목 공통 컴포넌트
// ---------------------------------------------------------------------------
function SectionTitle({ label }: { label: string }) {
  return (
    <div className="flex items-center gap-3 mb-6">
      <h2 className="text-xl font-bold text-slate-800 whitespace-nowrap">{label}</h2>
      <div className="flex-1 h-px bg-slate-200" />
    </div>
  )
}

// ---------------------------------------------------------------------------
// 1. 기본 정보
// ---------------------------------------------------------------------------
function ContactSection() {
  return (
    <section>
      <SectionTitle label="Contact & Channels" />
      <div className="flex flex-col sm:flex-row gap-8 items-start">
        {/* 아바타 */}
        <div className="shrink-0">
          <div className="w-28 h-28 rounded-2xl bg-slate-100 border border-slate-200 flex items-center justify-center text-4xl text-slate-300">
            {/* 프로필 사진 자리 */}
            <img src={profileImg} alt="Profile" className="w-full h-full object-cover rounded-2xl" />
          </div>
        </div>

        <div className="flex-1 space-y-4">
          {/* 이름 */}
          <div>
            <p className="text-2xl font-bold text-slate-800">김지연</p>
            <p className="text-base text-slate-400 font-medium mt-0.5">KIM JIYEON</p>
          </div>

          {/* 연락처 */}
          <div className="flex flex-wrap gap-3">
            <a
              href="mailto:your@email.com"
              className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-600 text-sm transition-colors"
            >
              <Mail size={14} /> jiyeeon1208@gmail.com
            </a>
          </div>

          {/* 채널 링크 */}
          <div className="flex flex-wrap gap-2">
            <a
              href="https://github.com/chubiang"
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-slate-200 hover:border-slate-400 text-slate-600 text-sm transition-colors"
            >
              <GitBranch size={14} /> GitHub
            </a>
            <a
              href="https://medium.com/@jiyeeon1208"
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-slate-200 hover:border-slate-400 text-slate-600 text-sm transition-colors"
            >
              <ExternalLink size={14} /> Medium
            </a>
          </div>
        </div>
      </div>
    </section>
  )
}

// ---------------------------------------------------------------------------
// 2. 한 줄 소개 및 요약
// ---------------------------------------------------------------------------
function SummarySection() {
  return (
    <section>
      <SectionTitle label="Summary" />
      <div className="space-y-4">
        {/* 캐치프레이즈 */}
        <div className="bg-blue-50 border-l-4 border-blue-500 rounded-r-xl px-5 py-4">
          <p className="text-lg font-bold text-blue-800 leading-snug">
            "사용자와 서비스 사이에 신뢰를 설계하는 개발자, 지연입니다."
          </p>
        </div>

        {/* 짧은 자기소개 */}
        <p className="text-slate-600 leading-relaxed text-sm md:text-base">
          빠른 완성보다 오래 쓰일 기준을 고민하고, 더 나은 구조를 만들기 위해 노력합니다.
        </p>
        <p className="text-slate-600 leading-relaxed text-sm md:text-base">
          새로운 기술을 배우고 적용하는 것을 좋아합니다.
        </p>
        <p className="text-slate-600 leading-relaxed text-sm md:text-base">
          동료들과 원활한 소통을 통해 팀워크를 강화합니다.
        </p>
      </div>
    </section>
  )
}

// ---------------------------------------------------------------------------
// 3. 핵심 역량
// ---------------------------------------------------------------------------
const skillGroups: SkillGroup[] = [
  {
    category: '기술 스택',
    items: ['Vue.js', 'jQuery', 'Angular', 'AngularJS', 'JavaScript', 'Webpack', 'Java',
           'Jenkins', 'Spring Boot', 'HTML/CSS', 'PostgreSQL', 'MSSQL', 'Oracle'],
    color: 'bg-blue-100 text-blue-700',
  },
  {
    category: '툴',
    items: ['xFrame5', 'Git', 'VS Code', 'IntelliJ', 'STS'],
    color: 'bg-green-100 text-green-700',
  },
  {
    category: '소프트 스킬',
    items: ['문제 해결', '커뮤니케이션', '데이터 분석'],
    color: 'bg-amber-100 text-amber-700',
  },
   {
    category: '개인학습 기술 스택',
    items: ['TypeScript', 'React', 'Vite', 'Kafka', 'Python', 'Redis', 'n8n', 'Docker', 'Kubernetes'],
    color: 'bg-blue-100 text-blue-700',
  },
]

function SkillsSection() {
  return (
    <section>
      <SectionTitle label="Skills & Tools" />
      <div className="space-y-5">
        {skillGroups.map(group => (
          <div key={group.category}>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
              {group.category}
            </p>
            <div className="flex flex-wrap gap-2">
              {group.items.map(item => (
                <span
                  key={item}
                  className={`px-3 py-1 rounded-full text-sm font-medium ${group.color}`}
                >
                  {item}
                </span>
              ))}
            </div>
          </div>
        ))}
      </div>
    </section>
  )
}

// ---------------------------------------------------------------------------
// 4. 핵심 이력 — 수직 타임라인
// ---------------------------------------------------------------------------
const timelineItems: TimelineItem[] = [
  {
    period: '2026. 05',
    type: 'cert',
    organization: 'Q-net',
    title: '정보처리기사 필기',
    highlights: [
      '합격'
    ],
  },
  {
    period: '2023. 07 ~ 2026. 04',
    type: 'work',
    organization: '고려신용정보㈜',
    title: 'IT협력실 / 매니저',
    highlights: [
      '금융서비스 이미지 고도화 시스템 구축',
      'K-VISION5 차세대 프로젝트',
      'SMART-WORK5 차세대 프로젝트',
    ],
  },
  {
    period: '2021. 09',
    type: 'cert',
    organization: '신용정보협회',
    title: '마이데이터관리사',
    highlights: [
      '합격'
    ],
  },
  {
    period: '2020. 10 ~ 2023. 04',
    type: 'work',
    organization: '웰컴에프앤디㈜',
    title: 'ICT서비스본부 / 계장',
    highlights: [
      'WFC캄보디아 차세대 기간계 프로젝트',
      '웰릭스렌탈 차세대 프로젝트'
    ],
  },
  {
    period: '2018. 06 ~ 2020. 09',
    type: 'work',
    organization: '소프트체인',
    title: '개발사업본부 / 사원',
    highlights: [
      'AssetCop 자산관리 신규 시스템 안정화',
      'KDB 산업은행 정보기기 관리 프로젝트'
    ],
  },
  {
    period: '2016. 04 ~ 2017. 01',
    type: 'work',
    organization: '크리젠솔루션',
    title: 'ASP / 사원',
    highlights: [
      '보증인 NICE SAFEKEY 발급 시스템 구축'
    ],
  },
  {
    period: '2015. 09 ~ 2016. 01',
    type: 'education',
    organization: '에이콘아카데미',
    title: '교육생',
    highlights: [
      '웹 콘텐츠 운영을 위한 응용 SW엔지니어링 양성과정'
    ],
  },
  {
    period: '2014. 02 ~ 2014. 07',
    type: 'education',
    organization: '아이티윌',
    title: '교육생',
    highlights: [
      '정부3.0중심의 공공IT스마트 웹 개발자 양성 취업과정'
    ],
  },
  {
    period: '2011. 12 ~ 2012. 02',
    type: 'education',
    organization: '시스게이트',
    title: '인턴',
    highlights: [
      '보조업무, PPT문서작성, 포토샵 작업 참여'
    ],
  },
  {
    period: '2013. 06 ~ 2013. 12',
    type: 'education',
    organization: '(주)아이티엔모아',
    title: '인턴',
    highlights: [
      '안드로이드 프로젝트 보조 개발'
    ],
  },
  {
    period: '2011. 12 ~ 2012. 02',
    type: 'education',
    organization: '시스게이트',
    title: '인턴',
    highlights: [
      '보조업무, PPT문서작성, 포토샵 작업 참여'
    ],
  },
]

const typeStyle: Record<TimelineItem['type'], { dot: string; badge: string; label: string }> = {
  work:      { dot: 'bg-blue-500',   badge: 'bg-blue-50 text-blue-600',   label: '경력' },
  education: { dot: 'bg-green-500',  badge: 'bg-green-50 text-green-600', label: '학력' },
  cert:      { dot: 'bg-amber-500',  badge: 'bg-amber-50 text-amber-600', label: '자격증' },
}

function ExperienceSection() {
  return (
    <section>
      <SectionTitle label="Experience & Education" />
      <div className="relative">
        {/* 수직 선 */}
        <div className="absolute left-[7px] top-2 bottom-2 w-px bg-slate-200" />

        <ol className="space-y-8">
          {timelineItems.map((item, idx) => {
            const style = typeStyle[item.type]
            return (
              <li key={idx} className="relative pl-8">
                {/* 점 */}
                <span className={`absolute left-0 top-1.5 w-3.5 h-3.5 rounded-full border-2 border-white ${style.dot} shadow-sm`} />

                <div className="flex flex-wrap items-center gap-2 mb-1">
                  <span className="text-xs text-slate-400 font-mono">{item.period}</span>
                  <span className={`px-2 py-0.5 rounded text-xs font-semibold ${style.badge}`}>
                    {style.label}
                  </span>
                </div>
                <p className="font-bold text-slate-800 text-sm md:text-base leading-snug">{item.title}</p>
                <p className="text-slate-500 text-sm mb-2">{item.organization}</p>
                {item.highlights.length > 0 && (
                  <ul className="space-y-1">
                    {item.highlights.map((h, i) => (
                      <li key={i} className="flex gap-2 text-sm text-slate-600">
                        <span className="text-slate-300 shrink-0">•</span>
                        {h}
                      </li>
                    ))}
                  </ul>
                )}
              </li>
            )
          })}
        </ol>
      </div>
    </section>
  )
}

// ---------------------------------------------------------------------------
// 5. 일하는 방식 및 가치관
// ---------------------------------------------------------------------------
const workStyles: WorkStyleCard[] = [
  { emoji: '🎯', title: '책임감', desc: '맡은 일에 대해 책임감을 가지고 최선을 다합니다.' },
  { emoji: '🔍', title: '세부사항', desc: '세부사항을 중요하게 생각하고, 구두가 아닌 정확한 정보를 바탕으로 의사결정을 합니다.' },
  { emoji: '🤝', title: '협업', desc: '팀원들과 원활한 소통과 협력을 통해 공동의 목표를 달성합니다.' },
  { emoji: '🚀', title: '성장', desc: '업무 프로세스 개선과 지속적인 학습을 통해 개인과 조직의 성장을 추구합니다.' },
]

function WorkStyleSection() {
  return (
    <section>
      <SectionTitle label="Work Style" />
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {workStyles.map(card => (
          <div
            key={card.title}
            className="p-5 rounded-xl border border-slate-200 bg-white hover:border-blue-200 hover:shadow-sm transition-all"
          >
            <span className="text-2xl mb-3 block">{card.emoji}</span>
            <p className="font-bold text-slate-800 mb-1">{card.title}</p>
            <p className="text-sm text-slate-500 leading-relaxed">{card.desc}</p>
          </div>
        ))}
      </div>
    </section>
  )
}

// ---------------------------------------------------------------------------
// 메인 페이지
// ---------------------------------------------------------------------------
export default function AboutMe() {
  return (
    <div className="max-w-3xl mx-auto space-y-14 py-4">
      <ContactSection />
      <SummarySection />
      <SkillsSection />
      <ExperienceSection />
      <WorkStyleSection />
    </div>
  )
}
