<template>
  <div class="header-controls absolute top-10 right-0 flex flex-row" tabindex="0" @keydown.k="handleOpenModel">
    <span class="ob-drop-shadow" data-dia="search" @click="handleOpenModel">
      <svg-icon icon-class="search"/>
    </span>
    <Dropdown v-if="multiLanguage === 1" @command="handleClick">
      <span class="ob-drop-shadow" data-dia="language">
        <svg-icon icon-class="globe"/>
        <span v-if="$i18n.locale == 'cn'">中文</span>
        <span v-if="$i18n.locale == 'en'">EN</span>
      </span>
      <DropdownMenu>
        <DropdownItem name="cn">中文</DropdownItem>
        <DropdownItem name="en">English</DropdownItem>
      </DropdownMenu>
    </Dropdown>
    <template v-if="userInfo === ''">
      <span class="mr-3" @click="openLoginDialog">{{ t('settings.login') }}</span>
    </template>
    <template v-if="userInfo !== ''">
      <Dropdown hover>
        <span class="mr-2">
          <div class="flex-shrink-0">
            <div class="rounded-full ring-gray-100 overflow-hidden shaodw-lg w-9">
              <img :src="userInfo.avatar" alt="" class="avatar-img"/>
            </div>
          </div>
        </span>
        <DropdownMenu>
          <template v-if="!isMobile">
            <DropdownItem @click="openUserCenter">{{ t('settings.personal-center') }}</DropdownItem>
            <DropdownItem @click="openArticleManage">{{ t('settings.articleManagement') }}</DropdownItem>
            <DropdownItem @click="openChat">{{ t('聊天') }}</DropdownItem>
          </template>
          <DropdownItem @click="logout">{{ t('settings.logout') }}</DropdownItem>
        </DropdownMenu>
      </Dropdown>
    </template>
    <span class="ob-drop-shadow" data-dia="light-switch" no-hover-effect>
      <ThemeToggle/>
    </span>
  </div>
  <el-dialog v-model="loginDialogVisible" :fullscreen="isMobile" width="30%">
    <el-form @keyup.enter.native="login">
      <el-form-item class="mt-5" model="userInfo">
        <el-input v-model="loginInfo.username" placeholder="邮箱"/>
      </el-form-item>
      <el-form-item class="mt-8" model="userInfo" type="password">
        <el-input v-model="loginInfo.password" placeholder="密码" show-password type="password"/>
      </el-form-item>
      <el-form-item>
        <el-button class="mx-auto mt-3" size="large" type="primary" @click="login">登录</el-button>
      </el-form-item>
      <!--      <el-form-item>-->
      <!--        <el-button type="button" class="mx-auto my-el-button" @click="qqLogin">-->
      <!--          <img src="../../../assets/QQ.png" width="25" height="25"/>QQ登录-->
      <!--        </el-button>-->
      <!--      </el-form-item>-->
      <div class="mt-8">
        <span class="text" @click="openRegisterDialog">立即注册</span>
        <span class="text float-right" @click="openForgetPasswordDialog">忘记密码?</span>
      </div>
    </el-form>
  </el-dialog>
  <el-dialog v-model="registerDialogVisible" :fullscreen="isMobile" width="30%">
    <el-form>
      <el-form-item class="mt-5" model="userInfo">
        <el-input v-model="loginInfo.username" placeholder="邮箱"/>
      </el-form-item>
      <el-form-item class="mt-8" model="userInfo">
        <el-input v-model="loginInfo.code" placeholder="验证码">
          <template #append>
            <span class="text" @click="sendCode">发送</span>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item class="mt-8" model="userInfo" type="password">
        <el-input v-model="loginInfo.password" placeholder="密码" show-password type="password"/>
      </el-form-item>
      <el-form-item>
        <el-button class="mx-auto mt-3" size="large" type="primary" @click="register">注册</el-button>
      </el-form-item>
      <span class="text" @click="returnLoginDialog">已有帐号?登录</span>
    </el-form>
  </el-dialog>
  <el-dialog v-model="forgetPasswordDialogVisible" :fullscreen="isMobile" width="30%">
    <el-form>
      <el-form-item class="mt-5" model="userInfo">
        <el-input v-model="loginInfo.username" placeholder="邮箱"/>
      </el-form-item>
      <el-form-item class="mt-8" model="userInfo">
        <el-input v-model="loginInfo.code" placeholder="验证码">
          <template #append>
            <span class="text" @click="sendCode">发送</span>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item class="mt-8" model="userInfo" type="password">
        <el-input v-model="loginInfo.password" placeholder="新密码" show-password type="password"/>
      </el-form-item>
      <el-form-item>
        <el-button class="mx-auto mt-3" size="large" type="primary" @click="updatePassword">确定</el-button>
      </el-form-item>
      <span class="text" @click="returnLoginDialog">返回登录</span>
    </el-form>
  </el-dialog>
  <el-dialog v-model="articlePasswordDialogVisible" :fullscreen="isMobile" width="30%">
    <el-form @submit.native.prevent @keyup.enter.native="accessArticle">
      <el-form-item class="mt-5" model="userInfo">
        <el-input id="article-password-input" v-model="articlePassword" placeholder="文章受密码保护,请输入密码"/>
      </el-form-item>
      <el-form-item>
        <el-button class="mx-auto mt-3" size="large" type="primary" @click="accessArticle">校验密码</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
  <teleport to="body">
    <SearchModel/>
  </teleport>
