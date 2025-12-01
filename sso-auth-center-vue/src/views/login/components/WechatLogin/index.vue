<template>
  <div>
    <div :class="loginTypeClass" @click="changeLoginType">
      <el-tooltip
        class="box-item"
        :content="showWeChatQrCode ? '账号登录' : '体验微信扫码登录'"
        placement="right-start"
      >
        <QrcodeVue v-if="!showWeChatQrCode" :value="demoCode" :size="50" />
        <i v-else class="el-icon-monitor" style="font-size: 40px;"></i>
      </el-tooltip>
    </div>
    <div class="login-wechat-wrap" v-show="showWeChatQrCode">
      <div class="login_title">
        微信扫码登录&nbsp;
      </div>
      <QRCode
        :loading="loading"
        :doRefresh="doRefresh"
        :scanCode="scanCode"
        :authCode="authCode"
        tips="请使用微信扫码登录"
      />
    </div>
  </div>
  </template>
  <script>
  import QrcodeVue from 'qrcode.vue'
  import { getAuthLink, getAuthResult } from '@/api/wechat.js'
  import QRCode from '../QRCode'
  import { sleep } from '@/utils/index.js'
  
  export default {
    name: 'WechatLogin',
    components: {
      QrcodeVue,
      QRCode
    },
    props: {
      afterLogin: {
        type: Function,
        default: () => {},
      },
    },
    data() {
      return {
        demoCode: 'test',
        showWeChatQrCode: false,
        loopGetScanStatus: null,
        authCode: '无效二维码',
        loading: false,
        stateValue: null,
        scanCode: 0
      }
    },
    computed: {
      loginTypeClass() {
        return {
          desktop: this.showWeChatQrCode,
          'login-wechat': true,
        }
      }
    },
    beforeDestroy() {
      if (this.loopGetScanStatus) {
        clearInterval(this.loopGetScanStatus)
      }
    },
    methods: {
      doRefresh() {
        if (this.loading) {
          return
        }
        this.scanCode = 0
        this.getQrCode()
      },
      /**
       * 判定用户是否首次绑定
       * 0-未关注公众号
       * 1-首次绑定
       * 2-已经绑定
       * 3-授权中
       * 4-授权失败
       */
      getScanStatus(immediate = false) {
        if (!this.showWeChatQrCode || this.loading) {
          return
        }
        sleep(immediate ? 0 : 2000)
          .then(() => getAuthResult({ state: this.stateValue }))
          .then(res => {
            if (res.code === 200) {
              const { code, token } = res.data
              this.scanCode = code - 0
              if ([1, 2].includes(this.scanCode)) {
                this.loading = true
                setTimeout(() => {
                  this.loading = false
                  // 1. 绑定手机号码
                  if (this.scanCode === 1) {
                    this.$router.push({
                      path: '/wechat-user-bind-phone',
                      query: { state: this.stateValue },
                    })
                  } else {
                    // this.$store.dispatch('ScanLogin', { token }).then(() => {
                      this.afterLogin()
                    // })
                  }
                }, 1500)
              } else {
                if (!this.loading) {
                  this.getScanStatus()
                }
              }
            }
          })
          .catch(e => {
            console.error('获取扫码状态失败:', e)
            this.loading = false
          })
      },
      getQrCode() {
        this.loading = true
        sleep(1000).then(() => getAuthLink(1))
          .then(res => {
            this.loading = false
            if (res.code === 200) {
              const data = res.data
              this.authCode = data.authorizeUrl
              this.stateValue = data.state
              this.scanCode = 0
  
              this.getScanStatus(true)
            } else {
              this.scanCode = -1
            }
          })
          .catch(e => {
            console.error(e)
            this.scanCode = -1
            this.loading = false
          })
      },
      changeLoginType() {
        this.showWeChatQrCode = !this.showWeChatQrCode
        if (this.showWeChatQrCode) {
          this.scanCode = 0
          this.getQrCode()
        }
      }
    }
  }
  </script>
  
  <style lang="scss" scoped>
  .login-wechat {
    position: absolute;
    top: 10px;
    right: 10px;
    cursor: pointer;
    overflow: hidden;
    z-index: 90;
    &::before {
      content: ' ';
      display: inline-block;
      position: absolute;
      width: 100px;
      height: 46px;
      background-color: #fff;
      left: -50px;
      top: 11px;
      transform: rotate(45deg);
      z-index: 9;
    }
    &.desktop::before {
      left: -60px;
    }
  }
  
  .login-wechat-wrap {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 9;
    background-color: #fff;
    padding: 48px 30px 20px;
    border-radius: 8px;
    text-align: center;
    .login_title {
      font-size: 20px;
      font-family: PingFang SC-Medium, PingFang SC;
      font-weight: 500;
      color: #1d2129;
      margin-bottom: 20px;
      text-align: left;
    }
  }
  </style>
  