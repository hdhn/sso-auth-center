<template>
  <el-form
    v-if="!chooseInstitutional"
    ref="loginRef"
    :model="loginForm"
    :rules="loginRules"
    :class="['login-form', isLoginPage ? 'login-page-style' : '']"
    :style="wrapStyle"
  >
    <div class="login_title">
      绑定微信账号&nbsp;
      <span v-if="mode !== 'production'" style="color: red"
        >({{ environmentMap[mode] || '开发' }}环境)</span
      >
    </div>

    <el-form-item prop="username">
      <el-input
        :disabled="phoneDisabled"
        v-model.trim="loginForm.username"
        type="text"
        size="large"
        auto-complete="off"
        placeholder="请输入手机号"
        :maxlength="11"
      >
        <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon"></svg-icon>
      </el-input>
    </el-form-item>
    <div style="display: flex; gap: 16px">
      <el-form-item prop="code" style="flex: 1">
        <el-input
          v-model.trim="loginForm.code"
          size="large"
          auto-complete="off"
          placeholder="请输入验证码"
          @keyup.enter="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon"></svg-icon>
        </el-input>
      </el-form-item>

      <div class="code_get">
        <span @click="getPhoneCode" v-if="!showTime">获取验证码</span>
        <span v-if="showTime">{{ second }}s</span>
      </div>
    </div>

    <el-button
      :loading="loading"
      size="large"
      type="primary"
      style="width: 100%"
      @click.prevent="handleLogin"
    >
      <span>提 交</span>
    </el-button>
    <slot name="footer"></slot>
  </el-form>
</template>
<script>
import { validPhone } from '@/utils/validate'
import { sendMsg } from '@/api/wechat'

export default {
  name: 'SendMessage',
  props: {
    isLoginPage: Boolean,
    phone: String,
    phoneDisabled: Boolean,
    state: String,
    chooseInstitutional: Boolean,
    wrapStyle: Object,
  },
  data() {
    return {
      showTime: false,
      second: 60,
      loading: false,
      loginForm: {
        username: '',
        password: '',
        rememberMe: false,
        code: '',
        uuid: '',
        phone: '',
        smsCode: '',
      },
      environmentMap: {
        development: '开发',
        test: '测试',
        production: '生产',
      },
      timer: null
    }
  },
  computed: {
    mode() {
      return process.env.NODE_ENV || 'development'
    },
    loginRules() {
      const validatePass = (rule, value, callback) => {
        if (!value) {
          callback(new Error('请输入手机号码'))
        } else {
          if (!validPhone(value)) {
            callback(new Error('请输入正确的手机号码'))
          }
          callback()
        }
      }
      return {
        username: [
          {
            required: true,
            trigger: 'blur',
            validator: validatePass,
          },
        ],
        password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
        code: [{ required: true, trigger: 'change', message: '请输入验证码' }],
        phone: [{ required: false, trigger: 'blur', message: '请输入手机号' }],
        smsCode: [{ required: false, trigger: 'blur', message: '请输入动态密码' }],
      }
    }
  },
  watch: {
    phone(newVal) {
      if (newVal) {
        this.loginForm.username = newVal
        this.loginForm.code = ''
      }
    }
  },
  mounted() {
    if (this.$refs.loginRef && this.$refs.loginRef.clearValidate) {
      this.$refs.loginRef.clearValidate()
    }
    this.loginForm.code = ''
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    getPhoneCode() {
      this.$refs.loginRef.validateField('username', err => {
        if (err) {
          return
        }
        this.showTime = true
        sendMsg({ phone: this.loginForm.username, state: this.state })
          .then(() => {
            this.timer = setInterval(() => {
              if (this.second > 0) {
                this.second = this.second - 1
              } else {
                clearInterval(this.timer)
                this.timer = null
                this.showTime = false
                this.second = 60
              }
            }, 1000)
          })
          .catch(() => {
            this.showTime = false
          })
        this.$nextTick(() => {
          const codeInput = this.$refs.loginRef.$el.querySelector('input[placeholder="请输入验证码"]')
          if (codeInput) {
            codeInput.focus()
          }
        })
      })
    },
    handleLogin() {
      this.$refs.loginRef.validate(valid => {
        if (valid) {
          this.$emit('onLogin', { ...this.loginForm, phone: this.loginForm.username })
        }
      })
    },
    setLoading(load) {
      console.log('load', load)
      this.loading = load
    }
  }
}
</script>

<style lang="scss" scoped>
// @import "@/assets/styles/mixin.scss";
// @import "@/assets/styles/variables.module.scss";

.login-form {
  border-radius: 8px;
  background: #ffffff;
  // height: 500px;
  // overflow: hidden;
  &.login-page-style {
    position: absolute;
    right: 19%;
    top: 20%;
    width: 420px;
    padding: 48px 30px 20px;
  }
  .code_input {
    // width: 360px;
    display: flex;
    justify-content: space-between;
  }
  .code_get {
    width: 102px;
    height: 40px;
    border-radius: 4px 4px 4px 4px;
    opacity: 1;
    border: 1px solid #e5e6eb;
    text-align: center;
    line-height: 40px;
    font-size: 14px;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #4e5969;
    cursor: pointer;
  }
  .login_title {
    font-size: 20px;
    font-family: PingFang SC-Medium, PingFang SC;
    font-weight: 500;
    color: #1d2129;
    margin-bottom: 20px;
  }
  .login_code {
    min-height: 14px;
    font-size: 14px;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #1890ff;
    text-align: right;
    margin-bottom: 16px;
    cursor: pointer;
  }
  .click_code {
    width: 360px;
    height: 40px;
    background: #ffffff;
    border-radius: 4px;
    border: 1px solid #e5e6eb;
    text-align: center;
    line-height: 40px;
    font-size: 14px;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #86909c;
    cursor: pointer;
  }
  .get_code {
    width: 102px;
    height: 40px;
    border-radius: 4px;
    border: 1px solid #e5e6eb;
    font-size: 14px;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #4e5969;
    text-align: center;
    line-height: 40px;
    margin-bottom: 10px;
    cursor: pointer;
  }
  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
    cursor: pointer;
    color: #959da8;
  }
  .show-pwd {
    height: 39px;
    width: 14px;
    position: absolute;
    left: 310px;
    font-size: 16px;
    cursor: pointer;
    user-select: none;
  }
}
</style>