</template>

<script lang="ts">
import {computed, defineComponent, getCurrentInstance, nextTick, reactive, toRef, toRefs} from 'vue'
import {Dropdown, DropdownItem, DropdownMenu} from '@/components/Dropdown'
import {useAppStore} from '@/stores/app'
import {useCommonStore} from '@/stores/common'
import {useUserStore} from '@/stores/user'
import {useRoute, useRouter} from 'vue-router'
import ThemeToggle from '@/components/ToggleSwitch/ThemeToggle.vue'
import api from '@/api/api'
import SearchModel from '@/components/SearchModel.vue'
import {useSearchStore} from '@/stores/search'
import config from '@/config/config'
import {useI18n} from 'vue-i18n'
import emitter from '@/utils/mitt'
import ArticleListManage from '@/views/ArticleListManage.vue'
import Chat from '@/views/Chat.vue'

export default defineComponent({
  name: 'Controls',
  components: {
    Dropdown,
    DropdownMenu,
    DropdownItem,
    ThemeToggle,
    SearchModel,
    ArticleListManage,
    Chat
  },
  setup() {
    const {t} = useI18n()
    const proxy: any = getCurrentInstance()?.appContext.config.globalProperties
    const appStore = useAppStore()
    const commonStore = useCommonStore()
    const userStore = useUserStore()
    const searchStore = useSearchStore()
    const route = useRoute()
    const router = useRouter()
    const loginInfo = reactive({
      username: '' as any,
      password: '' as any,
      code: '' as any
    })
    const reactiveDate = reactive({
      loginDialogVisible: false,
      registerDialogVisible: false,
      forgetPasswordDialogVisible: false,
      articlePasswordDialogVisible: false,
      articlePassword: '',
      articleId: ''
    })
    emitter.on('changeArticlePasswordDialogVisible', (articleId: any) => {
      reactiveDate.articlePasswordDialogVisible = true
      reactiveDate.articlePassword = ''
      reactiveDate.articleId = articleId
      nextTick(() => {
        document.getElementById('article-password-input')?.focus()
      })
    })
    const handleClick = (name: string): void => {
      appStore.changeLocale(name)
    }
    handleClick('cn');//设置默认为中文显示
    const login = () => {
      if (loginInfo.username.trim().length == 0 || loginInfo.password.trim().length == 0) {
        proxy.$notify({
          title: 'Warning',
          message: '账号或者密码不能为空',
          type: 'warning'
        })
        return
      }
      let params = new URLSearchParams()
      params.append('username', loginInfo.username)
      params.append('password', loginInfo.password)
      api.login(params).then(({data}) => {
        if (data.flag) {
          userStore.userInfo = data.data
          sessionStorage.setItem('token', data.data.token)
          userStore.token = data.data.token
          proxy.$notify({
            title: 'Success',
            message: '登录成功',
            type: 'success'
          })
          reactiveDate.loginDialogVisible = false
        } else {
          proxy.$notify({
            title: 'Error',
            message: data.message,
            type: 'error'
          })
        }
      })
    }
    const logout = () => {
      api.logout().then(({data}) => {
        if (data.flag) {
          userStore.userInfo = ''
          userStore.token = ''
          userStore.accessArticles = []
          sessionStorage.removeItem('token')
          proxy.$notify({
            title: 'Success',
            message: '登出成功',
            type: 'success'
          })
        } else {
          proxy.$notify({
            title: 'Error',
            message: data.message,
            type: 'error'
          })
        }
      })
    }
    const openUserCenter = () => {
      userStore.userVisible = true
    }
    const openArticleManage = () => {
      router.push({name: 'articleListManage'})
    }
    const openChat = () => {
      router.push({name: 'chat'})
    }

    const openLoginDialog = () => {
      reactiveDate.loginDialogVisible = true
    }
    const openRegisterDialog = () => {
      loginInfo.code = ''
      reactiveDate.loginDialogVisible = false
      reactiveDate.registerDialogVisible = true
    }
    const returnLoginDialog = () => {
      reactiveDate.registerDialogVisible = false
      reactiveDate.forgetPasswordDialogVisible = false
      reactiveDate.loginDialogVisible = true
    }
    const openForgetPasswordDialog = () => {
      loginInfo.code = ''
      reactiveDate.loginDialogVisible = false
      reactiveDate.forgetPasswordDialogVisible = true
    }
    const sendCode = () => {
      api.sendValidationCode(loginInfo.username).then(({data}) => {
        if (data.flag) {
          proxy.$notify({
            title: 'Success',
            message: '验证码已发送',
            type: 'success'
          })
        } else {
          proxy.$notify({
            title: 'Error',
            message: data.message,
            type: 'error'
          })
        }
      })
    }
    const register = () => {
      let params = {
        code: loginInfo.code,
        username: loginInfo.username,
        password: loginInfo.password
      }
      api.register(params).then(({data}) => {
        if (data.flag) {
          proxy.$notify({
            title: 'Success',
            message: '注册成功',
            type: 'success'
          })
          reactiveDate.registerDialogVisible = false
          reactiveDate.loginDialogVisible = true
        } else {
          proxy.$notify({
            title: 'Error',
            message: data.message,
            type: 'error'
          })
        }
      })
    }
    const handleOpenModel: any = (status: boolean) => {
      searchStore.setOpenModal(status)
    }

    const qqLogin = () => {
      userStore.currentUrl = route.path
      reactiveDate.loginDialogVisible = false
      if (commonStore.isMobile) {
        //@ts-ignore
        QC.Login.showPopup({
          appId: config.qqLogin.QQ_APP_ID,
          redirectURI: config.qqLogin.QQ_REDIRECT_URI
        })
      } else {
        window.open(
            'https://graph.qq.com/oauth2.0/show?which=Login&display=pc&client_id=' +
            +config.qqLogin.QQ_APP_ID +
            '&response_type=token&scope=all&redirect_uri=' +
            config.qqLogin.QQ_REDIRECT_URI,
            '_self'
        )
      }
    }
    const updatePassword = () => {
      api.updatePassword(loginInfo).then(({data}) => {
        if (data.flag) {
          proxy.$notify({
            title: 'Success',
            message: '修改成功',
            type: 'success'
          })
          reactiveDate.forgetPasswordDialogVisible = false
          reactiveDate.loginDialogVisible = true
        } else {
          proxy.$notify({
            title: 'Error',
            message: data.message,
            type: 'error'
          })
        }
      })
    }
    const accessArticle = () => {
      if (reactiveDate.articlePassword.trim().length == 0) {
        proxy.$notify({
          title: 'Warning',
          message: '密码不能为空',
          type: 'warning'
        })
        return
      }
      api
          .accessArticle({
            articleId: reactiveDate.articleId,
            articlePassword: reactiveDate.articlePassword
          })
          .then(({data}) => {
            if (data.flag) {
              reactiveDate.articlePasswordDialogVisible = false
              userStore.accessArticles.push(reactiveDate.articleId)
              router.push({path: '/articles/' + reactiveDate.articleId})
            } else {
              proxy.$notify({
                title: 'Error',
                message: data.message,
                type: 'error'
              })
            }
          })
    }
    return {
      handleOpenModel,
      loginInfo,
      ...toRefs(reactiveDate),
      userInfo: toRef(userStore.$state, 'userInfo'),
      isMobile: toRef(commonStore.$state, 'isMobile'),
      login,
      qqLogin,
      logout,
      handleClick,
      openUserCenter,
      openArticleManage,
      openChat,
      openLoginDialog,
      openRegisterDialog,
      returnLoginDialog,
      sendCode,
      register,
      updatePassword,
      openForgetPasswordDialog,
      accessArticle,
      multiLanguage: computed(() => {
        let websiteConfig: any = appStore.websiteConfig
        return websiteConfig.multiLanguage
      }),
      t
    }
  }
})
</script>
<style lang="scss">
.my-el-button {
  width: 300px !important;
}

