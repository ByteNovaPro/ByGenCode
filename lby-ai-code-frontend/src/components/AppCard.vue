<template>
  <article class="app-card" :class="{ 'app-card--featured': featured }">
    <div class="app-preview">
      <img v-if="app.cover" :src="app.cover" :alt="app.appName || '应用封面'" />
      <div v-else class="app-placeholder">
        <span class="placeholder-mark">AI</span>
      </div>
      <span v-if="featured" class="featured-badge">精选</span>
    </div>
    <div class="app-info">
      <div class="app-meta">
        <a-avatar :src="app.user?.userAvatar" :size="40">
          {{ app.user?.userName?.charAt(0) || 'U' }}
        </a-avatar>
        <div class="app-copy">
          <h3 class="app-title">{{ app.appName || '未命名应用' }}</h3>
          <p class="app-author">
            {{ app.user?.userName || (featured ? 'LBY 精选' : '未知用户') }}
          </p>
        </div>
      </div>
      <div class="app-actions">
        <a-button type="primary" size="small" @click="handleViewChat">查看对话</a-button>
        <a-button v-if="app.deployKey" size="small" @click="handleViewWork">查看作品</a-button>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
interface Props {
  app: API.AppVO
  featured?: boolean
}

interface Emits {
  (e: 'view-chat', appId: string | number | undefined): void
  (e: 'view-work', app: API.AppVO): void
}

const props = withDefaults(defineProps<Props>(), {
  featured: false,
})

const emit = defineEmits<Emits>()

const handleViewChat = () => {
  emit('view-chat', props.app.id)
}

const handleViewWork = () => {
  emit('view-work', props.app)
}
</script>

<style scoped>
.app-card {
  display: flex;
  flex-direction: column;
  min-height: 292px;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 14px 36px rgba(15, 23, 42, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(148, 163, 184, 0.22);
  transition:
    transform 0.25s,
    box-shadow 0.25s,
    border-color 0.25s;
}

.app-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 42px rgba(15, 23, 42, 0.16);
  border-color: rgba(37, 99, 235, 0.24);
}

.app-preview {
  height: 176px;
  background: linear-gradient(135deg, #e0f2fe 0%, #ecfdf5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

.app-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s;
}

.app-card:hover .app-preview img {
  transform: scale(1.04);
}

.app-placeholder {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.74);
  color: #2563eb;
  box-shadow: inset 0 0 0 1px rgba(37, 99, 235, 0.12);
}

.placeholder-mark {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0;
}

.featured-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 3px 10px;
  border-radius: 999px;
  background: rgba(15, 118, 110, 0.92);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
}

.app-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  flex: 1;
}

.app-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.app-copy {
  flex: 1;
  min-width: 0;
}

.app-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px;
  color: #0f172a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.app-author {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.app-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: auto;
}

@media (max-width: 768px) {
  .app-card {
    min-height: 0;
  }

  .app-preview {
    height: 164px;
  }
}
</style>
