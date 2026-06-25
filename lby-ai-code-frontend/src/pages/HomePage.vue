<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { addApp, listMyAppVoByPage, listGoodAppVoByPage } from '@/api/appController'
import { getDeployUrl } from '@/config/env'
import AppCard from '@/components/AppCard.vue'
import { SendOutlined, ThunderboltOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const userPrompt = ref('')
const creating = ref(false)

const myApps = ref<API.AppVO[]>([])
const myAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

const featuredApps = ref<API.AppVO[]>([])
const featuredAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

const quickPrompts = [
  {
    title: '个人博客',
    description: '文章、分类、搜索与个人简介',
    prompt:
      '创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。',
  },
  {
    title: '企业官网',
    description: '品牌介绍、产品服务与案例展示',
    prompt:
      '设计一个专业的企业官网，包含公司介绍、产品服务展示、新闻资讯、联系我们等页面。采用商务风格的设计，包含轮播图、产品展示卡片、团队介绍、客户案例展示，支持多语言切换和在线客服功能。',
  },
  {
    title: '在线商城',
    description: '商品、购物车、订单与会员体系',
    prompt:
      '构建一个功能完整的在线商城，包含商品展示、购物车、用户注册登录、订单管理、支付结算等功能。设计现代化的商品卡片布局，支持商品搜索筛选、用户评价、优惠券系统和会员积分功能。',
  },
  {
    title: '作品集',
    description: '画廊、项目详情与联系入口',
    prompt:
      '制作一个精美的作品展示网站，适合设计师、摄影师、艺术家等创作者。包含作品画廊、项目详情页、个人简历、联系方式等模块。采用瀑布流或网格布局展示作品，支持图片放大预览和作品分类筛选。',
  },
]

const setPrompt = (prompt: string) => {
  userPrompt.value = prompt
}

const createApp = async () => {
  if (!userPrompt.value.trim()) {
    message.warning('请输入应用描述')
    return
  }

  if (!loginUserStore.loginUser.id) {
    message.warning('请先登录')
    await router.push('/user/login')
    return
  }

  creating.value = true
  try {
    const res = await addApp({
      initPrompt: userPrompt.value.trim(),
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('应用创建成功')
      const appId = String(res.data.data)
      await router.push(`/app/chat/${appId}`)
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch (error) {
    console.error('创建应用失败：', error)
    message.error('创建失败，请重试')
  } finally {
    creating.value = false
  }
}

const loadMyApps = async () => {
  if (!loginUserStore.loginUser.id) {
    return
  }

  try {
    const res = await listMyAppVoByPage({
      pageNum: myAppsPage.current,
      pageSize: myAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      myApps.value = res.data.data.records || []
      myAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载我的应用失败：', error)
  }
}

const loadFeaturedApps = async () => {
  try {
    const res = await listGoodAppVoByPage({
      pageNum: featuredAppsPage.current,
      pageSize: featuredAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      featuredApps.value = res.data.data.records || []
      featuredAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载精选应用失败：', error)
  }
}

const viewChat = (appId: string | number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}?view=1`)
  }
}

const viewWork = (app: API.AppVO) => {
  if (app.deployKey) {
    const url = getDeployUrl(app.deployKey)
    window.open(url, '_blank')
  }
}

const handleMouseMove = (e: MouseEvent) => {
  const { clientX, clientY } = e
  const { innerWidth, innerHeight } = window
  const x = (clientX / innerWidth) * 100
  const y = (clientY / innerHeight) * 100

  document.documentElement.style.setProperty('--mouse-x', `${x}%`)
  document.documentElement.style.setProperty('--mouse-y', `${y}%`)
}

onMounted(() => {
  loadMyApps()
  loadFeaturedApps()
  document.addEventListener('mousemove', handleMouseMove)
})

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
})
</script>

<template>
  <div id="homePage">
    <div class="container">
      <section class="hero-section">
        <div class="hero-kicker">
          <ThunderboltOutlined />
          <span>AI 零代码应用生成</span>
        </div>
        <h1 class="hero-title">LBY AI 应用生成平台</h1>
        <p class="hero-description">输入一句需求，快速生成、预览并部署你的网站应用。</p>
      </section>

      <section class="input-section" aria-label="创建应用">
        <a-textarea
          v-model:value="userPrompt"
          placeholder="例如：帮我创建一个适合摄影师的作品集网站，包含首页、作品分类、项目详情和联系表单"
          :rows="4"
          :maxlength="1000"
          class="prompt-input"
        />
        <div class="prompt-meta">
          <span>描述越具体，生成结果越贴近预期</span>
          <span>{{ userPrompt.length }}/1000</span>
        </div>
        <div class="input-actions">
          <a-button
            type="primary"
            shape="circle"
            size="large"
            @click="createApp"
            :loading="creating"
            aria-label="创建应用"
          >
            <template #icon>
              <SendOutlined />
            </template>
          </a-button>
        </div>
      </section>

      <section class="quick-actions" aria-label="快捷模板">
        <a-button
          v-for="item in quickPrompts"
          :key="item.title"
          type="default"
          class="quick-action"
          @click="setPrompt(item.prompt)"
        >
          <span class="quick-action-title">{{ item.title }}</span>
          <span class="quick-action-description">{{ item.description }}</span>
        </a-button>
      </section>

      <section class="section">
        <h2 class="section-title">我的作品</h2>
        <div v-if="myApps.length > 0" class="app-grid">
          <AppCard
            v-for="app in myApps"
            :key="app.id"
            :app="app"
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <a-empty
          v-else
          :description="
            loginUserStore.loginUser.id
              ? '还没有作品，输入需求创建第一个应用吧'
              : '登录后可查看你的作品'
          "
        />
        <div v-if="myAppsPage.total > myAppsPage.pageSize" class="pagination-wrapper">
          <a-pagination
            v-model:current="myAppsPage.current"
            v-model:page-size="myAppsPage.pageSize"
            :total="myAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个应用`"
            @change="loadMyApps"
          />
        </div>
      </section>

      <section class="section">
        <h2 class="section-title">精选案例</h2>
        <div v-if="featuredApps.length > 0" class="featured-grid">
          <AppCard
            v-for="app in featuredApps"
            :key="app.id"
            :app="app"
            :featured="true"
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <a-empty v-else description="暂无精选案例" />
        <div v-if="featuredAppsPage.total > featuredAppsPage.pageSize" class="pagination-wrapper">
          <a-pagination
            v-model:current="featuredAppsPage.current"
            v-model:page-size="featuredAppsPage.pageSize"
            :total="featuredAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个案例`"
            @change="loadFeaturedApps"
          />
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
#homePage {
  width: 100%;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  background:
    radial-gradient(circle at 10% 20%, rgba(59, 130, 246, 0.12) 0%, transparent 32%),
    radial-gradient(circle at 88% 12%, rgba(16, 185, 129, 0.12) 0%, transparent 28%),
    linear-gradient(180deg, #f8fafc 0%, #eef4fb 45%, #f8fafc 100%);
  position: relative;
  overflow: hidden;
}

#homePage::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(59, 130, 246, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(59, 130, 246, 0.05) 1px, transparent 1px),
    linear-gradient(rgba(15, 118, 110, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(15, 118, 110, 0.04) 1px, transparent 1px);
  background-size:
    112px 112px,
    112px 112px,
    24px 24px,
    24px 24px;
  pointer-events: none;
  animation: gridFloat 24s ease-in-out infinite;
}

#homePage::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(
    560px circle at var(--mouse-x, 50%) var(--mouse-y, 50%),
    rgba(37, 99, 235, 0.08) 0%,
    rgba(20, 184, 166, 0.05) 42%,
    transparent 78%
  );
  pointer-events: none;
  animation: lightPulse 8s ease-in-out infinite alternate;
}

@keyframes gridFloat {
  0%,
  100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(5px, 5px);
  }
}

@keyframes lightPulse {
  0% {
    opacity: 0.35;
  }
  100% {
    opacity: 0.7;
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 20px 20px;
  position: relative;
  z-index: 2;
  width: 100%;
  box-sizing: border-box;
}

.hero-section {
  text-align: center;
  padding: 64px 0 40px;
  margin-bottom: 16px;
  color: #1e293b;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(
    ellipse 720px 320px at center,
    rgba(59, 130, 246, 0.1) 0%,
    transparent 70%
  );
  animation: heroGlow 10s ease-in-out infinite alternate;
}

@keyframes heroGlow {
  0% {
    opacity: 0.6;
    transform: scale(1);
  }
  100% {
    opacity: 1;
    transform: scale(1.02);
  }
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 18px;
  padding: 6px 14px;
  border: 1px solid rgba(37, 99, 235, 0.16);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  color: #2563eb;
  font-size: 14px;
  font-weight: 600;
  position: relative;
  z-index: 2;
}

.hero-title {
  font-size: 52px;
  font-weight: 700;
  margin: 0 0 16px;
  line-height: 1.2;
  background: linear-gradient(135deg, #1d4ed8 0%, #0f766e 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0;
  position: relative;
  z-index: 2;
  animation: titleShimmer 8s ease-in-out infinite;
}

@keyframes titleShimmer {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.hero-description {
  font-size: 20px;
  margin: 0;
  color: #475569;
  position: relative;
  z-index: 2;
}

.input-section {
  position: relative;
  margin: 0 auto 24px;
  max-width: 840px;
}

.prompt-input {
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  font-size: 16px;
  padding: 20px 64px 46px 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.12);
}

.prompt-input:focus {
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 20px 50px rgba(37, 99, 235, 0.16);
}

.input-actions {
  position: absolute;
  bottom: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  align-items: center;
}

.prompt-meta {
  position: absolute;
  left: 20px;
  right: 72px;
  bottom: 14px;
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: #64748b;
  font-size: 12px;
  pointer-events: none;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin: 0 auto 56px;
  max-width: 960px;
}

.quick-actions .ant-btn {
  border-radius: 8px;
  padding: 14px 16px;
  height: auto;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(148, 163, 184, 0.3);
  color: #334155;
  backdrop-filter: blur(15px);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  text-align: left;
}

.quick-actions .ant-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.1), transparent);
  transition: left 0.5s;
}

.quick-actions .ant-btn:hover::before {
  left: 100%;
}

.quick-actions .ant-btn:hover {
  background: rgba(255, 255, 255, 0.94);
  border-color: rgba(37, 99, 235, 0.35);
  color: #1d4ed8;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.18);
}

.quick-action {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.quick-action-title {
  display: block;
  font-weight: 600;
  line-height: 1.4;
}

.quick-action-description {
  display: block;
  margin-top: 4px;
  color: #64748b;
  font-size: 12px;
  line-height: 1.4;
  white-space: normal;
}

.section {
  margin-bottom: 56px;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #1e293b;
}

.app-grid,
.featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 900px) {
  .quick-actions {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .container {
    padding: 20px 14px;
  }

  .hero-section {
    padding: 48px 0 28px;
  }

  .hero-title {
    font-size: 32px;
  }

  .hero-description {
    font-size: 16px;
  }

  .app-grid,
  .featured-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions {
    grid-template-columns: 1fr;
    margin-bottom: 40px;
  }

  .prompt-meta {
    position: static;
    margin-top: 8px;
    padding: 0 2px;
  }

  .prompt-input {
    padding: 16px 58px 16px 16px;
  }
}
</style>