.el-button {
  width: 300px;
}

.el-dialog__headerbtn {
  outline: none !important;
}

.el-input-group__append {
  background-color: var(--background-primary-alt) !important;
}

.el-form-item__label {
  text-align: left;
  width: 70px;
  color: var(--text-normal) !important;
}

.el-input__inner {
  color: var(--text-normal) !important;
  background-color: var(--background-primary-alt) !important;
}

.el-input__wrapper {
  background: var(--background-primary-alt) !important;
}
</style>
<style lang="scss" scoped>
.text {
  color: var(--text-normal);
  cursor: pointer;
}

#submit-button {
  outline: none;
  background: #0fb6d6;
}

.header-controls {
  span {
    display: flex;
    justify-content: center;
    align-items: center;
    color: #fff;
    cursor: pointer;
    transition: opacity 250ms ease;
    padding-right: 0.5rem;

    &[no-hover-effect] {
      &:hover {
        opacity: 1;
      }
    }

    &:hover {
      opacity: 0.5;
    }

    .svg-icon {
      stroke: #fff;
      height: 2rem;
      width: 2rem;
      margin-right: 0.5rem;
      pointer-events: none;
    }
  }

  .search-bar {
    @apply bg-transparent flex flex-row px-0 mr-2 rounded-full;
    opacity: 0;
    width: 0;
    transition: 300ms all ease-out;

    &.active {
      @apply bg-ob-deep-800;
      opacity: 0.95;
      width: 200px;

      imput {
        width: initial;
      }
    }

    &:focus {
      appearance: none;
      outline: none;
    }

    input {
      @apply flex flex-1 bg-transparent text-ob-normal px-6 box-border;
      width: 0;
      appearance: none;
      outline: none;
    }

    svg {
      @apply float-right;
    }
  }
}

.avatar-img {
  transition-property: transform;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 800ms;
  transform: rotate(-360deg);
}

.avatar-img:hover {
  transform: rotate(360deg);
}
</style>
